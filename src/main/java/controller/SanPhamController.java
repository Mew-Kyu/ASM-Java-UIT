package controller;

import dao.impl.SanPhamDAO;
import dao.impl.DanhMucDAO;
import model.SanPham;
import model.DanhMuc;
import java.util.List;

public class SanPhamController {
    private final SanPhamDAO sanPhamDAO;
    private final DanhMucDAO danhMucDAO;

    public SanPhamController() {
        sanPhamDAO = new SanPhamDAO();
        danhMucDAO = new DanhMucDAO();
    }

    public List<SanPham> getAllSanPham() {
        return sanPhamDAO.findAll();
    }

    public List<SanPham> searchSanPham(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSanPham();
        }
        return sanPhamDAO.findByName(keyword.trim());
    }

    public void addSanPham(SanPham sp) {
        sanPhamDAO.insert(sp);
    }

    public void updateSanPham(SanPham sp) {
        sanPhamDAO.update(sp);
    }

    public void deleteSanPham(int id) {
        sanPhamDAO.delete(id);
    }

    public List<DanhMuc> getAllDanhMuc() {
        return danhMucDAO.findAll();
    }
}
