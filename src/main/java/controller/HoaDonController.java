package controller;

import dao.impl.HoaDonDAO;
import model.HoaDon;
import java.util.List;

public class HoaDonController {
    private HoaDonDAO hoaDonDAO;

    public HoaDonController() {
        hoaDonDAO = new HoaDonDAO();
    }

    public List<HoaDon> getAllHoaDon() {
        return hoaDonDAO.findAll();
    }

    public void addHoaDon(HoaDon hd) {
        hoaDonDAO.insert(hd);
    }

    public void updateHoaDon(HoaDon hd) {
        hoaDonDAO.update(hd);
    }

    public void deleteHoaDon(int maHD) {
        hoaDonDAO.delete(maHD);
    }

    public HoaDon getHoaDonById(int maHD) {
        return hoaDonDAO.findById(maHD);
    }
}
