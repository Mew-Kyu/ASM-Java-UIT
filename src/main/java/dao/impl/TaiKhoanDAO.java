package dao.impl;

import dao.interfaces.ITaiKhoanDAO;
import jakarta.persistence.*;
import model.TaiKhoan;

import java.util.List;

public class TaiKhoanDAO implements ITaiKhoanDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(TaiKhoan tk) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(tk);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(TaiKhoan tk) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(tk);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String tenDangNhap) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TaiKhoan tk = em.find(TaiKhoan.class, tenDangNhap);
            if (tk != null) {
                em.remove(tk);
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
    public TaiKhoan findById(String tenDangNhap) {
        EntityManager em = emf.createEntityManager();
        return em.find(TaiKhoan.class, tenDangNhap);
    }

    @Override
    public List<TaiKhoan> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT tk FROM TaiKhoan tk", TaiKhoan.class).getResultList();
    }
}
