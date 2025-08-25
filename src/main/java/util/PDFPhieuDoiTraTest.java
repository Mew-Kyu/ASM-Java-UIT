package util;

import dao.impl.PhieuDoiTraDAO;
import model.PhieuDoiTra;
import java.util.List;
import java.util.logging.Logger;

/**
 * Test utility for PDF generation of PhieuDoiTra
 */
public class PDFPhieuDoiTraTest {
    
    private static final Logger logger = Logger.getLogger(PDFPhieuDoiTraTest.class.getName());
    
    /**
     * Test PDF generation with existing PhieuDoiTra data
     */
    public static void testPDFGeneration() {
        logger.info("Starting PDF generation test...");
        
        try {
            // Get all PhieuDoiTra from database
            PhieuDoiTraDAO dao = new PhieuDoiTraDAO();
            List<PhieuDoiTra> phieuList = dao.findAll();
            
            if (phieuList.isEmpty()) {
                logger.warning("No PhieuDoiTra found in database");
                return;
            }
            
            logger.info("Found " + phieuList.size() + " PhieuDoiTra records");
            
            // Test with the first phieu
            PhieuDoiTra testPhieu = phieuList.get(0);
            logger.info("Testing with PhieuDoiTra ID: " + testPhieu.getMaPhieuDT());
            
            // Generate PDF
            String outputPath = "test_phieu_doi_tra_" + testPhieu.getMaPhieuDT() + ".pdf";
            PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(testPhieu, outputPath);
            
            logger.info("PDF generated successfully: " + outputPath);
            
            // Test with different types if available
            for (PhieuDoiTra phieu : phieuList) {
                if (phieu.getLyDo() != null && !phieu.getLyDo().trim().isEmpty()) {
                    String fileName = String.format("test_phieu_%s_%d.pdf", 
                        phieu.getLoaiPhieu(), phieu.getMaPhieuDT());
                    
                    try {
                        PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(phieu, fileName);
                        logger.info("Generated PDF for " + phieu.getLoaiPhieu() + " phieu: " + fileName);
                        break; // Test only one more
                    } catch (Exception e) {
                        logger.warning("Failed to generate PDF for phieu " + phieu.getMaPhieuDT() + ": " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.severe("Error during PDF generation test: " + e.getMessage());
            e.printStackTrace();
        }
        
        logger.info("PDF generation test completed");
    }
    
    /**
     * List all PhieuDoiTra with their basic info
     */
    public static void listAllPhieuDoiTra() {
        logger.info("Listing all PhieuDoiTra...");
        
        try {
            PhieuDoiTraDAO dao = new PhieuDoiTraDAO();
            List<PhieuDoiTra> phieuList = dao.findAll();
            
            logger.info("Found " + phieuList.size() + " PhieuDoiTra records:");
            
            for (PhieuDoiTra phieu : phieuList) {
                logger.info(String.format("ID: %d, Type: %s, Status: %s, Date: %s, Reason: %s", 
                    phieu.getMaPhieuDT(),
                    phieu.getLoaiPhieu(),
                    phieu.getTrangThai(),
                    phieu.getNgayTao(),
                    phieu.getLyDo() != null ? 
                        (phieu.getLyDo().length() > 50 ? phieu.getLyDo().substring(0, 50) + "..." : phieu.getLyDo()) 
                        : "N/A"));
            }
            
        } catch (Exception e) {
            logger.severe("Error listing PhieuDoiTra: " + e.getMessage());
        }
    }
    
    /**
     * Test database connection and basic operations
     */
    public static void testDatabaseConnection() {
        logger.info("Testing database connection...");
        
        try {
            PhieuDoiTraDAO dao = new PhieuDoiTraDAO();
            List<PhieuDoiTra> phieuList = dao.findAll();
            
            logger.info("Database connection successful. Found " + phieuList.size() + " records.");
            
            // Test different query methods
            List<PhieuDoiTra> pendingPhieu = dao.findByTrangThai("PENDING");
            logger.info("Found " + pendingPhieu.size() + " pending phieu");
            
            List<PhieuDoiTra> doiPhieu = dao.findByLoaiPhieu("DOI");
            logger.info("Found " + doiPhieu.size() + " DOI phieu");
            
            List<PhieuDoiTra> traPhieu = dao.findByLoaiPhieu("TRA");
            logger.info("Found " + traPhieu.size() + " TRA phieu");
            
        } catch (Exception e) {
            logger.severe("Database connection failed: " + e.getMessage());
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        logger.info("Starting PhieuDoiTra PDF Test Suite...");
        
        // Test database connection first
        testDatabaseConnection();
        
        // List all phieu
        listAllPhieuDoiTra();
        
        // Test PDF generation
        testPDFGeneration();
        
        logger.info("Test suite completed.");
    }
}
