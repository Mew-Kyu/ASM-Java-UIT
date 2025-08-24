package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class for Revenue Statistics
 * Contains aggregated revenue data for reporting
 */
public class ThongKeDoanhThu {
    private LocalDate ngay;
    private BigDecimal doanhThu;
    private int soLuongDonHang;
    private int soLuongSanPham;
    private BigDecimal doanhThuTrungBinh;
    private String khoangThoiGian; // "DAY", "WEEK", "MONTH", "YEAR"
    
    // Constructors
    public ThongKeDoanhThu() {}
    
    public ThongKeDoanhThu(LocalDate ngay, BigDecimal doanhThu, int soLuongDonHang, 
                          int soLuongSanPham, String khoangThoiGian) {
        this.ngay = ngay;
        this.doanhThu = doanhThu;
        this.soLuongDonHang = soLuongDonHang;
        this.soLuongSanPham = soLuongSanPham;
        this.khoangThoiGian = khoangThoiGian;
        
        if (soLuongDonHang > 0) {
            this.doanhThuTrungBinh = doanhThu.divide(new BigDecimal(soLuongDonHang), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.doanhThuTrungBinh = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public LocalDate getNgay() {
        return ngay;
    }
    
    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }
    
    public BigDecimal getDoanhThu() {
        return doanhThu;
    }
    
    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu;
        updateDoanhThuTrungBinh();
    }
    
    public int getSoLuongDonHang() {
        return soLuongDonHang;
    }
    
    public void setSoLuongDonHang(int soLuongDonHang) {
        this.soLuongDonHang = soLuongDonHang;
        updateDoanhThuTrungBinh();
    }
    
    public int getSoLuongSanPham() {
        return soLuongSanPham;
    }
    
    public void setSoLuongSanPham(int soLuongSanPham) {
        this.soLuongSanPham = soLuongSanPham;
    }
    
    public BigDecimal getDoanhThuTrungBinh() {
        return doanhThuTrungBinh;
    }
    
    public String getKhoangThoiGian() {
        return khoangThoiGian;
    }
    
    public void setKhoangThoiGian(String khoangThoiGian) {
        this.khoangThoiGian = khoangThoiGian;
    }
    
    private void updateDoanhThuTrungBinh() {
        if (soLuongDonHang > 0 && doanhThu != null) {
            this.doanhThuTrungBinh = doanhThu.divide(new BigDecimal(soLuongDonHang), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.doanhThuTrungBinh = BigDecimal.ZERO;
        }
    }
}
