package dao.impl;

import dao.interfaces.ISanPhamDAO;
import jakarta.persistence.*;
import model.SanPham;

import java.util.List;

public class SanPhamDAO implements ISanPhamDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(SanPham sp) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(sp);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(SanPham sp) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(sp);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maSP) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            SanPham sp = em.find(SanPham.class, maSP);
            if (sp != null) {
                em.remove(sp);
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
    public SanPham findById(int maSP) {
        EntityManager em = emf.createEntityManager();
        return em.find(SanPham.class, maSP);
    }

    @Override
    public List<SanPham> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT s FROM SanPham s", SanPham.class).getResultList();
    }
}
