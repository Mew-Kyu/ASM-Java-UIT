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
    private String selectedUsername = null;
    private boolean isUpdating = false;

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

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Tên Đăng Nhập", "Mã NV", "Quyền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Tên Đăng Nhập:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtTenDangNhap = new JTextField(20);
        formPanel.add(txtTenDangNhap, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Mật Khẩu:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setToolTipText("Để trống nếu không muốn thay đổi mật khẩu (khi cập nhật)");
        formPanel.add(txtMatKhau, gbc);

        // Employee ID
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMaNV = new JTextField(20);
        formPanel.add(txtMaNV, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Quyền:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbQuyen = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "STAFF"});
        formPanel.add(cmbQuyen, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xóa form");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);
        panel.add(buttonPanel, BorderLayout.SOUTH);

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
            List<TaiKhoan> accounts = controller.getAllTaiKhoan();
            for (TaiKhoan tk : accounts) {
                tableModel.addRow(new Object[]{
                        tk.getTenDangNhap(),
                        tk.getMaNV() != null ? tk.getMaNV().getId(): "N/A",
                        tk.getQuyen()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
