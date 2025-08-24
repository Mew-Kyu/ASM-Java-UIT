package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Entity class for Purchase Order (Đơn Đặt Hàng)
 */
@Entity
@Table(name = "DonDatHang")
public class DonDatHang {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDDH")
    private int maDDH;
    
    @Column(name = "MaNCC", nullable = false)
    private int maNCC;
    
    @Column(name = "MaNV", nullable = false)
    private int maNV; // Nhân viên tạo đơn
    
    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;
    
    @Column(name = "NgayDuKien")
    private LocalDate ngayDuKien;
    
    @Column(name = "NgayGiaoHang")
    private LocalDate ngayGiaoHang;
    
    @Column(name = "TrangThai", length = 20, nullable = false)
    private String trangThai; // "DRAFT", "SENT", "CONFIRMED", "DELIVERED", "CANCELLED"
    
    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;
    
    @Column(name = "ThueVAT", precision = 5, scale = 2)
    private BigDecimal thueVAT = BigDecimal.ZERO; // VAT percentage
    
    @Column(name = "PhiVanChuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;
    
    @Column(name = "TongThanhToan", precision = 18, scale = 2)
    private BigDecimal tongThanhToan = BigDecimal.ZERO;
    
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;
    
    @Column(name = "DieuKhoanThanhToan", length = 100)
    private String dieuKhoanThanhToan; // "COD", "BANK_TRANSFER", "30_DAYS", etc.
    
    @Column(name = "DiaChiGiaoHang", length = 300)
    private String diaChiGiaoHang;
    
    @Column(name = "NguoiNhan", length = 100)
    private String nguoiNhan;
    
    @Column(name = "SoDienThoaiNhan", length = 20)
    private String soDienThoaiNhan;
    
    // Transient fields for relationships
    @Transient
    private String tenNhaCungCap;
    
    @Transient
    private String tenNhanVien;
    
    @Transient
    private List<ChiTietDatHang> chiTietDatHangList = new ArrayList<>();
    
    // Constructors
    public DonDatHang() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = "DRAFT";
    }
    
    public DonDatHang(int maNCC, int maNV) {
        this();
        this.maNCC = maNCC;
        this.maNV = maNV;
    }
    
    // Getters and Setters
    public int getMaDDH() {
        return maDDH;
    }
    
    public void setMaDDH(int maDDH) {
        this.maDDH = maDDH;
    }
    
    public int getMaNCC() {
        return maNCC;
    }
    
    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }
    
    public int getMaNV() {
        return maNV;
    }
    
    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public LocalDate getNgayDuKien() {
        return ngayDuKien;
    }
    
    public void setNgayDuKien(LocalDate ngayDuKien) {
        this.ngayDuKien = ngayDuKien;
    }
    
    public LocalDate getNgayGiaoHang() {
        return ngayGiaoHang;
    }
    
    public void setNgayGiaoHang(LocalDate ngayGiaoHang) {
        this.ngayGiaoHang = ngayGiaoHang;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public BigDecimal getTongTien() {
        return tongTien;
    }
    
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien != null ? tongTien : BigDecimal.ZERO;
        calculateTongThanhToan();
    }
    
    public BigDecimal getThueVAT() {
        return thueVAT;
    }
    
    public void setThueVAT(BigDecimal thueVAT) {
        this.thueVAT = thueVAT != null ? thueVAT : BigDecimal.ZERO;
        calculateTongThanhToan();
    }
    
    public BigDecimal getPhiVanChuyen() {
        return phiVanChuyen;
    }
    
    public void setPhiVanChuyen(BigDecimal phiVanChuyen) {
        this.phiVanChuyen = phiVanChuyen != null ? phiVanChuyen : BigDecimal.ZERO;
        calculateTongThanhToan();
    }
    
    public BigDecimal getTongThanhToan() {
        return tongThanhToan;
    }
    
    public void setTongThanhToan(BigDecimal tongThanhToan) {
        this.tongThanhToan = tongThanhToan != null ? tongThanhToan : BigDecimal.ZERO;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public String getDieuKhoanThanhToan() {
        return dieuKhoanThanhToan;
    }
    
    public void setDieuKhoanThanhToan(String dieuKhoanThanhToan) {
        this.dieuKhoanThanhToan = dieuKhoanThanhToan;
    }
    
    public String getDiaChiGiaoHang() {
        return diaChiGiaoHang;
    }
    
    public void setDiaChiGiaoHang(String diaChiGiaoHang) {
        this.diaChiGiaoHang = diaChiGiaoHang;
    }
    
    public String getNguoiNhan() {
        return nguoiNhan;
    }
    
    public void setNguoiNhan(String nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }
    
    public String getSoDienThoaiNhan() {
        return soDienThoaiNhan;
    }
    
    public void setSoDienThoaiNhan(String soDienThoaiNhan) {
        this.soDienThoaiNhan = soDienThoaiNhan;
    }
    
    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }
    
    public void setTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
    }
    
    public String getTenNhanVien() {
        return tenNhanVien;
    }
    
    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }
    
    public List<ChiTietDatHang> getChiTietDatHangList() {
        return chiTietDatHangList;
    }
    
    public void setChiTietDatHangList(List<ChiTietDatHang> chiTietDatHangList) {
        this.chiTietDatHangList = chiTietDatHangList != null ? chiTietDatHangList : new ArrayList<>();
    }
    
    // Helper methods
    public String getTrangThaiText() {
        switch (trangThai) {
            case "DRAFT": return "Nháp";
            case "SENT": return "Đã gửi";
            case "CONFIRMED": return "Đã xác nhận";
            case "DELIVERED": return "Đã giao hàng";
            case "CANCELLED": return "Đã hủy";
            default: return trangThai;
        }
    }
    
    public boolean canEdit() {
        return "DRAFT".equals(trangThai) || "SENT".equals(trangThai);
    }
    
    public boolean canCancel() {
        return !"DELIVERED".equals(trangThai) && !"CANCELLED".equals(trangThai);
    }
    
    public boolean isCompleted() {
        return "DELIVERED".equals(trangThai);
    }
    
    private void calculateTongThanhToan() {
        if (tongTien != null) {
            BigDecimal thue = tongTien.multiply(thueVAT).divide(new BigDecimal("100"));
            tongThanhToan = tongTien.add(thue).add(phiVanChuyen != null ? phiVanChuyen : BigDecimal.ZERO);
        }
    }
    
    @Override
    public String toString() {
        return "DDH-" + String.format("%06d", maDDH) + " (" + getTrangThaiText() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DonDatHang that = (DonDatHang) obj;
        return maDDH == that.maDDH;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maDDH);
    }
}
