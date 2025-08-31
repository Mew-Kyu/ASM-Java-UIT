package view;

import model.HinhThucThanhToan;
import dao.impl.HinhThucThanhToanDAO;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * UI for Payment Methods Management
 */
public class HinhThucThanhToanUI extends BaseAuthenticatedUI {
    
    private HinhThucThanhToanDAO hinhThucThanhToanDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Form components
    private JTextField txtTenHTTT, txtMoTa;
    private JComboBox<String> cmbLoaiThanhToan;
    private JCheckBox chkTrangThai, chkThuPhi, chkYeuCauXacThuc;
    private JSpinner spnPhanTramPhi, spnPhiCoDinh, spnThoiGianXuLy, spnThuTuHienThi;
    
    private HinhThucThanhToan selectedHTTT;
    
    public HinhThucThanhToanUI() {
        super();
        if (!RoleManager.canAccessPaymentMethods()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý hình thức thanh toán", "Manager hoặc Admin");
            dispose();
            return;
        }
        
        this.hinhThucThanhToanDAO = new HinhThucThanhToanDAO();
        initComponents();
        loadData();
    }
    
    protected void initComponents() {
        setTitle("Quản Lý Hình Thức Thanh Toán");
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông Tin Hình Thức Thanh Toán"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1 - Tên HTTT và Loại thanh toán
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        JLabel lblTenHTTT = new JLabel("Tên hình thức thanh toán:");
        lblTenHTTT.setFont(lblTenHTTT.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblTenHTTT, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTenHTTT = new JTextField(25);
        txtTenHTTT.setPreferredSize(new Dimension(250, 28));
        panel.add(txtTenHTTT, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 30, 8, 10);
        JLabel lblLoai = new JLabel("Loại thanh toán:");
        lblLoai.setFont(lblLoai.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblLoai, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        cmbLoaiThanhToan = new JComboBox<>(new String[]{
            "CASH - Tiền mặt",
            "CARD - Thẻ",
            "BANK_TRANSFER - Chuyển khoản",
            "E_WALLET - Ví điện tử",
            "OTHER - Khác"
        });
        cmbLoaiThanhToan.setPreferredSize(new Dimension(200, 28));
        panel.add(cmbLoaiThanhToan, gbc);
        
        // Row 2 - Mô tả (full width)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setFont(lblMoTa.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblMoTa, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMoTa = new JTextField();
        txtMoTa.setPreferredSize(new Dimension(0, 28));
        panel.add(txtMoTa, gbc);
        
        // Row 3 - Phần trăm phí và Phí cố định
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblPhanTramPhi = new JLabel("Phần trăm phí (%):");
        lblPhanTramPhi.setFont(lblPhanTramPhi.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblPhanTramPhi, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        spnPhanTramPhi = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        spnPhanTramPhi.setPreferredSize(new Dimension(250, 28));
        // Format spinner to show percentage
        JSpinner.NumberEditor editor1 = new JSpinner.NumberEditor(spnPhanTramPhi, "0.0");
        spnPhanTramPhi.setEditor(editor1);
        panel.add(spnPhanTramPhi, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 30, 8, 10);
        JLabel lblPhiCoDinh = new JLabel("Phí cố định (VNĐ):");
        lblPhiCoDinh.setFont(lblPhiCoDinh.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblPhiCoDinh, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        spnPhiCoDinh = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 1000.0));
        spnPhiCoDinh.setPreferredSize(new Dimension(200, 28));
        // Format spinner to show currency
        JSpinner.NumberEditor editor2 = new JSpinner.NumberEditor(spnPhiCoDinh, "#,##0");
        spnPhiCoDinh.setEditor(editor2);
        panel.add(spnPhiCoDinh, gbc);
        
        // Row 4 - Thời gian xử lý và Thứ tự hiển thị
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblThoiGianXuLy = new JLabel("Thời gian xử lý (phút):");
        lblThoiGianXuLy.setFont(lblThoiGianXuLy.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblThoiGianXuLy, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        spnThoiGianXuLy = new JSpinner(new SpinnerNumberModel(0, 0, 1440, 1));
        spnThoiGianXuLy.setPreferredSize(new Dimension(250, 28));
        panel.add(spnThoiGianXuLy, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 30, 8, 10);
        JLabel lblThuTuHienThi = new JLabel("Thứ tự hiển thị:");
        lblThuTuHienThi.setFont(lblThuTuHienThi.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblThuTuHienThi, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        spnThuTuHienThi = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnThuTuHienThi.setPreferredSize(new Dimension(200, 28));
        panel.add(spnThuTuHienThi, gbc);
        
        // Row 5 - Checkboxes với khoảng cách đều
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 10, 8, 10);

        chkTrangThai = new JCheckBox("Kích hoạt");
        chkTrangThai.setSelected(true);
        chkTrangThai.setFont(chkTrangThai.getFont().deriveFont(Font.BOLD, 12f));
        chkTrangThai.setBackground(Color.WHITE);
        panel.add(chkTrangThai, gbc);
        
        gbc.gridx = 1;
        chkThuPhi = new JCheckBox("Thu phí");
        chkThuPhi.setFont(chkThuPhi.getFont().deriveFont(Font.BOLD, 12f));
        chkThuPhi.setBackground(Color.WHITE);
        panel.add(chkThuPhi, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        chkYeuCauXacThuc = new JCheckBox("Yêu cầu xác thực");
        chkYeuCauXacThuc.setFont(chkYeuCauXacThuc.getFont().deriveFont(Font.BOLD, 12f));
        chkYeuCauXacThuc.setBackground(Color.WHITE);
        panel.add(chkYeuCauXacThuc, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh Sách Hình Thức Thanh Toán"));
        
        // Create table
        String[] columns = {"Mã HTTT", "Tên HTTT", "Loại", "Trạng thái", "Thu phí", 
                           "Phần trăm phí", "Phí cố định", "Thời gian xử lý", "Thứ tự"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedHTTT();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnThem = new JButton("Thêm");
        btnThem.addActionListener(this::themHTTT);
        btnThem.setEnabled(RoleManager.canConfigurePaymentMethods());
        
        JButton btnSua = new JButton("Sửa");
        btnSua.addActionListener(this::suaHTTT);
        btnSua.setEnabled(RoleManager.canConfigurePaymentMethods());
        
        JButton btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(this::xoaHTTT);
        btnXoa.setEnabled(RoleManager.canConfigurePaymentMethods());
        
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            clearForm();
            loadData();
        });
        
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        panel.add(btnDong);
        
        return panel;
    }
    
    private void loadData() {
        try {
            List<HinhThucThanhToan> list = hinhThucThanhToanDAO.findAll();
            tableModel.setRowCount(0);
            
            for (HinhThucThanhToan httt : list) {
                tableModel.addRow(new Object[]{
                    httt.getMaHTTT(),
                    httt.getTenHTTT(),
                    httt.getLoaiThanhToan(),
                    httt.isTrangThai() ? "Kích hoạt" : "Vô hiệu hóa",
                    httt.isThuPhi() ? "Có" : "Không",
                    httt.getPhanTramPhi() + "%",
                    String.format("%,.0f VNĐ", httt.getPhiCoDinh()),
                    httt.getThoiGianXuLy() + " phút",
                    httt.getThuTuHienThi()
                });
            }
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void loadSelectedHTTT() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int maHTTT = (Integer) tableModel.getValueAt(selectedRow, 0);
                selectedHTTT = hinhThucThanhToanDAO.findById(maHTTT).orElse(null);
                
                if (selectedHTTT != null) {
                    populateForm(selectedHTTT);
                }
            } catch (Exception e) {
                showError("Lỗi khi tải thông tin hình thức thanh toán: " + e.getMessage());
            }
        }
    }
    
    private void populateForm(HinhThucThanhToan httt) {
        txtTenHTTT.setText(httt.getTenHTTT());
        txtMoTa.setText(httt.getMoTa());
        cmbLoaiThanhToan.setSelectedItem(httt.getLoaiThanhToan());
        chkTrangThai.setSelected(httt.isTrangThai());
        chkThuPhi.setSelected(httt.isThuPhi());
        chkYeuCauXacThuc.setSelected(httt.isYeuCauXacThuc());
        spnPhanTramPhi.setValue(httt.getPhanTramPhi().doubleValue());
        spnPhiCoDinh.setValue(httt.getPhiCoDinh().doubleValue());
        spnThoiGianXuLy.setValue(httt.getThoiGianXuLy());
        spnThuTuHienThi.setValue(httt.getThuTuHienThi());
    }
    
    private void clearForm() {
        txtTenHTTT.setText("");
        txtMoTa.setText("");
        cmbLoaiThanhToan.setSelectedIndex(0);
        chkTrangThai.setSelected(true);
        chkThuPhi.setSelected(false);
        chkYeuCauXacThuc.setSelected(false);
        spnPhanTramPhi.setValue(0.0);
        spnPhiCoDinh.setValue(0.0);
        spnThoiGianXuLy.setValue(0);
        spnThuTuHienThi.setValue(1);
        selectedHTTT = null;
    }
    
    private void themHTTT(ActionEvent e) {
        if (!RoleManager.canConfigurePaymentMethods()) {
            RoleManager.showAccessDeniedMessage(this, "Thêm hình thức thanh toán", "Admin");
            return;
        }
        
        try {
            // Validate
            if (txtTenHTTT.getText().trim().isEmpty()) {
                showError("Vui lòng nhập tên hình thức thanh toán!");
                return;
            }
            
            // Create new HinhThucThanhToan
            HinhThucThanhToan httt = new HinhThucThanhToan();
            httt.setTenHTTT(txtTenHTTT.getText().trim());
            httt.setMoTa(txtMoTa.getText().trim());
            httt.setLoaiThanhToan((String) cmbLoaiThanhToan.getSelectedItem());
            httt.setTrangThai(chkTrangThai.isSelected());
            httt.setThuPhi(chkThuPhi.isSelected());
            httt.setYeuCauXacThuc(chkYeuCauXacThuc.isSelected());
            httt.setPhanTramPhi(BigDecimal.valueOf((Double) spnPhanTramPhi.getValue()));
            httt.setPhiCoDinh(BigDecimal.valueOf((Double) spnPhiCoDinh.getValue()));
            // Extract the enum value from the selected item (e.g., "CASH - Tiền mặt" -> "CASH")
            String selectedItem = (String) cmbLoaiThanhToan.getSelectedItem();
            String loaiThanhToan = selectedItem.split(" - ")[0];

            httt.setThoiGianXuLy((Integer) spnThoiGianXuLy.getValue());
            httt.setThuTuHienThi((Integer) spnThuTuHienThi.getValue());
            
            hinhThucThanhToanDAO.insert(httt);
            httt.setLoaiThanhToan(loaiThanhToan);
            clearForm();
            loadData();
            
        } catch (Exception ex) {
            showError("Lỗi khi thêm hình thức thanh toán: " + ex.getMessage());
        }
    }

    private void suaHTTT(ActionEvent e) {
        if (!RoleManager.canConfigurePaymentMethods()) {
            RoleManager.showAccessDeniedMessage(this, "Sửa hình thức thanh toán", "Admin");
            return;
        }

        if (selectedHTTT == null) {
            showError("Vui lòng chọn hình thức thanh toán cần sửa!");
            return;
        }
        
        try {
            // Validate
            if (txtTenHTTT.getText().trim().isEmpty()) {
                showError("Vui lòng nhập tên hình thức thanh toán!");
                return;
            }
            
            // Update HinhThucThanhToan
            selectedHTTT.setTenHTTT(txtTenHTTT.getText().trim());
            selectedHTTT.setMoTa(txtMoTa.getText().trim());
            selectedHTTT.setLoaiThanhToan((String) cmbLoaiThanhToan.getSelectedItem());
            selectedHTTT.setTrangThai(chkTrangThai.isSelected());
            selectedHTTT.setThuPhi(chkThuPhi.isSelected());
            selectedHTTT.setYeuCauXacThuc(chkYeuCauXacThuc.isSelected());
            selectedHTTT.setPhanTramPhi(BigDecimal.valueOf((Double) spnPhanTramPhi.getValue()));
            selectedHTTT.setPhiCoDinh(BigDecimal.valueOf((Double) spnPhiCoDinh.getValue()));
            selectedHTTT.setThoiGianXuLy((Integer) spnThoiGianXuLy.getValue());
            // Extract the enum value from the selected item (e.g., "CASH - Tiền mặt" -> "CASH")
            String selectedItem = (String) cmbLoaiThanhToan.getSelectedItem();
            String loaiThanhToan = selectedItem.split(" - ")[0];

            selectedHTTT.setThuTuHienThi((Integer) spnThuTuHienThi.getValue());
            
            hinhThucThanhToanDAO.update(selectedHTTT);
            selectedHTTT.setLoaiThanhToan(loaiThanhToan);
            loadData();
            
        } catch (Exception ex) {
            showError("Lỗi khi cập nhật hình thức thanh toán: " + ex.getMessage());
        }
    }

    private void xoaHTTT(ActionEvent e) {
        if (!RoleManager.canConfigurePaymentMethods()) {
            RoleManager.showAccessDeniedMessage(this, "Xóa hình thức thanh toán", "Admin");
            return;
        }
        
        if (selectedHTTT == null) {
            showError("Vui lòng chọn hình thức thanh toán cần xóa!");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa hình thức thanh toán: " + selectedHTTT.getTenHTTT() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                hinhThucThanhToanDAO.delete(selectedHTTT.getMaHTTT());
                showSuccess("Xóa hình thức thanh toán thành công!");
                clearForm();
                loadData();
            } catch (Exception ex) {
                showError("Lỗi khi xóa hình thức thanh toán: " + ex.getMessage());
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
