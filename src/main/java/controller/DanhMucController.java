package controller;

import dao.impl.DanhMucDAO;
import model.DanhMuc;
import java.util.List;

public class DanhMucController {
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();

    public List<DanhMuc> getAllDanhMuc() {
        return danhMucDAO.findAll();
    }

    public void addDanhMuc(DanhMuc dm) {
        danhMucDAO.insert(dm);
    }

    public void updateDanhMuc(DanhMuc dm) {
        danhMucDAO.update(dm);
    }

    public void deleteDanhMuc(int id) {
        danhMucDAO.delete(id);
    }

    public DanhMuc getDanhMucById(int id) {
        return danhMucDAO.findById(id);
    }
}
