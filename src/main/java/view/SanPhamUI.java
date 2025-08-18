package view;

import model.SanPham;
import model.DanhMuc;
import controller.SanPhamController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

public class SanPhamUI extends JFrame {
    private SanPhamController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTenSP, txtMoTa, txtSearch;
    private JComboBox<DanhMuc> cmbDanhMuc;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch, btnClear;

    public SanPhamUI() {
        // Check authentication
        if (!SessionManager.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            this.dispose();
            return;
        }

        if (!RoleManager.canAccessSalesManagement()) {
            RoleManager.showAccessDeniedMessage(null);
            this.dispose();
            return;
        }

        controller = new SanPhamController();
        setTitle("Quản Lý Sản Phẩm - " + SessionManager.getInstance().getCurrentUsername() + " (" + SessionManager.getInstance().getCurrentUserRole() + ")");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadDanhMuc();
        loadTable();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Initialize buttons first
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnSearch = new JButton("Tìm kiếm");
        btnClear = new JButton("Xóa tìm kiếm");

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        searchPanel.add(new JLabel("Tìm theo tên:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Danh Mục", "Mô tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // Input panel with better layout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Mã SP:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        txtId.setEnabled(false);
        inputPanel.add(txtId, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Tên SP:"), gbc);
        gbc.gridx = 3;
        txtTenSP = new JTextField(15);
        inputPanel.add(txtTenSP, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Danh Mục:"), gbc);
        gbc.gridx = 1;
        cmbDanhMuc = new JComboBox<>();
        cmbDanhMuc.setPreferredSize(new Dimension(150, 25));
        inputPanel.add(cmbDanhMuc, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 3;
        txtMoTa = new JTextField(15);
        inputPanel.add(txtMoTa, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Enable/disable buttons based on user role
        boolean canModify = RoleManager.canModifyProducts();
        btnAdd.setEnabled(canModify);
        btnUpdate.setEnabled(canModify);
        btnDelete.setEnabled(canModify);

        // Add tooltips for disabled buttons
        if (!canModify) {
            btnAdd.setToolTipText("Chỉ Manager và Admin mới có thể thêm sản phẩm");
            btnUpdate.setToolTipText("Chỉ Manager và Admin mới có thể sửa sản phẩm");
            btnDelete.setToolTipText("Chỉ Manager và Admin mới có thể xóa sản phẩm");
        }

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // Layout assembly
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Event handlers
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        btnAdd.addActionListener(e -> {
            if (!RoleManager.canModifyProducts()) {
                RoleManager.showAccessDeniedMessage(this, "Manager hoặc Admin");
                return;
            }
            addSanPham();
        });

        btnUpdate.addActionListener(e -> {
            if (!RoleManager.canModifyProducts()) {
                RoleManager.showAccessDeniedMessage(this, "Manager hoặc Admin");
                return;
            }
            updateSanPham();
        });

        btnDelete.addActionListener(e -> {
            if (!RoleManager.canModifyProducts()) {
                RoleManager.showAccessDeniedMessage(this, "Manager hoặc Admin");
                return;
            }
            deleteSanPham();
        });

        btnRefresh.addActionListener(e -> {
            loadTable();
            clearInput();
            txtSearch.setText("");
        });

        btnSearch.addActionListener(e -> searchSanPham());

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            loadTable();
        });

        // Add Enter key support for search
        txtSearch.addActionListener(e -> searchSanPham());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    fillInputFromTable(row);
                }
            }
        });
    }

    private void addSanPham() {
        if (!validateInput()) return;

        try {
            SanPham sp = createSanPhamFromInput();
            controller.addSanPham(sp);
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            loadTable();
            clearInput();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }

    private void updateSanPham() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!");
            return;
        }

        if (!validateInput()) return;

        try {
            SanPham sp = createSanPhamFromInput();
            sp.setId(Integer.parseInt(txtId.getText()));
            controller.updateSanPham(sp);
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
            loadTable();
            clearInput();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    private void deleteSanPham() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                controller.deleteSanPham(id);
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                loadTable();
                clearInput();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm: " + e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        if (txtTenSP.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!");
            txtTenSP.requestFocus();
            return false;
        }

        if (cmbDanhMuc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục!");
            cmbDanhMuc.requestFocus();
            return false;
        }

        return true;
    }

    private SanPham createSanPhamFromInput() {
        SanPham sp = new SanPham();
        sp.setTenSP(txtTenSP.getText().trim());
        sp.setDanhMuc((DanhMuc) cmbDanhMuc.getSelectedItem());
        sp.setMoTa(txtMoTa.getText().trim());
        return sp;
    }

    private void fillInputFromTable(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtTenSP.setText(tableModel.getValueAt(row, 1).toString());
        txtMoTa.setText(tableModel.getValueAt(row, 3).toString());

        // Select the correct category - improved logic
        String danhMucName = tableModel.getValueAt(row, 2).toString();
        if (danhMucName != null && !danhMucName.equals("N/A")) {
            for (int i = 0; i < cmbDanhMuc.getItemCount(); i++) {
                DanhMuc dm = cmbDanhMuc.getItemAt(i);
                if (dm != null && dm.getTenDM() != null && dm.getTenDM().equals(danhMucName)) {
                    cmbDanhMuc.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cmbDanhMuc.setSelectedIndex(-1);
        }
    }

    private void clearInput() {
        txtId.setText("");
        txtTenSP.setText("");
        txtMoTa.setText("");
        cmbDanhMuc.setSelectedIndex(-1);
        table.clearSelection();
    }

    private void loadDanhMuc() {
        // This method should load categories from controller
        // Implementation depends on your controller structure
        cmbDanhMuc.removeAllItems();
        try {
            List<DanhMuc> categories = controller.getAllDanhMuc();
            for (DanhMuc dm : categories) {
                cmbDanhMuc.addItem(dm);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh mục: " + e.getMessage());
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<SanPham> list = controller.getAllSanPham();
            for (SanPham sp : list) {
                tableModel.addRow(new Object[]{
                        sp.getId(),
                        sp.getTenSP(),
                        sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDM() : "N/A",
                        sp.getMoTa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void searchSanPham() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<SanPham> list = controller.searchSanPham(keyword);
            for (SanPham sp : list) {
                tableModel.addRow(new Object[]{
                        sp.getId(),
                        sp.getTenSP(),
                        sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDM() : "N/A",
                        sp.getMoTa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }
}
