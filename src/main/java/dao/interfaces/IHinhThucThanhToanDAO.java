package dao.interfaces;

import model.HinhThucThanhToan;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Payment Method operations
 */
public interface IHinhThucThanhToanDAO {
    void insert(HinhThucThanhToan hinhThucThanhToan);
    void update(HinhThucThanhToan hinhThucThanhToan);
    void delete(int maHTTT);
    Optional<HinhThucThanhToan> findById(int maHTTT);
    List<HinhThucThanhToan> findAll();
    List<HinhThucThanhToan> findByTrangThai(boolean trangThai);
    List<HinhThucThanhToan> findByLoaiThanhToan(String loaiThanhToan);
}
