package view;

import model.NhaCungCap;
import dao.impl.NhaCungCapDAO;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
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
    
    // Search components
    private JTextField txtSearch;
    private JComboBox<String> cmbSearchType;
    private JButton btnSearch, btnClearSearch;

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
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1 - Tên NCC và Điện thoại
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        JLabel lblTenNCC = new JLabel("Tên nhà cung cấp:");
        lblTenNCC.setFont(lblTenNCC.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblTenNCC, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTenNCC = new JTextField(25);
        txtTenNCC.setPreferredSize(new Dimension(250, 28));
        panel.add(txtTenNCC, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 30, 8, 10);
        JLabel lblDienThoai = new JLabel("Điện thoại:");
        lblDienThoai.setFont(lblDienThoai.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblDienThoai, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        txtDienThoai = new JTextField(15);
        txtDienThoai.setPreferredSize(new Dimension(200, 28));
        panel.add(txtDienThoai, gbc);
        
        // Row 2 - Địa chỉ (full width)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(lblDiaChi.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblDiaChi, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDiaChi = new JTextField();
        txtDiaChi.setPreferredSize(new Dimension(0, 28));
        panel.add(txtDiaChi, gbc);
        
        // Row 3 - Email và Rating
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(lblEmail.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEmail = new JTextField(25);
        txtEmail.setPreferredSize(new Dimension(250, 28));
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 30, 8, 10);
        JLabel lblRating = new JLabel("Đánh giá (0-5):");
        lblRating.setFont(lblRating.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblRating, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        spnRating = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        spnRating.setPreferredSize(new Dimension(200, 28));
        panel.add(spnRating, gbc);
        
        // Row 4 - Người liên hệ và Chức vụ
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblNguoiLienHe = new JLabel("Người liên hệ:");
        lblNguoiLienHe.setFont(lblNguoiLienHe.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblNguoiLienHe, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNguoiLienHe = new JTextField(25);
        txtNguoiLienHe.setPreferredSize(new Dimension(250, 28));
        panel.add(txtNguoiLienHe, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 30, 8, 10);
        JLabel lblChucVu = new JLabel("Chức vụ:");
        lblChucVu.setFont(lblChucVu.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblChucVu, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        txtChucVuLienHe = new JTextField(15);
        txtChucVuLienHe.setPreferredSize(new Dimension(200, 28));
        panel.add(txtChucVuLienHe, gbc);
        
        // Row 5 - Mô tả (full width)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setFont(lblMoTa.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblMoTa, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMoTa = new JTextField();
        txtMoTa.setPreferredSize(new Dimension(0, 28));
        panel.add(txtMoTa, gbc);
        
        // Row 6 - Ghi chú và Trạng thái
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblGhiChu = new JLabel("Ghi chú:");
        lblGhiChu.setFont(lblGhiChu.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(lblGhiChu, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtGhiChu = new JTextField();
        txtGhiChu.setPreferredSize(new Dimension(0, 28));
        panel.add(txtGhiChu, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        chkTrangThai = new JCheckBox("Đang hợp tác");
        chkTrangThai.setSelected(true);
        chkTrangThai.setFont(chkTrangThai.getFont().deriveFont(Font.BOLD, 12f));
        chkTrangThai.setBackground(Color.WHITE);
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
        
        // Search functionality
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 28));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchNhaCungCap(null);
                }
            }
        });

        cmbSearchType = new JComboBox<>(new String[] {"Tên nhà cung cấp", "Địa chỉ", "Điện thoại", "Email"});
        cmbSearchType.setPreferredSize(new Dimension(150, 28));

        btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(this::searchNhaCungCap);

        btnClearSearch = new JButton("Xóa tìm kiếm");
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(cmbSearchType);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

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

    private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            int selectedIndex = cmbSearchType.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // Tên nhà cung cấp
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1));
                    break;
                case 1: // Địa chỉ
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 2));
                    break;
                case 2: // Điện thoại
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 3));
                    break;
                case 3: // Email
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 4));
                    break;
                default:
                    sorter.setRowFilter(null);
            }
        }
    }

    private void searchNhaCungCap(ActionEvent e) {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            showError("Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }

        try {
            List<NhaCungCap> resultList = new ArrayList<>();
            List<NhaCungCap> allSuppliers = nhaCungCapDAO.findAll();
            int selectedIndex = cmbSearchType.getSelectedIndex();

            switch (selectedIndex) {
                case 0: // Tên nhà cung cấp
                    for (NhaCungCap ncc : allSuppliers) {
                        if (ncc.getTenNCC() != null && ncc.getTenNCC().toLowerCase().contains(searchText.toLowerCase())) {
                            resultList.add(ncc);
                        }
                    }
                    break;
                case 1: // Địa chỉ
                    for (NhaCungCap ncc : allSuppliers) {
                        if (ncc.getDiaChi() != null && ncc.getDiaChi().toLowerCase().contains(searchText.toLowerCase())) {
                            resultList.add(ncc);
                        }
                    }
                    break;
                case 2: // Điện thoại
                    for (NhaCungCap ncc : allSuppliers) {
                        if (ncc.getDienThoai() != null && ncc.getDienThoai().contains(searchText)) {
                            resultList.add(ncc);
                        }
                    }
                    break;
                case 3: // Email
                    for (NhaCungCap ncc : allSuppliers) {
                        if (ncc.getEmail() != null && ncc.getEmail().toLowerCase().contains(searchText.toLowerCase())) {
                            resultList.add(ncc);
                        }
                    }
                    break;
                default:
                    showError("Chọn loại tìm kiếm hợp lệ!");
                    return;
            }

            tableModel.setRowCount(0);
            for (NhaCungCap ncc : resultList) {
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

            if (resultList.isEmpty()) {
                showError("Không tìm thấy nhà cung cấp nào khớp với từ khóa tìm kiếm!");
            }
        } catch (Exception ex) {
            showError("Lỗi khi tìm kiếm nhà cung cấp: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
