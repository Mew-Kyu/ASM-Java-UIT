package dao.impl;

import dao.interfaces.INhanVienDAO;
import jakarta.persistence.*;
import model.NhanVien;

import java.util.List;

public class NhanVienDAO implements INhanVienDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    @Override
    public void insert(NhanVien nv) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(nv);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(NhanVien nv) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(nv);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int maNV) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            NhanVien nv = em.find(NhanVien.class, maNV);
            if (nv != null) {
                em.remove(nv);
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
    public NhanVien findById(int maNV) {
        EntityManager em = emf.createEntityManager();
        return em.find(NhanVien.class, maNV);
    }

    @Override
    public List<NhanVien> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT nv FROM NhanVien nv", NhanVien.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<NhanVien> searchByKeyword(String keyword) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT nv FROM NhanVien nv WHERE " +
                         "LOWER(nv.hoTen) LIKE LOWER(:keyword) OR " +
                         "nv.dienThoai LIKE :keyword OR " +
                         "LOWER(nv.email) LIKE LOWER(:keyword) OR " +
                         "LOWER(nv.chucVu) LIKE LOWER(:keyword)";
            return em.createQuery(jpql, NhanVien.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
