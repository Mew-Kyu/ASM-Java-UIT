package dao.impl;

import dao.interfaces.IBienTheSanPhamDAO;
import jakarta.persistence.*;
import model.BienTheSanPham;

import java.util.List;

public class BienTheSanPhamDAO implements IBienTheSanPhamDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(BienTheSanPham bts) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(bts);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(BienTheSanPham bts) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(bts);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maBienThe) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BienTheSanPham bts = em.find(BienTheSanPham.class, maBienThe);
            if (bts != null) {
                em.remove(bts);
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
    public BienTheSanPham findById(int maBienThe) {
        EntityManager em = emf.createEntityManager();
        return em.find(BienTheSanPham.class, maBienThe);
    }

    @Override
    public List<BienTheSanPham> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT b FROM BienTheSanPham b", BienTheSanPham.class).getResultList();
    }

    @Override
    public List<BienTheSanPham> findBySanPhamId(int maSP) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT b FROM BienTheSanPham b WHERE b.maSP = :maSP", BienTheSanPham.class)
                .setParameter("maSP", maSP)
                .getResultList();
    }
}
