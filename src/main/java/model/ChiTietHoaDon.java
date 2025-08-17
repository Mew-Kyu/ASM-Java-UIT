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

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia", precision = 18, scale = 2)
    private BigDecimal donGia;

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

}