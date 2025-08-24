package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Entity class for Return/Exchange Receipt (Phiếu Đổi Trả)
 */
@Entity
@Table(name = "PhieuDoiTra")
public class PhieuDoiTra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPhieuDT")
    private int maPhieuDT;
    
    @Column(name = "MaHD", nullable = false)
    private int maHD; // Hóa đơn gốc
    
    @Column(name = "LoaiPhieu", length = 10, nullable = false)
    private String loaiPhieu; // "DOI" (đổi), "TRA" (trả)
    
    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;
    
    @Column(name = "MaNV", nullable = false)
    private int maNV; // Nhân viên xử lý
    
    @Column(name = "MaKH")
    private Integer maKH; // Khách hàng (có thể null nếu bán lẻ)
    
    @Column(name = "LyDo", length = 500, nullable = false)
    private String lyDo;
    
    @Column(name = "TrangThai", length = 20, nullable = false)
    private String trangThai; // "PENDING", "APPROVED", "REJECTED", "COMPLETED"
    
    @Column(name = "TongGiaTriTra", precision = 18, scale = 2)
    private BigDecimal tongGiaTriTra = BigDecimal.ZERO;
    
    @Column(name = "TongGiaTriDoi", precision = 18, scale = 2)
    private BigDecimal tongGiaTriDoi = BigDecimal.ZERO;
    
    @Column(name = "SoTienHoanLai", precision = 18, scale = 2)
    private BigDecimal soTienHoanLai = BigDecimal.ZERO;
    
    @Column(name = "SoTienBoSung", precision = 18, scale = 2)
    private BigDecimal soTienBoSung = BigDecimal.ZERO; // Khách cần trả thêm khi đổi sản phẩm đắt hơn
    
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;
    
    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;
    
    @Column(name = "NguoiDuyet")
    private Integer nguoiDuyet; // Người duyệt phiếu
    
    @Column(name = "LyDoTuChoi", length = 300)
    private String lyDoTuChoi;
    
    @Column(name = "HinhThucHoanTien", length = 20)
    private String hinhThucHoanTien; // "CASH", "BANK_TRANSFER", "CREDIT_NOTE"
    
    @Column(name = "SoTaiKhoanHoan", length = 50)
    private String soTaiKhoanHoan; // Số tài khoản hoàn tiền (nếu chuyển khoản)
    
    @Column(name = "TenChuTaiKhoan", length = 100)
    private String tenChuTaiKhoan;
    
    @Column(name = "NgayHoanTien")
    private LocalDateTime ngayHoanTien;
    
    // Transient fields for display
    @Transient
    private String soHoaDon;
    
    @Transient
    private String tenKhachHang;
    
    @Transient
    private String tenNhanVien;
    
    @Transient
    private String tenNguoiDuyet;
    
    @Transient
    private List<ChiTietPhieuDoiTra> chiTietDoiTraList = new ArrayList<>();
    
    // Constructors
    public PhieuDoiTra() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = "PENDING";
    }
    
    public PhieuDoiTra(int maHD, String loaiPhieu, String lyDo, int maNV) {
        this();
        this.maHD = maHD;
        this.loaiPhieu = loaiPhieu;
        this.lyDo = lyDo;
        this.maNV = maNV;
    }
    
    // Getters and Setters
    public int getMaPhieuDT() {
        return maPhieuDT;
    }
    
    public void setMaPhieuDT(int maPhieuDT) {
        this.maPhieuDT = maPhieuDT;
    }
    
    public int getMaHD() {
        return maHD;
    }
    
    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }
    
    public String getLoaiPhieu() {
        return loaiPhieu;
    }
    
    public void setLoaiPhieu(String loaiPhieu) {
        this.loaiPhieu = loaiPhieu;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public int getMaNV() {
        return maNV;
    }
    
    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }
    
    public Integer getMaKH() {
        return maKH;
    }
    
    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }
    
    public String getLyDo() {
        return lyDo;
    }
    
    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public BigDecimal getTongGiaTriTra() {
        return tongGiaTriTra;
    }
    
    public void setTongGiaTriTra(BigDecimal tongGiaTriTra) {
        this.tongGiaTriTra = tongGiaTriTra != null ? tongGiaTriTra : BigDecimal.ZERO;
        calculateSoTienHoanLai();
    }
    
    public BigDecimal getTongGiaTriDoi() {
        return tongGiaTriDoi;
    }
    
    public void setTongGiaTriDoi(BigDecimal tongGiaTriDoi) {
        this.tongGiaTriDoi = tongGiaTriDoi != null ? tongGiaTriDoi : BigDecimal.ZERO;
        calculateSoTienHoanLai();
    }
    
    public BigDecimal getSoTienHoanLai() {
        return soTienHoanLai;
    }
    
    public void setSoTienHoanLai(BigDecimal soTienHoanLai) {
        this.soTienHoanLai = soTienHoanLai != null ? soTienHoanLai : BigDecimal.ZERO;
    }
    
    public BigDecimal getSoTienBoSung() {
        return soTienBoSung;
    }
    
    public void setSoTienBoSung(BigDecimal soTienBoSung) {
        this.soTienBoSung = soTienBoSung != null ? soTienBoSung : BigDecimal.ZERO;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public LocalDateTime getNgayDuyet() {
        return ngayDuyet;
    }
    
    public void setNgayDuyet(LocalDateTime ngayDuyet) {
        this.ngayDuyet = ngayDuyet;
    }
    
    public Integer getNguoiDuyet() {
        return nguoiDuyet;
    }
    
    public void setNguoiDuyet(Integer nguoiDuyet) {
        this.nguoiDuyet = nguoiDuyet;
    }
    
    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }
    
    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }
    
    public String getHinhThucHoanTien() {
        return hinhThucHoanTien;
    }
    
    public void setHinhThucHoanTien(String hinhThucHoanTien) {
        this.hinhThucHoanTien = hinhThucHoanTien;
    }
    
    public String getSoTaiKhoanHoan() {
        return soTaiKhoanHoan;
    }
    
    public void setSoTaiKhoanHoan(String soTaiKhoanHoan) {
        this.soTaiKhoanHoan = soTaiKhoanHoan;
    }
    
    public String getTenChuTaiKhoan() {
        return tenChuTaiKhoan;
    }
    
    public void setTenChuTaiKhoan(String tenChuTaiKhoan) {
        this.tenChuTaiKhoan = tenChuTaiKhoan;
    }
    
    public LocalDateTime getNgayHoanTien() {
        return ngayHoanTien;
    }
    
    public void setNgayHoanTien(LocalDateTime ngayHoanTien) {
        this.ngayHoanTien = ngayHoanTien;
    }
    
    public String getSoHoaDon() {
        return soHoaDon;
    }
    
    public void setSoHoaDon(String soHoaDon) {
        this.soHoaDon = soHoaDon;
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
    
    public String getTenNguoiDuyet() {
        return tenNguoiDuyet;
    }
    
    public void setTenNguoiDuyet(String tenNguoiDuyet) {
        this.tenNguoiDuyet = tenNguoiDuyet;
    }
    
    public List<ChiTietPhieuDoiTra> getChiTietDoiTraList() {
        return chiTietDoiTraList;
    }
    
    public void setChiTietDoiTraList(List<ChiTietPhieuDoiTra> chiTietDoiTraList) {
        this.chiTietDoiTraList = chiTietDoiTraList != null ? chiTietDoiTraList : new ArrayList<>();
    }
    
    // Helper methods
    public String getLoaiPhieuText() {
        switch (loaiPhieu) {
            case "DOI": return "Đổi hàng";
            case "TRA": return "Trả hàng";
            default: return loaiPhieu;
        }
    }
    
    public String getTrangThaiText() {
        switch (trangThai) {
            case "PENDING": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Từ chối";
            case "COMPLETED": return "Hoàn thành";
            default: return trangThai;
        }
    }
    
    public String getHinhThucHoanTienText() {
        if (hinhThucHoanTien == null) return "";
        switch (hinhThucHoanTien) {
            case "CASH": return "Tiền mặt";
            case "BANK_TRANSFER": return "Chuyển khoản";
            case "CREDIT_NOTE": return "Ghi có tài khoản";
            default: return hinhThucHoanTien;
        }
    }
    
    public boolean isDoi() {
        return "DOI".equals(loaiPhieu);
    }
    
    public boolean isTra() {
        return "TRA".equals(loaiPhieu);
    }
    
    public boolean isPending() {
        return "PENDING".equals(trangThai);
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(trangThai);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(trangThai);
    }
    
    public boolean isRejected() {
        return "REJECTED".equals(trangThai);
    }
    
    public boolean canEdit() {
        return "PENDING".equals(trangThai);
    }
    
    public boolean canApprove() {
        return "PENDING".equals(trangThai);
    }
    
    public boolean canComplete() {
        return "APPROVED".equals(trangThai);
    }
    
    private void calculateSoTienHoanLai() {
        BigDecimal chenhLech = tongGiaTriTra.subtract(tongGiaTriDoi);
        if (chenhLech.compareTo(BigDecimal.ZERO) > 0) {
            soTienHoanLai = chenhLech;
            soTienBoSung = BigDecimal.ZERO;
        } else {
            soTienHoanLai = BigDecimal.ZERO;
            soTienBoSung = chenhLech.negate();
        }
    }
    
    public String getMaPhieuFormatted() {
        return (isDoi() ? "PDT-" : "PTR-") + String.format("%06d", maPhieuDT);
    }
    
    @Override
    public String toString() {
        return getMaPhieuFormatted() + " (" + getLoaiPhieuText() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhieuDoiTra that = (PhieuDoiTra) obj;
        return maPhieuDT == that.maPhieuDT;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maPhieuDT);
    }
}
