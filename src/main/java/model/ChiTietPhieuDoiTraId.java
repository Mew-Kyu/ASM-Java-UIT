package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Key class for ChiTietPhieuDoiTra entity
 */
public class ChiTietPhieuDoiTraId implements Serializable {
    
    private int maPhieuDT;
    private int maBienThe;
    
    // Constructors
    public ChiTietPhieuDoiTraId() {}
    
    public ChiTietPhieuDoiTraId(int maPhieuDT, int maBienThe) {
        this.maPhieuDT = maPhieuDT;
        this.maBienThe = maBienThe;
    }
    
    // Getters and Setters
    public int getMaPhieuDT() {
        return maPhieuDT;
    }
    
    public void setMaPhieuDT(int maPhieuDT) {
        this.maPhieuDT = maPhieuDT;
    }
    
    public int getMaBienThe() {
        return maBienThe;
    }
    
    public void setMaBienThe(int maBienThe) {
        this.maBienThe = maBienThe;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChiTietPhieuDoiTraId that = (ChiTietPhieuDoiTraId) obj;
        return maPhieuDT == that.maPhieuDT && maBienThe == that.maBienThe;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maPhieuDT, maBienThe);
    }
    
    @Override
    public String toString() {
        return "ChiTietPhieuDoiTraId{maPhieuDT=" + maPhieuDT + ", maBienThe=" + maBienThe + "}";
    }
}
