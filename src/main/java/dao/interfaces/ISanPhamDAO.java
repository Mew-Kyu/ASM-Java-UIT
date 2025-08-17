package dao.interfaces;

import model.SanPham;

import java.util.List;

public interface  ISanPhamDAO {
    void insert(SanPham sp);
    void update(SanPham sp);
    void delete(int maSP);
    SanPham findById(int maSP);
    List<SanPham> findAll();
}
