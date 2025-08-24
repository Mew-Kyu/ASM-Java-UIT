package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.ITheThanThietDAO;
import model.TheThanThiet;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object implementation for Loyalty Card operations
 */
public class TheThanThietDAO extends BaseDAO<TheThanThiet, Integer> implements ITheThanThietDAO {
    
    public TheThanThietDAO() {
        super(TheThanThiet.class);
    }
    
    @Override
    public void insert(TheThanThiet theThanThiet) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(theThanThiet);
        });
    }
    
    @Override
    public void update(TheThanThiet theThanThiet) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(theThanThiet);
        });
    }
    
    @Override
    public void delete(int maThe) {
        EntityManagerUtil.executeInTransaction(em -> {
            TheThanThiet the = em.find(TheThanThiet.class, maThe);
            if (the != null) {
                em.remove(the);
            }
        });
    }
    
    @Override
    public Optional<TheThanThiet> findById(int maThe) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TheThanThiet the = em.find(TheThanThiet.class, maThe);
            return Optional.ofNullable(the);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<TheThanThiet> findAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<TheThanThiet> query = em.createQuery(
                "SELECT t FROM TheThanThiet t ORDER BY t.ngayTao DESC", 
                TheThanThiet.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<TheThanThiet> findByKhachHang(int maKH) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<TheThanThiet> query = em.createQuery(
                "SELECT t FROM TheThanThiet t WHERE t.maKH = :maKH ORDER BY t.ngayTao DESC", 
                TheThanThiet.class);
            query.setParameter("maKH", maKH);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<TheThanThiet> findByTrangThai(String trangThai) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<TheThanThiet> query = em.createQuery(
                "SELECT t FROM TheThanThiet t WHERE t.trangThai = :trangThai ORDER BY t.ngayTao DESC", 
                TheThanThiet.class);
            query.setParameter("trangThai", trangThai);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<TheThanThiet> findBySoThe(String soThe) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<TheThanThiet> query = em.createQuery(
                "SELECT t FROM TheThanThiet t WHERE t.soThe = :soThe", 
                TheThanThiet.class);
            query.setParameter("soThe", soThe);
            List<TheThanThiet> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }
}
