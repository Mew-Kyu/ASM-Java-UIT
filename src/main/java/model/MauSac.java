package model;

import jakarta.persistence.*;

@Entity
@Table(name = "MauSac", schema = "dbo")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaMau", nullable = false)
    private Integer id;

    @Column(name = "TenMau", length = 50)
    private String tenMau;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenMau() {
        return tenMau;
    }

    public void setTenMau(String tenMau) {
        this.tenMau = tenMau;
    }

    @Override
    public String toString() {
        return tenMau != null ? tenMau : "Chưa có tên màu";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MauSac mauSac = (MauSac) obj;
        return id != null && id.equals(mauSac.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}