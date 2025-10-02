package controller;

import dao.impl.HoaDonDAO;
import model.HoaDon;
import exception.DAOException;
import java.util.List;

public class HoaDonController {
    private HoaDonDAO hoaDonDAO;

    public HoaDonController() {
        hoaDonDAO = new HoaDonDAO();
    }

    public List<HoaDon> getAllHoaDon() {
        // fetch basic list for light operations
        return hoaDonDAO.findAll();
    }

    public List<HoaDon> getAllHoaDonWithDetails() {
        // eager fetch related entities for UI rendering (avoid lazy errors)
        return hoaDonDAO.findAllWithDetails();
    }

    public void addHoaDon(HoaDon hd) {
        hoaDonDAO.insert(hd);
    }

    public void updateHoaDon(HoaDon hd) {
        hoaDonDAO.update(hd);
    }

    public void deleteHoaDon(int maHD) throws DAOException {
        hoaDonDAO.delete(maHD);
    }

    public HoaDon getHoaDonById(int maHD) {
        return hoaDonDAO.findById(maHD);
    }

    public HoaDon getHoaDonByIdWithDetails(int maHD) {
        return hoaDonDAO.findByIdWithDetails(maHD);
    }
}
