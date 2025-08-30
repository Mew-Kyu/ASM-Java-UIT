package util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;

import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;

import model.HoaDon;
import model.ChiTietHoaDon;
import controller.ChiTietHoaDonController;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFInvoiceGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static void generateInvoicePDF(HoaDon hoaDon, String filePath) throws IOException {
        // Create PDF document
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        try {
            // Set font with Unicode support for Vietnamese
            PdfFont font = createVietnameseFont(false);
            PdfFont boldFont = createVietnameseFont(true);
            
            // Title with proper Vietnamese text
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);
            
            // Invoice information
            addInvoiceInfo(document, hoaDon, font, boldFont);
            
            // Product table
            addProductTable(document, hoaDon, font, boldFont);
            
            // Total
            addTotal(document, hoaDon, font, boldFont);
            
            // Footer
            addFooter(document, font);
            
        } finally {
            document.close();
        }
    }
    
    private static void addInvoiceInfo(Document document, HoaDon hoaDon, PdfFont font, PdfFont boldFont) {
        // Invoice details table
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        // Left column with proper Vietnamese text
        infoTable.addCell(new Cell().add(new Paragraph("Mã hóa đơn: " + hoaDon.getId()).setFont(font)));
        infoTable.addCell(new Cell().add(new Paragraph("Ngày lập: " + hoaDon.getNgayLap().format(DATE_FORMATTER)).setFont(font)));
        
        // Safely access customer name to avoid lazy loading issues
        String khachHang = "Khách lẻ";
        try {
            if (hoaDon.getMaKH() != null) {
                khachHang = hoaDon.getMaKH().getHoTen();
                if (khachHang == null || khachHang.trim().isEmpty()) {
                    khachHang = "Khách lẻ";
                }
            }
        } catch (Exception e) {
            // Handle proxy initialization errors
            khachHang = "Khách lẻ";
        }
        infoTable.addCell(new Cell().add(new Paragraph("Khách hàng: " + khachHang).setFont(font)));
        
        // Safely access employee name to avoid lazy loading issues
        String nhanVien = "N/A";
        try {
            if (hoaDon.getMaNV() != null) {
                nhanVien = hoaDon.getMaNV().getHoTen();
                if (nhanVien == null || nhanVien.trim().isEmpty()) {
                    nhanVien = "N/A";
                }
            }
        } catch (Exception e) {
            // Handle proxy initialization errors
            nhanVien = "N/A";
        }
        infoTable.addCell(new Cell().add(new Paragraph("Nhân viên: " + nhanVien).setFont(font)));
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }
    
    private static void addProductTable(Document document, HoaDon hoaDon, PdfFont font, PdfFont boldFont) throws IOException {
        // Get invoice details
        ChiTietHoaDonController chiTietController = new ChiTietHoaDonController();
        List<ChiTietHoaDon> chiTietList = chiTietController.getByHoaDonId(hoaDon.getId());
        
        // Product table
        Table productTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 2, 2}));
        productTable.setWidth(UnitValue.createPercentValue(100));
        
        // Table headers with proper Vietnamese text
        productTable.addHeaderCell(new Cell().add(new Paragraph("STT").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Tên sản phẩm").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        productTable.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER));
        
        // Table data
        int stt = 1;
        for (ChiTietHoaDon chiTiet : chiTietList) {
            productTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++)).setFont(font)).setTextAlignment(TextAlignment.CENTER));
            
            // Safely build product name to avoid lazy loading issues
            String tenSanPham = "N/A";
            try {
                if (chiTiet.getMaBienThe() != null && chiTiet.getMaBienThe().getMaSP() != null) {
                    tenSanPham = chiTiet.getMaBienThe().getMaSP().getTenSP();
                    if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
                        tenSanPham = "N/A";
                    } else {
                        // Add color and size information if available
                        try {
                            if (chiTiet.getMaBienThe().getMaMau() != null) {
                                String mauSac = chiTiet.getMaBienThe().getMaMau().getTenMau();
                                if (mauSac != null && !mauSac.trim().isEmpty()) {
                                    tenSanPham += " - " + mauSac;
                                }
                            }
                        } catch (Exception e) {
                            // Ignore color loading errors
                        }

                        try {
                            if (chiTiet.getMaBienThe().getMaSize() != null) {
                                String kichThuoc = chiTiet.getMaBienThe().getMaSize().getTenSize();
                                if (kichThuoc != null && !kichThuoc.trim().isEmpty()) {
                                    tenSanPham += " - " + kichThuoc;
                                }
                            }
                        } catch (Exception e) {
                            // Ignore size loading errors
                        }
                    }
                }
            } catch (Exception e) {
                // Handle proxy initialization errors
                tenSanPham = "N/A";
            }
            productTable.addCell(new Cell().add(new Paragraph(tenSanPham).setFont(font)));
            
            productTable.addCell(new Cell().add(new Paragraph(String.valueOf(chiTiet.getSoLuong())).setFont(font)).setTextAlignment(TextAlignment.CENTER));
            productTable.addCell(new Cell().add(new Paragraph(formatCurrency(chiTiet.getDonGia())).setFont(font)).setTextAlignment(TextAlignment.RIGHT));
            productTable.addCell(new Cell().add(new Paragraph(formatCurrency(chiTiet.getThanhTien())).setFont(font)).setTextAlignment(TextAlignment.RIGHT));
        }
        
        document.add(productTable);
        document.add(new Paragraph("\n"));
    }
    
    private static void addTotal(Document document, HoaDon hoaDon, PdfFont font, PdfFont boldFont) {
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        totalTable.setWidth(UnitValue.createPercentValue(60));
        totalTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        
        totalTable.addCell(new Cell().add(new Paragraph("TỔNG TIỀN:").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));
        totalTable.addCell(new Cell().add(new Paragraph(formatCurrency(hoaDon.getTongTien())).setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));
        
        document.add(totalTable);
        document.add(new Paragraph("\n"));
    }
    
    private static void addFooter(Document document, PdfFont font) {
        Paragraph footer = new Paragraph("Cảm ơn quý khách đã mua hàng!")
            .setFont(font)
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30);
        document.add(footer);
        
        Paragraph signature = new Paragraph("Nhân viên bán hàng")
            .setFont(font)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(50);
        document.add(signature);
    }
    
    private static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        return String.format("%,.0f VND", amount);
    }
    
    /**
     * Creates a font that supports Vietnamese Unicode characters
     */
    private static PdfFont createVietnameseFont(boolean bold) throws IOException {
        try {
            // Try to use system fonts that support Vietnamese
            String[] fontPaths = {
                // Windows fonts
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/arialbd.ttf", 
                "C:/Windows/Fonts/calibri.ttf",
                "C:/Windows/Fonts/calibrib.ttf",
                // Linux fonts
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
                // macOS fonts
                "/System/Library/Fonts/Arial.ttf",
                "/System/Library/Fonts/Arial Bold.ttf"
            };
            
            // Try to find and use a system font
            for (String fontPath : fontPaths) {
                try {
                    File fontFile = new File(fontPath);
                    if (fontFile.exists()) {
                        if (bold && (fontPath.contains("bd") || fontPath.contains("Bold"))) {
                            return PdfFontFactory.createFont(fontPath, "Identity-H", EmbeddingStrategy.PREFER_EMBEDDED);
                        } else if (!bold && !fontPath.contains("bd") && !fontPath.contains("Bold")) {
                            return PdfFontFactory.createFont(fontPath, "Identity-H", EmbeddingStrategy.PREFER_EMBEDDED);
                        }
                    }
                } catch (Exception e) {
                    // Continue to next font
                }
            }
            
            // Fallback: Use built-in font with Identity-H encoding for better Unicode support
            return PdfFontFactory.createFont(bold ? StandardFonts.HELVETICA_BOLD : StandardFonts.HELVETICA, 
                                           "Identity-H", EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            
        } catch (Exception e) {
            // Final fallback: Standard font
            return PdfFontFactory.createFont(bold ? StandardFonts.HELVETICA_BOLD : StandardFonts.HELVETICA);
        }
    }
}
