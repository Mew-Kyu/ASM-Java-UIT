package dao.interfaces;

import model.DanhMuc;
import java.util.List;

public interface IDanhMucDAO {
    void insert(DanhMuc dm);
    void update(DanhMuc dm);
    void delete(int maDM);
    DanhMuc findById(int maDM);
    List<DanhMuc> findAll();
}
