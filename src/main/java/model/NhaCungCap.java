package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Entity class for Supplier (Nhà Cung Cấp)
 */
@Entity
@Table(name = "NhaCungCap")
public class NhaCungCap {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNCC")
    private int maNCC;
    
    @Column(name = "TenNCC", length = 200, nullable = false)
    private String tenNCC;
    
    @Column(name = "DiaChi", length = 300)
    private String diaChi;
    
    @Column(name = "DienThoai", length = 20)
    private String dienThoai;
    
    @Column(name = "Email", length = 100)
    private String email;
    
    @Column(name = "NguoiLienHe", length = 100)
    private String nguoiLienHe;
    
    @Column(name = "ChucVuLienHe", length = 50)
    private String chucVuLienHe;
    
    @Column(name = "MoTa", length = 500)
    private String moTa;
    
    @Column(name = "NgayHopTac")
    private LocalDate ngayHopTac;
    
    @Column(name = "TrangThai")
    private boolean trangThai = true; // true = active, false = inactive
    
    @Column(name = "Rating")
    private int rating = 0; // 0-5 stars rating
    
    @Column(name = "TongGiaTriMua", precision = 18, scale = 2)
    private BigDecimal tongGiaTriMua = BigDecimal.ZERO;
    
    @Column(name = "SoDonHang")
    private int soDonHang = 0;
    
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;
    
    // Constructors
    public NhaCungCap() {}
    
    public NhaCungCap(String tenNCC, String diaChi, String dienThoai, String email) {
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.dienThoai = dienThoai;
        this.email = email;
        this.ngayHopTac = LocalDate.now();
        this.trangThai = true;
    }
    
    // Getters and Setters
    public int getMaNCC() {
        return maNCC;
    }
    
    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }
    
    public String getTenNCC() {
        return tenNCC;
    }
    
    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }
    
    public String getDiaChi() {
        return diaChi;
    }
    
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    
    public String getDienThoai() {
        return dienThoai;
    }
    
    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNguoiLienHe() {
        return nguoiLienHe;
    }
    
    public void setNguoiLienHe(String nguoiLienHe) {
        this.nguoiLienHe = nguoiLienHe;
    }
    
    public String getChucVuLienHe() {
        return chucVuLienHe;
    }
    
    public void setChucVuLienHe(String chucVuLienHe) {
        this.chucVuLienHe = chucVuLienHe;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public LocalDate getNgayHopTac() {
        return ngayHopTac;
    }
    
    public void setNgayHopTac(LocalDate ngayHopTac) {
        this.ngayHopTac = ngayHopTac;
    }
    
    public boolean isTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        if (rating >= 0 && rating <= 5) {
            this.rating = rating;
        }
    }
    
    public BigDecimal getTongGiaTriMua() {
        return tongGiaTriMua;
    }
    
    public void setTongGiaTriMua(BigDecimal tongGiaTriMua) {
        this.tongGiaTriMua = tongGiaTriMua != null ? tongGiaTriMua : BigDecimal.ZERO;
    }
    
    public int getSoDonHang() {
        return soDonHang;
    }
    
    public void setSoDonHang(int soDonHang) {
        this.soDonHang = soDonHang;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    // Helper methods
    public String getTrangThaiText() {
        return trangThai ? "Hoạt động" : "Ngừng hoạt động";
    }
    
    public String getRatingText() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
    
    @Override
    public String toString() {
        return tenNCC + " (" + maNCC + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NhaCungCap that = (NhaCungCap) obj;
        return maNCC == that.maNCC;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maNCC);
    }
}
