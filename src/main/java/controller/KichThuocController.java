package controller;

import dao.impl.KichThuocDAO;
import model.KichThuoc;
import java.util.List;

public class KichThuocController {
    private KichThuocDAO dao;

    public KichThuocController() {
        dao = new KichThuocDAO();
    }

    public List<KichThuoc> getAllKichThuoc() {
        return dao.findAll();
    }

    public void addKichThuoc(String tenSize) {
        KichThuoc kt = new KichThuoc();
        kt.setTenSize(tenSize);
        dao.insert(kt);
    }

    public void updateKichThuoc(int id, String tenSize) {
        KichThuoc kt = dao.findById(id);
        if (kt != null) {
            kt.setTenSize(tenSize);
            dao.update(kt);
        }
    }

    public void deleteKichThuoc(int id) {
        dao.delete(id);
    }
}
