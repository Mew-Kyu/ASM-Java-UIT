package view;

import model.NhaCungCap;
import dao.impl.NhaCungCapDAO;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * UI for Supplier Management
 */
public class NhaCungCapUI extends BaseAuthenticatedUI {
    
    private NhaCungCapDAO nhaCungCapDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Form components
    private JTextField txtTenNCC, txtDiaChi, txtDienThoai, txtEmail;
    private JTextField txtNguoiLienHe, txtChucVuLienHe, txtMoTa, txtGhiChu;
    private JSpinner spnRating;
    private JCheckBox chkTrangThai;
    
    private NhaCungCap selectedNhaCungCap;
    
    public NhaCungCapUI() {
        super();
        if (!RoleManager.canAccessSupplierManagement()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý nhà cung cấp", "Manager hoặc Admin");
            dispose();
            return;
        }
        
        this.nhaCungCapDAO = new NhaCungCapDAO();
        initComponents();
        loadData();
    }
    
    protected void initComponents() {
        setTitle("Quản Lý Nhà Cung Cấp");
        setSize(1200, 700);
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
        panel.setBorder(BorderFactory.createTitledBorder("Thông Tin Nhà Cung Cấp"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên NCC:"), gbc);
        gbc.gridx = 1;
        txtTenNCC = new JTextField(20);
        panel.add(txtTenNCC, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Điện thoại:"), gbc);
        gbc.gridx = 3;
        txtDienThoai = new JTextField(15);
        panel.add(txtDienThoai, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDiaChi = new JTextField(40);
        panel.add(txtDiaChi, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Rating:"), gbc);
        gbc.gridx = 3;
        spnRating = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        panel.add(spnRating, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Người liên hệ:"), gbc);
        gbc.gridx = 1;
        txtNguoiLienHe = new JTextField(20);
        panel.add(txtNguoiLienHe, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridx = 3;
        txtChucVuLienHe = new JTextField(15);
        panel.add(txtChucVuLienHe, gbc);
        
        // Row 5
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtMoTa = new JTextField(40);
        panel.add(txtMoTa, gbc);
        
        // Row 6
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtGhiChu = new JTextField(30);
        panel.add(txtGhiChu, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1;
        chkTrangThai = new JCheckBox("Đang hợp tác");
        chkTrangThai.setSelected(true);
        panel.add(chkTrangThai, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh Sách Nhà Cung Cấp"));
        
        // Create table
        String[] columns = {"Mã NCC", "Tên NCC", "Địa chỉ", "Điện thoại", "Email", 
                           "Người liên hệ", "Rating", "Trạng thái", "Tổng GT mua", "Số đơn hàng"};
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
                loadSelectedNhaCungCap();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnThem = new JButton("Thêm");
        btnThem.addActionListener(this::themNhaCungCap);
        btnThem.setEnabled(RoleManager.canCreateSuppliers());
        
        JButton btnSua = new JButton("Sửa");
        btnSua.addActionListener(this::suaNhaCungCap);
        btnSua.setEnabled(RoleManager.canEditSuppliers());
        
        JButton btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(this::xoaNhaCungCap);
        btnXoa.setEnabled(RoleManager.canDeleteSuppliers());
        
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
            List<NhaCungCap> list = nhaCungCapDAO.findAll();
            tableModel.setRowCount(0);
            
            for (NhaCungCap ncc : list) {
                tableModel.addRow(new Object[]{
                    ncc.getMaNCC(),
                    ncc.getTenNCC(),
                    ncc.getDiaChi(),
                    ncc.getDienThoai(),
                    ncc.getEmail(),
                    ncc.getNguoiLienHe(),
                    ncc.getRating(),
                    ncc.isTrangThai() ? "Đang hợp tác" : "Ngừng hợp tác",
                    String.format("%,.0f VNĐ", ncc.getTongGiaTriMua()),
                    ncc.getSoDonHang()
                });
            }
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void loadSelectedNhaCungCap() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int maNCC = (Integer) tableModel.getValueAt(selectedRow, 0);
                selectedNhaCungCap = nhaCungCapDAO.findById(maNCC).orElse(null);
                
                if (selectedNhaCungCap != null) {
                    populateForm(selectedNhaCungCap);
                }
            } catch (Exception e) {
                showError("Lỗi khi tải thông tin nhà cung cấp: " + e.getMessage());
            }
        }
    }
    
    private void populateForm(NhaCungCap ncc) {
        txtTenNCC.setText(ncc.getTenNCC());
        txtDiaChi.setText(ncc.getDiaChi());
        txtDienThoai.setText(ncc.getDienThoai());
        txtEmail.setText(ncc.getEmail());
        txtNguoiLienHe.setText(ncc.getNguoiLienHe());
        txtChucVuLienHe.setText(ncc.getChucVuLienHe());
        txtMoTa.setText(ncc.getMoTa());
        txtGhiChu.setText(ncc.getGhiChu());
        spnRating.setValue(ncc.getRating());
        chkTrangThai.setSelected(ncc.isTrangThai());
    }
    
    private void clearForm() {
        txtTenNCC.setText("");
        txtDiaChi.setText("");
        txtDienThoai.setText("");
        txtEmail.setText("");
        txtNguoiLienHe.setText("");
        txtChucVuLienHe.setText("");
        txtMoTa.setText("");
        txtGhiChu.setText("");
        spnRating.setValue(0);
        chkTrangThai.setSelected(true);
        selectedNhaCungCap = null;
    }
    
    private void themNhaCungCap(ActionEvent e) {
        if (!RoleManager.canCreateSuppliers()) {
            RoleManager.showAccessDeniedMessage(this, "Thêm nhà cung cấp", "Manager hoặc Admin");
            return;
        }
        
        try {
            // Validate
            if (txtTenNCC.getText().trim().isEmpty()) {
                showError("Vui lòng nhập tên nhà cung cấp!");
                return;
            }
            
            // Create new NhaCungCap
            NhaCungCap ncc = new NhaCungCap();
            ncc.setTenNCC(txtTenNCC.getText().trim());
            ncc.setDiaChi(txtDiaChi.getText().trim());
            ncc.setDienThoai(txtDienThoai.getText().trim());
            ncc.setEmail(txtEmail.getText().trim());
            ncc.setNguoiLienHe(txtNguoiLienHe.getText().trim());
            ncc.setChucVuLienHe(txtChucVuLienHe.getText().trim());
            ncc.setMoTa(txtMoTa.getText().trim());
            ncc.setNgayHopTac(LocalDate.now());
            ncc.setTrangThai(chkTrangThai.isSelected());
            ncc.setRating((Integer) spnRating.getValue());
            ncc.setGhiChu(txtGhiChu.getText().trim());
            
            nhaCungCapDAO.insert(ncc);
            showSuccess("Thêm nhà cung cấp thành công!");
            clearForm();
            loadData();
            
        } catch (Exception ex) {
            showError("Lỗi khi thêm nhà cung cấp: " + ex.getMessage());
        }
    }
    
    private void suaNhaCungCap(ActionEvent e) {
        if (!RoleManager.canEditSuppliers()) {
            RoleManager.showAccessDeniedMessage(this, "Sửa nhà cung cấp", "Manager hoặc Admin");
            return;
        }
        
        if (selectedNhaCungCap == null) {
            showError("Vui lòng chọn nhà cung cấp cần sửa!");
            return;
        }
        
        try {
            // Validate
            if (txtTenNCC.getText().trim().isEmpty()) {
                showError("Vui lòng nhập tên nhà cung cấp!");
                return;
            }
            
            // Update NhaCungCap
            selectedNhaCungCap.setTenNCC(txtTenNCC.getText().trim());
            selectedNhaCungCap.setDiaChi(txtDiaChi.getText().trim());
            selectedNhaCungCap.setDienThoai(txtDienThoai.getText().trim());
            selectedNhaCungCap.setEmail(txtEmail.getText().trim());
            selectedNhaCungCap.setNguoiLienHe(txtNguoiLienHe.getText().trim());
            selectedNhaCungCap.setChucVuLienHe(txtChucVuLienHe.getText().trim());
            selectedNhaCungCap.setMoTa(txtMoTa.getText().trim());
            selectedNhaCungCap.setTrangThai(chkTrangThai.isSelected());
            selectedNhaCungCap.setRating((Integer) spnRating.getValue());
            selectedNhaCungCap.setGhiChu(txtGhiChu.getText().trim());
            
            nhaCungCapDAO.update(selectedNhaCungCap);
            showSuccess("Cập nhật nhà cung cấp thành công!");
            loadData();
            
        } catch (Exception ex) {
            showError("Lỗi khi cập nhật nhà cung cấp: " + ex.getMessage());
        }
    }
    
    private void xoaNhaCungCap(ActionEvent e) {
        if (!RoleManager.canDeleteSuppliers()) {
            RoleManager.showAccessDeniedMessage(this, "Xóa nhà cung cấp", "Admin");
            return;
        }
        
        if (selectedNhaCungCap == null) {
            showError("Vui lòng chọn nhà cung cấp cần xóa!");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhà cung cấp: " + selectedNhaCungCap.getTenNCC() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                nhaCungCapDAO.delete(selectedNhaCungCap.getMaNCC());
                showSuccess("Xóa nhà cung cấp thành công!");
                clearForm();
                loadData();
            } catch (Exception ex) {
                showError("Lỗi khi xóa nhà cung cấp: " + ex.getMessage());
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
