package dao.interfaces;

import model.KichThuoc;
import java.util.List;

public interface IKichThuocDAO {
    void insert(KichThuoc kt);
    void update(KichThuoc kt);
    void delete(int maSize);
    KichThuoc findById(int maSize);
    List<KichThuoc> findAll();
}
