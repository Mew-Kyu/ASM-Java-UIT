package controller;

import dao.impl.BienTheSanPhamDAO;
import model.BienTheSanPham;

import java.util.List;

public class BienTheSanPhamController {
    private BienTheSanPhamDAO dao = new BienTheSanPhamDAO();

    public void addBienTheSanPham(BienTheSanPham bts) {
        dao.insert(bts);
    }

    public void updateBienTheSanPham(BienTheSanPham bts) {
        dao.update(bts);
    }

    public void deleteBienTheSanPham(int maBienThe) {
        dao.delete(maBienThe);
    }

    public BienTheSanPham getBienTheSanPhamById(int maBienThe) {
        return dao.findById(maBienThe);
    }

    public List<BienTheSanPham> getAllBienTheSanPham() {
        return dao.findAll();
    }
}
