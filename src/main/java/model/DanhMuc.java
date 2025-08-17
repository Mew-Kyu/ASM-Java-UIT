package model;

import jakarta.persistence.*;

@Entity
@Table(name = "DanhMuc", schema = "dbo")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDM", nullable = false)
    private Integer id;

    @Column(name = "TenDM", length = 100)
    private String tenDM;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenDM() {
        return tenDM;
    }

    public void setTenDM(String tenDM) {
        this.tenDM = tenDM;
    }

    @Override
    public String toString() {
        return tenDM != null ? tenDM : "N/A";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DanhMuc danhMuc = (DanhMuc) obj;
        return id != null && id.equals(danhMuc.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}