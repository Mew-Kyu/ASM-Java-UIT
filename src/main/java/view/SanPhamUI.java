package view;

import model.SanPham;
import model.DanhMuc;
import controller.SanPhamController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class SanPhamUI extends JFrame {
    private SanPhamController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTenSP, txtGia, txtSoLuong, txtMoTa;
    private JComboBox<DanhMuc> cmbDanhMuc;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

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
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadDanhMuc();
        loadTable();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Giá", "Số lượng", "Danh Mục", "Mô tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 4));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));

        inputPanel.add(new JLabel("Mã SP:"));
        txtId = new JTextField();
        txtId.setEnabled(false);
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Tên SP:"));
        txtTenSP = new JTextField();
        inputPanel.add(txtTenSP);

        inputPanel.add(new JLabel("Giá:"));
        txtGia = new JTextField();
        inputPanel.add(txtGia);

        inputPanel.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField();
        inputPanel.add(txtSoLuong);

        inputPanel.add(new JLabel("Danh Mục:"));
        cmbDanhMuc = new JComboBox<>();
        inputPanel.add(cmbDanhMuc);

        inputPanel.add(new JLabel("Mô tả:"));
        txtMoTa = new JTextField();
        inputPanel.add(txtMoTa);

        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

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
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                fillInputFromTable(row);
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
            return false;
        }

        try {
            BigDecimal.valueOf(Double.parseDouble(txtGia.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải l�� số!");
            return false;
        }

        try {
            Integer.parseInt(txtSoLuong.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!");
            return false;
        }

        if (cmbDanhMuc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục!");
            return false;
        }

        return true;
    }

    private SanPham createSanPhamFromInput() {
        SanPham sp = new SanPham();
        sp.setTenSP(txtTenSP.getText().trim());
        sp.setGia(BigDecimal.valueOf(Double.parseDouble(txtGia.getText().trim())));
        sp.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
        sp.setDanhMuc((DanhMuc) cmbDanhMuc.getSelectedItem());
        sp.setMoTa(txtMoTa.getText().trim());
        return sp;
    }

    private void fillInputFromTable(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtTenSP.setText(tableModel.getValueAt(row, 1).toString());
        txtGia.setText(tableModel.getValueAt(row, 2).toString());
        txtSoLuong.setText(tableModel.getValueAt(row, 3).toString());
        txtMoTa.setText(tableModel.getValueAt(row, 5).toString());

        // Select the correct category - improved logic
        String danhMucName = tableModel.getValueAt(row, 4).toString();
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
        txtGia.setText("");
        txtSoLuong.setText("");
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
                        sp.getGia(),
                        sp.getSoLuong(),
                        sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDM() : "N/A",
                        sp.getMoTa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
}
