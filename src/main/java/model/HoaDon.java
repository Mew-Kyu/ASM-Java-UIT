package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "HoaDon", schema = "dbo")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHD", nullable = false)
    private Integer id;

    @Column(name = "NgayLap", nullable = false)
    private LocalDate ngayLap;

    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "MaHTTT")
    private Integer maHTTT;

    @Column(name = "PhiThanhToan", precision = 18, scale = 2)
    private BigDecimal phiThanhToan;

    @Column(name = "TrangThaiThanhToan", length = 20)
    private String trangThaiThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKH")
    private KhachHang maKH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNV", nullable = false)
    private NhanVien maNV;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ChiTietHoaDon> chiTietHoaDons = new LinkedHashSet<>();

    // Constructors
    public HoaDon() {
        this.ngayLap = LocalDate.now();
    }

    public HoaDon(KhachHang khachHang, NhanVien nhanVien) {
        this();
        this.maKH = khachHang;
        this.maNV = nhanVien;
    }

    public HoaDon(LocalDate ngayLap, KhachHang khachHang, NhanVien nhanVien) {
        this.ngayLap = ngayLap;
        this.maKH = khachHang;
        this.maNV = nhanVien;
        this.tongTien = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public Integer getMaHTTT() {
        return maHTTT;
    }

    public void setMaHTTT(Integer maHTTT) {
        this.maHTTT = maHTTT;
    }

    public BigDecimal getPhiThanhToan() {
        return phiThanhToan;
    }

    public void setPhiThanhToan(BigDecimal phiThanhToan) {
        this.phiThanhToan = phiThanhToan;
    }

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public KhachHang getMaKH() {
        return maKH;
    }

    public void setMaKH(KhachHang maKH) {
        this.maKH = maKH;
    }

    public NhanVien getMaNV() {
        return maNV;
    }

    public void setMaNV(NhanVien maNV) {
        this.maNV = maNV;
    }

    public Set<ChiTietHoaDon> getChiTietHoaDons() {
        return chiTietHoaDons;
    }

    public void setChiTietHoaDons(Set<ChiTietHoaDon> chiTietHoaDons) {
        this.chiTietHoaDons = chiTietHoaDons;
    }

    // Business methods
    public void addChiTietHoaDon(ChiTietHoaDon chiTiet) {
        chiTietHoaDons.add(chiTiet);
        chiTiet.setHoaDon(this);
        if (chiTiet.getId() == null) {
            chiTiet.setId(new ChiTietHoaDonId(this.id, chiTiet.getMaBienThe().getId()));
        }
        calculateTongTien();
    }

    public void removeChiTietHoaDon(ChiTietHoaDon chiTiet) {
        chiTietHoaDons.remove(chiTiet);
        chiTiet.setHoaDon(null);
        calculateTongTien();
    }

    public void calculateTongTien() {
        this.tongTien = chiTietHoaDons.stream()
                .map(ct -> ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalAmount() {
        return chiTietHoaDons.stream()
                .map(ct -> {
                    BigDecimal donGia = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                    Integer soLuong = ct.getSoLuong() != null ? ct.getSoLuong() : 0;
                    return donGia.multiply(BigDecimal.valueOf(soLuong));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return chiTietHoaDons.stream()
                .mapToInt(ct -> ct.getSoLuong() != null ? ct.getSoLuong() : 0)
                .sum();
    }

    public boolean hasItems() {
        return !chiTietHoaDons.isEmpty();
    }

    @PrePersist
    @PreUpdate
    private void updateTongTien() {
        calculateTongTien();
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "id=" + id +
                ", ngayLap=" + ngayLap +
                ", tongTien=" + tongTien +
                ", maHTTT=" + maHTTT +
                ", phiThanhToan=" + phiThanhToan +
                ", trangThaiThanhToan='" + trangThaiThanhToan + '\'' +
                ", khachHang=" + (maKH != null ? maKH.getHoTen() : "Khách lẻ") +
                ", nhanVien=" + (maNV != null ? maNV.getHoTen() : "null") +
                ", soLuongItems=" + chiTietHoaDons.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoaDon)) return false;
        HoaDon hoaDon = (HoaDon) o;
        return id != null && id.equals(hoaDon.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}