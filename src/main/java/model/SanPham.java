package model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SanPham", schema = "dbo")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSP", nullable = false)
    private Integer id;

    @Column(name = "TenSP", length = 100)
    private String tenSP;

    @Column(name = "Gia", precision = 18, scale = 2)
    private BigDecimal gia;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDM")
    private DanhMuc maDM;

    @Column(name = "MoTa", length = 200)
    private String moTa;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public BigDecimal getGia() {
        return gia;
    }

    public void setGia(BigDecimal gia) {
        this.gia = gia;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    // Add convenient methods with consistent naming for UI
    public DanhMuc getDanhMuc() {
        return maDM;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.maDM = danhMuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return tenSP != null ? tenSP : "Chưa có tên";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SanPham sanPham = (SanPham) obj;
        return id != null && id.equals(sanPham.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}