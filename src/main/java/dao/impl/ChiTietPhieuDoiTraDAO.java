package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.IChiTietPhieuDoiTraDAO;
import model.ChiTietPhieuDoiTra;
import model.ChiTietPhieuDoiTraId;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object implementation for Returns & Exchanges Detail operations
 */
public class ChiTietPhieuDoiTraDAO extends BaseDAO<ChiTietPhieuDoiTra, ChiTietPhieuDoiTraId> 
        implements IChiTietPhieuDoiTraDAO {
    
    public ChiTietPhieuDoiTraDAO() {
        super(ChiTietPhieuDoiTra.class);
    }
    
    @Override
    public void insert(ChiTietPhieuDoiTra chiTiet) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(chiTiet);
        });
    }
    
    @Override
    public void update(ChiTietPhieuDoiTra chiTiet) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(chiTiet);
        });
    }
    
    @Override
    public void delete(ChiTietPhieuDoiTraId id) {
        EntityManagerUtil.executeInTransaction(em -> {
            ChiTietPhieuDoiTra chiTiet = em.find(ChiTietPhieuDoiTra.class, id);
            if (chiTiet != null) {
                em.remove(chiTiet);
            }
        });
    }
    
    @Override
    public void deleteByPhieuDoiTra(int maPhieuDT) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.createQuery("DELETE FROM ChiTietPhieuDoiTra c WHERE c.maPhieuDT = :maPhieuDT")
              .setParameter("maPhieuDT", maPhieuDT)
              .executeUpdate();
        });
    }
    
    @Override
    public Optional<ChiTietPhieuDoiTra> findById(ChiTietPhieuDoiTraId id) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            ChiTietPhieuDoiTra chiTiet = em.find(ChiTietPhieuDoiTra.class, id);
            return Optional.ofNullable(chiTiet);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ChiTietPhieuDoiTra> findByPhieuDoiTra(int maPhieuDT) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<ChiTietPhieuDoiTra> query = em.createQuery(
                "SELECT c FROM ChiTietPhieuDoiTra c WHERE c.maPhieuDT = :maPhieuDT", 
                ChiTietPhieuDoiTra.class);
            query.setParameter("maPhieuDT", maPhieuDT);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ChiTietPhieuDoiTra> findByBienThe(int maBienThe) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<ChiTietPhieuDoiTra> query = em.createQuery(
                "SELECT c FROM ChiTietPhieuDoiTra c WHERE c.maBienThe = :maBienThe ORDER BY c.maPhieuDT DESC", 
                ChiTietPhieuDoiTra.class);
            query.setParameter("maBienThe", maBienThe);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ChiTietPhieuDoiTra> findByLoaiChiTiet(String loaiChiTiet) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<ChiTietPhieuDoiTra> query = em.createQuery(
                "SELECT c FROM ChiTietPhieuDoiTra c WHERE c.loaiChiTiet = :loaiChiTiet ORDER BY c.maPhieuDT DESC", 
                ChiTietPhieuDoiTra.class);
            query.setParameter("loaiChiTiet", loaiChiTiet);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
