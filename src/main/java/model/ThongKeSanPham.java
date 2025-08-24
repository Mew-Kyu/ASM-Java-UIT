package model;

import java.math.BigDecimal;

/**
 * Model class for Product Statistics
 * Contains aggregated product sales data for reporting
 */
public class ThongKeSanPham {
    private int maSP;
    private String tenSP;
    private String tenDanhMuc;
    private int soLuongBan;
    private BigDecimal doanhThu;
    private int soLuongTon;
    private BigDecimal giaTriTon;
    private int soDonHang;
    private BigDecimal giaTriTrungBinh;
    
    // Constructors
    public ThongKeSanPham() {}
    
    public ThongKeSanPham(int maSP, String tenSP, String tenDanhMuc, 
                         int soLuongBan, BigDecimal doanhThu, int soLuongTon, 
                         BigDecimal giaTriTon, int soDonHang) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.tenDanhMuc = tenDanhMuc;
        this.soLuongBan = soLuongBan;
        this.doanhThu = doanhThu;
        this.soLuongTon = soLuongTon;
        this.giaTriTon = giaTriTon;
        this.soDonHang = soDonHang;
        
        if (soLuongBan > 0) {
            this.giaTriTrungBinh = doanhThu.divide(new BigDecimal(soLuongBan), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.giaTriTrungBinh = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public int getMaSP() {
        return maSP;
    }
    
    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }
    
    public String getTenSP() {
        return tenSP;
    }
    
    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }
    
    public String getTenDanhMuc() {
        return tenDanhMuc;
    }
    
    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }
    
    public int getSoLuongBan() {
        return soLuongBan;
    }
    
    public void setSoLuongBan(int soLuongBan) {
        this.soLuongBan = soLuongBan;
        updateGiaTriTrungBinh();
    }
    
    public BigDecimal getDoanhThu() {
        return doanhThu;
    }
    
    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu;
        updateGiaTriTrungBinh();
    }
    
    public int getSoLuongTon() {
        return soLuongTon;
    }
    
    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
    
    public BigDecimal getGiaTriTon() {
        return giaTriTon;
    }
    
    public void setGiaTriTon(BigDecimal giaTriTon) {
        this.giaTriTon = giaTriTon;
    }
    
    public int getSoDonHang() {
        return soDonHang;
    }
    
    public void setSoDonHang(int soDonHang) {
        this.soDonHang = soDonHang;
    }
    
    public BigDecimal getGiaTriTrungBinh() {
        return giaTriTrungBinh;
    }
    
    private void updateGiaTriTrungBinh() {
        if (soLuongBan > 0 && doanhThu != null) {
            this.giaTriTrungBinh = doanhThu.divide(new BigDecimal(soLuongBan), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.giaTriTrungBinh = BigDecimal.ZERO;
        }
    }
}
