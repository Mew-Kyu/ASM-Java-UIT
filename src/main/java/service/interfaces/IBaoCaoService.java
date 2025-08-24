package service.interfaces;

import model.BaoCao;
import model.ThongKeDoanhThu;
import model.ThongKeSanPham;
import exception.BusinessException;
import exception.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

/**
 * Service interface for Report business logic
 */
public interface IBaoCaoService {
    
    // Report Management
    void taoBaoCao(BaoCao baoCao) throws ValidationException, BusinessException;
    void capNhatBaoCao(BaoCao baoCao) throws ValidationException, BusinessException;
    void xoaBaoCao(int maBaoCao) throws BusinessException;
    BaoCao layBaoCaoTheoId(int maBaoCao) throws BusinessException;
    List<BaoCao> layTatCaBaoCao();
    List<BaoCao> layBaoCaoTheoLoai(String loaiBaoCao);
    
    // Revenue Analytics
    List<ThongKeDoanhThu> thongKeDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    List<ThongKeDoanhThu> thongKeDoanhThuTheoTuan(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    List<ThongKeDoanhThu> thongKeDoanhThuTheoThang(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    List<ThongKeDoanhThu> thongKeDoanhThuTheoNam(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    
    // Product Analytics
    List<ThongKeSanPham> layTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int top) throws BusinessException;
    List<ThongKeSanPham> laySanPhamBanCham(LocalDate tuNgay, LocalDate denNgay, int top) throws BusinessException;
    List<ThongKeSanPham> laySanPhamTonKho() throws BusinessException;
    List<ThongKeSanPham> laySanPhamSapHet(int soLuongToiThieu) throws BusinessException;
    List<ThongKeSanPham> thongKeTheoLoaiSanPham(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    
    // Employee Performance
    List<Object[]> thongKeHieuSuatNhanVien(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    
    // Summary Statistics
    BigDecimal tinhTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    int demSoDonHang(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    int demSoSanPhamBan(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    BigDecimal tinhDoanhThuTrungBinhTheoNgay(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    
    // Inventory Analytics
    BigDecimal tinhTongGiaTriTonKho() throws BusinessException;
    int demTongSoLuongTonKho() throws BusinessException;
    
    // Customer Analytics
    int demSoKhachHangMoi(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    List<Object[]> layTopKhachHang(LocalDate tuNgay, LocalDate denNgay, int top) throws BusinessException;
    
    // Report Generation
    String taoBaoCaoDoanhThu(LocalDate tuNgay, LocalDate denNgay, String loaiThoiGian) throws BusinessException;
    String taoBaoCaoSanPham(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    String taoBaoCaoTonKho() throws BusinessException;
    String taoBaoCaoNhanVien(LocalDate tuNgay, LocalDate denNgay) throws BusinessException;
    
    // Export functionality
    void xuatBaoCaoExcel(BaoCao baoCao, String filePath) throws BusinessException;
    void xuatBaoCaoPDF(BaoCao baoCao, String filePath) throws BusinessException;
}
