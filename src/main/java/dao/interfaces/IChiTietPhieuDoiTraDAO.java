package dao.interfaces;

import model.ChiTietPhieuDoiTra;
import model.ChiTietPhieuDoiTraId;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Returns & Exchanges Detail operations
 */
public interface IChiTietPhieuDoiTraDAO {
    void insert(ChiTietPhieuDoiTra chiTiet);
    void update(ChiTietPhieuDoiTra chiTiet);
    void delete(ChiTietPhieuDoiTraId id);
    void deleteByPhieuDoiTra(int maPhieuDT);
    Optional<ChiTietPhieuDoiTra> findById(ChiTietPhieuDoiTraId id);
    List<ChiTietPhieuDoiTra> findByPhieuDoiTra(int maPhieuDT);
    List<ChiTietPhieuDoiTra> findByBienThe(int maBienThe);
    List<ChiTietPhieuDoiTra> findByLoaiChiTiet(String loaiChiTiet);
}
