package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entity class for Promotion Program (Chương Trình Khuyến Mãi)
 */
@Entity
@Table(name = "ChuongTrinhKhuyenMai")
public class ChuongTrinhKhuyenMai {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKM")
    private int maKM;
    
    @Column(name = "TenKM", length = 200, nullable = false)
    private String tenKM;
    
    @Column(name = "MoTa", length = 500)
    private String moTa;
    
    @Column(name = "LoaiKM", length = 20, nullable = false)
    private String loaiKM; // "PERCENTAGE", "FIXED_AMOUNT", "BUY_X_GET_Y", "FREE_SHIPPING"
    
    @Column(name = "GiaTriKM", precision = 18, scale = 2)
    private BigDecimal giaTriKM; // Giá trị giảm (% hoặc số tiền)
    
    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;
    
    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;
    
    @Column(name = "TrangThai", length = 20, nullable = false)
    private String trangThai; // "ACTIVE", "INACTIVE", "EXPIRED", "DRAFT"
    
    @Column(name = "DieuKienToiThieu", precision = 18, scale = 2)
    private BigDecimal dieuKienToiThieu = BigDecimal.ZERO; // Giá trị đơn hàng tối thiểu
    
    @Column(name = "GiamToiDa", precision = 18, scale = 2)
    private BigDecimal giamToiDa; // Số tiền giảm tối đa (cho loại %)
    
    @Column(name = "SoLuongSuDung")
    private int soLuongSuDung = 0; // Số lần đã sử dụng
    
    @Column(name = "GioiHanSuDung")
    private int gioiHanSuDung = 0; // Giới hạn số lần sử dụng (0 = không giới hạn)
    
    @Column(name = "ApDungCho", length = 20)
    private String apDungCho; // "ALL", "CATEGORY", "PRODUCT", "CUSTOMER_GROUP"
    
    @Column(name = "DanhSachApDung", length = 500)
    private String danhSachApDung; // Danh sách ID sản phẩm, danh mục, nhóm KH (cách nhau bởi dấu phẩy)
    
    @Column(name = "MaCode", length = 20, unique = true)
    private String maCode; // Mã code để áp dụng khuyến mãi
    
    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;
    
    @Column(name = "NguoiTao", nullable = false)
    private int nguoiTao; // MaNV
    
    @Column(name = "TuDongApDung")
    private boolean tuDongApDung = false; // Tự động áp dụng khi đủ điều kiện
    
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;
    
    // For "BUY_X_GET_Y" promotions
    @Column(name = "SoLuongMua")
    private int soLuongMua = 0; // Mua X sản phẩm
    
    @Column(name = "SoLuongTang")
    private int soLuongTang = 0; // Tặng Y sản phẩm
    
    @Column(name = "SanPhamTang")
    private int sanPhamTang = 0; // Mã sản phẩm được tặng
    
    // Transient fields
    @Transient
    private String tenNguoiTao;
    
