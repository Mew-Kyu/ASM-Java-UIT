package util;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import dao.impl.ChiTietPhieuDoiTraDAO;
import dao.impl.HoaDonDAO;
import dao.impl.NhanVienDAO;
import dao.impl.KhachHangDAO;
import dao.impl.BienTheSanPhamDAO;
import model.*;



import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * PDF Generator for Return/Exchange Receipt (Phiếu Đổi Trả)
 */
public class PDFPhieuDoiTraGenerator {
    
    private static final Logger logger = Logger.getLogger(PDFPhieuDoiTraGenerator.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Generate PDF for return/exchange receipt
     */
    public static void generatePhieuDoiTraPDF(PhieuDoiTra phieu, String filePath) throws IOException {
        logger.info("Generating PDF for PhieuDoiTra: " + phieu.getMaPhieuDT());
        
        // Create PDF document
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        try {
            // Set font with Unicode support for Vietnamese
            PdfFont font = createVietnameseFont(false);
            PdfFont boldFont = createVietnameseFont(true);
            
            // Title
            String title = getPhieuTitle(phieu.getLoaiPhieu());
            Paragraph titleParagraph = new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(titleParagraph);
            
            // Receipt information
            addPhieuInfo(document, phieu, font, boldFont);
            
            // Original invoice information
            addOriginalInvoiceInfo(document, phieu, font, boldFont);
            
            // Customer and employee information
            addCustomerEmployeeInfo(document, phieu, font, boldFont);
            
            // Product table
            addProductTable(document, phieu, font, boldFont);
            
            // Financial summary
            addFinancialSummary(document, phieu, font, boldFont);
            
            // Reason and notes
            addReasonAndNotes(document, phieu, font, boldFont);
            
            // Footer
            addFooter(document, font);
            
            logger.info("PDF generated successfully: " + filePath);
            
        } finally {
            document.close();
        }
    }
    
    private static String getPhieuTitle(String loaiPhieu) {
        return "DOI".equals(loaiPhieu) ? "PHIẾU ĐỔI HÀNG" : "PHIẾU TRẢ HÀNG";
    }
    
    private static PdfFont createVietnameseFont(boolean bold) throws IOException {
        logger.info("Creating Vietnamese font, bold: " + bold);

        // Try resource fonts first
        PdfFont resourceFont = tryResourceFonts(bold);
        if (resourceFont != null) {
            return resourceFont;
        }

        // Try system fonts
        PdfFont systemFont = trySystemFonts(bold);
        if (systemFont != null) {
            return systemFont;
        }

        // Try standard fonts with Unicode support
        PdfFont standardFont = tryStandardFonts(bold);
        if (standardFont != null) {
            return standardFont;
        }

        // Final fallback
        logger.warning("All font attempts failed, using basic Helvetica");
        return PdfFontFactory.createFont("Helvetica");
    }

    private static PdfFont tryResourceFonts(boolean bold) {
        String[] resourceFonts = {
            "/fonts/DejaVuSans.ttf",
            "/fonts/DejaVuSans-Bold.ttf",
            "/fonts/NotoSans-Regular.ttf",
            "/fonts/NotoSans-Bold.ttf",
            "/fonts/arial.ttf",
            "/fonts/arialbd.ttf"
        };

        for (String fontPath : resourceFonts) {
            boolean isBoldFont = fontPath.toLowerCase().contains("bold") || fontPath.toLowerCase().contains("bd");
            if (bold != isBoldFont) continue;

            try {
                logger.info("Trying resource font: " + fontPath);
                // Try to load from classpath
                java.io.InputStream fontStream = PDFPhieuDoiTraGenerator.class.getResourceAsStream(fontPath);
                if (fontStream != null) {
                    byte[] fontBytes = fontStream.readAllBytes();
                    fontStream.close();
                    PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                    logger.info("✅ Successfully created resource font: " + fontPath);
                    return font;
                }
            } catch (Exception e) {
                logger.warning("Failed to load resource font " + fontPath + ": " + e.getMessage());
            }
        }
        return null;
    }

    private static PdfFont trySystemFonts(boolean bold) {
        String[] systemFonts = {
            // Windows fonts
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/arialbd.ttf",
            "C:/Windows/Fonts/times.ttf",
            "C:/Windows/Fonts/timesbd.ttf",
            "C:/Windows/Fonts/calibri.ttf",
            "C:/Windows/Fonts/calibrib.ttf",
            "C:/Windows/Fonts/tahoma.ttf",
            "C:/Windows/Fonts/tahomabd.ttf",
            // Linux fonts
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf"
        };

        for (String fontPath : systemFonts) {
            boolean isBoldFont = fontPath.toLowerCase().contains("bold") || fontPath.toLowerCase().contains("bd");
            if (bold != isBoldFont) continue;

            try {
                java.io.File fontFile = new java.io.File(fontPath);
                if (fontFile.exists()) {
                    logger.info("Trying system font: " + fontPath);
                    PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                    logger.info("✅ Successfully created system font: " + fontPath);
                    return font;
                }
            } catch (Exception e) {
                logger.warning("Failed to load system font " + fontPath + ": " + e.getMessage());
            }
        }
        return null;
    }

    private static PdfFont tryStandardFonts(boolean bold) {
        String[] standardFonts = {
            bold ? "Helvetica-Bold" : "Helvetica",
            bold ? "Times-Bold" : "Times-Roman",
            bold ? "Courier-Bold" : "Courier"
        };

        for (String fontName : standardFonts) {
            try {
                logger.info("Trying standard font with IDENTITY_H: " + fontName);
                PdfFont font = PdfFontFactory.createFont(fontName, PdfEncodings.IDENTITY_H);
                logger.info("✅ Successfully created standard font: " + fontName);
                return font;
            } catch (Exception e) {
                logger.warning("Failed standard font " + fontName + " with IDENTITY_H: " + e.getMessage());

                // Try with UTF-8 as fallback
                try {
                    logger.info("Trying standard font with UTF-8: " + fontName);
                    PdfFont font = PdfFontFactory.createFont(fontName, PdfEncodings.UTF8);
                    logger.info("✅ Successfully created standard font with UTF-8: " + fontName);
                    return font;
                } catch (Exception e2) {
                    logger.warning("Failed standard font " + fontName + " with UTF-8: " + e2.getMessage());
                }
            }
        }
        return null;
    }
    
    private static void addPhieuInfo(Document document, PhieuDoiTra phieu, PdfFont font, PdfFont boldFont) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        // Receipt details
        infoTable.addCell(new Cell().add(new Paragraph("Mã phiếu: " + phieu.getMaPhieuDT()).setFont(font)));
        infoTable.addCell(new Cell().add(new Paragraph("Ngày tạo: " + phieu.getNgayTao().format(DATETIME_FORMATTER)).setFont(font)));
        
        String trangThaiText = getTrangThaiText(phieu.getTrangThai());
        infoTable.addCell(new Cell().add(new Paragraph("Loại phiếu: " + getLoaiPhieuText(phieu.getLoaiPhieu())).setFont(font)));
        infoTable.addCell(new Cell().add(new Paragraph("Trạng thái: " + trangThaiText).setFont(font)));
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }
    
