package service.impl;

import service.interfaces.IPhieuDoiTraService;
import dao.impl.PhieuDoiTraDAO;
import dao.impl.ChiTietPhieuDoiTraDAO;
import dao.impl.HoaDonDAO;
import dao.impl.BienTheSanPhamDAO;
import model.PhieuDoiTra;
import model.ChiTietPhieuDoiTra;
import model.HoaDon;
import model.BienTheSanPham;
import model.TaiKhoan;
import exception.BusinessException;
import exception.ValidationException;
import util.SessionManager;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service implementation for Returns & Exchanges business logic
 */
public class PhieuDoiTraServiceImpl implements IPhieuDoiTraService {
    
    private static final Logger logger = Logger.getLogger(PhieuDoiTraServiceImpl.class.getName());
    
    private final PhieuDoiTraDAO phieuDoiTraDAO;
    private final ChiTietPhieuDoiTraDAO chiTietDAO;
    private final HoaDonDAO hoaDonDAO;
    private final BienTheSanPhamDAO bienTheDAO;
    
    public PhieuDoiTraServiceImpl() {
        this.phieuDoiTraDAO = new PhieuDoiTraDAO();
        this.chiTietDAO = new ChiTietPhieuDoiTraDAO();
        this.hoaDonDAO = new HoaDonDAO();
        this.bienTheDAO = new BienTheSanPhamDAO();
    }
    
    @Override
    public PhieuDoiTra taoPhieuDoiTra(PhieuDoiTra phieu, List<ChiTietPhieuDoiTra> chiTietList) 
            throws BusinessException, ValidationException {
        try {
            // Validate dữ liệu
            validatePhieuDoiTra(phieu, chiTietList);
            
            // Kiểm tra hóa đơn
            if (!kiemTraHoaDonCoTheDoisTra(phieu.getMaHD())) {
                throw new BusinessException("Hóa đơn này không thể đổi trả");
            }
            
            // Thiết lập thông tin mặc định
            phieu.setNgayTao(LocalDateTime.now());
            TaiKhoan currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getMaNV() != null) {
                phieu.setMaNV(currentUser.getMaNV().getId());
            }
            phieu.setTrangThai("PENDING");
            
            // Tính toán giá trị
            tinhToanLaiGiaTri(phieu, chiTietList);
            
            // Lưu phiếu
            phieuDoiTraDAO.insert(phieu);
            
            // Lưu chi tiết
            for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                chiTiet.setMaPhieuDT(phieu.getMaPhieuDT());
                chiTiet.calculateThanhTien();
                chiTietDAO.insert(chiTiet);
            }
            
