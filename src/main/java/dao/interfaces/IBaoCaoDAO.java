package dao.interfaces;

import model.BaoCao;
import model.ThongKeDoanhThu;
import model.ThongKeSanPham;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * Data Access Object interface for Report operations
 */
public interface IBaoCaoDAO {
    
    // Basic CRUD operations for reports
    void insert(BaoCao baoCao);
    void update(BaoCao baoCao);
    void delete(int maBaoCao);
    Optional<BaoCao> findById(int maBaoCao);
    List<BaoCao> findAll();
    List<BaoCao> findByLoaiBaoCao(String loaiBaoCao);
    List<BaoCao> findByNguoiTao(int maNV);
    
    // Revenue Analytics Methods
    List<ThongKeDoanhThu> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay);
    List<ThongKeDoanhThu> getDoanhThuTheoTuan(LocalDate tuNgay, LocalDate denNgay);
    List<ThongKeDoanhThu> getDoanhThuTheoThang(LocalDate tuNgay, LocalDate denNgay);
    List<ThongKeDoanhThu> getDoanhThuTheoNam(LocalDate tuNgay, LocalDate denNgay);
    
    // Product Analytics Methods
    List<ThongKeSanPham> getTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int top);
    List<ThongKeSanPham> getSanPhamBanCham(LocalDate tuNgay, LocalDate denNgay, int top);
    List<ThongKeSanPham> getSanPhamTonKho();
    List<ThongKeSanPham> getSanPhamSapHet(int soLuongToiThieu);
    List<ThongKeSanPham> getThongKeTheoDanhMuc(LocalDate tuNgay, LocalDate denNgay);
    
    // Employee Performance Analytics
    List<Object[]> getHieuSuatNhanVien(LocalDate tuNgay, LocalDate denNgay);
    
    // Summary Analytics
    BigDecimal getTongDoanhThu(LocalDate tuNgay, LocalDate denNgay);
    int getTongSoDonHang(LocalDate tuNgay, LocalDate denNgay);
    int getTongSoSanPhamBan(LocalDate tuNgay, LocalDate denNgay);
    BigDecimal getDoanhThuTrungBinhTheoNgay(LocalDate tuNgay, LocalDate denNgay);
    
    // Inventory Analytics
    BigDecimal getTongGiaTriTonKho();
    int getTongSoLuongTonKho();
    
    // Customer Analytics
    int getSoKhachHangMoi(LocalDate tuNgay, LocalDate denNgay);
    List<Object[]> getTopKhachHang(LocalDate tuNgay, LocalDate denNgay, int top);
}
