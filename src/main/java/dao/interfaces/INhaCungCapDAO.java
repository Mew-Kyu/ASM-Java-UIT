package dao.interfaces;

import model.NhaCungCap;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Supplier operations
 */
public interface INhaCungCapDAO {
    void insert(NhaCungCap nhaCungCap);
    void update(NhaCungCap nhaCungCap);
    void delete(int maNCC);
    Optional<NhaCungCap> findById(int maNCC);
    List<NhaCungCap> findAll();
    List<NhaCungCap> findByTrangThai(boolean trangThai);
    List<NhaCungCap> findByTenNCC(String tenNCC);
}