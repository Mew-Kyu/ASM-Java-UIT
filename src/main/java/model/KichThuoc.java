package model;

import jakarta.persistence.*;

@Entity
@Table(name = "KichThuoc", schema = "dbo")
public class KichThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSize", nullable = false)
    private Integer id;

    @Column(name = "TenSize", length = 10)
    private String tenSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenSize() {
        return tenSize;
    }

    public void setTenSize(String tenSize) {
        this.tenSize = tenSize;
    }

    @Override
    public String toString() {
        return tenSize != null ? tenSize : "Chưa có kích thước";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KichThuoc kichThuoc = (KichThuoc) obj;
        return id != null && id.equals(kichThuoc.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}