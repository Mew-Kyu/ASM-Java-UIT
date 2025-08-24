package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.IHinhThucThanhToanDAO;
import model.HinhThucThanhToan;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object implementation for Payment Method operations
 */
public class HinhThucThanhToanDAO extends BaseDAO<HinhThucThanhToan, Integer> implements IHinhThucThanhToanDAO {
    
    public HinhThucThanhToanDAO() {
        super(HinhThucThanhToan.class);
    }
    
    @Override
    public void insert(HinhThucThanhToan hinhThucThanhToan) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(hinhThucThanhToan);
        });
    }
    
    @Override
    public void update(HinhThucThanhToan hinhThucThanhToan) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(hinhThucThanhToan);
        });
    }
    
    @Override
    public void delete(int maHTTT) {
        EntityManagerUtil.executeInTransaction(em -> {
            HinhThucThanhToan httt = em.find(HinhThucThanhToan.class, maHTTT);
            if (httt != null) {
                em.remove(httt);
            }
        });
    }
    
    @Override
    public Optional<HinhThucThanhToan> findById(int maHTTT) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            HinhThucThanhToan httt = em.find(HinhThucThanhToan.class, maHTTT);
            return Optional.ofNullable(httt);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<HinhThucThanhToan> findAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<HinhThucThanhToan> query = em.createQuery(
                "SELECT h FROM HinhThucThanhToan h ORDER BY h.thuTuHienThi ASC", 
                HinhThucThanhToan.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<HinhThucThanhToan> findByTrangThai(boolean trangThai) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<HinhThucThanhToan> query = em.createQuery(
                "SELECT h FROM HinhThucThanhToan h WHERE h.trangThai = :trangThai ORDER BY h.thuTuHienThi ASC", 
                HinhThucThanhToan.class);
            query.setParameter("trangThai", trangThai);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<HinhThucThanhToan> findByLoaiThanhToan(String loaiThanhToan) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<HinhThucThanhToan> query = em.createQuery(
                "SELECT h FROM HinhThucThanhToan h WHERE h.loaiThanhToan = :loaiThanhToan ORDER BY h.thuTuHienThi ASC", 
                HinhThucThanhToan.class);
            query.setParameter("loaiThanhToan", loaiThanhToan);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
