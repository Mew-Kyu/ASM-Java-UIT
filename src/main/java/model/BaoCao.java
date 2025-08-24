package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class for Report entity
 * Represents various types of reports in the system
 */
public class BaoCao {
    private int maBaoCao;
    private String tenBaoCao;
    private String loaiBaoCao; // "DOANH_THU", "SAN_PHAM", "TON_KHO", "NHAN_VIEN"
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private String noiDung;
    private LocalDate ngayTao;
    private int nguoiTao; // MaNV
    private String trangThai; // "DRAFT", "COMPLETED", "ARCHIVED"
    
    // Constructors
    public BaoCao() {}
    
    public BaoCao(String tenBaoCao, String loaiBaoCao, LocalDate tuNgay, 
                  LocalDate denNgay, int nguoiTao) {
        this.tenBaoCao = tenBaoCao;
        this.loaiBaoCao = loaiBaoCao;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.nguoiTao = nguoiTao;
        this.ngayTao = LocalDate.now();
        this.trangThai = "DRAFT";
    }
    
    // Getters and Setters
    public int getMaBaoCao() {
        return maBaoCao;
    }
    
    public void setMaBaoCao(int maBaoCao) {
        this.maBaoCao = maBaoCao;
    }
    
    public String getTenBaoCao() {
        return tenBaoCao;
    }
    
    public void setTenBaoCao(String tenBaoCao) {
        this.tenBaoCao = tenBaoCao;
    }
    
    public String getLoaiBaoCao() {
        return loaiBaoCao;
    }
    
    public void setLoaiBaoCao(String loaiBaoCao) {
        this.loaiBaoCao = loaiBaoCao;
    }
    
    public LocalDate getTuNgay() {
        return tuNgay;
    }
    
    public void setTuNgay(LocalDate tuNgay) {
        this.tuNgay = tuNgay;
    }
    
    public LocalDate getDenNgay() {
        return denNgay;
    }
    
    public void setDenNgay(LocalDate denNgay) {
        this.denNgay = denNgay;
    }
    
    public String getNoiDung() {
        return noiDung;
    }
    
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
    
    public LocalDate getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public int getNguoiTao() {
        return nguoiTao;
    }
    
    public void setNguoiTao(int nguoiTao) {
        this.nguoiTao = nguoiTao;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    @Override
    public String toString() {
        return tenBaoCao + " (" + loaiBaoCao + ")";
    }
}
