package dao.interfaces;

import model.HoaDon;
import exception.DAOException;
import java.util.List;

public interface IHoaDonDAO {
    void insert(HoaDon hd);
    void update(HoaDon hd);
    void delete(int maHD) throws DAOException;
    HoaDon findById(int maHD);
    HoaDon findByIdWithDetails(int maHD);
    List<HoaDon> findAll();
}
