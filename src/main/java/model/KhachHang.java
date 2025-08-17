package model;

import jakarta.persistence.*;

@Entity
@Table(name = "KhachHang", schema = "dbo")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKH", nullable = false)
    private Integer id;

    @Column(name = "HoTen", length = 100)
    private String hoTen;

    @Column(name = "DienThoai", length = 20)
    private String dienThoai;

    @Column(name = "DiaChi", length = 200)
    private String diaChi;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

}