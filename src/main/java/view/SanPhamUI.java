package view;

import model.SanPham;
import model.DanhMuc;
import controller.SanPhamController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamUI extends JFrame {
    private SanPhamController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTenSP, txtMoTa, txtSearch;
    private JComboBox<DanhMuc> cmbDanhMuc;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch, btnClear;

    private List<SanPham> allSanPham = new ArrayList<>();
    private List<SanPham> filteredSanPham = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 30;
    private int totalPages = 1;
    private JLabel lblPageInfo;
    private JButton btnFirstPage, btnPrevPage, btnNextPage, btnLastPage;
    private JComboBox<Integer> cboPageSize;

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
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        // Initialize buttons first
        btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(new Dimension(80, 30));
        btnUpdate = new JButton("Sửa");
        btnUpdate.setPreferredSize(new Dimension(80, 30));
        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(80, 30));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnClear = new JButton("Xóa tìm kiếm");
        btnClear.setPreferredSize(new Dimension(120, 30));

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
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "📝 Thông tin sản phẩm",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(51, 102, 153)),
            BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));
        inputPanel.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Create styled labels
        JLabel lblId = new JLabel("🏷️ Mã sản phẩm:");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblId.setForeground(new Color(68, 68, 68));

        JLabel lblTen = new JLabel("📦 Tên sản phẩm:");
        lblTen.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTen.setForeground(new Color(68, 68, 68));

        JLabel lblDanhMuc = new JLabel("📂 Danh mục:");
        lblDanhMuc.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDanhMuc.setForeground(new Color(68, 68, 68));

        JLabel lblMoTa = new JLabel("📄 Mô tả:");
        lblMoTa.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblMoTa.setForeground(new Color(68, 68, 68));

        // Row 1 - Mã SP và Tên SP
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        txtId = new JTextField(12);
        txtId.setEnabled(false);
        txtId.setBackground(new Color(240, 240, 240));
        txtId.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 12));
        inputPanel.add(txtId, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        inputPanel.add(lblTen, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 8, 8, 8);
        txtTenSP = new JTextField(18);
        txtTenSP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtTenSP.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtTenSP.setToolTipText("Nhập tên sản phẩm (bắt buộc)");
        inputPanel.add(txtTenSP, gbc);

        // Row 2 - Danh mục và Mô tả
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        inputPanel.add(lblDanhMuc, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        cmbDanhMuc = new JComboBox<>();
        cmbDanhMuc.setPreferredSize(new Dimension(150, 30));
        cmbDanhMuc.setBorder(null);
        cmbDanhMuc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cmbDanhMuc.setBackground(Color.WHITE);
        cmbDanhMuc.setToolTipText("Chọn danh mục cho sản phẩm (bắt buộc)");
        inputPanel.add(cmbDanhMuc, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        inputPanel.add(lblMoTa, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 8, 8, 8);
        txtMoTa = new JTextField(18);
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtMoTa.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtMoTa.setToolTipText("Nhập mô tả chi tiết về sản phẩm (tùy chọn)");
        inputPanel.add(txtMoTa, gbc);

        // Add some visual separation
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 5, 0);
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(220, 220, 220));
        inputPanel.add(separator, gbc);

        // Add instruction label
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 0, 0);
        JLabel instructionLabel = new JLabel("💡 Chọn một dòng trong bảng để chỉnh sửa hoặc nhập thông tin mới để thêm sản phẩm");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructionLabel.setForeground(new Color(102, 102, 102));
        inputPanel.add(instructionLabel, gbc);

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

        // Pagination panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnFirstPage = new JButton("|<");
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        btnLastPage = new JButton(">|");
        lblPageInfo = new JLabel("Page 1/1 (0 records)");
        cboPageSize = new JComboBox<>(new Integer[]{10,30,50,100});
        cboPageSize.setSelectedItem(30);
        paginationPanel.add(new JLabel("Rows:"));
        paginationPanel.add(cboPageSize);
        paginationPanel.add(btnFirstPage);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNextPage);
        paginationPanel.add(btnLastPage);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);

        // ADD: top container combining search + input (previously missing)
        JPanel topContainer = new JPanel(new BorderLayout(5,5));
        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(inputPanel, BorderLayout.CENTER);
        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        // Event handlers
        setupEventHandlers();
        // Add pagination listeners
        btnFirstPage.addActionListener(e -> { if(currentPage!=1){currentPage=1; renderCurrentPage();}});
        btnPrevPage.addActionListener(e -> { if(currentPage>1){currentPage--; renderCurrentPage();}});
        btnNextPage.addActionListener(e -> { if(currentPage< totalPages){currentPage++; renderCurrentPage();}});
        btnLastPage.addActionListener(e -> { if(currentPage!= totalPages){currentPage= totalPages; renderCurrentPage();}});
        cboPageSize.addActionListener(e -> { Integer sel=(Integer)cboPageSize.getSelectedItem(); if(sel!=null && sel!=pageSize){pageSize=sel; currentPage=1; recalcPagination(); renderCurrentPage();}});
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

        SanPham sp = createSanPhamFromInput();
        if (controller.addSanPham(sp, this)) {
            // Success message is handled by controller
            loadTable();
            clearInput();
        }
        // Error messages are handled by controller
    }

    private void updateSanPham() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!");
            return;
        }

        if (!validateInput()) return;

        SanPham sp = createSanPhamFromInput();
        sp.setId(Integer.parseInt(txtId.getText()));
        if (controller.updateSanPham(sp, this)) {
            // Success message is handled by controller
            loadTable();
            clearInput();
        }
        // Error messages are handled by controller
    }

    private void deleteSanPham() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText());
            if (controller.deleteSanPham(id, this)) {
                // Success message and confirmation are handled by controller
                loadTable();
                clearInput();
            }
            // Error messages are handled by controller
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID sản phẩm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            allSanPham = controller.getAllSanPham();
            filteredSanPham = new ArrayList<>(allSanPham);
            currentPage = 1;
            recalcPagination();
            renderCurrentPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void searchSanPham() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<SanPham> list = controller.searchSanPham(keyword);
            filteredSanPham = new ArrayList<>(list);
            currentPage = 1;
            recalcPagination();
            renderCurrentPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    // Pagination helpers
    private void recalcPagination(){
        int total = filteredSanPham.size();
        totalPages = total==0?1:(int)Math.ceil(total/(double)pageSize);
        if(currentPage>totalPages) currentPage= totalPages;
        updatePaginationControls();
    }
    private void updatePaginationControls(){
        int totalRecords = filteredSanPham.size();
        if(lblPageInfo!=null) lblPageInfo.setText("Page "+currentPage+"/"+totalPages+" ("+totalRecords+" records)");
        if(btnFirstPage!=null){
            btnFirstPage.setEnabled(currentPage>1);
            btnPrevPage.setEnabled(currentPage>1);
            btnNextPage.setEnabled(currentPage< totalPages);
            btnLastPage.setEnabled(currentPage< totalPages);
        }
    }
    private void renderCurrentPage(){
        tableModel.setRowCount(0);
        if(filteredSanPham.isEmpty()){ updatePaginationControls(); return; }
        int start = (currentPage-1)*pageSize;
        int end = Math.min(start+pageSize, filteredSanPham.size());
        for(int i=start;i<end;i++){
            SanPham sp = filteredSanPham.get(i);
            tableModel.addRow(new Object[]{
                sp.getId(),
                sp.getTenSP(),
                sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDM() : "N/A",
                sp.getMoTa()
            });
        }
        updatePaginationControls();
    }
}
