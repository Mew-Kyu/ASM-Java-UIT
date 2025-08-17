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

}