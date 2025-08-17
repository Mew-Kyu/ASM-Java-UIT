package dao.impl;

import dao.interfaces.IHoaDonDAO;
import jakarta.persistence.*;
import model.HoaDon;

import java.util.List;

public class HoaDonDAO implements IHoaDonDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(HoaDon hd) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(hd);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(HoaDon hd) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(hd);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maHD) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            HoaDon hd = em.find(HoaDon.class, maHD);
            if (hd != null) {
                em.remove(hd);
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
    public HoaDon findById(int maHD) {
        EntityManager em = emf.createEntityManager();
        return em.find(HoaDon.class, maHD);
    }

    @Override
    public List<HoaDon> findAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT hd FROM HoaDon hd", HoaDon.class).getResultList();
    }
}
