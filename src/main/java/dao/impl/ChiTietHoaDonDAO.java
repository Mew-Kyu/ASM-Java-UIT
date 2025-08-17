package dao.impl;

import dao.interfaces.IChiTietHoaDonDAO;
import jakarta.persistence.*;
import model.ChiTietHoaDon;
import model.ChiTietHoaDonId;

import java.util.List;

public class ChiTietHoaDonDAO implements IChiTietHoaDonDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(ChiTietHoaDon ct) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(ct);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(ChiTietHoaDon ct) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(ct);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(ChiTietHoaDonId id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ChiTietHoaDon ct = em.find(ChiTietHoaDon.class, id);
            if (ct != null) {
                em.remove(ct);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public ChiTietHoaDon findById(ChiTietHoaDonId id) {
        EntityManager em = emf.createEntityManager();
        return em.find(ChiTietHoaDon.class, id);
    }

    @Override
    public List<ChiTietHoaDon> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT ct FROM ChiTietHoaDon ct", ChiTietHoaDon.class).getResultList();
    }

    @Override
    public List<ChiTietHoaDon> findByHoaDonId(int maHD) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT ct FROM ChiTietHoaDon ct WHERE ct.id.maHD = :maHD", ChiTietHoaDon.class)
                .setParameter("maHD", maHD)
                .getResultList();
    }
}
