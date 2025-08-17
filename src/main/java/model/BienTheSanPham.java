package model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "BienTheSanPham")
public class BienTheSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBienThe", nullable = false)
    private Integer id;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "GiaBan", precision = 18, scale = 2)
    private BigDecimal giaBan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public void setMaBienThe(Integer maBienThe) {
        this.setId(maBienThe);
    }

}