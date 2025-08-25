package util;

import model.PhieuDoiTra;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Simple test for PDF generation without database dependency
 */
public class SimplePDFTest {
    
    public static void main(String[] args) {
        System.out.println("Testing PDF generation with mock data...");
        
        try {
            // Create mock PhieuDoiTra
            PhieuDoiTra mockPhieu = new PhieuDoiTra();
            mockPhieu.setMaPhieuDT(999);
            mockPhieu.setMaHD(59);
            mockPhieu.setLoaiPhieu("DOI");
            mockPhieu.setNgayTao(LocalDateTime.now());
            mockPhieu.setMaNV(1);
            mockPhieu.setMaKH(1);
            mockPhieu.setLyDo("Sản phẩm bị lỗi, khách hàng yêu cầu đổi");
            mockPhieu.setTrangThai("APPROVED");
            mockPhieu.setTongGiaTriTra(new BigDecimal("200000"));
            mockPhieu.setTongGiaTriDoi(new BigDecimal("250000"));
            mockPhieu.setSoTienBoSung(new BigDecimal("50000"));
            mockPhieu.setGhiChu("Đổi sang sản phẩm cùng loại màu khác");
            
            // Generate PDF
            String outputPath = "test_mock_phieu_doi_tra.pdf";
            PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(mockPhieu, outputPath);
            
            System.out.println("✅ PDF generated successfully: " + outputPath);
            System.out.println("📄 Please check the generated PDF file to verify content");
            
        } catch (Exception e) {
            System.err.println("❌ Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
