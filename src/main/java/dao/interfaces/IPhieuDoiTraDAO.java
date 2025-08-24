package dao.interfaces;

import model.PhieuDoiTra;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Returns & Exchanges operations
 */
public interface IPhieuDoiTraDAO {
    void insert(PhieuDoiTra phieuDoiTra);
    void update(PhieuDoiTra phieuDoiTra);
    void delete(int maPhieuDT);
    Optional<PhieuDoiTra> findById(int maPhieuDT);
    List<PhieuDoiTra> findAll();
    List<PhieuDoiTra> findByHoaDon(int maHD);
    List<PhieuDoiTra> findByTrangThai(String trangThai);
    List<PhieuDoiTra> findByLoaiPhieu(String loaiPhieu);
}
