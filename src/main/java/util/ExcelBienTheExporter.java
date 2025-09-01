package util;

import model.BienTheSanPham;
import model.MauSac;
import model.KichThuoc;
import model.SanPham;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Export list of BienTheSanPham (product variants) to Excel (.xlsx).
 */
public class ExcelBienTheExporter {

    public static void export(List<BienTheSanPham> variants, String filePath) throws IOException {
        if (variants == null) throw new IllegalArgumentException("Danh sách biến thể null");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("BienThe");

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            DataFormat df = workbook.createDataFormat();
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setDataFormat(df.getFormat("#,##0"));

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            moneyStyle.setDataFormat(df.getFormat("#,##0"));

            // Header
            String[] headers = {"Mã BT","Sản phẩm","Màu sắc","Kích thước","Số lượng","Giá bán","Tổng giá trị","Trạng thái"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (BienTheSanPham b : variants) {
                Row r = sheet.createRow(rowIdx++);
                int col = 0;
                r.createCell(col++).setCellValue(b.getId() != null ? b.getId() : 0);
                r.createCell(col++).setCellValue(getSafeName(b.getMaSP()));
                r.createCell(col++).setCellValue(getSafeName(b.getMaMau()));
                r.createCell(col++).setCellValue(getSafeName(b.getMaSize()));

                // Số lượng
                Cell soLuongCell = r.createCell(col++);
                int so = b.getSoLuong() == null ? 0 : b.getSoLuong();
                soLuongCell.setCellValue(so);
                soLuongCell.setCellStyle(numberStyle);

                // Giá bán
                Cell giaCell = r.createCell(col++);
                BigDecimal gia = b.getGiaBan() == null ? BigDecimal.ZERO : b.getGiaBan();
                giaCell.setCellValue(gia.doubleValue());
                giaCell.setCellStyle(moneyStyle);

                // Tổng giá trị
                Cell totalCell = r.createCell(col++);
                BigDecimal total = gia.multiply(BigDecimal.valueOf(so));
                totalCell.setCellValue(total.doubleValue());
                totalCell.setCellStyle(moneyStyle);

                // Trạng thái
                r.createCell(col).setCellValue(getStockStatus(so));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private static String getSafeName(SanPham sp){
        try { return sp != null && sp.getTenSP()!=null ? sp.getTenSP() : ""; } catch (Exception e){ return ""; }
    }
    private static String getSafeName(MauSac m){
        try { return m != null && m.getTenMau()!=null ? m.getTenMau() : ""; } catch (Exception e){ return ""; }
    }
    private static String getSafeName(KichThuoc k){
        try { return k != null && k.getTenSize()!=null ? k.getTenSize() : ""; } catch (Exception e){ return ""; }
    }

    private static String getStockStatus(int soLuong){
        if (soLuong <= 0) return "Hết hàng";
        if (soLuong <= 10) return "Sắp hết";
        return "Còn hàng";
    }
}

