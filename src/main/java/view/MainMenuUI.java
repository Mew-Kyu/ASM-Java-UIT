package view;

import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.event.KeyEvent;

public class MainMenuUI extends JFrame {
    private final SessionManager sessionManager;

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
        setSize(1000, 680);
        setMinimumSize(new Dimension(960, 640));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        // Fix potential first paint overlap by forcing layout & repaint after all components added
        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
    }

    private void initComponents() {
        // Root layout with border
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));

        // Header panel with gradient look
        JPanel headerPanel = new GradientPanel(new Color(58, 123, 213), new Color(58, 96, 115));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG QUẦN ÁO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Xin chào: " + sessionManager.getCurrentUsername() + " (" + sessionManager.getCurrentUserRole() + ")");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Replace standard button with custom logout button
        JButton logoutBtn = new LogoutButton("Đăng xuất");
        logoutBtn.setToolTipText("Đăng xuất (Alt+L)");
        logoutBtn.setMnemonic(KeyEvent.VK_L);
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLabel);
        userPanel.add(logoutBtn);
        headerPanel.add(userPanel, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);

        // Center content with scrollable sections
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));
        contentWrapper.setOpaque(false);

        JPanel sectionsContainer = new JPanel();
        sectionsContainer.setOpaque(false);
        sectionsContainer.setLayout(new BoxLayout(sectionsContainer, BoxLayout.Y_AXIS));

        // Build button groups based on role permissions
        Map<String, List<JButton>> groupedButtons = new LinkedHashMap<>();

        // Admin only section
        List<JButton> adminButtons = new ArrayList<>();
        if (RoleManager.canAccessAccountManagement()) {
            JButton btnTaiKhoan = createMenuButton("Quản Lý Tài Khoản", "icons/account.png");
            btnTaiKhoan.addActionListener(e -> openTaiKhoanUI());
            adminButtons.add(btnTaiKhoan);
        }
        if (RoleManager.canAccessEmployeeManagement()) {
            JButton btnNhanVien = createMenuButton("Quản Lý Nhân Viên", "icons/employee.png");
            btnNhanVien.addActionListener(e -> openNhanVienUI());
            adminButtons.add(btnNhanVien);
        }
        if (!adminButtons.isEmpty()) groupedButtons.put("Quản trị hệ thống (Admin)", adminButtons);

        // Manager & Admin configuration / analytics section
        List<JButton> managerButtons = new ArrayList<>();
        if (RoleManager.canAccessProductConfiguration()) {
            JButton btnDanhMuc = createMenuButton("Danh Mục", "icons/category.png");
            btnDanhMuc.addActionListener(e -> openDanhMucUI());
            managerButtons.add(btnDanhMuc);

            JButton btnMauSac = createMenuButton("Màu Sắc", "icons/color.png");
            btnMauSac.addActionListener(e -> openMauSacUI());
            managerButtons.add(btnMauSac);

            JButton btnKichThuoc = createMenuButton("Kích Thước", "icons/size.png");
            btnKichThuoc.addActionListener(e -> openKichThuocUI());
            managerButtons.add(btnKichThuoc);
        }
        if (RoleManager.canAccessReports()) {
            JButton btnBaoCao = createMenuButton("Báo Cáo & Thống Kê", "icons/report.png");
            btnBaoCao.addActionListener(e -> openBaoCaoUI());
            managerButtons.add(btnBaoCao);
        }
        if (RoleManager.canAccessSupplierManagement()) {
            JButton btnNhaCungCap = createMenuButton("Nhà Cung Cấp", "icons/supplier.png");
            btnNhaCungCap.addActionListener(e -> openNhaCungCapUI());
            managerButtons.add(btnNhaCungCap);
        }
        if (RoleManager.canAccessPaymentMethods()) {
            JButton btnHinhThucThanhToan = createMenuButton("Thanh Toán", "icons/payment.png");
            btnHinhThucThanhToan.addActionListener(e -> openHinhThucThanhToanUI());
            managerButtons.add(btnHinhThucThanhToan);
        }
        if (!managerButtons.isEmpty()) groupedButtons.put("Cấu hình & Phân tích (Manager / Admin)", managerButtons);

        // Operational (Staff +) section
        List<JButton> staffButtons = new ArrayList<>();
        if (RoleManager.canAccessCustomerManagement()) {
            JButton btnKhachHang = createMenuButton("Khách Hàng", "icons/customer.png");
            btnKhachHang.addActionListener(e -> openKhachHangUI());
            staffButtons.add(btnKhachHang);
        }
        if (RoleManager.canAccessSalesManagement()) {
            JButton btnSanPham = createMenuButton("Sản Phẩm", "icons/product.png");
            btnSanPham.addActionListener(e -> openSanPhamUI());
            staffButtons.add(btnSanPham);

            JButton btnBienThe = createMenuButton("Hàng Hóa", "icons/variant.png");
            btnBienThe.addActionListener(e -> openBienTheUI());
            staffButtons.add(btnBienThe);

            JButton btnHoaDon = createMenuButton("Hóa Đơn", "icons/invoice.png");
            btnHoaDon.addActionListener(e -> openHoaDonUI());
            staffButtons.add(btnHoaDon);
        }
        if (RoleManager.canAccessLoyalty()) {
            JButton btnKhachHangThanThiet = createMenuButton("Thân Thiết", "icons/loyalty.png");
            btnKhachHangThanThiet.addActionListener(e -> openKhachHangThanThietUI());
            staffButtons.add(btnKhachHangThanThiet);
        }
        if (RoleManager.canAccessReturns()) {
            JButton btnDoiTra = createMenuButton("Đổi Trả", "icons/return.png");
            btnDoiTra.addActionListener(e -> openDoiTraUI());
            staffButtons.add(btnDoiTra);
        }
        if (!staffButtons.isEmpty()) groupedButtons.put("Nghiệp vụ bán hàng (Staff trở lên)", staffButtons);

        if (groupedButtons.isEmpty()) {
            JLabel noAccess = new JLabel("Bạn chưa có quyền truy cập tính năng nào.");
            noAccess.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noAccess.setForeground(new Color(120, 120, 120));
            noAccess.setHorizontalAlignment(SwingConstants.CENTER);
            sectionsContainer.add(noAccess);
        } else {
            groupedButtons.forEach((title, buttons) -> sectionsContainer.add(createSectionPanel(title, buttons)));
        }

        JScrollPane scrollPane = new JScrollPane(sectionsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        root.add(contentWrapper, BorderLayout.CENTER);
        add(root);
    }

    private JPanel createSectionPanel(String title, List<JButton> buttons) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 22, 0),
                BorderFactory.createEmptyBorder()));

        JLabel lbl = new JLabel(title.toUpperCase());
        lbl.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        lbl.setForeground(new Color(70, 80, 95));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 6, 4));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        for (JButton b : buttons) {
            grid.add(b, gbc);
            gbc.gridx++;
            if (gbc.gridx % 4 == 0) { // 4 buttons per row
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        JPanel bodyWrapper = new RoundedPanel();
        bodyWrapper.setLayout(new BorderLayout());
        bodyWrapper.setOpaque(false);
        bodyWrapper.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        bodyWrapper.add(grid, BorderLayout.CENTER);

        panel.add(bodyWrapper, BorderLayout.CENTER);
        return panel;
    }

    private void styleHeaderButton(AbstractButton b) {
        b.setFocusPainted(false);
        b.setForeground(new Color(40, 55, 70));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        final Color normalBg = new Color(255, 255, 255, 200);
        final Color hoverBg = new Color(255, 255, 255, 235);
        final Color pressBg = new Color(240, 248, 255, 255);
        final Color normalBorder = new Color(255, 255, 255, 160);
        final Color hoverBorder = new Color(210, 230, 250, 220);
        final Color pressBorder = new Color(180, 210, 240, 220);
        b.setBackground(normalBg);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normalBorder, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(hoverBg);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverBorder, 1, true),
                        BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(normalBg);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalBorder, 1, true),
                        BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            }
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(pressBg);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(pressBorder, 1, true),
                        BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                if (b.getBounds().contains(e.getPoint())) {
                    b.setBackground(hoverBg);
                    b.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(hoverBorder, 1, true),
                            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
                } else {
                    b.setBackground(normalBg);
                    b.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(normalBorder, 1, true),
                            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
                }
            }
        });
    }

    private JButton createMenuButton(String text, String iconPath) {
        Icon icon = loadIcon(iconPath);
        if (icon == null) {
            icon = IconFactory.get(text);
        }
        return new MenuButton(text, icon);
    }

    // Added custom MenuButton class (was missing causing cannot find symbol)
    private static class MenuButton extends JButton {
        private static final Color FG = new Color(55,65,80);
        private static final Color HOVER_BG = new Color(255,255,255,195);
        private static final Color PRESS_BG = new Color(235,242,250,210);
        private static final Color BORDER_HOVER = new Color(90,140,200,190);
        private static final Color BORDER_PRESS = new Color(70,120,180,210);
        private static final Insets PAD = new Insets(8,8,8,8);
        MenuButton(String text, Icon icon){
            super(text, icon);
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setPreferredSize(new Dimension(150,120));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder(PAD.top, PAD.left, PAD.bottom, PAD.right));
            setForeground(FG);
            setRolloverEnabled(true);
        }
        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), arc=20;
            ButtonModel m=getModel();
            boolean hov=m.isRollover();
            boolean press=m.isArmed() && m.isPressed();
            if(hov||press){
                g2.setColor(press?PRESS_BG:HOVER_BG);
                g2.fillRoundRect(0,0,w,h,arc,arc);
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(press?BORDER_PRESS:BORDER_HOVER);
                g2.drawRoundRect(0,0,w-1,h-1,arc,arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Fallback icon factory producing colored circular badge icons with first letter
    private static class IconFactory {
        private static final Map<String, Icon> CACHE = new ConcurrentHashMap<>();
        private static final Color[] PALETTE = new Color[]{
                new Color(76, 140, 240),
                new Color(90, 180, 120),
                new Color(245, 170, 85),
                new Color(200, 120, 220),
                new Color(240, 95, 110),
                new Color(80, 200, 210),
                new Color(140, 110, 250)
        };
        static Icon get(String label) {
            return CACHE.computeIfAbsent(label, IconFactory::createBadge);
        }
        private static Icon createBadge(String label) {
            String trimmed = label == null ? "?" : label.trim();
            String glyph = trimmed.isEmpty() ? "?" : trimmed.substring(0,1).toUpperCase();
            int size = 48;
            Color base = PALETTE[Math.abs(trimmed.hashCode()) % PALETTE.length];
            return new BadgeIcon(size, base, glyph);
        }
    }

    // Simple circular badge icon
    private static class BadgeIcon implements Icon {
        private final int size; private final Color base; private final String glyph;
        BadgeIcon(int size, Color base, String glyph) { this.size=size; this.base=base; this.glyph=glyph; }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Gradient circle
            GradientPaint gp = new GradientPaint(x, y, base.brighter(), x+size, y+size, base.darker());
            g2.setPaint(gp);
            g2.fillOval(x, y, size, size);
            g2.setColor(new Color(255,255,255,210));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x+1, y+1, size-2, size-2);
            // Glyph
            Font font = c.getFont().deriveFont(Font.BOLD, size * 0.45f);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int gx = x + (size - fm.stringWidth(glyph)) / 2;
            int gy = y + (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(glyph, gx, gy);
            g2.dispose();
        }
    }

    private ImageIcon loadIcon(String path) {
        if (path == null || path.isEmpty()) return null;
        try {
            java.net.URL url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                // Try without classloader (relative file path)
                java.io.File f = new java.io.File(path);
                if (!f.exists()) return null;
                url = f.toURI().toURL();
            }
            Image img = new ImageIcon(url).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            return null; // Silent fail if icon missing
        }
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
    private void openBaoCaoUI() {
        if (!RoleManager.canAccessReports()) {
            RoleManager.showAccessDeniedMessage(this, "Báo cáo & Thống kê", "Manager hoặc Admin");
            return;
        }
        new BaoCaoUI().setVisible(true);
    }
    private void openNhaCungCapUI() {
        if (!RoleManager.canAccessSupplierManagement()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý nhà cung cấp", "Manager hoặc Admin");
            return;
        }
        new NhaCungCapUI().setVisible(true);
    }
    private void openHinhThucThanhToanUI() {
        if (!RoleManager.canAccessPaymentMethods()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý hình thức thanh toán", "Manager hoặc Admin");
            return;
        }
        new HinhThucThanhToanUI().setVisible(true);
    }
    private void openKhachHangThanThietUI() {
        if (!RoleManager.canAccessLoyalty()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý khách hàng thân thiết", "Staff trở lên");
            return;
        }
        new TheThanThietUI().setVisible(true);
    }
    private void openDoiTraUI() {
        if (!RoleManager.canAccessReturns()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý đổi trả hàng", "Staff trở lên");
            return;
        }
        new PhieuDoiTraUI().setVisible(true);
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

    // Decorative gradient panel
    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;
        GradientPanel(Color start, Color end) { this.start = start; this.end = end; setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Rounded translucent wrapper
    private static class RoundedPanel extends JPanel {
        RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 24;
            g2.setColor(new Color(255, 255, 255, 170));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(new Color(210, 220, 230, 180));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int THUMB_ARC = 14;
        private static final Color TRACK_COLOR = new Color(255, 255, 255, 60);
        private static final Color THUMB_COLOR = new Color(120, 150, 190, 140);
        private static final Color THUMB_HOVER = new Color(110, 140, 180, 200);
        private static final Color THUMB_PRESSED = new Color(90, 120, 165, 220);

        @Override
        protected void configureScrollBarColors() {
            super.configureScrollBarColors();
            this.trackColor = new Color(0,0,0,0);
            this.thumbColor = THUMB_COLOR;
        }

        @Override
        protected Dimension getMinimumThumbSize() { return new Dimension(8, 40); }
        @Override
        public Dimension getPreferredSize(JComponent c) {
            return scrollbar.getOrientation() == Adjustable.VERTICAL ? new Dimension(10, super.getPreferredSize(c).height)
                    : new Dimension(super.getPreferredSize(c).width, 10);
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(TRACK_COLOR);
            int arc = THUMB_ARC;
            g2.fillRoundRect(trackBounds.x + 2, trackBounds.y + 2,
                    trackBounds.width - 4, trackBounds.height - 4, arc, arc);
            g2.dispose();
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!c.isEnabled() || thumbBounds.width <= 0 || thumbBounds.height <= 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = THUMB_COLOR;
            if (isDragging) base = THUMB_PRESSED; else if (isThumbRollover()) base = THUMB_HOVER;
            g2.setColor(base);
            int arc = THUMB_ARC;
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 4, thumbBounds.height - 4, arc, arc);
            g2.dispose();
        }
        @Override protected JButton createDecreaseButton(int orientation) { return invisibleButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return invisibleButton(); }
        private JButton invisibleButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); b.setMinimumSize(new Dimension(0,0)); b.setMaximumSize(new Dimension(0,0)); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorder(null); return b; }
    }

    // Custom gradient logout button with hover/press/focus visuals
    private static class LogoutButton extends JButton {
        private final Color baseStart = new Color(244, 81, 96);
        private final Color baseEnd   = new Color(220, 46, 60);
        private final Color hoverStart= new Color(252,101,116);
        private final Color hoverEnd  = new Color(232, 56, 70);
        private final Color pressStart= new Color(200, 35, 48);
        private final Color pressEnd  = new Color(180, 24, 36);
        private final Color focusRing = new Color(255,255,255,150);
        private final int arc = 26;
        LogoutButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // symmetric padding to ensure centered text
            setBorder(BorderFactory.createEmptyBorder(8,24,10,24));
            setRolloverEnabled(true);
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            ButtonModel m = getModel();
            boolean hover = m.isRollover();
            boolean press = m.isArmed() && m.isPressed();
            Color s1 = baseStart, s2 = baseEnd;
            if (press) { s1 = pressStart; s2 = pressEnd; }
            else if (hover) { s1 = hoverStart; s2 = hoverEnd; }
            // Shadow behind (slight offset)
            g2.setColor(new Color(0,0,0,45));
            g2.fillRoundRect(2, 3, w-4, h-4, arc, arc);
            // Main gradient full size
            g2.setPaint(new GradientPaint(0, 0, s1, 0, h, s2));
            g2.fillRoundRect(0, 0, w-1, h-1, arc, arc);
            // Outline
            g2.setColor(new Color(255,255,255,85));
            g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
            // Focus ring inside
            if (isFocusOwner()) {
                Stroke old = g2.getStroke();
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(focusRing);
                g2.drawRoundRect(2,2,w-5,h-5,arc-4,arc-4);
                g2.setStroke(old);
            }
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width = Math.max(d.width, 150);
            return d;
        }
    }
}