            logger.info("Tạo phiếu đổi trả thành công: " + phieu.getMaPhieuDT());
            return phieu;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo phiếu đổi trả", e);
            throw new BusinessException("Không thể tạo phiếu đổi trả: " + e.getMessage());
        }
    }
    
    @Override
    public void capNhatPhieuDoiTra(PhieuDoiTra phieu) throws BusinessException, ValidationException {
        try {
            // Kiểm tra phiếu có tồn tại
            Optional<PhieuDoiTra> existing = phieuDoiTraDAO.findById(phieu.getMaPhieuDT());
            if (!existing.isPresent()) {
                throw new BusinessException("Không tìm thấy phiếu đổi trả");
            }
            
            PhieuDoiTra currentPhieu = existing.get();
            
            // Chỉ cho phép cập nhật khi ở trạng thái PENDING
            if (!"PENDING".equals(currentPhieu.getTrangThai())) {
                throw new BusinessException("Chỉ có thể cập nhật phiếu ở trạng thái chờ xử lý");
            }
            
            // Validate dữ liệu
            List<String> errors = new ArrayList<>();
            if (phieu.getLyDo() == null || phieu.getLyDo().trim().isEmpty()) {
                errors.add("Lý do đổi trả không được để trống");
            }
            
            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }
            
            phieuDoiTraDAO.update(phieu);
            logger.info("Cập nhật phiếu đổi trả thành công: " + phieu.getMaPhieuDT());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật phiếu đổi trả", e);
            throw new BusinessException("Không thể cập nhật phiếu đổi trả: " + e.getMessage());
        }
    }
    
    @Override
    public void xoaPhieuDoiTra(int maPhieuDT) throws BusinessException {
        try {
            Optional<PhieuDoiTra> phieu = phieuDoiTraDAO.findById(maPhieuDT);
            if (!phieu.isPresent()) {
                throw new BusinessException("Không tìm thấy phiếu đổi trả");
            }
            
            // Chỉ cho phép xóa khi ở trạng thái PENDING
            if (!"PENDING".equals(phieu.get().getTrangThai())) {
                throw new BusinessException("Chỉ có thể xóa phiếu ở trạng thái chờ xử lý");
            }
            
            // Xóa chi tiết trước
            chiTietDAO.deleteByPhieuDoiTra(maPhieuDT);
            
            // Xóa phiếu
            phieuDoiTraDAO.delete(maPhieuDT);
            
            logger.info("Xóa phiếu đổi trả thành công: " + maPhieuDT);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa phiếu đổi trả", e);
            throw new BusinessException("Không thể xóa phiếu đổi trả: " + e.getMessage());
        }
    }
    
    @Override
    public void pheDuyetPhieu(int maPhieuDT, int nguoiDuyet, String ghiChu) throws BusinessException {
        try {
            Optional<PhieuDoiTra> phieuOpt = phieuDoiTraDAO.findById(maPhieuDT);
            if (!phieuOpt.isPresent()) {
                throw new BusinessException("Không tìm thấy phiếu đổi trả");
            }
            
            PhieuDoiTra phieu = phieuOpt.get();
            
            if (!"PENDING".equals(phieu.getTrangThai())) {
                throw new BusinessException("Chỉ có thể phê duyệt phiếu ở trạng thái chờ xử lý");
            }
            
            phieu.setTrangThai("APPROVED");
            phieu.setNguoiDuyet(nguoiDuyet);
            phieu.setNgayDuyet(LocalDateTime.now());
            if (ghiChu != null && !ghiChu.trim().isEmpty()) {
                phieu.setGhiChu(ghiChu);
            }
            
            phieuDoiTraDAO.update(phieu);
            
            logger.info("Phê duyệt phiếu đổi trả thành công: " + maPhieuDT);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi phê duyệt phiếu đổi trả", e);
            throw new BusinessException("Không thể phê duyệt phiếu đổi trả: " + e.getMessage());
        }
    }
    
    @Override
    public void tuChoiPhieu(int maPhieuDT, int nguoiDuyet, String lyDoTuChoi) throws BusinessException {
        try {
            Optional<PhieuDoiTra> phieuOpt = phieuDoiTraDAO.findById(maPhieuDT);
            if (!phieuOpt.isPresent()) {
                throw new BusinessException("Không tìm thấy phiếu đổi trả");
            }
            
            PhieuDoiTra phieu = phieuOpt.get();
            
            if (!"PENDING".equals(phieu.getTrangThai())) {
                throw new BusinessException("Chỉ có thể từ chối phiếu ở trạng thái chờ xử lý");
            }
            
            phieu.setTrangThai("REJECTED");
            phieu.setNguoiDuyet(nguoiDuyet);
            phieu.setNgayDuyet(LocalDateTime.now());
            phieu.setLyDoTuChoi(lyDoTuChoi);
            
            phieuDoiTraDAO.update(phieu);
            
            logger.info("Từ chối phiếu đổi trả thành công: " + maPhieuDT);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi từ chối phiếu đổi trả", e);
            throw new BusinessException("Không thể từ chối phiếu đổi trả: " + e.getMessage());
        }
    }
    
    @Override
    public void hoanThanhPhieu(int maPhieuDT, String hinhThucHoanTien, String soTaiKhoan, 
                              String tenChuTaiKhoan) throws BusinessException {
        try {
            Optional<PhieuDoiTra> phieuOpt = phieuDoiTraDAO.findById(maPhieuDT);
            if (!phieuOpt.isPresent()) {
                throw new BusinessException("Không tìm thấy phiếu đổi trả");
            }
            
            PhieuDoiTra phieu = phieuOpt.get();
            
            if (!"APPROVED".equals(phieu.getTrangThai())) {
                throw new BusinessException("Chỉ có thể hoàn thành phiếu đã được phê duyệt");
            }
            
            phieu.setTrangThai("COMPLETED");
            phieu.setHinhThucHoanTien(hinhThucHoanTien);
            phieu.setSoTaiKhoanHoan(soTaiKhoan);
            phieu.setTenChuTaiKhoan(tenChuTaiKhoan);
            phieu.setNgayHoanTien(LocalDateTime.now());
            
            phieuDoiTraDAO.update(phieu);
            
            // Cập nhật tồn kho nếu cần thiết
            capNhatTonKho(maPhieuDT);
            
            logger.info("Hoàn thành phiếu đổi trả thành công: " + maPhieuDT);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi hoàn thành phiếu đổi trả", e);
            throw new BusinessException("Không thể hoàn thành phiếu đổi trả: " + e.getMessage());
        }
    }
    
    @Override
    public List<PhieuDoiTra> layDanhSachPhieu() {
        return phieuDoiTraDAO.findAll();
    }
    
    @Override
    public Optional<PhieuDoiTra> layPhieuTheoId(int maPhieuDT) {
        return phieuDoiTraDAO.findById(maPhieuDT);
    }
    
    @Override
    public List<PhieuDoiTra> layPhieuTheoHoaDon(int maHD) {
        return phieuDoiTraDAO.findByHoaDon(maHD);
    }
    
    @Override
    public List<PhieuDoiTra> layPhieuTheoTrangThai(String trangThai) {
        return phieuDoiTraDAO.findByTrangThai(trangThai);
    }
    
    @Override
    public List<PhieuDoiTra> layPhieuTheoLoai(String loaiPhieu) {
        return phieuDoiTraDAO.findByLoaiPhieu(loaiPhieu);
    }
    
    @Override
    public boolean kiemTraHoaDonCoTheDoisTra(int maHD) throws BusinessException {
        try {
            HoaDon hoaDon = hoaDonDAO.findById(maHD);
            if (hoaDon == null) {
                return false;
            }
            
            // Kiểm tra hóa đơn có tồn tại và có chi tiết
            if (hoaDon.getChiTietHoaDons() == null || hoaDon.getChiTietHoaDons().isEmpty()) {
                return false;
            }
            
            // Kiểm tra thời hạn đổi trả (ví dụ: 30 ngày)
            LocalDate ngayMua = hoaDon.getNgayLap();
            LocalDate ngayHienTai = LocalDate.now();
            long soNgayDaQua = java.time.temporal.ChronoUnit.DAYS.between(ngayMua, ngayHienTai);
            
            return soNgayDaQua <= 30; // 30 ngày đổi trả
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra hóa đơn", e);
            throw new BusinessException("Không thể kiểm tra hóa đơn: " + e.getMessage());
        }
    }
    
    @Override
    public void tinhToanLaiGiaTri(PhieuDoiTra phieu, List<ChiTietPhieuDoiTra> chiTietList) {
        BigDecimal tongGiaTriTra = BigDecimal.ZERO;
        BigDecimal tongGiaTriDoi = BigDecimal.ZERO;
        
        for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
            chiTiet.calculateThanhTien();
            
            if ("TRA".equals(chiTiet.getLoaiChiTiet())) {
                tongGiaTriTra = tongGiaTriTra.add(chiTiet.getThanhTien());
            } else if ("DOI".equals(chiTiet.getLoaiChiTiet())) {
                tongGiaTriDoi = tongGiaTriDoi.add(chiTiet.getThanhTien());
            }
        }
        
        phieu.setTongGiaTriTra(tongGiaTriTra);
        phieu.setTongGiaTriDoi(tongGiaTriDoi);
        
        // Tính số tiền hoàn lại hoặc bổ sung
        BigDecimal chenhLech = tongGiaTriTra.subtract(tongGiaTriDoi);
        if (chenhLech.compareTo(BigDecimal.ZERO) > 0) {
            // Khách được hoàn tiền
            phieu.setSoTienHoanLai(chenhLech);
            phieu.setSoTienBoSung(BigDecimal.ZERO);
        } else if (chenhLech.compareTo(BigDecimal.ZERO) < 0) {
            // Khách cần trả thêm
            phieu.setSoTienHoanLai(BigDecimal.ZERO);
            phieu.setSoTienBoSung(chenhLech.negate());
        } else {
            // Bằng nhau
            phieu.setSoTienHoanLai(BigDecimal.ZERO);
            phieu.setSoTienBoSung(BigDecimal.ZERO);
        }
    }
    
    @Override
    public void validatePhieuDoiTra(PhieuDoiTra phieu, List<ChiTietPhieuDoiTra> chiTietList) 
            throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        // Validate phiếu
        if (phieu.getMaHD() <= 0) {
            errors.add("Mã hóa đơn không hợp lệ");
        }
        
        if (phieu.getLoaiPhieu() == null || 
            (!phieu.getLoaiPhieu().equals("DOI") && !phieu.getLoaiPhieu().equals("TRA"))) {
            errors.add("Loại phiếu phải là DOI hoặc TRA");
        }
        
        if (phieu.getLyDo() == null || phieu.getLyDo().trim().isEmpty()) {
            errors.add("Lý do đổi trả không được để trống");
        }
        
        if (phieu.getMaKH() != null && phieu.getMaKH() <= 0) {
            errors.add("Mã khách hàng không hợp lệ");
        }
        
        // Validate chi tiết
        if (chiTietList == null || chiTietList.isEmpty()) {
            errors.add("Phải có ít nhất một sản phẩm đổi trả");
        } else {
            for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                if (chiTiet.getMaBienThe() <= 0) {
                    errors.add("Mã biến thể sản phẩm không hợp lệ");
                }
                
                if (chiTiet.getSoLuong() <= 0) {
                    errors.add("Số lượng phải lớn hơn 0");
                }
                
                if (chiTiet.getDonGia() == null || chiTiet.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Đơn giá không hợp lệ");
                }
                
                if (chiTiet.getLoaiChiTiet() == null || 
                    (!chiTiet.getLoaiChiTiet().equals("DOI") && !chiTiet.getLoaiChiTiet().equals("TRA"))) {
                    errors.add("Loại chi tiết phải là DOI hoặc TRA");
                }
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
    
    /**
     * Cập nhật tồn kho khi hoàn thành đổi trả
     */
    private void capNhatTonKho(int maPhieuDT) throws BusinessException {
        try {
            List<ChiTietPhieuDoiTra> chiTietList = chiTietDAO.findByPhieuDoiTra(maPhieuDT);
            
            for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                BienTheSanPham bienThe = bienTheDAO.findById(chiTiet.getMaBienThe());
                if (bienThe != null) {
                    if ("TRA".equals(chiTiet.getLoaiChiTiet())) {
                        // Trả hàng: tăng tồn kho
                        bienThe.setSoLuong(bienThe.getSoLuong() + chiTiet.getSoLuong());
                    } else if ("DOI".equals(chiTiet.getLoaiChiTiet())) {
                        // Đổi hàng: giảm tồn kho
                        int soLuongConLai = bienThe.getSoLuong() - chiTiet.getSoLuong();
                        if (soLuongConLai < 0) {
                            throw new BusinessException("Không đủ tồn kho cho sản phẩm: " + chiTiet.getTenSanPham());
                        }
                        bienThe.setSoLuong(soLuongConLai);
                    }
                    
                    bienTheDAO.update(bienThe);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật tồn kho", e);
            throw new BusinessException("Không thể cập nhật tồn kho: " + e.getMessage());
        }
    }
}
