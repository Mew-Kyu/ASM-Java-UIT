package util;

import dao.impl.HoaDonDAO;
import model.HoaDon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for testing database operations and debugging invoice search issues
 */
public class DatabaseTestUtil {
    private static final Logger logger = Logger.getLogger(DatabaseTestUtil.class.getName());
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.createQuery("SELECT COUNT(h) FROM HoaDon h").getSingleResult();
            logger.info("Database connection successful");
            return true;
        } catch (Exception e) {
            logger.severe("Database connection failed: " + e.getMessage());
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * List all invoice IDs in the database
     */
    public static void listAllInvoiceIds() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            List<Integer> ids = em.createQuery("SELECT h.id FROM HoaDon h ORDER BY h.id", Integer.class)
                                 .getResultList();
            
            logger.info("Found " + ids.size() + " invoices in database:");
            for (Integer id : ids) {
                logger.info("Invoice ID: " + id);
            }
        } catch (Exception e) {
            logger.severe("Error listing invoice IDs: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Test finding a specific invoice
     */
    public static void testFindInvoice(int maHD) {
        logger.info("Testing search for invoice ID: " + maHD);
        
        HoaDonDAO dao = new HoaDonDAO();
        HoaDon hoaDon = dao.findByIdWithDetails(maHD);
        
        if (hoaDon != null) {
            logger.info("SUCCESS: Found invoice " + hoaDon.getId());
            logger.info("Invoice date: " + hoaDon.getNgayLap());
            logger.info("Total amount: " + hoaDon.getTotalAmount());
            logger.info("Number of items: " + hoaDon.getChiTietHoaDons().size());
            
            if (hoaDon.getMaKH() != null) {
                logger.info("Customer: " + hoaDon.getMaKH().getHoTen());
            } else {
                logger.info("Customer: Walk-in customer");
            }
        } else {
            logger.warning("FAILED: Invoice not found with ID: " + maHD);
        }
    }
    
    /**
     * Test the return/exchange eligibility check
     */
    public static void testReturnEligibility(int maHD) {
        try {
            service.impl.PhieuDoiTraServiceImpl service = new service.impl.PhieuDoiTraServiceImpl();
            boolean canReturn = service.kiemTraHoaDonCoTheDoisTra(maHD);
            
            logger.info("Return eligibility for invoice " + maHD + ": " + canReturn);
        } catch (Exception e) {
            logger.severe("Error checking return eligibility: " + e.getMessage());
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        logger.info("Starting database tests...");
        
        // Test connection
        if (!testConnection()) {
            logger.severe("Cannot connect to database. Exiting.");
            return;
        }
        
        // List all invoices
        listAllInvoiceIds();
        
        // Test specific invoice (change this ID to test)
        int testInvoiceId = 59; // Change this to an actual invoice ID from your database
        testFindInvoice(testInvoiceId);
        testReturnEligibility(testInvoiceId);

        // Test non-existing invoice
        int nonExistingId = 999;
        testFindInvoice(nonExistingId);
        testReturnEligibility(nonExistingId);
        
        logger.info("Database tests completed.");
    }
}
