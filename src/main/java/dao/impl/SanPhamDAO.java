package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.ISanPhamDAO;
import jakarta.persistence.TypedQuery;
import model.SanPham;
import util.EntityManagerUtil;

import java.util.List;

/**
 * SanPham DAO implementation extending BaseDAO
 * Eliminates code duplication and follows DRY principle
 */
public class SanPhamDAO extends BaseDAO<SanPham, Integer> implements ISanPhamDAO {
    
    public SanPhamDAO() {
        super(SanPham.class);
    }
    
    @Override
    public void insert(SanPham sp) {
        super.insert(sp);
    }
    
    @Override
    public void update(SanPham sp) {
        super.update(sp);
    }
    
    @Override
    public void delete(int maSP) {
        super.delete(maSP);
    }
    
    @Override
    public SanPham findById(int maSP) {
        return super.findById(maSP).orElse(null);
    }
    
    @Override
    public List<SanPham> findAll() {
        // Use custom query with JOIN FETCH for better performance
        return EntityManagerUtil.executeReadOnly(em -> {
            TypedQuery<SanPham> query = em.createQuery(
                "SELECT s FROM SanPham s LEFT JOIN FETCH s.maDM", SanPham.class);
            return query.getResultList();
        });
    }
    
    @Override
    public List<SanPham> findByName(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            return findAll();
        }
        
        return EntityManagerUtil.executeReadOnly(em -> {
            TypedQuery<SanPham> query = em.createQuery(
                "SELECT s FROM SanPham s LEFT JOIN FETCH s.maDM WHERE s.tenSP LIKE :tenSP", SanPham.class);
            query.setParameter("tenSP", "%" + tenSP.trim() + "%");
            return query.getResultList();
        });
    }
    
    /**
     * Find products by category ID
     */
    public List<SanPham> findByCategory(Integer categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        
        return EntityManagerUtil.executeReadOnly(em -> {
            TypedQuery<SanPham> query = em.createQuery(
                "SELECT s FROM SanPham s LEFT JOIN FETCH s.maDM WHERE s.maDM.id = :categoryId", SanPham.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        });
    }
    
    /**
     * Check if product name exists (excluding specific ID)
     */
    public boolean existsByName(String productName, Integer excludeId) {
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }
        
        return EntityManagerUtil.executeReadOnly(em -> {
            String jpql = "SELECT COUNT(s) FROM SanPham s WHERE LOWER(s.tenSP) = LOWER(:name)";
            if (excludeId != null) {
                jpql += " AND s.id != :excludeId";
            }
            
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("name", productName.trim());
            if (excludeId != null) {
                query.setParameter("excludeId", excludeId);
            }
            
            return query.getSingleResult() > 0;
        });
    }
    
    @Override
    protected void validateEntity(SanPham entity) {
        super.validateEntity(entity);
        
        if (entity.getTenSP() == null || entity.getTenSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        
        if (entity.getDanhMuc() == null) {
            throw new IllegalArgumentException("Danh mục sản phẩm không được để trống");
        }
    }
}