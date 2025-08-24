package service.impl;

import service.interfaces.IBaoCaoService;
import dao.interfaces.IBaoCaoDAO;
import model.BaoCao;
import model.ThongKeDoanhThu;
import model.ThongKeSanPham;
import exception.BusinessException;
import exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service implementation for Report business logic
 */
public class BaoCaoServiceImpl implements IBaoCaoService {
    
    private static final Logger logger = Logger.getLogger(BaoCaoServiceImpl.class.getName());
    private final IBaoCaoDAO baoCaoDAO;
    
    public BaoCaoServiceImpl(IBaoCaoDAO baoCaoDAO) {
        this.baoCaoDAO = baoCaoDAO;
    }
    
    @Override
    public void taoBaoCao(BaoCao baoCao) throws ValidationException, BusinessException {
        try {
            // Validation
            validateBaoCao(baoCao);
            
            // Set default values
            if (baoCao.getNgayTao() == null) {
                baoCao.setNgayTao(LocalDate.now());
            }
            if (baoCao.getTrangThai() == null) {
                baoCao.setTrangThai("DRAFT");
            }
            
            baoCaoDAO.insert(baoCao);
            logger.info("Created new report: " + baoCao.getTenBaoCao());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating report", e);
            throw new BusinessException("Không thể tạo báo cáo: " + e.getMessage());
        }
    }
    
    @Override
    public void capNhatBaoCao(BaoCao baoCao) throws ValidationException, BusinessException {
        try {
            validateBaoCao(baoCao);
            
            // Check if report exists
            if (!baoCaoDAO.findById(baoCao.getMaBaoCao()).isPresent()) {
                throw new BusinessException("Báo cáo không tồn tại");
            }
            
            baoCaoDAO.update(baoCao);
            logger.info("Updated report: " + baoCao.getTenBaoCao());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating report", e);
            throw new BusinessException("Không thể cập nhật báo cáo: " + e.getMessage());
        }
    }
    
    @Override
    public void xoaBaoCao(int maBaoCao) throws BusinessException {
        try {
            if (!baoCaoDAO.findById(maBaoCao).isPresent()) {
                throw new BusinessException("Báo cáo không tồn tại");
            }
            
            baoCaoDAO.delete(maBaoCao);
            logger.info("Deleted report with ID: " + maBaoCao);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting report", e);
            throw new BusinessException("Không thể xóa báo cáo: " + e.getMessage());
        }
    }
    
