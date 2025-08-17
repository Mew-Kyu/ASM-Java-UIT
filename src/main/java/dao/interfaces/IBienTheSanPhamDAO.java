package dao.interfaces;

import model.BienTheSanPham;
import java.util.List;

public interface IBienTheSanPhamDAO {
    void insert(BienTheSanPham bts);
    void update(BienTheSanPham bts);
    void delete(int maBienThe);
    BienTheSanPham findById(int maBienThe);
    List<BienTheSanPham> findAll();
    List<BienTheSanPham> findBySanPhamId(int maSP); // optional
}
