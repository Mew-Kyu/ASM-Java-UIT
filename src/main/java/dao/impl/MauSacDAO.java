package dao.impl;

import dao.interfaces.IMauSacDAO;
import jakarta.persistence.*;
import model.MauSac;

import java.util.List;

public class MauSacDAO implements IMauSacDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(MauSac ms) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(ms);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(MauSac ms) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(ms);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maMau) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MauSac ms = em.find(MauSac.class, maMau);
            if (ms != null) {
                em.remove(ms);
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
    public MauSac findById(int maMau) {
        EntityManager em = emf.createEntityManager();
        return em.find(MauSac.class, maMau);
    }

    @Override
    public List<MauSac> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT ms FROM MauSac ms", MauSac.class).getResultList();
    }
}
