package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for Payment Method (Hình Thức Thanh Toán)
 */
@Entity
@Table(name = "HinhThucThanhToan")
public class HinhThucThanhToan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHTTT")
    private int maHTTT;
    
    @Column(name = "TenHTTT", length = 100, nullable = false)
    private String tenHTTT;
    
    @Column(name = "MoTa", length = 200)
    private String moTa;
    
    @Column(name = "LoaiThanhToan", length = 20, nullable = false)
    private String loaiThanhToan; // "CASH", "CARD", "BANK_TRANSFER", "E_WALLET", "INSTALLMENT"
    
    @Column(name = "TrangThai")
    private boolean trangThai = true; // true = active, false = inactive
    
    @Column(name = "ThuPhi")
    private boolean thuPhi = false; // Có thu phí không
    
    @Column(name = "PhanTramPhi", precision = 5, scale = 2)
    private java.math.BigDecimal phanTramPhi = java.math.BigDecimal.ZERO;
    
    @Column(name = "PhiCoDinh", precision = 18, scale = 2)
    private java.math.BigDecimal phiCoDinh = java.math.BigDecimal.ZERO;
    
    @Column(name = "ThoiGianXuLy")
    private int thoiGianXuLy = 0; // Thời gian xử lý (phút)
    
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;
    
    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;
    
    @Column(name = "NguoiTao")
    private Integer nguoiTao;
    
    @Column(name = "ThuTuHienThi")
    private int thuTuHienThi = 0; // Thứ tự hiển thị trong danh sách
    
    @Column(name = "YeuCauXacThuc")
    private boolean yeuCauXacThuc = false; // Yêu cầu xác thực (PIN, OTP, etc.)
    
    @Column(name = "SoTaiKhoan", length = 50)
    private String soTaiKhoan; // Số tài khoản/thẻ (cho bank transfer, card)
    
    @Column(name = "TenChuTaiKhoan", length = 100)
    private String tenChuTaiKhoan;
    
    @Column(name = "TenNganHang", length = 100)
    private String tenNganHang;
    
    @Column(name = "Logo", length = 200)
    private String logo; // Đường dẫn logo
    
    // Transient fields
    @Transient
    private String tenNguoiTao;
    
    // Constructors
    public HinhThucThanhToan() {
        this.ngayTao = LocalDateTime.now();
    }
    
    public HinhThucThanhToan(String tenHTTT, String loaiThanhToan) {
        this();
        this.tenHTTT = tenHTTT;
        this.loaiThanhToan = loaiThanhToan;
    }
    
    // Getters and Setters
    public int getMaHTTT() {
        return maHTTT;
    }
    
    public void setMaHTTT(int maHTTT) {
        this.maHTTT = maHTTT;
    }
    
    public String getTenHTTT() {
        return tenHTTT;
    }
    
    public void setTenHTTT(String tenHTTT) {
        this.tenHTTT = tenHTTT;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public String getLoaiThanhToan() {
        return loaiThanhToan;
    }
    
    public void setLoaiThanhToan(String loaiThanhToan) {
        this.loaiThanhToan = loaiThanhToan;
    }
    
    public boolean isTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
    
    public boolean isThuPhi() {
        return thuPhi;
    }
    
    public void setThuPhi(boolean thuPhi) {
        this.thuPhi = thuPhi;
    }
    
    public java.math.BigDecimal getPhanTramPhi() {
        return phanTramPhi;
    }
    
    public void setPhanTramPhi(java.math.BigDecimal phanTramPhi) {
        this.phanTramPhi = phanTramPhi != null ? phanTramPhi : java.math.BigDecimal.ZERO;
    }
    
    public java.math.BigDecimal getPhiCoDinh() {
        return phiCoDinh;
    }
    
    public void setPhiCoDinh(java.math.BigDecimal phiCoDinh) {
        this.phiCoDinh = phiCoDinh != null ? phiCoDinh : java.math.BigDecimal.ZERO;
    }
    
    public int getThoiGianXuLy() {
        return thoiGianXuLy;
    }
    
    public void setThoiGianXuLy(int thoiGianXuLy) {
        this.thoiGianXuLy = thoiGianXuLy;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
    
        public Integer getNguoiTao() {
        return nguoiTao;
    }

    public void setNguoiTao(Integer nguoiTao) {
        this.nguoiTao = nguoiTao;
    }
    
    public int getThuTuHienThi() {
        return thuTuHienThi;
    }
    
    public void setThuTuHienThi(int thuTuHienThi) {
        this.thuTuHienThi = thuTuHienThi;
    }
    
    public boolean isYeuCauXacThuc() {
        return yeuCauXacThuc;
    }
    
    public void setYeuCauXacThuc(boolean yeuCauXacThuc) {
        this.yeuCauXacThuc = yeuCauXacThuc;
    }
    
    public String getSoTaiKhoan() {
        return soTaiKhoan;
    }
    
    public void setSoTaiKhoan(String soTaiKhoan) {
        this.soTaiKhoan = soTaiKhoan;
    }
    
    public String getTenChuTaiKhoan() {
        return tenChuTaiKhoan;
    }
    
    public void setTenChuTaiKhoan(String tenChuTaiKhoan) {
        this.tenChuTaiKhoan = tenChuTaiKhoan;
    }
    
    public String getTenNganHang() {
        return tenNganHang;
    }
    
    public void setTenNganHang(String tenNganHang) {
        this.tenNganHang = tenNganHang;
    }
    
    public String getLogo() {
        return logo;
    }
    
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    public String getTenNguoiTao() {
        return tenNguoiTao;
    }
    
    public void setTenNguoiTao(String tenNguoiTao) {
        this.tenNguoiTao = tenNguoiTao;
    }
    
    // Helper methods
    public String getLoaiThanhToanText() {
        switch (loaiThanhToan) {
            case "CASH": return "Tiền mặt";
            case "CARD": return "Thẻ tín dụng/ghi nợ";
            case "BANK_TRANSFER": return "Chuyển khoản ngân hàng";
            case "E_WALLET": return "Ví điện tử";
            case "INSTALLMENT": return "Trả góp";
            default: return loaiThanhToan;
        }
    }
    
    public String getTrangThaiText() {
        return trangThai ? "Hoạt động" : "Tạm dừng";
    }
    
    public String getThoiGianXuLyText() {
        if (thoiGianXuLy == 0) return "Tức thì";
        if (thoiGianXuLy < 60) return thoiGianXuLy + " phút";
        if (thoiGianXuLy < 1440) return (thoiGianXuLy / 60) + " giờ";
        return (thoiGianXuLy / 1440) + " ngày";
    }
    
    public boolean isCash() {
        return "CASH".equals(loaiThanhToan);
    }
    
    public boolean isDigital() {
        return "CARD".equals(loaiThanhToan) || 
               "BANK_TRANSFER".equals(loaiThanhToan) || 
               "E_WALLET".equals(loaiThanhToan);
    }
    
    public java.math.BigDecimal tinhPhiThanhToan(java.math.BigDecimal soTien) {
        if (!thuPhi || soTien == null) {
            return java.math.BigDecimal.ZERO;
        }
        
        java.math.BigDecimal phi = phiCoDinh;
        
        if (phanTramPhi.compareTo(java.math.BigDecimal.ZERO) > 0) {
            java.math.BigDecimal phiPhanTram = soTien.multiply(phanTramPhi)
                    .divide(new java.math.BigDecimal("100"));
            phi = phi.add(phiPhanTram);
        }
        
        return phi;
    }
    
    @Override
    public String toString() {
        return tenHTTT + " (" + getLoaiThanhToanText() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HinhThucThanhToan that = (HinhThucThanhToan) obj;
        return maHTTT == that.maHTTT;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maHTTT);
    }
}
