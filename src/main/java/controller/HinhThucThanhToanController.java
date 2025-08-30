package controller;

import dao.interfaces.IHinhThucThanhToanDAO;
import di.ServiceContainer;
import model.HinhThucThanhToan;

import java.util.List;
import java.util.Optional;

public class HinhThucThanhToanController {
    private IHinhThucThanhToanDAO hinhThucThanhToanDAO;

    public HinhThucThanhToanController() {
        try {
            this.hinhThucThanhToanDAO = ServiceContainer.getInstance().getService(IHinhThucThanhToanDAO.class);
        } catch (Exception e) {
            throw new RuntimeException("Không thể khởi tạo HinhThucThanhToanController: " + e.getMessage(), e);
        }
    }

    public List<HinhThucThanhToan> getAllHinhThucThanhToan() {
        try {
            return hinhThucThanhToanDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hình thức thanh toán: " + e.getMessage(), e);
        }
    }

    public List<HinhThucThanhToan> getActiveHinhThucThanhToan() {
        try {
            return hinhThucThanhToanDAO.findByTrangThai(true);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hình thức thanh toán hoạt động: " + e.getMessage(), e);
        }
    }

    public HinhThucThanhToan getHinhThucThanhToanById(int id) {
        try {
            Optional<HinhThucThanhToan> result = hinhThucThanhToanDAO.findById(id);
            return result.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm hình thức thanh toán: " + e.getMessage(), e);
        }
    }

    public void addHinhThucThanhToan(HinhThucThanhToan hinhThucThanhToan) {
        try {
            hinhThucThanhToanDAO.insert(hinhThucThanhToan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm hình thức thanh toán: " + e.getMessage(), e);
        }
    }

    public void updateHinhThucThanhToan(HinhThucThanhToan hinhThucThanhToan) {
        try {
            hinhThucThanhToanDAO.update(hinhThucThanhToan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật hình thức thanh toán: " + e.getMessage(), e);
        }
    }

    public void deleteHinhThucThanhToan(int id) {
        try {
            hinhThucThanhToanDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa hình thức thanh toán: " + e.getMessage(), e);
        }
    }

    public List<HinhThucThanhToan> findByLoaiThanhToan(String loaiThanhToan) {
        try {
            return hinhThucThanhToanDAO.findByLoaiThanhToan(loaiThanhToan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm hình thức thanh toán theo loại: " + e.getMessage(), e);
        }
    }
}
