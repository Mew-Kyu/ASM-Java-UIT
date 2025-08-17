package dao.interfaces;

import model.MauSac;
import java.util.List;

public interface IMauSacDAO {
    void insert(MauSac ms);
    void update(MauSac ms);
    void delete(int maMau);
    MauSac findById(int maMau);
    List<MauSac> findAll();
}
