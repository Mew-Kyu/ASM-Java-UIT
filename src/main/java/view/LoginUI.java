package view;

import controller.TaiKhoanController;
import util.SessionManager;
import model.TaiKhoan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginUI extends JFrame {
    private TaiKhoanController controller;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnLogin;
    private JCheckBox chkHienMatKhau;

    public LoginUI() {
        controller = new TaiKhoanController();
        setTitle("Đăng Nhập - Quản Lý Cửa Hàng Quần Áo");
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Tên Đăng Nhập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenDangNhap = new JTextField(15);
        formPanel.add(txtTenDangNhap, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mật Khẩu:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtMatKhau = new JPasswordField(15);
        formPanel.add(txtMatKhau, gbc);

        // Show password checkbox
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        chkHienMatKhau = new JCheckBox("Hiện mật khẩu");
        chkHienMatKhau.addActionListener(e -> togglePasswordVisibility());
        formPanel.add(chkHienMatKhau, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event handlers
        btnLogin.addActionListener(e -> performLogin());

        // Enter key support
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };

        txtTenDangNhap.addKeyListener(enterKeyListener);
        txtMatKhau.addKeyListener(enterKeyListener);

        // Focus on username field
        txtTenDangNhap.requestFocus();
    }

    private void togglePasswordVisibility() {
        if (chkHienMatKhau.isSelected()) {
            txtMatKhau.setEchoChar((char) 0); // Show password
        } else {
            txtMatKhau.setEchoChar('*'); // Hide password
        }
    }

    private void performLogin() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.login(tenDangNhap, matKhau)) {
            // Get user information and create session
            TaiKhoan user = controller.getTaiKhoanByUsername(tenDangNhap);
            SessionManager.getInstance().login(user);

            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            // Mở màn hình menu chính
            new MainMenuUI().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.setText("");
            txtTenDangNhap.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}






