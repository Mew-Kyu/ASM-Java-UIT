package controller;

import dao.impl.NhanVienDAO;
import model.NhanVien;
import java.util.List;

public class NhanVienController {
    private NhanVienDAO dao;

    public NhanVienController() {
        dao = new NhanVienDAO();
    }

    public List<NhanVien> getAllNhanVien() {
        return dao.findAll();
    }

    public List<NhanVien> layDanhSachNhanVien() {
        return dao.findAll();
    }

    public void addNhanVien(NhanVien nv) {
        dao.insert(nv);
    }

    public void updateNhanVien(NhanVien nv) {
        dao.update(nv);
    }

    public void deleteNhanVien(int id) {
        dao.delete(id);
    }

    public NhanVien getNhanVienById(int id) {
        return dao.findById(id);
    }

    public List<NhanVien> timKiemNhanVien(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNhanVien();
        }
        return dao.searchByKeyword(keyword.trim());
    }

    public List<NhanVien> searchNhanVien(String keyword) {
        return timKiemNhanVien(keyword);
    }
}
