package dao.interfaces;

import model.HoaDon;
import java.util.List;

public interface IHoaDonDAO {
    void insert(HoaDon hd);
    void update(HoaDon hd);
    void delete(int maHD);
    HoaDon findById(int maHD);
    List<HoaDon> findAll();
}
