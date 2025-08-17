package dao.impl;

import dao.interfaces.IKhachHangDAO;
import jakarta.persistence.*;
import model.KhachHang;

import java.util.List;

public class KhachHangDAO implements IKhachHangDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(KhachHang kh) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(kh);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(KhachHang kh) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(kh);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maKH) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            KhachHang kh = em.find(KhachHang.class, maKH);
            if (kh != null) {
                em.remove(kh);
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
    public KhachHang findById(int maKH) {
        EntityManager em = emf.createEntityManager();
        return em.find(KhachHang.class, maKH);
    }

    @Override
    public List<KhachHang> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT k FROM KhachHang k", KhachHang.class).getResultList();
    }
}
