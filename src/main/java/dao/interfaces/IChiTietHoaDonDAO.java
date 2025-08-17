package dao.interfaces;

import model.ChiTietHoaDon;
import model.ChiTietHoaDonId;

import java.util.List;

public interface IChiTietHoaDonDAO {
    void insert(ChiTietHoaDon ct);
    void update(ChiTietHoaDon ct);
    void delete(ChiTietHoaDonId id); // dùng composite key
    ChiTietHoaDon findById(ChiTietHoaDonId id);
    List<ChiTietHoaDon> findAll();
    List<ChiTietHoaDon> findByHoaDonId(int maHD); // optional: lấy theo mã hóa đơn
}
