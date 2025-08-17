package controller;

import dao.impl.KhachHangDAO;
import model.KhachHang;
import java.util.List;

public class KhachHangController {
    private KhachHangDAO khachHangDAO;

    public KhachHangController() {
        khachHangDAO = new KhachHangDAO();
    }

    public List<KhachHang> layDanhSachKhachHang() {
        return khachHangDAO.findAll();
    }

    public void themKhachHang(KhachHang kh) {
        khachHangDAO.insert(kh);
    }

    public void suaKhachHang(KhachHang kh) {
        khachHangDAO.update(kh);
    }

    public void xoaKhachHang(int id) {
        khachHangDAO.delete(id);
    }
}
