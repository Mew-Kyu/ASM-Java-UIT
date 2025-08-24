package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity class for Return/Exchange Detail (Chi Tiết Phiếu Đổi Trả)
 */
@Entity
@Table(name = "ChiTietPhieuDoiTra")
@IdClass(ChiTietPhieuDoiTraId.class)
public class ChiTietPhieuDoiTra {
    
    @Id
    @Column(name = "MaPhieuDT")
    private int maPhieuDT;
    
    @Id
    @Column(name = "MaBienThe")
    private int maBienThe;
    
    @Column(name = "LoaiChiTiet", length = 10, nullable = false)
    private String loaiChiTiet; // "TRA" (sản phẩm trả), "DOI" (sản phẩm đổi)
    
    @Column(name = "SoLuong", nullable = false)
    private int soLuong;
    
    @Column(name = "DonGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;
    
    @Column(name = "ThanhTien", precision = 18, scale = 2)
    private BigDecimal thanhTien;
    
    @Column(name = "TinhTrangSanPham", length = 50)
    private String tinhTrangSanPham; // "MOI", "DA_SU_DUNG", "LOI", "HONG"
    
    @Column(name = "LyDoChiTiet", length = 300)
    private String lyDoChiTiet;
    
    @Column(name = "GhiChu", length = 200)
    private String ghiChu;
    
    // Transient fields for display
    @Transient
    private String tenSanPham;
    
    @Transient
    private String tenMauSac;
    
    @Transient
    private String tenKichThuoc;
    
    @Transient
    private String tenDanhMuc;
    
    // Constructors
    public ChiTietPhieuDoiTra() {}
    
    public ChiTietPhieuDoiTra(int maPhieuDT, int maBienThe, String loaiChiTiet, 
                             int soLuong, BigDecimal donGia) {
        this.maPhieuDT = maPhieuDT;
        this.maBienThe = maBienThe;
        this.loaiChiTiet = loaiChiTiet;
        this.soLuong = soLuong;
        this.donGia = donGia;
        calculateThanhTien();
    }
    
    // Getters and Setters
    public int getMaPhieuDT() {
        return maPhieuDT;
    }
    
    public void setMaPhieuDT(int maPhieuDT) {
        this.maPhieuDT = maPhieuDT;
    }
    
    public int getMaBienThe() {
        return maBienThe;
    }
    
    public void setMaBienThe(int maBienThe) {
        this.maBienThe = maBienThe;
    }
    
    public String getLoaiChiTiet() {
        return loaiChiTiet;
    }
    
    public void setLoaiChiTiet(String loaiChiTiet) {
        this.loaiChiTiet = loaiChiTiet;
    }
    
    public int getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        calculateThanhTien();
    }
    
    public BigDecimal getDonGia() {
        return donGia;
    }
    
    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia != null ? donGia : BigDecimal.ZERO;
        calculateThanhTien();
    }
    
    public BigDecimal getThanhTien() {
        return thanhTien;
    }
    
    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien != null ? thanhTien : BigDecimal.ZERO;
    }
    
    public String getTinhTrangSanPham() {
        return tinhTrangSanPham;
    }
    
    public void setTinhTrangSanPham(String tinhTrangSanPham) {
        this.tinhTrangSanPham = tinhTrangSanPham;
    }
    
    public String getLyDoChiTiet() {
        return lyDoChiTiet;
    }
    
    public void setLyDoChiTiet(String lyDoChiTiet) {
        this.lyDoChiTiet = lyDoChiTiet;
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
    
    public String getTenMauSac() {
        return tenMauSac;
    }
    
    public void setTenMauSac(String tenMauSac) {
        this.tenMauSac = tenMauSac;
    }
    
    public String getTenKichThuoc() {
        return tenKichThuoc;
    }
    
    public void setTenKichThuoc(String tenKichThuoc) {
        this.tenKichThuoc = tenKichThuoc;
    }
    
    public String getTenDanhMuc() {
        return tenDanhMuc;
    }
    
    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }
    
    // Helper methods
    public String getLoaiChiTietText() {
        switch (loaiChiTiet) {
            case "TRA": return "Trả lại";
            case "DOI": return "Đổi mới";
            default: return loaiChiTiet;
        }
    }
    
    public String getTinhTrangSanPhamText() {
        if (tinhTrangSanPham == null) return "";
        switch (tinhTrangSanPham) {
            case "MOI": return "Mới";
            case "DA_SU_DUNG": return "Đã sử dụng";
            case "LOI": return "Lỗi";
            case "HONG": return "Hỏng";
            default: return tinhTrangSanPham;
        }
    }
    
    public boolean isTra() {
        return "TRA".equals(loaiChiTiet);
    }
    
    public boolean isDoi() {
        return "DOI".equals(loaiChiTiet);
    }
    
    public String getSanPhamInfo() {
        StringBuilder sb = new StringBuilder();
        if (tenSanPham != null) {
            sb.append(tenSanPham);
        }
        if (tenMauSac != null || tenKichThuoc != null) {
            sb.append(" (");
            if (tenMauSac != null) {
                sb.append(tenMauSac);
            }
            if (tenKichThuoc != null) {
                if (tenMauSac != null) sb.append(", ");
                sb.append(tenKichThuoc);
            }
            sb.append(")");
        }
        return sb.toString();
    }
    
    public void calculateThanhTien() {
        if (donGia != null) {
            thanhTien = donGia.multiply(new BigDecimal(soLuong));
        } else {
            thanhTien = BigDecimal.ZERO;
        }
    }
    
    @Override
    public String toString() {
        return getSanPhamInfo() + " x" + soLuong + " (" + getLoaiChiTietText() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChiTietPhieuDoiTra that = (ChiTietPhieuDoiTra) obj;
        return maPhieuDT == that.maPhieuDT && maBienThe == that.maBienThe;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(maPhieuDT, maBienThe);
    }
}
