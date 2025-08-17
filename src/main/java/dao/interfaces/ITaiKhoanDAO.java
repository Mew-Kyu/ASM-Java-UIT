package dao.interfaces;

import model.TaiKhoan;
import java.util.List;

public interface ITaiKhoanDAO {
    void insert(TaiKhoan tk);
    void update(TaiKhoan tk);
    void delete(String tenDangNhap);
    TaiKhoan findById(String tenDangNhap);
    List<TaiKhoan> findAll();
}
