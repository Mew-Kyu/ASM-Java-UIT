package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entity class for Loyalty Card (Thẻ Thân Thiết)
 */
@Entity
@Table(name = "TheThanThiet")
public class TheThanThiet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaThe")
    private int maThe;
    
    @Column(name = "MaKH", nullable = false, unique = true)
    private int maKH; // Mỗi khách hàng chỉ có 1 thẻ
    
    @Column(name = "SoThe", length = 20, nullable = false, unique = true)
    private String soThe; // Số thẻ duy nhất
    
    @Column(name = "LoaiThe", length = 20, nullable = false)
    private String loaiThe; // "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"
    
    @Column(name = "DiemTichLuy", nullable = false)
    private int diemTichLuy = 0;
    
    @Column(name = "DiemDaSuDung")
    private int diemDaSuDung = 0;
    
    @Column(name = "TongChiTieu", precision = 18, scale = 2)
    private BigDecimal tongChiTieu = BigDecimal.ZERO;
    
    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;
    
    @Column(name = "NgayKichHoat")
    private LocalDateTime ngayKichHoat;
    
    @Column(name = "NgayHetHan")
    private LocalDate ngayHetHan;
    
    @Column(name = "TrangThai", length = 20, nullable = false)
    private String trangThai; // "ACTIVE", "INACTIVE", "EXPIRED", "SUSPENDED"
    
    @Column(name = "SoDonHang")
    private int soDonHang = 0;
    
    @Column(name = "LanMuaGanNhat")
    private LocalDateTime lanMuaGanNhat;
    
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;
    
    @Column(name = "NguoiTao")
    private int nguoiTao;
    
    @Column(name = "NgayNangCap")
    private LocalDateTime ngayNangCap; // Ngày nâng cấp loại thẻ gần nhất
    
    // Transient fields for display
    @Transient
    private String tenKhachHang;
    
    @Transient
    private String dienThoaiKhachHang;
    
    @Transient
    private String emailKhachHang;
    
    @Transient
    private String tenNguoiTao;
    
    // Constructors
    public TheThanThiet() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = "ACTIVE";
        this.loaiThe = "BRONZE";
    }
    
    public TheThanThiet(int maKH, String soThe) {
        this();
        this.maKH = maKH;
        this.soThe = soThe;
        this.ngayKichHoat = LocalDateTime.now();
        // Thẻ có hiệu lực 2 năm
        this.ngayHetHan = LocalDate.now().plusYears(2);
    }
    
    // Getters and Setters
    public int getMaThe() {
        return maThe;
    }
    
    public void setMaThe(int maThe) {
        this.maThe = maThe;
    }
    
    public int getMaKH() {
        return maKH;
    }
    
    public void setMaKH(int maKH) {
        this.maKH = maKH;
    }
    
    public String getSoThe() {
        return soThe;
    }
    
    public void setSoThe(String soThe) {
        this.soThe = soThe;
    }
    
    public String getLoaiThe() {
        return loaiThe;
    }
    
    public void setLoaiThe(String loaiThe) {
        this.loaiThe = loaiThe;
    }
    
    public int getDiemTichLuy() {
        return diemTichLuy;
    }
    
    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }
    
    public int getDiemDaSuDung() {
        return diemDaSuDung;
    }
    
    public void setDiemDaSuDung(int diemDaSuDung) {
        this.diemDaSuDung = diemDaSuDung;
    }
    
    public BigDecimal getTongChiTieu() {
        return tongChiTieu;
    }
    
    public void setTongChiTieu(BigDecimal tongChiTieu) {
        this.tongChiTieu = tongChiTieu != null ? tongChiTieu : BigDecimal.ZERO;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public LocalDateTime getNgayKichHoat() {
        return ngayKichHoat;
    }
    
    public void setNgayKichHoat(LocalDateTime ngayKichHoat) {
        this.ngayKichHoat = ngayKichHoat;
    }
    
    public LocalDate getNgayHetHan() {
        return ngayHetHan;
    }
    
    public void setNgayHetHan(LocalDate ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public int getSoDonHang() {
        return soDonHang;
    }
    
    public void setSoDonHang(int soDonHang) {
        this.soDonHang = soDonHang;
    }
    
    public LocalDateTime getLanMuaGanNhat() {
        return lanMuaGanNhat;
    }
    
    public void setLanMuaGanNhat(LocalDateTime lanMuaGanNhat) {
        this.lanMuaGanNhat = lanMuaGanNhat;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public int getNguoiTao() {
        return nguoiTao;
    }
    
    public void setNguoiTao(int nguoiTao) {
        this.nguoiTao = nguoiTao;
    }
    
    public LocalDateTime getNgayNangCap() {
        return ngayNangCap;
    }
    
    public void setNgayNangCap(LocalDateTime ngayNangCap) {
        this.ngayNangCap = ngayNangCap;
    }
    
    public String getTenKhachHang() {
        return tenKhachHang;
    }
    
    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }
    
    public String getDienThoaiKhachHang() {
        return dienThoaiKhachHang;
    }
    
    public void setDienThoaiKhachHang(String dienThoaiKhachHang) {
        this.dienThoaiKhachHang = dienThoaiKhachHang;
    }
    
    public String getEmailKhachHang() {
        return emailKhachHang;
    }
    
    public void setEmailKhachHang(String emailKhachHang) {
        this.emailKhachHang = emailKhachHang;
    }
    
    public String getTenNguoiTao() {
        return tenNguoiTao;
    }
    
    public void setTenNguoiTao(String tenNguoiTao) {
        this.tenNguoiTao = tenNguoiTao;
    }
    
    // Helper methods
    public String getLoaiTheText() {
        switch (loaiThe) {
            case "BRONZE": return "Đồng";
            case "SILVER": return "Bạc";
            case "GOLD": return "Vàng";
            case "PLATINUM": return "Bạch kim";
            case "DIAMOND": return "Kim cương";
            default: return loaiThe;
        }
    }
    
    public String getTrangThaiText() {
        switch (trangThai) {
            case "ACTIVE": return "Hoạt động";
            case "INACTIVE": return "Chưa kích hoạt";
            case "EXPIRED": return "Hết hạn";
            case "SUSPENDED": return "Tạm khóa";
            default: return trangThai;
        }
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(trangThai) && 
               (ngayHetHan == null || !LocalDate.now().isAfter(ngayHetHan));
    }
    
    public boolean isExpired() {
        return ngayHetHan != null && LocalDate.now().isAfter(ngayHetHan);
    }
    
    public int getDiemKhaDung() {
        return diemTichLuy - diemDaSuDung;
    }
    
    public int getTongDiem() {
        return diemTichLuy;
    }
    
    // Loyalty card benefits based on card type
    public double getTyLeHoanDiem() {
        switch (loaiThe) {
            case "BRONZE": return 1.0; // 1% hoàn điểm
            case "SILVER": return 1.5; // 1.5% hoàn điểm
            case "GOLD": return 2.0;   // 2% hoàn điểm
            case "PLATINUM": return 2.5; // 2.5% hoàn điểm
            case "DIAMOND": return 3.0;  // 3% hoàn điểm
            default: return 1.0;
        }
    }
    
    public double getTyLeGiamGia() {
        switch (loaiThe) {
            case "BRONZE": return 0.0;  // Không giảm giá
            case "SILVER": return 3.0;  // Giảm 3%
            case "GOLD": return 5.0;    // Giảm 5%
            case "PLATINUM": return 7.0; // Giảm 7%
            case "DIAMOND": return 10.0; // Giảm 10%
            default: return 0.0;
        }
    }
    
    public BigDecimal getChiTieuToiThieuNangCap() {
        switch (loaiThe) {
            case "BRONZE": return new BigDecimal("5000000");  // 5 triệu để lên Silver
            case "SILVER": return new BigDecimal("15000000"); // 15 triệu để lên Gold
            case "GOLD": return new BigDecimal("30000000");   // 30 triệu để lên Platinum
            case "PLATINUM": return new BigDecimal("50000000"); // 50 triệu để lên Diamond
            case "DIAMOND": return BigDecimal.ZERO; // Đã là cấp cao nhất
            default: return BigDecimal.ZERO;
        }
    }
    
    public String getLoaiTheKeTiep() {
        switch (loaiThe) {
            case "BRONZE": return "SILVER";
            case "SILVER": return "GOLD";
            case "GOLD": return "PLATINUM";
            case "PLATINUM": return "DIAMOND";
            case "DIAMOND": return null; // Đã là cấp cao nhất
            default: return null;
        }
    }
    
    public boolean canUpgrade() {
        BigDecimal chiTieuToiThieu = getChiTieuToiThieuNangCap();
        return chiTieuToiThieu.compareTo(BigDecimal.ZERO) > 0 && 
               tongChiTieu.compareTo(chiTieuToiThieu) >= 0;
    }
    
    public int tinhDiemTuSoTien(BigDecimal soTien) {
        if (soTien == null) return 0;
        // 1 điểm = 1000 VND * tỷ lệ hoàn điểm
        double diem = soTien.doubleValue() / 1000.0 * (getTyLeHoanDiem() / 100.0);
        return (int) Math.floor(diem);
    }
    
    public BigDecimal tinhGiamGiaToiDa(BigDecimal soTien) {
        if (soTien == null) return BigDecimal.ZERO;
        double tyLeGiamGia = getTyLeGiamGia() / 100.0;
        BigDecimal giamGia = soTien.multiply(new BigDecimal(tyLeGiamGia));
        
        // Giới hạn giảm giá tối đa theo loại thẻ
        BigDecimal giamGiaToiDa;
        switch (loaiThe) {
            case "SILVER": giamGiaToiDa = new BigDecimal("100000"); break;  // 100k
            case "GOLD": giamGiaToiDa = new BigDecimal("300000"); break;    // 300k
            case "PLATINUM": giamGiaToiDa = new BigDecimal("500000"); break; // 500k
            case "DIAMOND": giamGiaToiDa = new BigDecimal("1000000"); break; // 1 triệu
            default: giamGiaToiDa = BigDecimal.ZERO;
        }
        
        return giamGia.min(giamGiaToiDa);
    }
    
    public int getIconColor() {
        switch (loaiThe) {
            case "BRONZE": return 0xCD7F32; // Bronze color
            case "SILVER": return 0xC0C0C0; // Silver color
            case "GOLD": return 0xFFD700;   // Gold color
            case "PLATINUM": return 0xE5E4E2; // Platinum color
            case "DIAMOND": return 0xB9F2FF;  // Diamond color
            default: return 0x808080; // Gray
        }
    }
    
    @Override
    public String toString() {
        return soThe + " (" + getLoaiTheText() + " - " + tenKhachHang + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TheThanThiet that = (TheThanThiet) obj;
        return maThe == that.maThe;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(maThe);
    }
}
