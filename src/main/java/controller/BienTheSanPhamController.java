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

    // Additional methods for consistency with the new UI
    public List<BienTheSanPham> getAll() {
        return dao.findAll();
    }

    public List<BienTheSanPham> getAllWithDetails() {
        return dao.findAllWithDetails();
    }

    public BienTheSanPham getById(int id) {
        return dao.findById(id);
    }

    public BienTheSanPham getByIdWithDetails(int id) {
        return dao.findByIdWithDetails(id);
    }

    public void update(BienTheSanPham bts) {
        dao.update(bts);
    }

    public void insert(BienTheSanPham bts) {
        dao.insert(bts);
    }

    public void delete(int id) {
        dao.delete(id);
    }
}
