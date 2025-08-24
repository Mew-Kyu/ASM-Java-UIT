package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.INhaCungCapDAO;
import model.NhaCungCap;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object implementation for Supplier operations
 */
public class NhaCungCapDAO extends BaseDAO<NhaCungCap, Integer> implements INhaCungCapDAO {
    
    public NhaCungCapDAO() {
        super(NhaCungCap.class);
    }
    
    @Override
    public void insert(NhaCungCap nhaCungCap) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(nhaCungCap);
        });
    }
    
    @Override
    public void update(NhaCungCap nhaCungCap) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(nhaCungCap);
        });
    }
    
    @Override
    public void delete(int maNCC) {
        EntityManagerUtil.executeInTransaction(em -> {
            NhaCungCap ncc = em.find(NhaCungCap.class, maNCC);
            if (ncc != null) {
                em.remove(ncc);
            }
        });
    }
    
    @Override
    public Optional<NhaCungCap> findById(int maNCC) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            NhaCungCap ncc = em.find(NhaCungCap.class, maNCC);
            return Optional.ofNullable(ncc);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<NhaCungCap> findAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<NhaCungCap> query = em.createQuery(
                "SELECT n FROM NhaCungCap n ORDER BY n.tenNCC ASC", 
                NhaCungCap.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<NhaCungCap> findByTrangThai(boolean trangThai) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<NhaCungCap> query = em.createQuery(
                "SELECT n FROM NhaCungCap n WHERE n.trangThai = :trangThai ORDER BY n.tenNCC ASC", 
                NhaCungCap.class);
            query.setParameter("trangThai", trangThai);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<NhaCungCap> findByTenNCC(String tenNCC) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<NhaCungCap> query = em.createQuery(
                "SELECT n FROM NhaCungCap n WHERE n.tenNCC LIKE :tenNCC ORDER BY n.tenNCC ASC", 
                NhaCungCap.class);
            query.setParameter("tenNCC", "%" + tenNCC + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}