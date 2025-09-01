package util;

import model.TaiKhoan;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionManager {
    private static SessionManager instance;
    private TaiKhoan currentUser;
    private boolean isLoggedIn = false;
    // Listeners invoked upon logout
    private final List<Runnable> logoutListeners = new CopyOnWriteArrayList<>();

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
        // Notify listeners so open secured windows can self-close
        for (Runnable r : logoutListeners) {
            try {
                r.run();
            } catch (Exception ignored) {
            }
        }
    }

    public void addLogoutListener(Runnable listener) {
        if (listener != null) logoutListeners.add(listener);
    }

    public void removeLogoutListener(Runnable listener) {
        if (listener != null) logoutListeners.remove(listener);
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
