package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Key class for ChiTietDatHang entity
 */
public class ChiTietDatHangId implements Serializable {
    
    private int maDDH;
    private int maSP;
    
    // Constructors
    public ChiTietDatHangId() {}
    
    public ChiTietDatHangId(int maDDH, int maSP) {
        this.maDDH = maDDH;
        this.maSP = maSP;
    }
    
    // Getters and Setters
    public int getMaDDH() {
        return maDDH;
    }
    
    public void setMaDDH(int maDDH) {
        this.maDDH = maDDH;
    }
    
    public int getMaSP() {
        return maSP;
    }
    
    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChiTietDatHangId that = (ChiTietDatHangId) obj;
        return maDDH == that.maDDH && maSP == that.maSP;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maDDH, maSP);
    }
    
    @Override
    public String toString() {
        return "ChiTietDatHangId{maDDH=" + maDDH + ", maSP=" + maSP + "}";
    }
}
