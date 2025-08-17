package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietHoaDonId implements Serializable {
    private static final long serialVersionUID = 2302950810416877977L;
    @Column(name = "MaHD", nullable = false)
    private Integer maHD;

    @Column(name = "MaBienThe", nullable = false)
    private Integer maBienThe;

    public ChiTietHoaDonId() {
    }

    public ChiTietHoaDonId(Integer maHD, Integer maBienThe) {
        this.maHD = maHD;
        this.maBienThe = maBienThe;
    }

    public Integer getMaHD() {
        return maHD;
    }

    public void setMaHD(Integer maHD) {
        this.maHD = maHD;
    }

    public Integer getMaBienThe() {
        return maBienThe;
    }

    public void setMaBienThe(Integer maBienThe) {
        this.maBienThe = maBienThe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDonId entity = (ChiTietHoaDonId) o;
        return Objects.equals(this.maBienThe, entity.maBienThe) &&
                Objects.equals(this.maHD, entity.maHD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maBienThe, maHD);
    }

}