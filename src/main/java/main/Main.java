package main;

import view.LoginUI;
import util.SessionManager;
import config.ApplicationConfig;
import javax.swing.*;
import java.util.logging.Logger;

public class Main {
    static {
        // Route java.util.logging through Log4j2 before any Logger instances are created
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        try {
            java.util.logging.LogManager.getLogManager().reset();
        } catch (Exception ignored) {}
    }
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        try {
            LOGGER.info("Starting application (Log4j2 bridged)...");
            // Initialize application configuration and DI container
            ApplicationConfig.initialize();
            
            // Set look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
                LOGGER.info("Look and feel set successfully");
            } catch (Exception e) {
                LOGGER.warning("Failed to set look and feel: " + e.getMessage());
                // Continue with default look and feel
            }

            // Ensure clean session start
            SessionManager.getInstance().logout();

            // Add shutdown hook for cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Application shutting down...");
                ApplicationConfig.shutdown();
            }));

            // Start application with login screen
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginUI().setVisible(true);
                    LOGGER.info("Application started successfully");
                } catch (Exception e) {
                    LOGGER.severe("Failed to start application: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }
            });
            
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog to user
            JOptionPane.showMessageDialog(
                null,
                "Không thể khởi tạo ứng dụng:\n" + e.getMessage(),
                "Lỗi khởi tạo",
                JOptionPane.ERROR_MESSAGE
            );
            
            System.exit(1);
        }
    }
}
