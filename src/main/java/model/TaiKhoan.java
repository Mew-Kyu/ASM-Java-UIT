package model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaiKhoan", schema = "dbo")
public class TaiKhoan {
    @Id
    @Column(name = "TenDangNhap", nullable = false, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhau", length = 100)
    private String matKhau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNV")
    private NhanVien maNV;

    @Column(name = "Quyen", length = 20)
    private String quyen;

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public NhanVien getMaNV() {
        return maNV;
    }

    public void setMaNV(NhanVien maNV) {
        this.maNV = maNV;
    }

    public String getQuyen() {
        return quyen;
    }

    public void setQuyen(String quyen) {
        this.quyen = quyen;
    }

}