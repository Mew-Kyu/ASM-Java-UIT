package controller;

import dao.impl.TaiKhoanDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.TaiKhoan;
import model.NhanVien;
import util.PasswordUtils;

import java.util.List;

public class TaiKhoanController {
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");

    public void addTaiKhoan(TaiKhoan tk) {
        tk.setMatKhau(PasswordUtils.hashPassword(tk.getMatKhau()));
        taiKhoanDAO.insert(tk);
    }

    public void updateTaiKhoan(TaiKhoan tk) {
        TaiKhoan existing = taiKhoanDAO.findById(tk.getTenDangNhap());
        if (existing != null) {
            // If password is null or empty, keep the old password
            if (tk.getMatKhau() == null || tk.getMatKhau().isEmpty()) {
                tk.setMatKhau(existing.getMatKhau());
            } else {
                // Check if the new password is different from the old one
                if (!PasswordUtils.checkPassword(tk.getMatKhau(), existing.getMatKhau())) {
                    tk.setMatKhau(PasswordUtils.hashPassword(tk.getMatKhau()));
                } else {
                    tk.setMatKhau(existing.getMatKhau());
                }
            }
            taiKhoanDAO.update(tk);
        }
    }

    public void deleteTaiKhoan(String tenDangNhap) {
        taiKhoanDAO.delete(tenDangNhap);
    }

    public List<TaiKhoan> getAllTaiKhoan() {
        return taiKhoanDAO.findAll();
    }

    public NhanVien getNhanVienById(String maNV) {
        EntityManager em = emf.createEntityManager();
        try {
            Integer id = null;
            try {
                id = Integer.parseInt(maNV);
            } catch (NumberFormatException e) {
                return null;
            }
            return em.find(NhanVien.class, id);
        } finally {
            em.close();
        }
    }

    public boolean login(String tenDangNhap, String matKhau) {
        TaiKhoan tk = taiKhoanDAO.findById(tenDangNhap);
        if (tk != null && PasswordUtils.checkPassword(matKhau, tk.getMatKhau())) {
            return true;
        }
        return false;
    }

    public TaiKhoan getTaiKhoanByUsername(String tenDangNhap) {
        return taiKhoanDAO.findById(tenDangNhap);
    }
}
