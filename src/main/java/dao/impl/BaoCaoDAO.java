package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.IBaoCaoDAO;
import model.BaoCao;
import model.ThongKeDoanhThu;
import model.ThongKeSanPham;
import util.EntityManagerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Access Object implementation for Report operations
 */
public class BaoCaoDAO extends BaseDAO<BaoCao, Integer> implements IBaoCaoDAO {
    
    private static final Logger logger = Logger.getLogger(BaoCaoDAO.class.getName());
    
    public BaoCaoDAO() {
        super(BaoCao.class);
    }
    
    /**
     * Helper method to safely convert Object to BigDecimal
     */
    private BigDecimal safeToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(((Number) value).toString());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Helper method to safely convert Object to int
     */
    private int safeToInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
    
    @Override
    public void insert(BaoCao baoCao) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(baoCao);
        });
    }
    
    @Override
    public void update(BaoCao baoCao) {
        EntityManagerUtil.executeInTransaction(em -> {
            em.merge(baoCao);
        });
    }
    
    @Override
    public void delete(int maBaoCao) {
        EntityManagerUtil.executeInTransaction(em -> {
            BaoCao baoCao = em.find(BaoCao.class, maBaoCao);
            if (baoCao != null) {
                em.remove(baoCao);
            }
        });
    }
    
    @Override
    public Optional<BaoCao> findById(int maBaoCao) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            BaoCao baoCao = em.find(BaoCao.class, maBaoCao);
            return Optional.ofNullable(baoCao);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BaoCao> findAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<BaoCao> query = em.createQuery("SELECT b FROM BaoCao b ORDER BY b.ngayTao DESC", BaoCao.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BaoCao> findByLoaiBaoCao(String loaiBaoCao) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<BaoCao> query = em.createQuery(
                "SELECT b FROM BaoCao b WHERE b.loaiBaoCao = :loaiBaoCao ORDER BY b.ngayTao DESC", 
                BaoCao.class);
            query.setParameter("loaiBaoCao", loaiBaoCao);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BaoCao> findByNguoiTao(int maNV) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<BaoCao> query = em.createQuery(
                "SELECT b FROM BaoCao b WHERE b.nguoiTao = :maNV ORDER BY b.ngayTao DESC", 
                BaoCao.class);
            query.setParameter("maNV", maNV);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT h.NgayLap, 
                       COALESCE(SUM(h.TongTien), 0) as DoanhThu,
                       COUNT(h.MaHD) as SoDonHang,
                       COALESCE(SUM(ct.SoLuong), 0) as SoSanPham
                FROM HoaDon h
                LEFT JOIN ChiTietHoaDon ct ON h.MaHD = ct.MaHD
                WHERE h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY h.NgayLap
                ORDER BY h.NgayLap
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeDoanhThu> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                LocalDate ngay = ((java.sql.Date) row[0]).toLocalDate();
                BigDecimal doanhThu = safeToBigDecimal(row[1]);
                int soDonHang = ((Number) row[2]).intValue();
                int soSanPham = ((Number) row[3]).intValue();
                
                thongKe.add(new ThongKeDoanhThu(ngay, doanhThu, soDonHang, soSanPham, "DAY"));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting daily revenue statistics", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> getDoanhThuTheoTuan(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT DATEPART(YEAR, h.NgayLap) as Nam,
                       DATEPART(WEEK, h.NgayLap) as Tuan,
                       MIN(h.NgayLap) as NgayDauTuan,
                       COALESCE(SUM(h.TongTien), 0) as DoanhThu,
                       COUNT(h.MaHD) as SoDonHang,
                       COALESCE(SUM(ct.SoLuong), 0) as SoSanPham
                FROM HoaDon h
                LEFT JOIN ChiTietHoaDon ct ON h.MaHD = ct.MaHD
                WHERE h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY DATEPART(YEAR, h.NgayLap), DATEPART(WEEK, h.NgayLap)
                ORDER BY Nam, Tuan
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeDoanhThu> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                LocalDate ngayDauTuan = ((java.sql.Date) row[2]).toLocalDate();
                BigDecimal doanhThu = safeToBigDecimal(row[3]);
                int soDonHang = ((Number) row[4]).intValue();
                int soSanPham = ((Number) row[5]).intValue();
                
                thongKe.add(new ThongKeDoanhThu(ngayDauTuan, doanhThu, soDonHang, soSanPham, "WEEK"));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting weekly revenue statistics", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> getDoanhThuTheoThang(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT DATEPART(YEAR, h.NgayLap) as Nam,
                       DATEPART(MONTH, h.NgayLap) as Thang,
                       DATEFROMPARTS(DATEPART(YEAR, h.NgayLap), DATEPART(MONTH, h.NgayLap), 1) as NgayDauThang,
                       COALESCE(SUM(h.TongTien), 0) as DoanhThu,
                       COUNT(h.MaHD) as SoDonHang,
                       COALESCE(SUM(ct.SoLuong), 0) as SoSanPham
                FROM HoaDon h
                LEFT JOIN ChiTietHoaDon ct ON h.MaHD = ct.MaHD
                WHERE h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY DATEPART(YEAR, h.NgayLap), DATEPART(MONTH, h.NgayLap)
                ORDER BY Nam, Thang
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeDoanhThu> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                LocalDate ngayDauThang = ((java.sql.Date) row[2]).toLocalDate();
                BigDecimal doanhThu = safeToBigDecimal(row[3]);
                int soDonHang = ((Number) row[4]).intValue();
                int soSanPham = ((Number) row[5]).intValue();
                
                thongKe.add(new ThongKeDoanhThu(ngayDauThang, doanhThu, soDonHang, soSanPham, "MONTH"));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting monthly revenue statistics", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> getDoanhThuTheoNam(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT DATEPART(YEAR, h.NgayLap) as Nam,
                       DATEFROMPARTS(DATEPART(YEAR, h.NgayLap), 1, 1) as NgayDauNam,
                       COALESCE(SUM(h.TongTien), 0) as DoanhThu,
                       COUNT(h.MaHD) as SoDonHang,
                       COALESCE(SUM(ct.SoLuong), 0) as SoSanPham
                FROM HoaDon h
                LEFT JOIN ChiTietHoaDon ct ON h.MaHD = ct.MaHD
                WHERE h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY DATEPART(YEAR, h.NgayLap)
                ORDER BY Nam
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeDoanhThu> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                LocalDate ngayDauNam = ((java.sql.Date) row[1]).toLocalDate();
                BigDecimal doanhThu = safeToBigDecimal(row[2]);
                int soDonHang = ((Number) row[3]).intValue();
                int soSanPham = ((Number) row[4]).intValue();
                
                thongKe.add(new ThongKeDoanhThu(ngayDauNam, doanhThu, soDonHang, soSanPham, "YEAR"));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting yearly revenue statistics", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeSanPham> getTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int top) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT TOP (:top) sp.MaSP, sp.TenSP, dm.TenDM,
                       COALESCE(SUM(ct.SoLuong), 0) as SoLuongBan,
                       COALESCE(SUM(ct.SoLuong * ct.DonGia), 0) as DoanhThu,
                       COALESCE(SUM(bt.SoLuong), 0) as SoLuongTon,
                       COALESCE(SUM(bt.SoLuong * bt.GiaBan), 0) as GiaTriTon,
                       COUNT(DISTINCT h.MaHD) as SoDonHang
                FROM SanPham sp
                LEFT JOIN DanhMuc dm ON sp.MaDM = dm.MaDM
                LEFT JOIN BienTheSanPham bt ON sp.MaSP = bt.MaSP
                LEFT JOIN ChiTietHoaDon ct ON bt.MaBienThe = ct.MaBienThe
                LEFT JOIN HoaDon h ON ct.MaHD = h.MaHD AND h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY sp.MaSP, sp.TenSP, dm.TenDM
                ORDER BY SoLuongBan DESC
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("top", top);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeSanPham> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                int maSP = ((Number) row[0]).intValue();
                String tenSP = (String) row[1];
                String tenDanhMuc = (String) row[2];
                int soLuongBan = ((Number) row[3]).intValue();
                BigDecimal doanhThu = safeToBigDecimal(row[4]);
                int soLuongTon = ((Number) row[5]).intValue();
                BigDecimal giaTriTon = safeToBigDecimal(row[6]);
                int soDonHang = ((Number) row[7]).intValue();
                
                thongKe.add(new ThongKeSanPham(maSP, tenSP, tenDanhMuc, soLuongBan, 
                                             doanhThu, soLuongTon, giaTriTon, soDonHang));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting top selling products", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeSanPham> getSanPhamBanCham(LocalDate tuNgay, LocalDate denNgay, int top) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT TOP (:top) sp.MaSP, sp.TenSP, dm.TenDM,
                       COALESCE(SUM(ct.SoLuong), 0) as SoLuongBan,
                       COALESCE(SUM(ct.SoLuong * ct.DonGia), 0) as DoanhThu,
                       COALESCE(SUM(bt.SoLuong), 0) as SoLuongTon,
                       COALESCE(SUM(bt.SoLuong * bt.GiaBan), 0) as GiaTriTon,
                       COUNT(DISTINCT h.MaHD) as SoDonHang
                FROM SanPham sp
                LEFT JOIN DanhMuc dm ON sp.MaDM = dm.MaDM
                LEFT JOIN BienTheSanPham bt ON sp.MaSP = bt.MaSP
                LEFT JOIN ChiTietHoaDon ct ON bt.MaBienThe = ct.MaBienThe
                LEFT JOIN HoaDon h ON ct.MaHD = h.MaHD AND h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY sp.MaSP, sp.TenSP, dm.TenDM
                ORDER BY SoLuongBan ASC
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("top", top);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeSanPham> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                int maSP = ((Number) row[0]).intValue();
                String tenSP = (String) row[1];
                String tenDanhMuc = (String) row[2];
                int soLuongBan = ((Number) row[3]).intValue();
                BigDecimal doanhThu = safeToBigDecimal(row[4]);
                int soLuongTon = ((Number) row[5]).intValue();
                BigDecimal giaTriTon = safeToBigDecimal(row[6]);
                int soDonHang = ((Number) row[7]).intValue();
                
                thongKe.add(new ThongKeSanPham(maSP, tenSP, tenDanhMuc, soLuongBan, 
                                             doanhThu, soLuongTon, giaTriTon, soDonHang));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting slow selling products", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeSanPham> getSanPhamTonKho() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT sp.MaSP, sp.TenSP, dm.TenDM,
                       0 as SoLuongBan,
                       0 as DoanhThu,
                       COALESCE(SUM(bt.SoLuong), 0) as SoLuongTon,
                       COALESCE(SUM(bt.SoLuong * bt.GiaBan), 0) as GiaTriTon,
                       0 as SoDonHang
                FROM SanPham sp
                LEFT JOIN DanhMuc dm ON sp.MaDM = dm.MaDM
                LEFT JOIN BienTheSanPham bt ON sp.MaSP = bt.MaSP
                GROUP BY sp.MaSP, sp.TenSP, dm.TenDM
                HAVING SUM(bt.SoLuong) > 0
                ORDER BY SoLuongTon DESC
                """;
            
            Query query = em.createNativeQuery(sql);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeSanPham> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                int maSP = ((Number) row[0]).intValue();
                String tenSP = (String) row[1];
                String tenDanhMuc = (String) row[2];
                int soLuongBan = ((Number) row[3]).intValue();
                BigDecimal doanhThu = safeToBigDecimal(row[4]);
                int soLuongTon = ((Number) row[5]).intValue();
                BigDecimal giaTriTon = safeToBigDecimal(row[6]);
                int soDonHang = ((Number) row[7]).intValue();
                
                thongKe.add(new ThongKeSanPham(maSP, tenSP, tenDanhMuc, soLuongBan, 
                                             doanhThu, soLuongTon, giaTriTon, soDonHang));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting inventory products", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeSanPham> getSanPhamSapHet(int soLuongToiThieu) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT sp.MaSP, sp.TenSP, dm.TenDM,
                       0 as SoLuongBan,
                       0 as DoanhThu,
                       COALESCE(SUM(bt.SoLuong), 0) as SoLuongTon,
                       COALESCE(SUM(bt.SoLuong * bt.GiaBan), 0) as GiaTriTon,
                       0 as SoDonHang
                FROM SanPham sp
                LEFT JOIN DanhMuc dm ON sp.MaDM = dm.MaDM
                LEFT JOIN BienTheSanPham bt ON sp.MaSP = bt.MaSP
                GROUP BY sp.MaSP, sp.TenSP, dm.TenDM
                HAVING SUM(bt.SoLuong) <= :soLuongToiThieu AND SUM(bt.SoLuong) > 0
                ORDER BY SoLuongTon ASC
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("soLuongToiThieu", soLuongToiThieu);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeSanPham> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                int maSP = ((Number) row[0]).intValue();
                String tenSP = (String) row[1];
                String tenDanhMuc = (String) row[2];
                int soLuongBan = ((Number) row[3]).intValue();
                BigDecimal doanhThu = safeToBigDecimal(row[4]);
                int soLuongTon = ((Number) row[5]).intValue();
                BigDecimal giaTriTon = safeToBigDecimal(row[6]);
                int soDonHang = ((Number) row[7]).intValue();
                
                thongKe.add(new ThongKeSanPham(maSP, tenSP, tenDanhMuc, soLuongBan, 
                                             doanhThu, soLuongTon, giaTriTon, soDonHang));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting low stock products", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ThongKeSanPham> getThongKeTheoDanhMuc(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT dm.MaDM, dm.TenDM, dm.TenDM,
                       COALESCE(SUM(ct.SoLuong), 0) as SoLuongBan,
                       COALESCE(SUM(ct.SoLuong * ct.DonGia), 0) as DoanhThu,
                       COALESCE(SUM(bt.SoLuong), 0) as SoLuongTon,
                       COALESCE(SUM(bt.SoLuong * bt.GiaBan), 0) as GiaTriTon,
                       COUNT(DISTINCT h.MaHD) as SoDonHang
                FROM DanhMuc dm
                LEFT JOIN SanPham sp ON dm.MaDM = sp.MaDM
                LEFT JOIN BienTheSanPham bt ON sp.MaSP = bt.MaSP
                LEFT JOIN ChiTietHoaDon ct ON bt.MaBienThe = ct.MaBienThe
                LEFT JOIN HoaDon h ON ct.MaHD = h.MaHD AND h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY dm.MaDM, dm.TenDM
                ORDER BY DoanhThu DESC
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            List<ThongKeSanPham> thongKe = new ArrayList<>();
            
            for (Object[] row : results) {
                int maSP = ((Number) row[0]).intValue(); // Using category ID as product ID for category statistics
                String tenSP = (String) row[1]; // Category name as product name
                String tenDanhMuc = (String) row[2];
                int soLuongBan = ((Number) row[3]).intValue();
                BigDecimal doanhThu = safeToBigDecimal(row[4]);
                int soLuongTon = ((Number) row[5]).intValue();
                BigDecimal giaTriTon = safeToBigDecimal(row[6]);
                int soDonHang = ((Number) row[7]).intValue();
                
                thongKe.add(new ThongKeSanPham(maSP, tenSP, tenDanhMuc, soLuongBan, 
                                             doanhThu, soLuongTon, giaTriTon, soDonHang));
            }
            
            return thongKe;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting category statistics", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getHieuSuatNhanVien(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT nv.MaNV, nv.HoTen,
                       COUNT(h.MaHD) as SoDonHang,
                       COALESCE(SUM(h.TongTien), 0) as TongDoanhThu,
                       COALESCE(SUM(ct.SoLuong), 0) as TongSoLuongBan,
                       CASE WHEN COUNT(h.MaHD) > 0 
                            THEN COALESCE(SUM(h.TongTien), 0) / COUNT(h.MaHD)
                            ELSE 0 END as DoanhThuTrungBinh
                FROM NhanVien nv
                LEFT JOIN HoaDon h ON nv.MaNV = h.MaNV AND h.NgayLap BETWEEN :tuNgay AND :denNgay
                LEFT JOIN ChiTietHoaDon ct ON h.MaHD = ct.MaHD
                WHERE nv.TrangThai = 1
                GROUP BY nv.MaNV, nv.HoTen
                ORDER BY TongDoanhThu DESC
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            return results;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting employee performance", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    @Override
    public BigDecimal getTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = "SELECT COALESCE(SUM(TongTien), 0) FROM HoaDon WHERE NgayLap BETWEEN :tuNgay AND :denNgay";
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            Object result = query.getSingleResult();
            return result != null ? (BigDecimal) result : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total revenue", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getTongSoDonHang(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = "SELECT COUNT(*) FROM HoaDon WHERE NgayLap BETWEEN :tuNgay AND :denNgay";
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total orders", e);
            return 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getTongSoSanPhamBan(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT COALESCE(SUM(ct.SoLuong), 0) 
                FROM ChiTietHoaDon ct
                JOIN HoaDon h ON ct.MaHD = h.MaHD
                WHERE h.NgayLap BETWEEN :tuNgay AND :denNgay
                """;
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total products sold", e);
            return 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public BigDecimal getDoanhThuTrungBinhTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            BigDecimal tongDoanhThu = getTongDoanhThu(tuNgay, denNgay);
            long soNgay = java.time.temporal.ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
            
            if (soNgay > 0) {
                return tongDoanhThu.divide(new BigDecimal(soNgay), 2, java.math.RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting average daily revenue", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    @Override
    public BigDecimal getTongGiaTriTonKho() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = "SELECT COALESCE(SUM(SoLuong * GiaBan), 0) FROM BienTheSanPham WHERE SoLuong > 0";
            Query query = em.createNativeQuery(sql);
            
            Object result = query.getSingleResult();
            return result != null ? (BigDecimal) result : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total inventory value", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getTongSoLuongTonKho() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = "SELECT COALESCE(SUM(SoLuong), 0) FROM BienTheSanPham WHERE SoLuong > 0";
            Query query = em.createNativeQuery(sql);
            
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total inventory quantity", e);
            return 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getSoKhachHangMoi(LocalDate tuNgay, LocalDate denNgay) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT COUNT(DISTINCT h.MaKH) 
                FROM HoaDon h
                WHERE h.MaKH IS NOT NULL 
                AND h.NgayLap BETWEEN :tuNgay AND :denNgay
                AND NOT EXISTS (
                    SELECT 1 FROM HoaDon h2 
                    WHERE h2.MaKH = h.MaKH 
                    AND h2.NgayLap < :tuNgay
                )
                """;
            Query query = em.createNativeQuery(sql);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting new customers count", e);
            return 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopKhachHang(LocalDate tuNgay, LocalDate denNgay, int top) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            String sql = """
                SELECT TOP (:top) kh.MaKH, kh.HoTen, kh.DienThoai,
                       COUNT(h.MaHD) as SoDonHang,
                       COALESCE(SUM(h.TongTien), 0) as TongChiTieu,
                       COALESCE(SUM(ct.SoLuong), 0) as TongSoLuongMua
                FROM KhachHang kh
                JOIN HoaDon h ON kh.MaKH = h.MaKH
                LEFT JOIN ChiTietHoaDon ct ON h.MaHD = ct.MaHD
                WHERE h.NgayLap BETWEEN :tuNgay AND :denNgay
                GROUP BY kh.MaKH, kh.HoTen, kh.DienThoai
                ORDER BY TongChiTieu DESC
                """;
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("top", top);
            query.setParameter("tuNgay", tuNgay);
            query.setParameter("denNgay", denNgay);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            return results;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting top customers", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}
