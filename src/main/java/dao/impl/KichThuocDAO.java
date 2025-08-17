package dao.impl;

import dao.interfaces.IKichThuocDAO;
import jakarta.persistence.*;
import model.KichThuoc;

import java.util.List;

public class KichThuocDAO implements IKichThuocDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(KichThuoc kt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(kt);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(KichThuoc kt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(kt);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maSize) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            KichThuoc kt = em.find(KichThuoc.class, maSize);
            if (kt != null) {
                em.remove(kt);
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
    public KichThuoc findById(int maSize) {
        EntityManager em = emf.createEntityManager();
        return em.find(KichThuoc.class, maSize);
    }

    @Override
    public List<KichThuoc> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT kt FROM KichThuoc kt", KichThuoc.class).getResultList();
    }
}
