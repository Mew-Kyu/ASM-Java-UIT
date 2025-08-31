package dao.interfaces;

import model.KhachHang;
import java.util.List;

public interface IKhachHangDAO {
    void insert(KhachHang kh);
    void update(KhachHang kh);
    void delete(int maKH);
    KhachHang findById(int maKH);
    List<KhachHang> findAll();
    List<KhachHang> searchByKeyword(String keyword);
}