    @Override
    public BaoCao layBaoCaoTheoId(int maBaoCao) throws BusinessException {
        try {
            return baoCaoDAO.findById(maBaoCao)
                    .orElseThrow(() -> new BusinessException("Báo cáo không tồn tại"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting report by ID", e);
            throw new BusinessException("Không thể lấy thông tin báo cáo: " + e.getMessage());
        }
    }
    
    @Override
    public List<BaoCao> layTatCaBaoCao() {
        try {
            return baoCaoDAO.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting all reports", e);
            return List.of();
        }
    }
    
    @Override
    public List<BaoCao> layBaoCaoTheoLoai(String loaiBaoCao) {
        try {
            return baoCaoDAO.findByLoaiBaoCao(loaiBaoCao);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting reports by type", e);
            return List.of();
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> thongKeDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getDoanhThuTheoNgay(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting daily revenue statistics", e);
            throw new BusinessException("Không thể lấy thống kê doanh thu theo ngày: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> thongKeDoanhThuTheoTuan(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getDoanhThuTheoTuan(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting weekly revenue statistics", e);
            throw new BusinessException("Không thể lấy thống kê doanh thu theo tuần: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> thongKeDoanhThuTheoThang(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getDoanhThuTheoThang(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting monthly revenue statistics", e);
            throw new BusinessException("Không thể lấy thống kê doanh thu theo tháng: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeDoanhThu> thongKeDoanhThuTheoNam(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getDoanhThuTheoNam(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting yearly revenue statistics", e);
            throw new BusinessException("Không thể lấy thống kê doanh thu theo năm: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeSanPham> layTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int top) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        if (top <= 0) {
            throw new BusinessException("Số lượng top phải lớn hơn 0");
        }
        try {
            return baoCaoDAO.getTopSanPhamBanChay(tuNgay, denNgay, top);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting top selling products", e);
            throw new BusinessException("Không thể lấy danh sách sản phẩm bán chạy: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeSanPham> laySanPhamBanCham(LocalDate tuNgay, LocalDate denNgay, int top) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        if (top <= 0) {
            throw new BusinessException("Số lượng top phải lớn hơn 0");
        }
        try {
            return baoCaoDAO.getSanPhamBanCham(tuNgay, denNgay, top);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting slow selling products", e);
            throw new BusinessException("Không thể lấy danh sách sản phẩm bán chậm: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeSanPham> laySanPhamTonKho() throws BusinessException {
        try {
            return baoCaoDAO.getSanPhamTonKho();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting inventory products", e);
            throw new BusinessException("Không thể lấy danh sách sản phẩm tồn kho: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeSanPham> laySanPhamSapHet(int soLuongToiThieu) throws BusinessException {
        if (soLuongToiThieu < 0) {
            throw new BusinessException("Số lượng tối thiểu phải >= 0");
        }
        try {
            return baoCaoDAO.getSanPhamSapHet(soLuongToiThieu);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting low stock products", e);
            throw new BusinessException("Không thể lấy danh sách sản phẩm sắp hết: " + e.getMessage());
        }
    }
    
    @Override
    public List<ThongKeSanPham> thongKeTheoLoaiSanPham(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getThongKeTheoDanhMuc(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting category statistics", e);
            throw new BusinessException("Không thể lấy thống kê theo loại sản phẩm: " + e.getMessage());
        }
    }
    
    @Override
    public List<Object[]> thongKeHieuSuatNhanVien(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getHieuSuatNhanVien(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting employee performance", e);
            throw new BusinessException("Không thể lấy thống kê hiệu suất nhân viên: " + e.getMessage());
        }
    }
    
    @Override
    public BigDecimal tinhTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getTongDoanhThu(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating total revenue", e);
            throw new BusinessException("Không thể tính tổng doanh thu: " + e.getMessage());
        }
    }
    
    @Override
    public int demSoDonHang(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getTongSoDonHang(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting total orders", e);
            throw new BusinessException("Không thể đếm số đơn hàng: " + e.getMessage());
        }
    }
    
    @Override
    public int demSoSanPhamBan(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getTongSoSanPhamBan(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting products sold", e);
            throw new BusinessException("Không thể đếm số sản phẩm bán: " + e.getMessage());
        }
    }
    
    @Override
    public BigDecimal tinhDoanhThuTrungBinhTheoNgay(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getDoanhThuTrungBinhTheoNgay(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating average daily revenue", e);
            throw new BusinessException("Không thể tính doanh thu trung bình theo ngày: " + e.getMessage());
        }
    }
    
    @Override
    public BigDecimal tinhTongGiaTriTonKho() throws BusinessException {
        try {
            return baoCaoDAO.getTongGiaTriTonKho();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating total inventory value", e);
            throw new BusinessException("Không thể tính tổng giá trị tồn kho: " + e.getMessage());
        }
    }
    
    @Override
    public int demTongSoLuongTonKho() throws BusinessException {
        try {
            return baoCaoDAO.getTongSoLuongTonKho();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting total inventory quantity", e);
            throw new BusinessException("Không thể đếm tổng số lượng tồn kho: " + e.getMessage());
        }
    }
    
    @Override
    public int demSoKhachHangMoi(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            return baoCaoDAO.getSoKhachHangMoi(tuNgay, denNgay);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting new customers", e);
            throw new BusinessException("Không thể đếm số khách hàng mới: " + e.getMessage());
        }
    }
    
    @Override
    public List<Object[]> layTopKhachHang(LocalDate tuNgay, LocalDate denNgay, int top) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        if (top <= 0) {
            throw new BusinessException("Số lượng top phải lớn hơn 0");
        }
        try {
            return baoCaoDAO.getTopKhachHang(tuNgay, denNgay, top);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting top customers", e);
            throw new BusinessException("Không thể lấy danh sách khách hàng VIP: " + e.getMessage());
        }
    }
    
    @Override
    public String taoBaoCaoDoanhThu(LocalDate tuNgay, LocalDate denNgay, String loaiThoiGian) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            StringBuilder sb = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            sb.append("BÁO CÁO DOANH THU\n");
            sb.append("Từ ngày: ").append(tuNgay.format(formatter));
            sb.append(" đến ngày: ").append(denNgay.format(formatter)).append("\n\n");
            
            BigDecimal tongDoanhThu = tinhTongDoanhThu(tuNgay, denNgay);
            int tongDonHang = demSoDonHang(tuNgay, denNgay);
            int tongSanPham = demSoSanPhamBan(tuNgay, denNgay);
            BigDecimal doanhThuTB = tinhDoanhThuTrungBinhTheoNgay(tuNgay, denNgay);
            
            sb.append("TỔNG QUAN:\n");
            sb.append("- Tổng doanh thu: ").append(formatCurrency(tongDoanhThu)).append("\n");
            sb.append("- Tổng số đơn hàng: ").append(tongDonHang).append("\n");
            sb.append("- Tổng số sản phẩm bán: ").append(tongSanPham).append("\n");
            sb.append("- Doanh thu trung bình/ngày: ").append(formatCurrency(doanhThuTB)).append("\n");
            
            return sb.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating revenue report", e);
            throw new BusinessException("Không thể tạo báo cáo doanh thu: " + e.getMessage());
        }
    }
    
    @Override
    public String taoBaoCaoSanPham(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            StringBuilder sb = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            sb.append("BÁO CÁO SẢN PHẨM\n");
            sb.append("Từ ngày: ").append(tuNgay.format(formatter));
            sb.append(" đến ngày: ").append(denNgay.format(formatter)).append("\n\n");
            
            // Top sản phẩm bán chạy
            List<ThongKeSanPham> topBanChay = layTopSanPhamBanChay(tuNgay, denNgay, 10);
            sb.append("TOP 10 SẢN PHẨM BÁN CHẠY:\n");
            for (int i = 0; i < topBanChay.size(); i++) {
                ThongKeSanPham sp = topBanChay.get(i);
                sb.append((i + 1)).append(". ").append(sp.getTenSP());
                sb.append(" - Số lượng bán: ").append(sp.getSoLuongBan());
                sb.append(" - Doanh thu: ").append(formatCurrency(sp.getDoanhThu())).append("\n");
            }
            sb.append("\n");
            
            // Sản phẩm sắp hết
            List<ThongKeSanPham> sapHet = laySanPhamSapHet(10);
            sb.append("SẢN PHẨM SẮP HẾT HÀNG (dưới 10 sản phẩm):\n");
            for (ThongKeSanPham sp : sapHet) {
                sb.append("- ").append(sp.getTenSP());
                sb.append(" - Tồn kho: ").append(sp.getSoLuongTon()).append("\n");
            }
            
            return sb.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating product report", e);
            throw new BusinessException("Không thể tạo báo cáo sản phẩm: " + e.getMessage());
        }
    }
    
    @Override
    public String taoBaoCaoTonKho() throws BusinessException {
        try {
            StringBuilder sb = new StringBuilder();
            
            sb.append("BÁO CÁO TỒN KHO\n");
            sb.append("Ngày tạo: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
            
            BigDecimal tongGiaTri = tinhTongGiaTriTonKho();
            int tongSoLuong = demTongSoLuongTonKho();
            
            sb.append("TỔNG QUAN TỒN KHO:\n");
            sb.append("- Tổng giá trị tồn kho: ").append(formatCurrency(tongGiaTri)).append("\n");
            sb.append("- Tổng số lượng tồn kho: ").append(tongSoLuong).append(" sản phẩm\n\n");
            
            // Chi tiết tồn kho
            List<ThongKeSanPham> tonKho = laySanPhamTonKho();
            sb.append("CHI TIẾT TỒN KHO:\n");
            for (ThongKeSanPham sp : tonKho) {
                sb.append("- ").append(sp.getTenSP());
                sb.append(" (").append(sp.getTenDanhMuc()).append(")");
                sb.append(" - Số lượng: ").append(sp.getSoLuongTon());
                sb.append(" - Giá trị: ").append(formatCurrency(sp.getGiaTriTon())).append("\n");
            }
            
            return sb.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating inventory report", e);
            throw new BusinessException("Không thể tạo báo cáo tồn kho: " + e.getMessage());
        }
    }
    
    @Override
    public String taoBaoCaoNhanVien(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        validateDateRange(tuNgay, denNgay);
        try {
            StringBuilder sb = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            sb.append("BÁO CÁO HIỆU SUẤT NHÂN VIÊN\n");
            sb.append("Từ ngày: ").append(tuNgay.format(formatter));
            sb.append(" đến ngày: ").append(denNgay.format(formatter)).append("\n\n");
            
            List<Object[]> hieuSuat = thongKeHieuSuatNhanVien(tuNgay, denNgay);
            sb.append("HIỆU SUẤT NHÂN VIÊN:\n");
            for (Object[] row : hieuSuat) {
                String tenNV = (String) row[1];
                int soDonHang = ((Number) row[2]).intValue();
                BigDecimal doanhThu = (BigDecimal) row[3];
                int soSanPham = ((Number) row[4]).intValue();
                BigDecimal doanhThuTB = (BigDecimal) row[5];
                
                sb.append("- ").append(tenNV).append(":\n");
                sb.append("  + Số đơn hàng: ").append(soDonHang).append("\n");
                sb.append("  + Doanh thu: ").append(formatCurrency(doanhThu)).append("\n");
                sb.append("  + Số sản phẩm bán: ").append(soSanPham).append("\n");
                sb.append("  + Doanh thu TB/đơn: ").append(formatCurrency(doanhThuTB)).append("\n\n");
            }
            
            return sb.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating employee report", e);
            throw new BusinessException("Không thể tạo báo cáo nhân viên: " + e.getMessage());
        }
    }
    
    @Override
    public void xuatBaoCaoExcel(BaoCao baoCao, String filePath) throws BusinessException {
        // TODO: Implement Excel export functionality
        throw new BusinessException("Chức năng xuất Excel chưa được triển khai");
    }
    
    @Override
    public void xuatBaoCaoPDF(BaoCao baoCao, String filePath) throws BusinessException {
        // TODO: Implement PDF export functionality  
        throw new BusinessException("Chức năng xuất PDF chưa được triển khai");
    }
    
    // Helper methods
    private void validateBaoCao(BaoCao baoCao) throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        if (baoCao == null) {
            errors.add("Báo cáo không được null");
        } else {
            if (baoCao.getTenBaoCao() == null || baoCao.getTenBaoCao().trim().isEmpty()) {
                errors.add("Tên báo cáo không được trống");
            }
            if (baoCao.getLoaiBaoCao() == null || baoCao.getLoaiBaoCao().trim().isEmpty()) {
                errors.add("Loại báo cáo không được trống");
            }
            if (baoCao.getTuNgay() != null && baoCao.getDenNgay() != null) {
                if (baoCao.getTuNgay().isAfter(baoCao.getDenNgay())) {
                    errors.add("Từ ngày phải nhỏ hơn hoặc bằng đến ngày");
                }
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
    
    private void validateDateRange(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        if (tuNgay == null || denNgay == null) {
            throw new BusinessException("Ngày bắt đầu và ngày kết thúc không được null");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new BusinessException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }
        if (tuNgay.isAfter(LocalDate.now())) {
            throw new BusinessException("Ngày bắt đầu không được ở tương lai");
        }
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return String.format("%,.0f VNĐ", amount);
    }
}
