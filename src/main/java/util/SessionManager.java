package util;

import model.TaiKhoan;

public class SessionManager {
    private static SessionManager instance;
    private TaiKhoan currentUser;
    private boolean isLoggedIn = false;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(TaiKhoan user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }

    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getTenDangNhap() : null;
    }

    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getQuyen() : null;
    }
}
