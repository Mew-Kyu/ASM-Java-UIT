package dao.impl;

import jakarta.persistence.*;
import model.DanhMuc;
import java.util.List;

public class DanhMucDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    public List<DanhMuc> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT d FROM DanhMuc d", DanhMuc.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void insert(DanhMuc dm) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(dm);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(DanhMuc dm) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(dm);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            DanhMuc dm = em.find(DanhMuc.class, id);
            if (dm != null) {
                em.remove(dm);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public DanhMuc findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(DanhMuc.class, id);
        } finally {
            em.close();
        }
    }
}
