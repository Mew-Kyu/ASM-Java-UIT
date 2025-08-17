package controller;

import dao.impl.ChiTietHoaDonDAO;
import model.ChiTietHoaDon;
import model.ChiTietHoaDonId;

import java.util.List;

public class ChiTietHoaDonController {
    private final ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    public List<ChiTietHoaDon> getAll() {
        return dao.findAll();
    }

    public List<ChiTietHoaDon> getByHoaDonId(int maHD) {
        return dao.findByHoaDonId(maHD);
    }

    public ChiTietHoaDon getById(ChiTietHoaDonId id) {
        return dao.findById(id);
    }

    public void add(ChiTietHoaDon ct) {
        dao.insert(ct);
    }

    public void update(ChiTietHoaDon ct) {
        dao.update(ct);
    }

    public void delete(ChiTietHoaDonId id) {
        dao.delete(id);
    }
}
