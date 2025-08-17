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
        try {
            return em.find(MauSac.class, maMau);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<MauSac> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<MauSac> query = em.createQuery("SELECT m FROM MauSac m", MauSac.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    @Override
    public List<MauSac> findByName(String tenMau) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<MauSac> query = em.createQuery(
                "SELECT m FROM MauSac m WHERE m.tenMau LIKE :tenMau", MauSac.class);
            query.setParameter("tenMau", "%" + tenMau + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }
}
