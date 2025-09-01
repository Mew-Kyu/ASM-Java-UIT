package util;

import model.HoaDon;
import model.KhachHang;
import model.NhanVien;
import model.HinhThucThanhToan;
import controller.HinhThucThanhToanController;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to export invoice list to an Excel (.xlsx) file.
 */
public class ExcelInvoiceExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Mapping DB payment status to display (duplicate of HoaDonUI map to decouple)
    private static final Map<String, String> DB_TO_DISPLAY = new HashMap<>();
    static {
        DB_TO_DISPLAY.put("PENDING", "Chờ thanh toán");
        DB_TO_DISPLAY.put("PROCESSING", "Đang xử lý");
        DB_TO_DISPLAY.put("COMPLETED", "Đã hoàn tất");
        DB_TO_DISPLAY.put("FAILED", "Thất bại");
        DB_TO_DISPLAY.put("REFUNDED", "Đã hoàn tiền");
        DB_TO_DISPLAY.put("CANCELLED", "Đã hủy");
    }

    public static void export(List<HoaDon> invoices, String filePath) throws IOException {
        if (invoices == null) throw new IllegalArgumentException("Danh sách hóa đơn null");

        // Cache payment method names
        Map<Integer, String> paymentMethodNames = new HashMap<>();
        try {
            HinhThucThanhToanController controller = new HinhThucThanhToanController();
            for (HinhThucThanhToan m : controller.getActiveHinhThucThanhToan()) {
                paymentMethodNames.put(m.getMaHTTT(), m.getTenHTTT());
            }
        } catch (Exception ignored) {
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            CreationHelper creationHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("HoaDon");

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle moneyStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Header
            String[] headers = {"Mã hóa đơn", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền", "Số items", "Trạng thái", "Hình thức thanh toán"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (HoaDon hd : invoices) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                // Mã HĐ
                row.createCell(col++).setCellValue(hd.getId() != null ? hd.getId() : 0);
                // Ngày lập
                String ngay = hd.getNgayLap() != null ? hd.getNgayLap().format(DATE_FORMATTER) : "";
                row.createCell(col++).setCellValue(ngay);
                // Khách hàng
                KhachHang kh = null;
                try { kh = hd.getMaKH(); } catch (Exception ignored) {}
                row.createCell(col++).setCellValue(kh != null ? safe(kh.getHoTen(), "Khách lẻ") : "Khách lẻ");
                // Nhân viên
                NhanVien nv = null;
                try { nv = hd.getMaNV(); } catch (Exception ignored) {}
                row.createCell(col++).setCellValue(nv != null ? safe(nv.getHoTen(), "N/A") : "N/A");
                // Tổng tiền
                BigDecimal tong = hd.getTongTien();
                Cell moneyCell = row.createCell(col++);
                moneyCell.setCellValue(tong != null ? tong.doubleValue() : 0d);
                moneyCell.setCellStyle(moneyStyle);
                // Số items
                int items = 0;
                try { items = hd.getTotalItems(); } catch (Exception ignored) {}
                row.createCell(col++).setCellValue(items);
                // Trạng thái
                String status = hd.getTrangThaiThanhToan();
                row.createCell(col++).setCellValue(DB_TO_DISPLAY.getOrDefault(status, status != null ? status : ""));
                // Hình thức thanh toán
                if (hd.getMaHTTT() != null) {
                    row.createCell(col++).setCellValue(paymentMethodNames.getOrDefault(hd.getMaHTTT(), String.valueOf(hd.getMaHTTT())));
                } else {
                    row.createCell(col++).setCellValue("");
                }
            }

            // Autosize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private static String safe(String v, String def) {
        if (v == null) return def;
        v = v.trim();
        return v.isEmpty() ? def : v;
    }
}

