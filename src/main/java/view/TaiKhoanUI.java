package view;

import controller.TaiKhoanController;
import model.TaiKhoan;
import model.NhanVien;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanUI extends JFrame {
    private TaiKhoanController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JTextField txtMaNV;
    private JButton btnSelectNhanVien;
    private JComboBox<String> cmbQuyen;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private String selectedUsername = null;
    private boolean isUpdating = false;
    private List<TaiKhoan> allAccounts;
    private List<TaiKhoan> filteredAccounts;
    private NhanVien selectedNhanVien = null;
    // Pagination fields
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private JLabel lblPageInfo;
    private JButton btnFirstPage, btnPrevPage, btnNextPage, btnLastPage;
    private JComboBox<Integer> cmbPageSize;
    private Runnable logoutListenerRef; // reference to remove later

    public TaiKhoanUI() {
        // Check authentication and authorization
        if (!SessionManager.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            this.dispose();
            return;
        }

        if (!RoleManager.canAccessAccountManagement()) {
            RoleManager.showAccessDeniedMessage(null, "Admin");
            this.dispose();
            return;
        }

        controller = new TaiKhoanController();
        setTitle("Quản Lý Tài Khoản - " + SessionManager.getInstance().getCurrentUsername());
        setSize(600, 560); // increased height for pagination bar
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        loadTable();

        // Auto-close on logout (security)
        logoutListenerRef = () -> {
            if (!SessionManager.getInstance().isLoggedIn()) {
                SwingUtilities.invokeLater(() -> {
                    try { dispose(); } catch (Exception ignored) { }
                });
            }
        };
        SessionManager.getInstance().addLogoutListener(logoutListenerRef);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupLogoutListener();
                dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                cleanupLogoutListener();
            }
        });
    }

    private void cleanupLogoutListener() {
        if (logoutListenerRef != null) {
            SessionManager.getInstance().removeLogoutListener(logoutListenerRef);
            logoutListenerRef = null;
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Tìm theo tên đăng nhập, tên nhân viên hoặc quyền"); // updated tooltip
        searchPanel.add(txtSearch);

        btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(70, 25));
        searchPanel.add(btnSearch);

        btnClearSearch = new JButton("Xóa");
        btnClearSearch.setPreferredSize(new Dimension(70, 25));
        searchPanel.add(btnClearSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Tên Đăng Nhập", "Nhân Viên", "Quyền"}, 0) { // column header changed
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        // Pagination bar below table
        tablePanel.add(createPaginationBar(), BorderLayout.SOUTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Tên Đăng Nhập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenDangNhap = new JTextField(20);
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
        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setToolTipText("Để trống nếu không muốn thay đổi mật khẩu (khi cập nhật)");
        formPanel.add(txtMatKhau, gbc);

        // Employee (selection instead of manual ID)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nhân Viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel employeeSelectPanel = new JPanel(new BorderLayout(5, 0));
        txtMaNV = new JTextField(20);
        txtMaNV.setEditable(false); // make read-only
        txtMaNV.setToolTipText("Chọn nhân viên bằng nút bên cạnh");
        btnSelectNhanVien = new JButton("Chọn");
        btnSelectNhanVien.setPreferredSize(new Dimension(70, txtMaNV.getPreferredSize().height));
        employeeSelectPanel.add(txtMaNV, BorderLayout.CENTER);
        employeeSelectPanel.add(btnSelectNhanVien, BorderLayout.EAST);
        formPanel.add(employeeSelectPanel, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Quyền:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbQuyen = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "STAFF"});
        formPanel.add(cmbQuyen, gbc);

        // Create a container for form and buttons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(new Dimension(80, 30));
        btnUpdate = new JButton("Cập nhật");
        btnUpdate.setPreferredSize(new Dimension(100, 30));
        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(80, 30));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnClear = new JButton("Xóa form");
        btnClear.setPreferredSize(new Dimension(100, 30));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel);

        // Add action listeners
        setupEventHandlers();
    }

    private JPanel createPaginationBar() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 1));
        btnFirstPage = new JButton("|<");
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        btnLastPage = new JButton(">|");
        // Further shrink pagination buttons
        Dimension tinyBtnSize = new Dimension(32, 20);
        Insets tinyInsets = new Insets(0, 4, 0, 4);
        JButton[] navButtons = {btnFirstPage, btnPrevPage, btnNextPage, btnLastPage};
        for (JButton b : navButtons) {
            b.setPreferredSize(tinyBtnSize);
            b.setMinimumSize(tinyBtnSize);
            b.setMargin(tinyInsets);
            b.setFont(b.getFont().deriveFont(Font.PLAIN, 10f));
        }
        lblPageInfo = new JLabel("Trang 1/1");
        lblPageInfo.setFont(lblPageInfo.getFont().deriveFont(Font.PLAIN, 10f));
        cmbPageSize = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        cmbPageSize.setSelectedItem(10);
        cmbPageSize.setFont(cmbPageSize.getFont().deriveFont(Font.PLAIN, 10f));
        cmbPageSize.setPreferredSize(new Dimension(50, 20));
        JLabel lblShow = new JLabel("Hiển thị:");
        lblShow.setFont(lblShow.getFont().deriveFont(Font.PLAIN, 10f));
        JLabel lblPerPage = new JLabel("/ trang");
        lblPerPage.setFont(lblPerPage.getFont().deriveFont(Font.PLAIN, 10f));
        paginationPanel.add(lblShow);
        paginationPanel.add(cmbPageSize);
        paginationPanel.add(lblPerPage);
        paginationPanel.add(btnFirstPage);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNextPage);
        paginationPanel.add(btnLastPage);
        return paginationPanel;
    }

    private void setupEventHandlers() {
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTaiKhoan();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTaiKhoan();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTaiKhoan();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTable();
                clearForm();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // Add search handlers
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        btnClearSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSearch();
            }
        });

        // Allow Enter key to trigger search
        txtSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // Select employee button
        btnSelectNhanVien.addActionListener(e -> {
            NhanVien nv = EmployeeSelectionDialog.showDialog(this);
            if (nv != null) {
                selectedNhanVien = nv;
                txtMaNV.setText(nv.getHoTen() + " (" + nv.getId() + ")");
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    isUpdating = true;
                    selectedUsername = tableModel.getValueAt(row, 0).toString();
                    txtTenDangNhap.setText(selectedUsername);
                    txtMatKhau.setText(""); // Clear password field - don't show hashed password
                    // Find TaiKhoan object in cache to set selectedNhanVien
                    TaiKhoan tk = findAccountByUsername(selectedUsername);
                    if (tk != null && tk.getMaNV() != null) {
                        selectedNhanVien = tk.getMaNV();
                        txtMaNV.setText(selectedNhanVien.getHoTen() + " (" + selectedNhanVien.getId() + ")");
                    } else {
                        selectedNhanVien = null;
                        txtMaNV.setText("");
                    }
                    cmbQuyen.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                } else {
                    isUpdating = false;
                }
            }
        });

        // Pagination listeners
        btnFirstPage.addActionListener(e -> { currentPage = 1; refreshTablePage(); });
        btnPrevPage.addActionListener(e -> { if(currentPage>1){ currentPage--; refreshTablePage(); } });
        btnNextPage.addActionListener(e -> { if(currentPage<totalPages){ currentPage++; refreshTablePage(); } });
        btnLastPage.addActionListener(e -> { currentPage = totalPages; refreshTablePage(); });
        cmbPageSize.addActionListener(e -> { Integer sel = (Integer)cmbPageSize.getSelectedItem(); if(sel!=null && sel!=pageSize){ pageSize = sel; currentPage = 1; refreshTablePage(); } });
    }

    private TaiKhoan findAccountByUsername(String username) {
        if (allAccounts == null) return null;
        for (TaiKhoan tk : allAccounts) {
            if (tk.getTenDangNhap().equals(username)) return tk;
        }
        return null;
    }

    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            // Reset to full list
            filteredAccounts = new ArrayList<>(allAccounts != null ? allAccounts : new ArrayList<>());
        } else {
            if (allAccounts == null) allAccounts = controller.getAllTaiKhoan();
            filteredAccounts = new ArrayList<>();
            for (TaiKhoan tk : allAccounts) {
                String username = tk.getTenDangNhap().toLowerCase();
                String employeeName = tk.getMaNV() != null ? tk.getMaNV().getHoTen().toLowerCase() : "";
                String role = tk.getQuyen().toLowerCase();
                if (username.contains(searchText) || employeeName.contains(searchText) || role.contains(searchText)) {
                    filteredAccounts.add(tk);
                }
            }
            if (filteredAccounts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản nào phù hợp với từ khóa: " + txtSearch.getText(), "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        currentPage = 1;
        recalcTotalPages();
        refreshTablePage();
    }

    private void clearSearch() {
        txtSearch.setText("");
        filteredAccounts = new ArrayList<>(allAccounts != null ? allAccounts : new ArrayList<>());
        currentPage = 1;
        recalcTotalPages();
        refreshTablePage();
        clearForm();
    }

    private void recalcTotalPages() {
        int totalRecords = filteredAccounts != null ? filteredAccounts.size() : 0;
        totalPages = totalRecords == 0 ? 1 : (int) Math.ceil(totalRecords / (double) pageSize);
        if (currentPage > totalPages) currentPage = totalPages;
    }

    private void refreshTablePage() {
        tableModel.setRowCount(0);
        if (filteredAccounts == null) {
            filteredAccounts = new ArrayList<>();
        }
        int totalRecords = filteredAccounts.size();
        if (totalRecords == 0) {
            lblPageInfo.setText("Trang 0/0");
            return;
        }
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalRecords);
        for (int i = startIndex; i < endIndex; i++) {
            TaiKhoan tk = filteredAccounts.get(i);
            tableModel.addRow(new Object[]{ tk.getTenDangNhap(), tk.getMaNV() != null ? tk.getMaNV().getHoTen() : "N/A", tk.getQuyen() });
        }
        lblPageInfo.setText("Trang " + currentPage + "/" + totalPages + " (" + totalRecords + " mục)");
        updatePaginationButtonsState();
    }

    private void updatePaginationButtonsState() {
        boolean hasData = filteredAccounts != null && !filteredAccounts.isEmpty();
        btnFirstPage.setEnabled(hasData && currentPage > 1);
        btnPrevPage.setEnabled(hasData && currentPage > 1);
        btnNextPage.setEnabled(hasData && currentPage < totalPages);
        btnLastPage.setEnabled(hasData && currentPage < totalPages);
    }

    private void addTaiKhoan() {
        if (!validateForm()) return;

        String password = new String(txtMatKhau.getPassword()).trim();
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            TaiKhoan tk = getTaiKhoanFromForm();
            controller.addTaiKhoan(tk);
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTaiKhoan() {
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) return;

        try {
            TaiKhoan tk = getTaiKhoanFromForm();
            // If password field is empty, it means user doesn't want to change password
            String password = new String(txtMatKhau.getPassword()).trim();
            if (password.isEmpty()) {
                tk.setMatKhau(null); // Let controller handle keeping old password
            }

            controller.updateTaiKhoan(tk);
            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTaiKhoan() {
        if (selectedUsername == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Prevent deleting current user
        if (selectedUsername.equals(SessionManager.getInstance().getCurrentUsername())) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài khoản: " + selectedUsername + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                controller.deleteTaiKhoan(selectedUsername);
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTable();
                clearForm();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (selectedNhanVien == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private TaiKhoan getTaiKhoanFromForm() {
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(txtTenDangNhap.getText().trim());
        tk.setMatKhau(new String(txtMatKhau.getPassword()).trim());
        tk.setQuyen(cmbQuyen.getSelectedItem().toString());
        tk.setMaNV(selectedNhanVien); // set selected employee
        return tk;
    }

    private void clearForm() {
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtMaNV.setText("");
        cmbQuyen.setSelectedIndex(0);
        selectedUsername = null;
        selectedNhanVien = null;
        isUpdating = false;
        table.clearSelection();
    }

    private void loadTable() {
        try {
            allAccounts = controller.getAllTaiKhoan();
            filteredAccounts = new ArrayList<>(allAccounts);
        } catch (Exception e) {
            allAccounts = new ArrayList<>();
            filteredAccounts = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        currentPage = 1;
        recalcTotalPages();
        refreshTablePage();
    }
}
