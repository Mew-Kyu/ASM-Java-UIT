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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên HTTT:"), gbc);
        gbc.gridx = 1;
        txtTenHTTT = new JTextField(20);
        panel.add(txtTenHTTT, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Loại:"), gbc);
        gbc.gridx = 3;
        cmbLoaiThanhToan = new JComboBox<>(new String[]{"CASH", "CARD", "BANK_TRANSFER", "E_WALLET", "OTHER"});
        panel.add(cmbLoaiThanhToan, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtMoTa = new JTextField(40);
        panel.add(txtMoTa, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Phần trăm phí (%):"), gbc);
        gbc.gridx = 1;
        spnPhanTramPhi = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        panel.add(spnPhanTramPhi, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Phí cố định:"), gbc);
        gbc.gridx = 3;
        spnPhiCoDinh = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 1000.0));
        panel.add(spnPhiCoDinh, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Thời gian xử lý (phút):"), gbc);
        gbc.gridx = 1;
        spnThoiGianXuLy = new JSpinner(new SpinnerNumberModel(0, 0, 1440, 1));
        panel.add(spnThoiGianXuLy, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Thứ tự hiển thị:"), gbc);
        gbc.gridx = 3;
        spnThuTuHienThi = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        panel.add(spnThuTuHienThi, gbc);
        
        // Row 5 - Checkboxes
        gbc.gridx = 0; gbc.gridy = 4;
        chkTrangThai = new JCheckBox("Kích hoạt");
        chkTrangThai.setSelected(true);
        panel.add(chkTrangThai, gbc);
        
        gbc.gridx = 1;
        chkThuPhi = new JCheckBox("Thu phí");
        panel.add(chkThuPhi, gbc);
        
        gbc.gridx = 2;
        chkYeuCauXacThuc = new JCheckBox("Yêu cầu xác thực");
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
            httt.setThoiGianXuLy((Integer) spnThoiGianXuLy.getValue());
            httt.setThuTuHienThi((Integer) spnThuTuHienThi.getValue());
            
            hinhThucThanhToanDAO.insert(httt);
            showSuccess("Thêm hình thức thanh toán thành công!");
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
            selectedHTTT.setThuTuHienThi((Integer) spnThuTuHienThi.getValue());
            
            hinhThucThanhToanDAO.update(selectedHTTT);
            showSuccess("Cập nhật hình thức thanh toán thành công!");
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
