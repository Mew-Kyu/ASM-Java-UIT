package controller;

import dao.impl.MauSacDAO;
import model.MauSac;
import java.util.List;

public class MauSacController {
    private MauSacDAO dao;

    public MauSacController() {
        dao = new MauSacDAO();
    }

    public List<MauSac> getAllMauSac() {
        return dao.findAll();
    }

    public void addMauSac(String tenMau) {
        MauSac ms = new MauSac();
        ms.setTenMau(tenMau);
        dao.insert(ms);
    }

    public void updateMauSac(int id, String tenMau) {
        MauSac ms = dao.findById(id);
        if (ms != null) {
            ms.setTenMau(tenMau);
            dao.update(ms);
        }
    }

    public void deleteMauSac(int id) {
        dao.delete(id);
    }
}
