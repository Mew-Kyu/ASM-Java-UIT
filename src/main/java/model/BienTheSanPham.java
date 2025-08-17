package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "BienTheSanPham")
public class BienTheSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBienThe", nullable = false)
    private Integer id;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong = 0;

    @Column(name = "GiaBan", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham maSP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSize", nullable = false)
    private KichThuoc maSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaMau", nullable = false)
    private MauSac maMau;

    @OneToMany(mappedBy = "maBienThe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChiTietHoaDon> chiTietHoaDons = new LinkedHashSet<>();

    // Constructors
    public BienTheSanPham() {
    }

    public BienTheSanPham(Integer soLuong, BigDecimal giaBan, SanPham maSP, KichThuoc maSize, MauSac maMau) {
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.maSP = maSP;
        this.maSize = maSize;
        this.maMau = maMau;
    }

    // Getters and Setters
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

    public SanPham getMaSP() {
        return maSP;
    }

    public void setMaSP(SanPham maSP) {
        this.maSP = maSP;
    }

    public KichThuoc getMaSize() {
        return maSize;
    }

    public void setMaSize(KichThuoc maSize) {
        this.maSize = maSize;
    }

    public MauSac getMaMau() {
        return maMau;
    }

    public void setMaMau(MauSac maMau) {
        this.maMau = maMau;
    }

    public Set<ChiTietHoaDon> getChiTietHoaDons() {
        return chiTietHoaDons;
    }

    public void setChiTietHoaDons(Set<ChiTietHoaDon> chiTietHoaDons) {
        this.chiTietHoaDons = chiTietHoaDons;
    }

    // Helper method for backward compatibility
    public void setMaBienThe(Integer maBienThe) {
        this.setId(maBienThe);
    }

    // Business methods
    public void addChiTietHoaDon(ChiTietHoaDon chiTiet) {
        chiTietHoaDons.add(chiTiet);
        chiTiet.setMaBienThe(this);
    }

    public void removeChiTietHoaDon(ChiTietHoaDon chiTiet) {
        chiTietHoaDons.remove(chiTiet);
        chiTiet.setMaBienThe(null);
    }

    public boolean isAvailable(Integer requestedQuantity) {
        return soLuong != null && soLuong >= requestedQuantity;
    }

    public void decreaseStock(Integer quantity) {
        if (soLuong != null && soLuong >= quantity) {
            this.soLuong -= quantity;
        } else {
            throw new IllegalArgumentException("Không đủ số lượng trong kho");
        }
    }

    public void increaseStock(Integer quantity) {
        if (soLuong == null) {
            this.soLuong = quantity;
        } else {
            this.soLuong += quantity;
        }
    }

    @Override
    public String toString() {
        return "BienTheSanPham{" +
                "id=" + id +
                ", soLuong=" + soLuong +
                ", giaBan=" + giaBan +
                ", sanPham=" + (maSP != null ? maSP.getTenSP() : "null") +
                ", size=" + (maSize != null ? maSize.getTenSize() : "null") +
                ", mauSac=" + (maMau != null ? maMau.getTenMau() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BienTheSanPham that = (BienTheSanPham) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}