    private static void addOriginalInvoiceInfo(Document document, PhieuDoiTra phieu, PdfFont font, PdfFont boldFont) {
        try {
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            HoaDon hoaDon = hoaDonDAO.findByIdWithDetails(phieu.getMaHD());
            
            if (hoaDon != null) {
                Paragraph invoiceHeader = new Paragraph("THÔNG TIN HÓA ĐƠN GỐC")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setMarginBottom(10);
                document.add(invoiceHeader);
                
                Table invoiceTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
                invoiceTable.setWidth(UnitValue.createPercentValue(100));
                
                invoiceTable.addCell(new Cell().add(new Paragraph("Mã hóa đơn: " + hoaDon.getId()).setFont(font)));
                invoiceTable.addCell(new Cell().add(new Paragraph("Ngày lập: " + hoaDon.getNgayLap().format(DATE_FORMATTER)).setFont(font)));
                invoiceTable.addCell(new Cell().add(new Paragraph("Tổng tiền HĐ: " + formatCurrency(hoaDon.getTotalAmount())).setFont(font)));
                invoiceTable.addCell(new Cell().add(new Paragraph("Số lượng SP: " + hoaDon.getTotalItems()).setFont(font)));
                
                document.add(invoiceTable);
                document.add(new Paragraph("\n"));
            }
        } catch (Exception e) {
            logger.warning("Could not load original invoice info: " + e.getMessage());
        }
    }
    
