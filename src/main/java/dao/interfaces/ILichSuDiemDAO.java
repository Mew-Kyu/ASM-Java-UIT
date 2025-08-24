package dao.interfaces;

import model.LichSuDiem;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Points History operations
 */
public interface ILichSuDiemDAO {
    void insert(LichSuDiem lichSuDiem);
    void update(LichSuDiem lichSuDiem);
    void delete(int maLS);
    Optional<LichSuDiem> findById(int maLS);
    List<LichSuDiem> findAll();
    List<LichSuDiem> findByMaThe(int maThe);
    List<LichSuDiem> findByLoaiGiaoDich(String loaiGiaoDich);
}
