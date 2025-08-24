package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.ILichSuDiemDAO;
import model.LichSuDiem;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object implementation for Points History operations
 */
public class LichSuDiemDAO extends BaseDAO<LichSuDiem, Integer> implements ILichSuDiemDAO {
    
    public LichSuDiemDAO() {
        super(LichSuDiem.class);
    }
    
    @Override
    public void insert(LichSuDiem lichSuDiem) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(lichSuDiem);
        });
    }
    
    @Override
    public void update(LichSuDiem lichSuDiem) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(lichSuDiem);
        });
    }
    
    @Override
    public void delete(int maLS) {
        EntityManagerUtil.executeInTransaction(em -> {
            LichSuDiem lichSu = em.find(LichSuDiem.class, maLS);
            if (lichSu != null) {
                em.remove(lichSu);
            }
        });
    }
    
    @Override
    public Optional<LichSuDiem> findById(int maLS) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            LichSuDiem lichSu = em.find(LichSuDiem.class, maLS);
            return Optional.ofNullable(lichSu);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<LichSuDiem> findAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<LichSuDiem> query = em.createQuery(
                "SELECT l FROM LichSuDiem l ORDER BY l.ngayGiaoDich DESC", 
                LichSuDiem.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<LichSuDiem> findByMaThe(int maThe) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<LichSuDiem> query = em.createQuery(
                "SELECT l FROM LichSuDiem l WHERE l.maThe = :maThe ORDER BY l.ngayGiaoDich DESC", 
                LichSuDiem.class);
            query.setParameter("maThe", maThe);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<LichSuDiem> findByLoaiGiaoDich(String loaiGiaoDich) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<LichSuDiem> query = em.createQuery(
                "SELECT l FROM LichSuDiem l WHERE l.loaiGiaoDich = :loaiGiaoDich ORDER BY l.ngayGiaoDich DESC", 
                LichSuDiem.class);
            query.setParameter("loaiGiaoDich", loaiGiaoDich);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
