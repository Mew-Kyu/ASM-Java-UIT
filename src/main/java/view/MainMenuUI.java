package view;

import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainMenuUI extends JFrame {
    private SessionManager sessionManager;

    public MainMenuUI() {
        sessionManager = SessionManager.getInstance();

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            this.dispose();
            return;
        }

        setTitle("Hệ Thống Quản Lý Cửa Hàng Quần Áo - " + sessionManager.getCurrentUsername() + " (" + sessionManager.getCurrentUserRole() + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG QUẦN ÁO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(70, 130, 180));
        JLabel userLabel = new JLabel("Xin chào: " + sessionManager.getCurrentUsername() + " (" + sessionManager.getCurrentUserRole() + ")");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(logoutBtn);
        headerPanel.add(userPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Menu panel with dynamic layout based on role
        JPanel menuPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Create menu buttons based on role permissions

        // Admin-only buttons
        if (RoleManager.canAccessAccountManagement()) {
            JButton btnTaiKhoan = createMenuButton("Quản Lý Tài Khoản", "icons/account.png");
            btnTaiKhoan.addActionListener(e -> openTaiKhoanUI());
            menuPanel.add(btnTaiKhoan);
        }

        if (RoleManager.canAccessEmployeeManagement()) {
            JButton btnNhanVien = createMenuButton("Quản Lý Nhân Viên", "icons/employee.png");
            btnNhanVien.addActionListener(e -> openNhanVienUI());
            menuPanel.add(btnNhanVien);
        }

        // Manager and Admin buttons
        if (RoleManager.canAccessProductConfiguration()) {
            JButton btnDanhMuc = createMenuButton("Quản Lý Danh Mục", "icons/category.png");
            btnDanhMuc.addActionListener(e -> openDanhMucUI());
            menuPanel.add(btnDanhMuc);

            JButton btnMauSac = createMenuButton("Quản Lý Màu Sắc", "icons/color.png");
            btnMauSac.addActionListener(e -> openMauSacUI());
            menuPanel.add(btnMauSac);

            JButton btnKichThuoc = createMenuButton("Quản Lý Kích Thước", "icons/size.png");
            btnKichThuoc.addActionListener(e -> openKichThuocUI());
            menuPanel.add(btnKichThuoc);
        }

        // All roles can access these (with different permissions inside)
        if (RoleManager.canAccessCustomerManagement()) {
            JButton btnKhachHang = createMenuButton("Quản Lý Khách Hàng", "icons/customer.png");
            btnKhachHang.addActionListener(e -> openKhachHangUI());
            menuPanel.add(btnKhachHang);
        }

        if (RoleManager.canAccessSalesManagement()) {
            JButton btnSanPham = createMenuButton("Quản Lý Sản Phẩm", "icons/product.png");
            btnSanPham.addActionListener(e -> openSanPhamUI());
            menuPanel.add(btnSanPham);

            JButton btnBienThe = createMenuButton("Quản Lý Biến Thể", "icons/variant.png");
            btnBienThe.addActionListener(e -> openBienTheUI());
            menuPanel.add(btnBienThe);

            JButton btnHoaDon = createMenuButton("Quản Lý Hóa Đơn", "icons/invoice.png");
            btnHoaDon.addActionListener(e -> openHoaDonUI());
            menuPanel.add(btnHoaDon);
        }

        mainPanel.add(menuPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 100));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Set colors
        button.setBackground(new Color(240, 248, 255));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(173, 216, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255));
            }
        });

        return button;
    }

    private void openTaiKhoanUI() {
        if (!RoleManager.canAccessAccountManagement()) {
            RoleManager.showAccessDeniedMessage(this, "Admin");
            return;
        }
        new TaiKhoanUI().setVisible(true);
    }

    private void openNhanVienUI() {
        if (!RoleManager.canAccessEmployeeManagement()) {
            RoleManager.showAccessDeniedMessage(this, "Admin");
            return;
        }
        new NhanVienUI().setVisible(true);
    }

    private void openKhachHangUI() {
        if (!RoleManager.canAccessCustomerManagement()) {
            RoleManager.showAccessDeniedMessage(this);
            return;
        }
        new KhachHangUI().setVisible(true);
    }

    private void openSanPhamUI() {
        if (!RoleManager.canAccessSalesManagement()) {
            RoleManager.showAccessDeniedMessage(this);
            return;
        }
        new SanPhamUI().setVisible(true);
    }

    private void openDanhMucUI() {
        if (!RoleManager.canAccessProductConfiguration()) {
            RoleManager.showAccessDeniedMessage(this, "Manager hoặc Admin");
            return;
        }
        new DanhMucUI().setVisible(true);
    }

    private void openHoaDonUI() {
        if (!RoleManager.canAccessSalesManagement()) {
            RoleManager.showAccessDeniedMessage(this);
            return;
        }
        new HoaDonUI().setVisible(true);
    }

    private void openMauSacUI() {
        if (!RoleManager.canAccessProductConfiguration()) {
            RoleManager.showAccessDeniedMessage(this, "Manager hoặc Admin");
            return;
        }
        new MauSacUI().setVisible(true);
    }

    private void openKichThuocUI() {
        if (!RoleManager.canAccessProductConfiguration()) {
            RoleManager.showAccessDeniedMessage(this, "Manager hoặc Admin");
            return;
        }
        new KichThuocUI().setVisible(true);
    }

    private void openBienTheUI() {
        if (!RoleManager.canAccessSalesManagement()) {
            RoleManager.showAccessDeniedMessage(this);
            return;
        }
        new BienTheSanPhamUI().setVisible(true);
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất không?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            sessionManager.logout();
            new LoginUI().setVisible(true);
            this.dispose();
        }
    }
}
