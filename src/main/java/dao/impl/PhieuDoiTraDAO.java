package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.IPhieuDoiTraDAO;
import model.PhieuDoiTra;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object implementation for Returns & Exchanges operations
 */
public class PhieuDoiTraDAO extends BaseDAO<PhieuDoiTra, Integer> implements IPhieuDoiTraDAO {
    
    public PhieuDoiTraDAO() {
        super(PhieuDoiTra.class);
    }
    
    @Override
    public void insert(PhieuDoiTra phieuDoiTra) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(phieuDoiTra);
        });
    }
    
    @Override
    public void update(PhieuDoiTra phieuDoiTra) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(phieuDoiTra);
        });
    }
    
    @Override
    public void delete(int maPhieuDT) {
        EntityManagerUtil.executeInTransaction(em -> {
            PhieuDoiTra phieu = em.find(PhieuDoiTra.class, maPhieuDT);
            if (phieu != null) {
                em.remove(phieu);
            }
        });
    }
    
    @Override
    public Optional<PhieuDoiTra> findById(int maPhieuDT) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            PhieuDoiTra phieu = em.find(PhieuDoiTra.class, maPhieuDT);
            return Optional.ofNullable(phieu);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<PhieuDoiTra> findAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<PhieuDoiTra> query = em.createQuery(
                "SELECT p FROM PhieuDoiTra p ORDER BY p.ngayTao DESC", 
                PhieuDoiTra.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<PhieuDoiTra> findByHoaDon(int maHD) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<PhieuDoiTra> query = em.createQuery(
                "SELECT p FROM PhieuDoiTra p WHERE p.maHD = :maHD ORDER BY p.ngayTao DESC", 
                PhieuDoiTra.class);
            query.setParameter("maHD", maHD);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<PhieuDoiTra> findByTrangThai(String trangThai) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<PhieuDoiTra> query = em.createQuery(
                "SELECT p FROM PhieuDoiTra p WHERE p.trangThai = :trangThai ORDER BY p.ngayTao DESC", 
                PhieuDoiTra.class);
            query.setParameter("trangThai", trangThai);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<PhieuDoiTra> findByLoaiPhieu(String loaiPhieu) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<PhieuDoiTra> query = em.createQuery(
                "SELECT p FROM PhieuDoiTra p WHERE p.loaiPhieu = :loaiPhieu ORDER BY p.ngayTao DESC", 
                PhieuDoiTra.class);
            query.setParameter("loaiPhieu", loaiPhieu);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