    // Constructors
    public ChuongTrinhKhuyenMai() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = "DRAFT";
    }
    
    public ChuongTrinhKhuyenMai(String tenKM, String loaiKM, BigDecimal giaTriKM, 
                               LocalDate ngayBatDau, LocalDate ngayKetThuc, int nguoiTao) {
        this();
        this.tenKM = tenKM;
        this.loaiKM = loaiKM;
        this.giaTriKM = giaTriKM;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.nguoiTao = nguoiTao;
    }
    
    // Getters and Setters
    public int getMaKM() {
        return maKM;
    }
    
    public void setMaKM(int maKM) {
        this.maKM = maKM;
    }
    
    public String getTenKM() {
        return tenKM;
    }
    
    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public String getLoaiKM() {
        return loaiKM;
    }
    
    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }
    
    public BigDecimal getGiaTriKM() {
        return giaTriKM;
    }
    
    public void setGiaTriKM(BigDecimal giaTriKM) {
        this.giaTriKM = giaTriKM;
    }
    
    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }
    
    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }
    
    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }
    
    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public BigDecimal getDieuKienToiThieu() {
        return dieuKienToiThieu;
    }
    
    public void setDieuKienToiThieu(BigDecimal dieuKienToiThieu) {
        this.dieuKienToiThieu = dieuKienToiThieu != null ? dieuKienToiThieu : BigDecimal.ZERO;
    }
    
    public BigDecimal getGiamToiDa() {
        return giamToiDa;
    }
    
    public void setGiamToiDa(BigDecimal giamToiDa) {
        this.giamToiDa = giamToiDa;
    }
    
    public int getSoLuongSuDung() {
        return soLuongSuDung;
    }
    
    public void setSoLuongSuDung(int soLuongSuDung) {
        this.soLuongSuDung = soLuongSuDung;
    }
    
    public int getGioiHanSuDung() {
        return gioiHanSuDung;
    }
    
    public void setGioiHanSuDung(int gioiHanSuDung) {
        this.gioiHanSuDung = gioiHanSuDung;
    }
    
    public String getApDungCho() {
        return apDungCho;
    }
    
    public void setApDungCho(String apDungCho) {
        this.apDungCho = apDungCho;
    }
    
    public String getDanhSachApDung() {
        return danhSachApDung;
    }
    
    public void setDanhSachApDung(String danhSachApDung) {
        this.danhSachApDung = danhSachApDung;
    }
    
    public String getMaCode() {
        return maCode;
    }
    
    public void setMaCode(String maCode) {
        this.maCode = maCode;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public int getNguoiTao() {
        return nguoiTao;
    }
    
    public void setNguoiTao(int nguoiTao) {
        this.nguoiTao = nguoiTao;
    }
    
    public boolean isTuDongApDung() {
        return tuDongApDung;
    }
    
    public void setTuDongApDung(boolean tuDongApDung) {
        this.tuDongApDung = tuDongApDung;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public int getSoLuongMua() {
        return soLuongMua;
    }
    
    public void setSoLuongMua(int soLuongMua) {
        this.soLuongMua = soLuongMua;
    }
    
    public int getSoLuongTang() {
        return soLuongTang;
    }
    
    public void setSoLuongTang(int soLuongTang) {
        this.soLuongTang = soLuongTang;
    }
    
    public int getSanPhamTang() {
        return sanPhamTang;
    }
    
    public void setSanPhamTang(int sanPhamTang) {
        this.sanPhamTang = sanPhamTang;
    }
    
    public String getTenNguoiTao() {
        return tenNguoiTao;
    }
    
    public void setTenNguoiTao(String tenNguoiTao) {
        this.tenNguoiTao = tenNguoiTao;
    }
    
    // Helper methods
    public String getLoaiKMText() {
        switch (loaiKM) {
            case "PERCENTAGE": return "Giảm theo phần trăm";
            case "FIXED_AMOUNT": return "Giảm số tiền cố định";
            case "BUY_X_GET_Y": return "Mua X tặng Y";
            case "FREE_SHIPPING": return "Miễn phí vận chuyển";
            default: return loaiKM;
        }
    }
    
    public String getTrangThaiText() {
        switch (trangThai) {
            case "ACTIVE": return "Đang hoạt động";
            case "INACTIVE": return "Tạm dừng";
            case "EXPIRED": return "Đã hết hạn";
            case "DRAFT": return "Nháp";
            default: return trangThai;
        }
    }
    
    public String getApDungChoText() {
        switch (apDungCho) {
            case "ALL": return "Tất cả sản phẩm";
            case "CATEGORY": return "Theo danh mục";
            case "PRODUCT": return "Sản phẩm cụ thể";
            case "CUSTOMER_GROUP": return "Nhóm khách hàng";
            default: return apDungCho;
        }
    }
    
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return "ACTIVE".equals(trangThai) && 
               (ngayBatDau == null || !today.isBefore(ngayBatDau)) &&
               (ngayKetThuc == null || !today.isAfter(ngayKetThuc)) &&
               (gioiHanSuDung == 0 || soLuongSuDung < gioiHanSuDung);
    }
    
    public boolean isExpired() {
        LocalDate today = LocalDate.now();
        return ngayKetThuc != null && today.isAfter(ngayKetThuc);
    }
    
    public boolean canUse() {
        return isActive() && (gioiHanSuDung == 0 || soLuongSuDung < gioiHanSuDung);
    }
    
    public int getSoLuongConLai() {
        if (gioiHanSuDung == 0) return Integer.MAX_VALUE;
        return Math.max(0, gioiHanSuDung - soLuongSuDung);
    }
    
    public BigDecimal tinhGiamGia(BigDecimal giaTriDonHang) {
        if (!canUse() || giaTriDonHang.compareTo(dieuKienToiThieu) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal giamGia = BigDecimal.ZERO;
        
        switch (loaiKM) {
            case "PERCENTAGE":
                giamGia = giaTriDonHang.multiply(giaTriKM).divide(new BigDecimal("100"));
                if (giamToiDa != null && giamGia.compareTo(giamToiDa) > 0) {
                    giamGia = giamToiDa;
                }
                break;
            case "FIXED_AMOUNT":
                giamGia = giaTriKM;
                break;
            case "FREE_SHIPPING":
                // Logic for free shipping would be handled elsewhere
                giamGia = BigDecimal.ZERO;
                break;
            // BUY_X_GET_Y handled separately in business logic
        }
        
        return giamGia;
    }
    
    @Override
    public String toString() {
        return tenKM + " (" + getLoaiKMText() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChuongTrinhKhuyenMai that = (ChuongTrinhKhuyenMai) obj;
        return maKM == that.maKM;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maKM);
    }
}
