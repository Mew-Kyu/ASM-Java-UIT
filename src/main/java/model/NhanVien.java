package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "NhanVien", schema = "dbo")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNV", nullable = false)
    private Integer id;

    @Column(name = "HoTen", length = 100)
    private String hoTen;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "DienThoai", length = 20)
    private String dienThoai;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "ChucVu", length = 50)
    private String chucVu;

    @Column(name = "TrangThai")
    private Boolean trangThai;

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

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

}