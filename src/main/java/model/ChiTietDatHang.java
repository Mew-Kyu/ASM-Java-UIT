package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity class for Purchase Order Detail (Chi Tiết Đặt Hàng)
 */
@Entity
@Table(name = "ChiTietDatHang")
@IdClass(ChiTietDatHangId.class)
public class ChiTietDatHang {
    
    @Id
    @Column(name = "MaDDH")
    private int maDDH;
    
    @Id
    @Column(name = "MaSP")
    private int maSP;
    
    @Column(name = "SoLuong", nullable = false)
    private int soLuong;
    
    @Column(name = "GiaNhap", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaNhap;
    
    @Column(name = "ThanhTien", precision = 18, scale = 2)
    private BigDecimal thanhTien;
    
    @Column(name = "SoLuongDaNhan")
    private int soLuongDaNhan = 0;
    
    @Column(name = "GhiChu", length = 200)
    private String ghiChu;
    
    // Transient fields for display
    @Transient
    private String tenSanPham;
    
    @Transient
    private String tenDanhMuc;
    
    @Transient
    private String donViTinh = "Cái";
    
    // Constructors
    public ChiTietDatHang() {}
    
    public ChiTietDatHang(int maDDH, int maSP, int soLuong, BigDecimal giaNhap) {
        this.maDDH = maDDH;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
        calculateThanhTien();
    }
    
    // Getters and Setters
    public int getMaDDH() {
        return maDDH;
    }
    
    public void setMaDDH(int maDDH) {
        this.maDDH = maDDH;
    }
    
    public int getMaSP() {
        return maSP;
    }
    
    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }
    
    public int getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        calculateThanhTien();
    }
    
    public BigDecimal getGiaNhap() {
        return giaNhap;
    }
    
    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap != null ? giaNhap : BigDecimal.ZERO;
        calculateThanhTien();
    }
    
    public BigDecimal getThanhTien() {
        return thanhTien;
    }
    
    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien != null ? thanhTien : BigDecimal.ZERO;
    }
    
    public int getSoLuongDaNhan() {
        return soLuongDaNhan;
    }
    
    public void setSoLuongDaNhan(int soLuongDaNhan) {
        this.soLuongDaNhan = soLuongDaNhan;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public String getTenSanPham() {
        return tenSanPham;
    }
    
    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }
    
    public String getTenDanhMuc() {
        return tenDanhMuc;
    }
    
    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }
    
    public String getDonViTinh() {
        return donViTinh;
    }
    
    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }
    
    // Helper methods
    public int getSoLuongConLai() {
        return soLuong - soLuongDaNhan;
    }
    
    public boolean isHoanThanh() {
        return soLuongDaNhan >= soLuong;
    }
    
    public double getPhanTramHoanThanh() {
        if (soLuong == 0) return 0;
        return (double) soLuongDaNhan / soLuong * 100;
    }
    
    private void calculateThanhTien() {
        if (giaNhap != null) {
            thanhTien = giaNhap.multiply(new BigDecimal(soLuong));
        } else {
            thanhTien = BigDecimal.ZERO;
        }
    }
    
    @Override
    public String toString() {
        return tenSanPham + " x" + soLuong;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChiTietDatHang that = (ChiTietDatHang) obj;
        return maDDH == that.maDDH && maSP == that.maSP;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(maDDH, maSP);
    }
}
