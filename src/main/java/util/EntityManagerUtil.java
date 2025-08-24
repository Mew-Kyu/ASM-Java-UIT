package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import exception.DAOException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized EntityManager utility following Singleton pattern
 * Provides transaction management and resource cleanup
 */
public class EntityManagerUtil {
    private static final Logger LOGGER = Logger.getLogger(EntityManagerUtil.class.getName());
    private static EntityManagerFactory entityManagerFactory;
    private static final String PERSISTENCE_UNIT_NAME = "QuanLyCuaHangPU";
    
    // Private constructor to prevent instantiation
    private EntityManagerUtil() {
    }
    
    /**
     * Get EntityManagerFactory instance (Singleton)
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                LOGGER.info("EntityManagerFactory created successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create EntityManagerFactory", e);
                throw new DAOException("Failed to initialize database connection", e);
            }
        }
        return entityManagerFactory;
    }
    
    /**
     * Get a new EntityManager instance
     * Note: Caller is responsible for closing the EntityManager
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Execute operation within transaction (for write operations)
     */
    public static void executeInTransaction(Consumer<EntityManager> operation) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            operation.accept(em);
            tx.commit();
            LOGGER.fine("Transaction completed successfully");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
                LOGGER.warning("Transaction rolled back due to error");
            }
            LOGGER.log(Level.SEVERE, "Transaction failed", e);
            throw new DAOException("Database operation failed", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Execute operation and return result (for read operations)
     */
    public static <T> T executeInTransaction(Function<EntityManager, T> operation) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            T result = operation.apply(em);
            tx.commit();
            LOGGER.fine("Transaction completed successfully");
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
                LOGGER.warning("Transaction rolled back due to error");
            }
            LOGGER.log(Level.SEVERE, "Transaction failed", e);
            throw new DAOException("Database operation failed", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Execute read-only operation (no transaction needed)
     */
    public static <T> T executeReadOnly(Function<EntityManager, T> operation) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        
        try {
            return operation.apply(em);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Read operation failed", e);
            throw new DAOException("Database read operation failed", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Close EntityManagerFactory on application shutdown
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            LOGGER.info("EntityManagerFactory closed");
        }
    }
}
