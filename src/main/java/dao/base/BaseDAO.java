package dao.base;

import jakarta.persistence.TypedQuery;
import util.EntityManagerUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Abstract base DAO class implementing common CRUD operations
 * Follows Template Method pattern and eliminates code duplication
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public abstract class BaseDAO<T, ID> {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    protected final Class<T> entityClass;
    
    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Insert new entity
     */
    public void insert(T entity) {
        validateEntity(entity);
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(entity);
            logger.fine("Entity inserted: " + entity.getClass().getSimpleName());
        });
    }
    
    /**
     * Update existing entity
     */
    public void update(T entity) {
        validateEntity(entity);
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(entity);
            logger.fine("Entity updated: " + entity.getClass().getSimpleName());
        });
    }
    
    /**
     * Delete entity by ID
     */
    public void delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        
        EntityManagerUtil.executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                logger.fine("Entity deleted: " + entityClass.getSimpleName() + " with ID: " + id);
            } else {
                logger.warning("Entity not found for deletion: " + entityClass.getSimpleName() + " with ID: " + id);
            }
        });
    }
    
    /**
     * Find entity by ID
     */
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        
        return EntityManagerUtil.executeReadOnly(em -> {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        });
    }
    
    /**
     * Find all entities
     */
    public List<T> findAll() {
        return EntityManagerUtil.executeReadOnly(em -> {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        });
    }
    
    /**
     * Count all entities
     */
    public long count() {
        return EntityManagerUtil.executeReadOnly(em -> {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        });
    }
    
    /**
     * Check if entity exists by ID
     */
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
    
    /**
     * Execute custom query
     */
    protected List<T> executeQuery(String jpql, Object... parameters) {
        return EntityManagerUtil.executeReadOnly(em -> {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        });
    }
    
    /**
     * Execute custom query with named parameters
     */
    protected List<T> executeNamedQuery(String jpql, String[] paramNames, Object[] paramValues) {
        if (paramNames.length != paramValues.length) {
            throw new IllegalArgumentException("Parameter names and values arrays must have same length");
        }
        
        return EntityManagerUtil.executeReadOnly(em -> {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < paramNames.length; i++) {
                query.setParameter(paramNames[i], paramValues[i]);
            }
            return query.getResultList();
        });
    }
    
    /**
     * Template method for entity validation - can be overridden by subclasses
     */
    protected void validateEntity(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        // Subclasses can override for specific validation
    }
    
    /**
     * Get entity class name for logging
     */
    protected String getEntityName() {
        return entityClass.getSimpleName();
    }
}
