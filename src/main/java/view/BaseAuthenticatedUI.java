package view;

import util.SessionManager;
import javax.swing.*;

public abstract class BaseAuthenticatedUI extends JFrame {

    public BaseAuthenticatedUI() {
        checkAuthentication();
    }

    public BaseAuthenticatedUI(String title) {
        super(title);
        checkAuthentication();
    }

    private void checkAuthentication() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Vui lòng đăng nhập để truy cập chức năng này!",
                    "Yêu cầu đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
                new LoginUI().setVisible(true);
                this.dispose();
            });
        }
    }

    protected void showAuthenticationError() {
        JOptionPane.showMessageDialog(this,
            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
            "Lỗi xác thực",
            JOptionPane.ERROR_MESSAGE);
        SessionManager.getInstance().logout();
        new LoginUI().setVisible(true);
        this.dispose();
    }
}