    private static void addCustomerEmployeeInfo(Document document, PhieuDoiTra phieu, PdfFont font, PdfFont boldFont) {
        Paragraph customerHeader = new Paragraph("THÔNG TIN KHÁCH HÀNG & NHÂN VIÊN")
            .setFont(boldFont)
            .setFontSize(12)
            .setMarginBottom(10);
        document.add(customerHeader);
        
        Table customerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        customerTable.setWidth(UnitValue.createPercentValue(100));
        
        // Customer info
        try {
            if (phieu.getMaKH() != null) {
                KhachHangDAO khachHangDAO = new KhachHangDAO();
                KhachHang khachHang = khachHangDAO.findById(phieu.getMaKH());
                if (khachHang != null) {
                    customerTable.addCell(new Cell().add(new Paragraph("Khách hàng: " + khachHang.getHoTen()).setFont(font)));
                    customerTable.addCell(new Cell().add(new Paragraph("Điện thoại: " + (khachHang.getDienThoai() != null ? khachHang.getDienThoai() : "N/A")).setFont(font)));
                } else {
                    customerTable.addCell(new Cell().add(new Paragraph("Khách hàng: Khách lẻ").setFont(font)));
                    customerTable.addCell(new Cell().add(new Paragraph("Điện thoại: N/A").setFont(font)));
                }
            } else {
                customerTable.addCell(new Cell().add(new Paragraph("Khách hàng: Khách lẻ").setFont(font)));
                customerTable.addCell(new Cell().add(new Paragraph("Điện thoại: N/A").setFont(font)));
            }
        } catch (Exception e) {
            customerTable.addCell(new Cell().add(new Paragraph("Khách hàng: Không xác định").setFont(font)));
            customerTable.addCell(new Cell().add(new Paragraph("Điện thoại: N/A").setFont(font)));
        }
        
        // Employee info
        try {
            NhanVienDAO nhanVienDAO = new NhanVienDAO();
            NhanVien nhanVien = nhanVienDAO.findById(phieu.getMaNV());
            if (nhanVien != null) {
                customerTable.addCell(new Cell().add(new Paragraph("Nhân viên xử lý: " + nhanVien.getHoTen()).setFont(font)));
                customerTable.addCell(new Cell().add(new Paragraph("Chức vụ: " + (nhanVien.getChucVu() != null ? nhanVien.getChucVu() : "N/A")).setFont(font)));
            } else {
                customerTable.addCell(new Cell().add(new Paragraph("Nhân viên xử lý: Không xác định").setFont(font)));
                customerTable.addCell(new Cell().add(new Paragraph("Chức vụ: N/A").setFont(font)));
            }
        } catch (Exception e) {
            customerTable.addCell(new Cell().add(new Paragraph("Nhân viên xử lý: Không xác định").setFont(font)));
            customerTable.addCell(new Cell().add(new Paragraph("Chức vụ: N/A").setFont(font)));
        }
        
        document.add(customerTable);
        document.add(new Paragraph("\n"));
    }
    
