package controller;

import dao.impl.SanPhamDAO;
import dao.impl.DanhMucDAO;
import model.SanPham;
import model.DanhMuc;
import java.util.List;

public class SanPhamController {
    private SanPhamDAO sanPhamDAO;
    private DanhMucDAO danhMucDAO;

    public SanPhamController() {
        sanPhamDAO = new SanPhamDAO();
        danhMucDAO = new DanhMucDAO();
    }

    public List<SanPham> getAllSanPham() {
        return sanPhamDAO.findAll();
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
