package dao.impl;

import dao.interfaces.IHoaDonDAO;
import jakarta.persistence.*;
import model.HoaDon;
import exception.DAOException;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class HoaDonDAO implements IHoaDonDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");
    private static final Logger logger = Logger.getLogger(HoaDonDAO.class.getName());

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
    public void delete(int maHD) throws DAOException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // First check if there are any PhieuDoiTra records referencing this HoaDon
            Long phieuDoiTraCount = em.createQuery(
                "SELECT COUNT(p) FROM PhieuDoiTra p WHERE p.maHD = :maHD", Long.class)
                .setParameter("maHD", maHD)
                .getSingleResult();

            if (phieuDoiTraCount > 0) {
                throw new DAOException("Không thể xóa hóa đơn này vì có " + phieuDoiTraCount +
                    " phiếu đổi trả liên quan. Vui lòng xóa các phiếu đổi trả trước.");
            }

            HoaDon hd = em.find(HoaDon.class, maHD);
            if (hd != null) {
                em.remove(hd);
            } else {
                throw new DAOException("Không tìm thấy hóa đơn với mã: " + maHD);
            }

            tx.commit();
            logger.info("Successfully deleted invoice with ID: " + maHD);

        } catch (DAOException e) {
            if (tx.isActive()) tx.rollback();
            throw e; // Re-throw DAOException as-is
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting invoice with ID: " + maHD, e);

            // Check if it's a foreign key constraint violation
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("REFERENCE constraint")) {
                if (errorMessage.contains("FK_PhieuDoiTra_HoaDon_MaHD")) {
                    throw new DAOException("Không thể xóa hóa đơn này vì có phiếu đổi trả liên quan. Vui lòng xóa các phiếu đổi trả trước.");
                } else {
                    throw new DAOException("Không thể xóa hóa đơn này vì có dữ liệu liên quan trong hệ thống.");
                }
            } else {
                throw new DAOException("Lỗi khi xóa hóa đơn: " + e.getMessage());
            }
        } finally {
            em.close();
        }
    }

    @Override
    public HoaDon findById(int maHD) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(HoaDon.class, maHD);
        } finally {
            em.close();
        }
    }

    @Override
    public HoaDon findByIdWithDetails(int maHD) {
        EntityManager em = emf.createEntityManager();
        try {
            logger.info("Searching for invoice with ID: " + maHD);

            // Single query with joins to initialize all required associations
            List<HoaDon> results = em.createQuery(
                    "SELECT hd FROM HoaDon hd " +
                    "LEFT JOIN FETCH hd.maKH " +
                    "LEFT JOIN FETCH hd.maNV " +
                    "LEFT JOIN FETCH hd.chiTietHoaDons ctd " +
                    "LEFT JOIN FETCH ctd.maBienThe mbt " +
                    "LEFT JOIN FETCH mbt.maSP " +
                    "LEFT JOIN FETCH mbt.maMau " +
                    "LEFT JOIN FETCH mbt.maSize " +
                    "WHERE hd.id = :maHD",
                    HoaDon.class)
                .setParameter("maHD", maHD)
                .getResultList();

            if (results.isEmpty()) {
                logger.warning("No invoice found with ID: " + maHD);
                return null;
            } else {
                HoaDon hoaDon = results.get(0);
                logger.info("Found invoice: " + hoaDon.getId() + " with " +
                           hoaDon.getChiTietHoaDons().size() + " items");
                return hoaDon;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding invoice with ID: " + maHD, e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<HoaDon> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT hd FROM HoaDon hd", HoaDon.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trả về danh sách hóa đơn đã fetch-join các thực thể liên quan
     * để tránh LazyInitializationException trong UI.
     */
    public List<HoaDon> findAllWithDetails() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT hd FROM HoaDon hd " +
                    "LEFT JOIN FETCH hd.maKH " +
                    "LEFT JOIN FETCH hd.maNV " +
                    "LEFT JOIN FETCH hd.chiTietHoaDons ctd " +
                    "LEFT JOIN FETCH ctd.maBienThe mbt " +
                    "LEFT JOIN FETCH mbt.maSP " +
                    "LEFT JOIN FETCH mbt.maMau " +
                    "LEFT JOIN FETCH mbt.maSize",
                    HoaDon.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