    private static String getLoaiPhieuText(String loaiPhieu) {
        return "DOI".equals(loaiPhieu) ? "Đổi hàng" : "Trả hàng";
    }
    
    private static String getTrangThaiText(String trangThai) {
        switch (trangThai) {
            case "PENDING": return "Chờ xử lý";
            case "APPROVED": return "Đã phê duyệt";
            case "REJECTED": return "Đã từ chối";
            case "COMPLETED": return "Đã hoàn thành";
            default: return trangThai;
        }
    }
    
    private static void addProductTable(Document document, PhieuDoiTra phieu, PdfFont font, PdfFont boldFont) {
        try {
            ChiTietPhieuDoiTraDAO chiTietDAO = new ChiTietPhieuDoiTraDAO();
            List<ChiTietPhieuDoiTra> chiTietList = chiTietDAO.findByPhieuDoiTra(phieu.getMaPhieuDT());

            if (chiTietList.isEmpty()) {
                document.add(new Paragraph("Không có sản phẩm nào trong phiếu").setFont(font));
                return;
            }

            Paragraph productHeader = new Paragraph("DANH SÁCH SẢN PHẨM")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(10);
            document.add(productHeader);

            // Product table
            Table productTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 1, 1, 2, 2}));
            productTable.setWidth(UnitValue.createPercentValue(100));

            // Headers
            productTable.addHeaderCell(new Cell().add(new Paragraph("STT").setFont(boldFont)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Tên sản phẩm").setFont(boldFont)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Màu sắc").setFont(boldFont)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Kích thước").setFont(boldFont)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("SL").setFont(boldFont)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(boldFont)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(boldFont)));

            BienTheSanPhamDAO bienTheDAO = new BienTheSanPhamDAO();
            int stt = 1;

            for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                try {
                    BienTheSanPham bienThe = bienTheDAO.findByIdWithDetails(chiTiet.getMaBienThe());

                    productTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++)).setFont(font)));

                    String tenSP = bienThe != null && bienThe.getMaSP() != null ?
                                  bienThe.getMaSP().getTenSP() : "N/A";
                    productTable.addCell(new Cell().add(new Paragraph(tenSP).setFont(font)));

                    String mauSac = bienThe != null && bienThe.getMaMau() != null ?
                                   bienThe.getMaMau().getTenMau() : "N/A";
                    productTable.addCell(new Cell().add(new Paragraph(mauSac).setFont(font)));

                    String kichThuoc = bienThe != null && bienThe.getMaSize() != null ?
                                      bienThe.getMaSize().getTenSize() : "N/A";
                    productTable.addCell(new Cell().add(new Paragraph(kichThuoc).setFont(font)));

                    productTable.addCell(new Cell().add(new Paragraph(String.valueOf(chiTiet.getSoLuong())).setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph(formatCurrency(chiTiet.getDonGia())).setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph(formatCurrency(chiTiet.getThanhTien())).setFont(font)));

                } catch (Exception e) {
                    logger.warning("Error loading product details for item: " + chiTiet.getMaBienThe());
                    productTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++)).setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph("Lỗi tải thông tin").setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph("N/A").setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph("N/A").setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph(String.valueOf(chiTiet.getSoLuong())).setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph(formatCurrency(chiTiet.getDonGia())).setFont(font)));
                    productTable.addCell(new Cell().add(new Paragraph(formatCurrency(chiTiet.getThanhTien())).setFont(font)));
                }
            }

            document.add(productTable);
            document.add(new Paragraph("\n"));

        } catch (Exception e) {
            logger.warning("Error loading product details: " + e.getMessage());
            document.add(new Paragraph("Lỗi khi tải danh sách sản phẩm").setFont(font));
        }
    }

    private static void addFinancialSummary(Document document, PhieuDoiTra phieu, PdfFont font, PdfFont boldFont) {
        Paragraph financialHeader = new Paragraph("TỔNG KẾT TÀI CHÍNH")
            .setFont(boldFont)
            .setFontSize(12)
            .setMarginBottom(10);
        document.add(financialHeader);

        Table financialTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        financialTable.setWidth(UnitValue.createPercentValue(100));

        financialTable.addCell(new Cell().add(new Paragraph("Tổng giá trị trả: " + formatCurrency(phieu.getTongGiaTriTra())).setFont(font)));
        financialTable.addCell(new Cell().add(new Paragraph("Tổng giá trị đổi: " + formatCurrency(phieu.getTongGiaTriDoi())).setFont(font)));
        financialTable.addCell(new Cell().add(new Paragraph("Số tiền hoàn lại: " + formatCurrency(phieu.getSoTienHoanLai())).setFont(font)));
        financialTable.addCell(new Cell().add(new Paragraph("Số tiền bổ sung: " + formatCurrency(phieu.getSoTienBoSung())).setFont(font)));

        if (phieu.getHinhThucHoanTien() != null) {
            String hinhThucText = getHinhThucHoanTienText(phieu.getHinhThucHoanTien());
            financialTable.addCell(new Cell().add(new Paragraph("Hình thức hoàn tiền: " + hinhThucText).setFont(font)));

            if (phieu.getSoTaiKhoanHoan() != null) {
                financialTable.addCell(new Cell().add(new Paragraph("Số tài khoản: " + phieu.getSoTaiKhoanHoan()).setFont(font)));
            } else {
                financialTable.addCell(new Cell().add(new Paragraph("Số tài khoản: N/A").setFont(font)));
            }
        }

        document.add(financialTable);
        document.add(new Paragraph("\n"));
    }

    private static void addReasonAndNotes(Document document, PhieuDoiTra phieu, PdfFont font, PdfFont boldFont) {
        Paragraph reasonHeader = new Paragraph("LÝ DO VÀ GHI CHÚ")
            .setFont(boldFont)
            .setFontSize(12)
            .setMarginBottom(10);
        document.add(reasonHeader);

        Table reasonTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        reasonTable.setWidth(UnitValue.createPercentValue(100));

        reasonTable.addCell(new Cell().add(new Paragraph("Lý do đổi/trả: " + (phieu.getLyDo() != null ? phieu.getLyDo() : "N/A")).setFont(font)));

        if (phieu.getGhiChu() != null && !phieu.getGhiChu().trim().isEmpty()) {
            reasonTable.addCell(new Cell().add(new Paragraph("Ghi chú: " + phieu.getGhiChu()).setFont(font)));
        }

        if (phieu.getLyDoTuChoi() != null && !phieu.getLyDoTuChoi().trim().isEmpty()) {
            reasonTable.addCell(new Cell().add(new Paragraph("Lý do từ chối: " + phieu.getLyDoTuChoi()).setFont(font)));
        }

        document.add(reasonTable);
        document.add(new Paragraph("\n"));
    }

    private static void addFooter(Document document, PdfFont font) {
        document.add(new Paragraph("\n"));

        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));

        footerTable.addCell(new Cell().add(new Paragraph("Khách hàng\n(Ký và ghi rõ họ tên)").setFont(font).setTextAlignment(TextAlignment.CENTER)));
        footerTable.addCell(new Cell().add(new Paragraph("Nhân viên xử lý\n(Ký và ghi rõ họ tên)").setFont(font).setTextAlignment(TextAlignment.CENTER)));

        document.add(footerTable);

        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!")
            .setFont(font)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10));
    }

    private static String getHinhThucHoanTienText(String hinhThuc) {
        switch (hinhThuc) {
            case "CASH": return "Tiền mặt";
            case "BANK_TRANSFER": return "Chuyển khoản";
            case "CREDIT_NOTE": return "Ghi có";
            default: return hinhThuc;
        }
    }

    private static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        return String.format("%,.0f VNĐ", amount);
    }
}
