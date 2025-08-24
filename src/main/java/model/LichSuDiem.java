package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entity class for Points History (Lịch Sử Điểm)
 */
@Entity
@Table(name = "LichSuDiem")
public class LichSuDiem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLS")
    private int maLS;
    
    @Column(name = "MaThe", nullable = false)
    private int maThe;
    
    @Column(name = "LoaiGiaoDich", length = 20, nullable = false)
    private String loaiGiaoDich; // "EARN", "REDEEM", "EXPIRE", "BONUS", "PENALTY"
    
    @Column(name = "SoDiem", nullable = false)
    private int soDiem; // Số điểm thay đổi (+ hoặc -)
    
    @Column(name = "DiemTruoc", nullable = false)
    private int diemTruoc; // Số điểm trước khi thay đổi
    
    @Column(name = "DiemSau", nullable = false)
    private int diemSau; // Số điểm sau khi thay đổi
    
    @Column(name = "NgayGiaoDich", nullable = false)
    private LocalDateTime ngayGiaoDich;
    
    @Column(name = "MaHoaDon")
    private Integer maHoaDon; // Mã hóa đơn liên quan (nếu có)
    
    @Column(name = "GiaTriGiaoDich", precision = 18, scale = 2)
    private BigDecimal giaTriGiaoDich; // Giá trị giao dịch tạo ra điểm
    
    @Column(name = "MoTa", length = 500)
    private String moTa;
    
    @Column(name = "NguoiThucHien")
    private Integer nguoiThucHien; // Mã nhân viên thực hiện
    
    @Column(name = "TrangThai", length = 20)
    private String trangThai = "COMPLETED"; // "PENDING", "COMPLETED", "CANCELLED"
    
    @Column(name = "NgayHetHan")
    private LocalDateTime ngayHetHan; // Ngày hết hạn điểm (nếu có)
    
    @Column(name = "GhiChu", length = 300)
    private String ghiChu;
    
    // Transient fields for display
    @Transient
    private String soThe;
    
    @Transient
    private String tenKhachHang;
    
    @Transient
    private String tenNhanVien;
    
    @Transient
    private String soHoaDon;
    
    // Constructors
    public LichSuDiem() {
        this.ngayGiaoDich = LocalDateTime.now();
    }
    
    public LichSuDiem(int maThe, String loaiGiaoDich, int soDiem, 
                      int diemTruoc, String moTa) {
        this();
        this.maThe = maThe;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soDiem = soDiem;
        this.diemTruoc = diemTruoc;
        this.diemSau = diemTruoc + soDiem;
        this.moTa = moTa;
    }
    
    // Getters and Setters
    public int getMaLS() {
        return maLS;
    }
    
    public void setMaLS(int maLS) {
        this.maLS = maLS;
    }
    
    public int getMaThe() {
        return maThe;
    }
    
    public void setMaThe(int maThe) {
        this.maThe = maThe;
    }
    
    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }
    
    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }
    
    public int getSoDiem() {
        return soDiem;
    }
    
    public void setSoDiem(int soDiem) {
        this.soDiem = soDiem;
        this.diemSau = this.diemTruoc + soDiem;
    }
    
    public int getDiemTruoc() {
        return diemTruoc;
    }
    
    public void setDiemTruoc(int diemTruoc) {
        this.diemTruoc = diemTruoc;
        this.diemSau = diemTruoc + this.soDiem;
    }
    
    public int getDiemSau() {
        return diemSau;
    }
    
    public void setDiemSau(int diemSau) {
        this.diemSau = diemSau;
    }
    
    public LocalDateTime getNgayGiaoDich() {
        return ngayGiaoDich;
    }
    
    public void setNgayGiaoDich(LocalDateTime ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }
    
    public Integer getMaHoaDon() {
        return maHoaDon;
    }
    
    public void setMaHoaDon(Integer maHoaDon) {
        this.maHoaDon = maHoaDon;
    }
    
    public BigDecimal getGiaTriGiaoDich() {
        return giaTriGiaoDich;
    }
    
    public void setGiaTriGiaoDich(BigDecimal giaTriGiaoDich) {
        this.giaTriGiaoDich = giaTriGiaoDich;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public Integer getNguoiThucHien() {
        return nguoiThucHien;
    }
    
    public void setNguoiThucHien(Integer nguoiThucHien) {
        this.nguoiThucHien = nguoiThucHien;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public LocalDateTime getNgayHetHan() {
        return ngayHetHan;
    }
    
    public void setNgayHetHan(LocalDateTime ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public String getSoThe() {
        return soThe;
    }
    
    public void setSoThe(String soThe) {
        this.soThe = soThe;
    }
    
    public String getTenKhachHang() {
        return tenKhachHang;
    }
    
    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }
    
    public String getTenNhanVien() {
        return tenNhanVien;
    }
    
    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }
    
    public String getSoHoaDon() {
        return soHoaDon;
    }
    
    public void setSoHoaDon(String soHoaDon) {
        this.soHoaDon = soHoaDon;
    }
    
    // Helper methods
    public String getLoaiGiaoDichText() {
        switch (loaiGiaoDich) {
            case "EARN": return "Tích điểm";
            case "REDEEM": return "Đổi điểm";
            case "EXPIRE": return "Hết hạn";
            case "BONUS": return "Thưởng";
            case "PENALTY": return "Phạt";
            case "TRANSFER": return "Chuyển điểm";
            case "REFUND": return "Hoàn điểm";
            default: return loaiGiaoDich;
        }
    }
    
    public String getTrangThaiText() {
        switch (trangThai) {
            case "PENDING": return "Chờ xử lý";
            case "COMPLETED": return "Hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return trangThai;
        }
    }
    
    public boolean isEarn() {
        return "EARN".equals(loaiGiaoDich) || "BONUS".equals(loaiGiaoDich);
    }
    
    public boolean isRedeem() {
        return "REDEEM".equals(loaiGiaoDich) || "PENALTY".equals(loaiGiaoDich);
    }
    
    public boolean isExpired() {
        return ngayHetHan != null && LocalDateTime.now().isAfter(ngayHetHan);
    }
    
    public String getDiemText() {
        if (soDiem > 0) {
            return "+" + soDiem;
        } else {
            return String.valueOf(soDiem);
        }
    }
    
    public String getGiaoDichInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLoaiGiaoDichText()).append(" ");
        sb.append(getDiemText()).append(" điểm");
        
        if (maHoaDon != null) {
            sb.append(" (HĐ: ").append(soHoaDon != null ? soHoaDon : maHoaDon).append(")");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getGiaoDichInfo() + " - " + ngayGiaoDich.toLocalDate();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LichSuDiem that = (LichSuDiem) obj;
        return maLS == that.maLS;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maLS);
    }
}
