package service.interfaces;

import exception.BusinessException;
import exception.ValidationException;
import model.PhieuDoiTra;
import model.ChiTietPhieuDoiTra;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Service interface for Returns & Exchanges business logic
 */
public interface IPhieuDoiTraService {
    
    /**
     * Tạo phiếu đổi trả mới
     */
    PhieuDoiTra taoPhieuDoiTra(PhieuDoiTra phieu, List<ChiTietPhieuDoiTra> chiTietList) 
            throws BusinessException, ValidationException;
    
    /**
     * Cập nhật phiếu đổi trả
     */
    void capNhatPhieuDoiTra(PhieuDoiTra phieu) throws BusinessException, ValidationException;
    
    /**
     * Xóa phiếu đổi trả (chỉ khi ở trạng thái PENDING)
     */
    void xoaPhieuDoiTra(int maPhieuDT) throws BusinessException;
    
    /**
     * Phê duyệt phiếu đổi trả
     */
    void pheDuyetPhieu(int maPhieuDT, int nguoiDuyet, String ghiChu) throws BusinessException;
    
    /**
     * Từ chối phiếu đổi trả
     */
    void tuChoiPhieu(int maPhieuDT, int nguoiDuyet, String lyDoTuChoi) throws BusinessException;
    
    /**
     * Hoàn thành xử lý phiếu đổi trả
     */
    void hoanThanhPhieu(int maPhieuDT, String hinhThucHoanTien, String soTaiKhoan, 
                       String tenChuTaiKhoan) throws BusinessException;
    
    /**
     * Lấy danh sách tất cả phiếu
     */
    List<PhieuDoiTra> layDanhSachPhieu();
    
    /**
     * Lấy phiếu theo ID
     */
    Optional<PhieuDoiTra> layPhieuTheoId(int maPhieuDT);
    
    /**
     * Lấy phiếu theo hóa đơn
     */
    List<PhieuDoiTra> layPhieuTheoHoaDon(int maHD);
    
    /**
     * Lấy phiếu theo trạng thái
     */
    List<PhieuDoiTra> layPhieuTheoTrangThai(String trangThai);
    
    /**
     * Lấy phiếu theo loại (DOI/TRA)
     */
    List<PhieuDoiTra> layPhieuTheoLoai(String loaiPhieu);
    
    /**
     * Kiểm tra hóa đơn có thể đổi trả không
     */
    boolean kiemTraHoaDonCoTheDoisTra(int maHD) throws BusinessException;
    
    /**
     * Tính toán lại giá trị phiếu đổi trả
     */
    void tinhToanLaiGiaTri(PhieuDoiTra phieu, List<ChiTietPhieuDoiTra> chiTietList);
    
    /**
     * Validate dữ liệu phiếu đổi trả
     */
    void validatePhieuDoiTra(PhieuDoiTra phieu, List<ChiTietPhieuDoiTra> chiTietList) 
            throws ValidationException;
}
