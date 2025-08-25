package util;

import model.PhieuDoiTra;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Test utility for Vietnamese font support in PDF generation
 */
public class VietnameseFontTest {
    
    private static final Logger logger = Logger.getLogger(VietnameseFontTest.class.getName());
    
    /**
     * Test Vietnamese characters in PDF
     */
    public static void testVietnameseCharacters() {
        logger.info("Testing Vietnamese font support...");
        
        try {
            // Create mock PhieuDoiTra with Vietnamese text
            PhieuDoiTra mockPhieu = new PhieuDoiTra();
            mockPhieu.setMaPhieuDT(888);
            mockPhieu.setMaHD(59);
            mockPhieu.setLoaiPhieu("DOI");
            mockPhieu.setNgayTao(LocalDateTime.now());
            mockPhieu.setMaNV(1);
            mockPhieu.setMaKH(1);
            
            // Vietnamese text with all special characters
            String vietnameseReason = "Sản phẩm bị lỗi, đường may không đều. " +
                                    "Khách hàng muốn đổi size từ M sang L. " +
                                    "Màu sắc không phù hợp với dáng người. " +
                                    "Chất liệu vải không như mô tả. " +
                                    "Áo quần bị nhăn, không đẹp như hình ảnh.";
            
            String vietnameseNote = "Ghi chú: Khách hàng là người quen, " +
                                  "đã mua hàng nhiều lần. Cần xử lý nhanh chóng. " +
                                  "Đổi sang sản phẩm cùng loại, màu khác. " +
                                  "Hoàn tiền nếu không có hàng thay thế.";
            
            mockPhieu.setLyDo(vietnameseReason);
            mockPhieu.setTrangThai("APPROVED");
            mockPhieu.setTongGiaTriTra(new BigDecimal("350000"));
            mockPhieu.setTongGiaTriDoi(new BigDecimal("420000"));
            mockPhieu.setSoTienBoSung(new BigDecimal("70000"));
            mockPhieu.setGhiChu(vietnameseNote);
            
            // Generate PDF
            String outputPath = "test_vietnamese_font.pdf";
            PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(mockPhieu, outputPath);
            
            logger.info("✅ Vietnamese font test PDF generated: " + outputPath);
            logger.info("📄 Please check the PDF to verify Vietnamese characters display correctly");
            
            // Test specific Vietnamese characters
            testSpecificCharacters();
            
        } catch (Exception e) {
            logger.severe("❌ Error generating Vietnamese font test PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test specific Vietnamese characters that commonly cause issues
     */
    private static void testSpecificCharacters() {
        logger.info("Testing specific Vietnamese characters...");
        
        try {
            PhieuDoiTra testPhieu = new PhieuDoiTra();
            testPhieu.setMaPhieuDT(777);
            testPhieu.setMaHD(59);
            testPhieu.setLoaiPhieu("TRA");
            testPhieu.setNgayTao(LocalDateTime.now());
            testPhieu.setMaNV(1);
            testPhieu.setMaKH(1);
            
            // Test all Vietnamese special characters
            String specialChars = "Test các ký tự đặc biệt tiếng Việt:\n" +
                                "- Ă ă: Tăng, băng, mặt, lắc\n" +
                                "- Â â: Cầu, tâm, lâu, mầu\n" +
                                "- Ê ê: Kê, mê, rể, lễ\n" +
                                "- Ô ô: Cô, tô, lô, mô\n" +
                                "- Ơ ơ: Cơ, tơ, lơ, mơ\n" +
                                "- Ư ư: Cư, tư, lư, mư\n" +
                                "- Đ đ: Đá, đi, đó, đủ\n" +
                                "- Dấu sắc: á é í ó ú ý\n" +
                                "- Dấu huyền: à è ì ò ù ỳ\n" +
                                "- Dấu hỏi: ả ẻ ỉ ỏ ủ ỷ\n" +
                                "- Dấu ngã: ã ẽ ĩ õ ũ ỹ\n" +
                                "- Dấu nặng: ạ ẹ ị ọ ụ ỵ";
            
            testPhieu.setLyDo(specialChars);
            testPhieu.setTrangThai("COMPLETED");
            testPhieu.setTongGiaTriTra(new BigDecimal("500000"));
            testPhieu.setGhiChu("Kiểm tra hiển thị ký tự đặc biệt");
            
            String outputPath = "test_vietnamese_special_chars.pdf";
            PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(testPhieu, outputPath);
            
            logger.info("✅ Special characters test PDF generated: " + outputPath);
            
        } catch (Exception e) {
            logger.severe("❌ Error generating special characters test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test with real database data containing Vietnamese
     */
    public static void testWithRealData() {
        logger.info("Testing with real database data...");
        
        try {
            dao.impl.PhieuDoiTraDAO dao = new dao.impl.PhieuDoiTraDAO();
            java.util.List<model.PhieuDoiTra> phieuList = dao.findAll();
            
            if (phieuList.isEmpty()) {
                logger.warning("No PhieuDoiTra found in database");
                return;
            }
            
            // Find a phieu with Vietnamese text
            model.PhieuDoiTra vietnamesePhieu = null;
            for (model.PhieuDoiTra phieu : phieuList) {
                if (phieu.getLyDo() != null && containsVietnamese(phieu.getLyDo())) {
                    vietnamesePhieu = phieu;
                    break;
                }
            }
            
            if (vietnamesePhieu != null) {
                String outputPath = "test_real_vietnamese_data_" + vietnamesePhieu.getMaPhieuDT() + ".pdf";
                PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(vietnamesePhieu, outputPath);
                
                logger.info("✅ Real Vietnamese data test PDF generated: " + outputPath);
                logger.info("📝 Phieu ID: " + vietnamesePhieu.getMaPhieuDT());
                logger.info("📝 Reason: " + vietnamesePhieu.getLyDo());
            } else {
                logger.info("ℹ️ No phieu with Vietnamese text found in database");
            }
            
        } catch (Exception e) {
            logger.severe("❌ Error testing with real data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if text contains Vietnamese characters
     */
    private static boolean containsVietnamese(String text) {
        if (text == null) return false;
        
        // Check for Vietnamese special characters
        String vietnameseChars = "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ" +
                                "ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";
        
        for (char c : text.toCharArray()) {
            if (vietnameseChars.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        logger.info("Starting Vietnamese font test suite...");
        
        // Test 1: Mock data with Vietnamese
        testVietnameseCharacters();
        
        // Test 2: Real database data
        testWithRealData();
        
        logger.info("Vietnamese font test suite completed.");
        logger.info("📋 Check the generated PDF files to verify Vietnamese character display:");
        logger.info("   - test_vietnamese_font.pdf");
        logger.info("   - test_vietnamese_special_chars.pdf");
        logger.info("   - test_real_vietnamese_data_*.pdf (if available)");
    }
}
