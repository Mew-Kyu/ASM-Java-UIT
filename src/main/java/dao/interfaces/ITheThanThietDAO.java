package dao.interfaces;

import model.TheThanThiet;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Loyalty Card operations
 */
public interface ITheThanThietDAO {
    void insert(TheThanThiet theThanThiet);
    void update(TheThanThiet theThanThiet);
    void delete(int maThe);
    Optional<TheThanThiet> findById(int maThe);
    List<TheThanThiet> findAll();
    List<TheThanThiet> findByKhachHang(int maKH);
    List<TheThanThiet> findByTrangThai(String trangThai);
    Optional<TheThanThiet> findBySoThe(String soThe);
}
