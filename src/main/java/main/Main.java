package main;

import view.LoginUI;
import util.SessionManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ensure clean session start
        SessionManager.getInstance().logout();

        // Start application with login screen
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}
