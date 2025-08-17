package model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietHoaDon", schema = "dbo")
public class ChiTietHoaDon {
    @EmbeddedId
    private ChiTietHoaDonId id;

    @MapsId("maBienThe")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaBienThe", nullable = false)
    private BienTheSanPham maBienThe;

    @MapsId("maHD")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaHD", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "DonGia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    // Constructors
    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, BienTheSanPham bienThe, Integer soLuong, BigDecimal donGia) {
        this.hoaDon = hoaDon;
        this.maBienThe = bienThe;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.id = new ChiTietHoaDonId(hoaDon.getId(), bienThe.getId());
    }

    // Getters and Setters
    public ChiTietHoaDonId getId() {
        return id;
    }

    public void setId(ChiTietHoaDonId id) {
        this.id = id;
    }

    public BienTheSanPham getMaBienThe() {
        return maBienThe;
    }

    public void setMaBienThe(BienTheSanPham maBienThe) {
        this.maBienThe = maBienThe;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    // Business methods
    public BigDecimal getThanhTien() {
        if (donGia != null && soLuong != null) {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + getThanhTien() +
                ", bienThe=" + (maBienThe != null ? maBienThe.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDon)) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}