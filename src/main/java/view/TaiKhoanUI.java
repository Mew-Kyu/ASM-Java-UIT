package view;

import controller.TaiKhoanController;
import model.TaiKhoan;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TaiKhoanUI extends JFrame {
    private TaiKhoanController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTenDangNhap, txtMaNV;
    private JComboBox<String> cmbQuyen;
    private JPasswordField txtMatKhau;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    // Add search components
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private String selectedUsername = null;
    private boolean isUpdating = false;
    private List<TaiKhoan> allAccounts; // Store all accounts for filtering

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
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Prevent window resizing/zooming
        initComponents();
        loadTable();

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Tìm theo tên đăng nhập, mã NV hoặc quyền");
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
        tableModel = new DefaultTableModel(new Object[]{"Tên Đăng Nhập", "Mã NV", "Quyền"}, 0) {
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

        // Employee ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtMaNV = new JTextField(20);
        formPanel.add(txtMaNV, gbc);

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

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    isUpdating = true;
                    selectedUsername = tableModel.getValueAt(row, 0).toString();
                    txtTenDangNhap.setText(selectedUsername);
                    txtMatKhau.setText(""); // Clear password field - don't show hashed password
                    txtMaNV.setText(tableModel.getValueAt(row, 1).toString());
                    cmbQuyen.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                } else {
                    isUpdating = false;
                }
            }
        });
    }

    // Add search methods
    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadTable(); // Show all records if search is empty
            return;
        }

        tableModel.setRowCount(0);
        try {
            if (allAccounts == null) {
                allAccounts = controller.getAllTaiKhoan();
            }

            for (TaiKhoan tk : allAccounts) {
                String username = tk.getTenDangNhap().toLowerCase();
                String employeeId = tk.getMaNV() != null ? tk.getMaNV().getId().toString() : "";
                String role = tk.getQuyen().toLowerCase();

                // Check if any field contains the search text
                if (username.contains(searchText) ||
                        employeeId.contains(searchText) ||
                        role.contains(searchText)) {

                    tableModel.addRow(new Object[]{
                            tk.getTenDangNhap(),
                            tk.getMaNV() != null ? tk.getMaNV().getId() : "N/A",
                            tk.getQuyen()
                    });
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy tài khoản nào phù hợp với từ khóa: " + txtSearch.getText(),
                        "Kết quả tìm kiếm",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        loadTable(); // Reload all data
        clearForm();
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

        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private TaiKhoan getTaiKhoanFromForm() {
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(txtTenDangNhap.getText().trim());
        tk.setMatKhau(new String(txtMatKhau.getPassword()).trim());
        tk.setQuyen(cmbQuyen.getSelectedItem().toString());

        // Get employee by ID
        String maNV = txtMaNV.getText().trim();
        tk.setMaNV(controller.getNhanVienById(maNV));

        return tk;
    }

    private void clearForm() {
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtMaNV.setText("");
        cmbQuyen.setSelectedIndex(0);
        selectedUsername = null;
        isUpdating = false;
        table.clearSelection();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            allAccounts = controller.getAllTaiKhoan(); // Store for search functionality
            for (TaiKhoan tk : allAccounts) {
                tableModel.addRow(new Object[]{
                        tk.getTenDangNhap(),
                        tk.getMaNV() != null ? tk.getMaNV().getId() : "N/A",
                        tk.getQuyen()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
