package dao.interfaces;

import model.NhanVien;
import java.util.List;

public interface INhanVienDAO {
    void insert(NhanVien nv);
    void update(NhanVien nv);
    void delete(int maNV);
    NhanVien findById(int maNV);
    List<NhanVien> findAll();
}
