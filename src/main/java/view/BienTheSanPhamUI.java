package view;

import model.BienTheSanPham;
import model.SanPham;
import model.MauSac;
import model.KichThuoc;
import dao.impl.BienTheSanPhamDAO;
import dao.impl.SanPhamDAO;
import dao.impl.MauSacDAO;
import dao.impl.KichThuocDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class BienTheSanPhamUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtId, txtSoLuong, txtGiaBan, txtSearchField, txtSelectedProduct;
    private JButton btnSelectProduct;
    private JComboBox<MauSac> cbMauSac;
    private JComboBox<KichThuoc> cbKichThuoc;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnStockIn, btnStockOut, btnLowStock;
    private JLabel lblTotalItems, lblTotalValue, lblLowStockAlert;
    private SanPham selectedProduct = null;

    private final BienTheSanPhamDAO dao = new BienTheSanPhamDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final MauSacDAO mauSacDAO = new MauSacDAO();
    private final KichThuocDAO kichThuocDAO = new KichThuocDAO();

    public BienTheSanPhamUI() {
        setTitle("Quản Lý Hàng Hóa");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        setupLayout();
        loadComboBoxData();
        loadTable();
        setupEventHandlers();
        updateStatistics();
    }

    private void initializeComponents() {
        // Table with enhanced columns
        String[] columns = {"Mã BT", "Sản Phẩm", "Màu Sắc", "Kích Thước", "Số Lượng", "Giá Bán", "Tổng Giá Trị", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        // Row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Form fields with enhanced user experience
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 245, 245));
        txtId.setToolTipText("Mã biến thể sẽ được tự động tạo");
        txtId.setPreferredSize(new Dimension(120, 30));

        txtSoLuong = new JTextField();
        txtSoLuong.setPreferredSize(new Dimension(120, 30));
        txtSoLuong.setToolTipText("Nhập số lượng sản phẩm (VD: 100)");
        txtSoLuong.setHorizontalAlignment(JTextField.RIGHT);

        txtGiaBan = new JTextField();
        txtGiaBan.setPreferredSize(new Dimension(120, 30));
        txtGiaBan.setToolTipText("Nhập giá bán (VD: 150000)");
        txtGiaBan.setHorizontalAlignment(JTextField.RIGHT);

        txtSearchField = new JTextField(20);
        txtSearchField.setToolTipText("Tìm kiếm theo tên sản phẩm, màu sắc, kích thước...");

        txtSelectedProduct = new JTextField();
        txtSelectedProduct.setEditable(false);
        txtSelectedProduct.setPreferredSize(new Dimension(200, 30));
        txtSelectedProduct.setBackground(new Color(250, 250, 250));
        txtSelectedProduct.setToolTipText("Sản phẩm đã chọn - Nhấn nút 'Chọn Sản Phẩm' để thay đổi");

        cbMauSac = new JComboBox<>();
        cbMauSac.setPreferredSize(new Dimension(120, 30));
        cbMauSac.setToolTipText("Chọn màu sắc cho sản phẩm");

        cbKichThuoc = new JComboBox<>();
        cbKichThuoc.setPreferredSize(new Dimension(120, 30));
        cbKichThuoc.setToolTipText("Chọn kích thước cho sản phẩm");

        // Buttons with icons and enhanced functionality
        btnAdd = new JButton("📦 Thêm Sản Phẩm");
        btnAdd.setPreferredSize(new Dimension(150, 35));
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setToolTipText("Thêm sản phẩm mới vào kho");

        btnUpdate = new JButton("✏️ Cập Nhật");
        btnUpdate.setPreferredSize(new Dimension(120, 35));
        btnUpdate.setBackground(new Color(33, 150, 243));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setToolTipText("Cập nhật thông tin sản phẩm đã chọn");

        btnDelete = new JButton("🗑️ Xóa");
        btnDelete.setPreferredSize(new Dimension(100, 35));
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setToolTipText("Xóa sản phẩm khỏi kho");

        btnRefresh = new JButton("🔄 Làm Mới");
        btnRefresh.setPreferredSize(new Dimension(120, 35));
        btnRefresh.setBackground(new Color(158, 158, 158));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setToolTipText("Làm mới danh sách và xóa form");

        btnStockIn = new JButton("📥 Nhập Kho");
        btnStockIn.setPreferredSize(new Dimension(120, 35));
        btnStockIn.setBackground(new Color(139, 195, 74));
        btnStockIn.setForeground(Color.WHITE);
        btnStockIn.setFocusPainted(false);
        btnStockIn.setToolTipText("Nhập thêm hàng vào kho");

        btnStockOut = new JButton("📤 Xuất Kho");
        btnStockOut.setPreferredSize(new Dimension(120, 35));
        btnStockOut.setBackground(new Color(255, 152, 0));
        btnStockOut.setForeground(Color.WHITE);
        btnStockOut.setFocusPainted(false);
        btnStockOut.setToolTipText("Xuất hàng khỏi kho");

        btnLowStock = new JButton("⚠️ Hàng Sắp Hết");
        btnLowStock.setPreferredSize(new Dimension(140, 30));
        btnLowStock.setBackground(new Color(255, 193, 7));
        btnLowStock.setForeground(Color.BLACK);
        btnLowStock.setFocusPainted(false);
        btnLowStock.setToolTipText("Xem danh sách hàng sắp hết");

        btnSelectProduct = new JButton("📋 Chọn");
        btnSelectProduct.setPreferredSize(new Dimension(80, 30));
        btnSelectProduct.setBackground(new Color(103, 58, 183));
        btnSelectProduct.setForeground(Color.WHITE);
        btnSelectProduct.setFocusPainted(false);
        btnSelectProduct.setToolTipText("Mở danh sách sản phẩm đã chọn");

        // Statistics labels
        lblTotalItems = new JLabel("Tổng số mặt hàng: 0");
        lblTotalValue = new JLabel("Tổng giá trị kho: 0 VNĐ");
        lblLowStockAlert = new JLabel("Cảnh báo hết hàng: 0 sản phẩm");
        lblLowStockAlert.setForeground(Color.RED);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - Search and statistics
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearchField);
        searchPanel.add(btnLowStock);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.add(lblTotalItems);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(lblTotalValue);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(lblLowStockAlert);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);

        // Center panel - Table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Hàng Trong Kho"));

        // Bottom panel - Form and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "📝 Thông tin sản phẩm",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(51, 102, 153)),
            BorderFactory.createEmptyBorder(15, 20, 20, 20)
        ));
        formPanel.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Create styled labels with icons
        JLabel lblId = new JLabel("🏷️ Mã biến thể:");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblId.setForeground(new Color(68, 68, 68));

        JLabel lblSanPham = new JLabel("📦 Sản phẩm:");
        lblSanPham.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSanPham.setForeground(new Color(68, 68, 68));

        JLabel lblKichThuoc = new JLabel("📏 Kích thước:");
        lblKichThuoc.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblKichThuoc.setForeground(new Color(68, 68, 68));

        JLabel lblMauSac = new JLabel("🎨 Màu sắc:");
        lblMauSac.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblMauSac.setForeground(new Color(68, 68, 68));

        JLabel lblSoLuong = new JLabel("📊 Số lượng:");
        lblSoLuong.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSoLuong.setForeground(new Color(68, 68, 68));

        JLabel lblGiaBan = new JLabel("💰 Giá bán:");
        lblGiaBan.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblGiaBan.setForeground(new Color(68, 68, 68));

        // Style the input fields
        txtId.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtSelectedProduct.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtSelectedProduct.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtSoLuong.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtSoLuong.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtGiaBan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtGiaBan.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Style combo boxes
        cbKichThuoc.setBorder(null);
        cbKichThuoc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbKichThuoc.setBackground(Color.WHITE);

        cbMauSac.setBorder(null);
        cbMauSac.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbMauSac.setBackground(Color.WHITE);

        // Row 1 - Mã biến thể và Sản phẩm
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.2;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        formPanel.add(lblSanPham, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(txtSelectedProduct, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        btnSelectProduct.setPreferredSize(new Dimension(120, 30));
        btnSelectProduct.setFont(new Font("SansSerif", Font.BOLD, 11));
        formPanel.add(btnSelectProduct, gbc);

        // Row 2 - Kích thước và Màu sắc
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(lblKichThuoc, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.2;
        formPanel.add(cbKichThuoc, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        formPanel.add(lblMauSac, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(cbMauSac, gbc);

        // Row 3 - Số lượng và Giá bán
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(lblSoLuong, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.2;
        formPanel.add(txtSoLuong, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        formPanel.add(lblGiaBan, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(txtGiaBan, gbc);

        // Add some visual separation
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        gbc.weightx = 1.0;
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(220, 220, 220));
        formPanel.add(separator, gbc);

        // Add instruction label
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.weightx = 0;
        JLabel instructionLabel = new JLabel("💡 Chọn một dòng trong bảng để chỉnh sửa hoặc nhập thông tin mới để thêm vào kho");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructionLabel.setForeground(new Color(102, 102, 102));
        formPanel.add(instructionLabel, gbc);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(btnStockIn);
        btnPanel.add(btnStockOut);

        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        // Add panels to main frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadComboBoxData() {
        // Load MauSac
        cbMauSac.removeAllItems();
        cbMauSac.addItem(null);
        List<MauSac> mauSacs = mauSacDAO.findAll();
        for (MauSac ms : mauSacs) {
            cbMauSac.addItem(ms);
        }

        // Load KichThuoc
        cbKichThuoc.removeAllItems();
        cbKichThuoc.addItem(null);
        List<KichThuoc> kichThuocs = kichThuocDAO.findAll();
        for (KichThuoc kt : kichThuocs) {
            cbKichThuoc.addItem(kt);
        }
    }

    private void setupEventHandlers() {
        // Table selection
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    loadSelectedItem(row);
                }
            }
        });

        // Search functionality
        txtSearchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        // Button events
        btnAdd.addActionListener(e -> addBienThe());
        btnUpdate.addActionListener(e -> updateBienThe());
        btnDelete.addActionListener(e -> deleteBienThe());
        btnRefresh.addActionListener(e -> {
            loadTable();
            clearForm();
        });
        btnStockIn.addActionListener(e -> stockInDialog());
        btnStockOut.addActionListener(e -> stockOutDialog());
        btnLowStock.addActionListener(e -> showLowStockItems());
        btnSelectProduct.addActionListener(e -> selectProductDialog());
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        // Use eager fetch to avoid LazyInitializationException when accessing associations in UI thread
        List<BienTheSanPham> list = dao.findAllWithDetails();
        for (BienTheSanPham bts : list) {
            String sanPhamName = bts.getMaSP() != null ? bts.getMaSP().getTenSP() : "N/A";
            String mauSacName = bts.getMaMau() != null ? bts.getMaMau().getTenMau() : "N/A";
            String kichThuocName = bts.getMaSize() != null ? bts.getMaSize().getTenSize() : "N/A";
            BigDecimal totalValue = bts.getGiaBan().multiply(new BigDecimal(bts.getSoLuong()));
            String status = getStockStatus(bts.getSoLuong());

            tableModel.addRow(new Object[]{
                bts.getId(),
                sanPhamName,
                mauSacName,
                kichThuocName,
                bts.getSoLuong(),
                String.format("%,.0f VNĐ", bts.getGiaBan()),
                String.format("%,.0f VNĐ", totalValue),
                status
            });
        }
        updateStatistics();
    }

    private String getStockStatus(Integer soLuong) {
        if (soLuong == null || soLuong == 0) {
            return "Hết hàng";
        } else if (soLuong <= 10) {
            return "Sắp hết";
        } else if (soLuong <= 50) {
            return "Ít hàng";
        } else {
            return "Còn hàng";
        }
    }

    private void loadSelectedItem(int row) {
        if (row < 0 || row >= table.getRowCount()) {
            return; // Invalid row index, do nothing
        }
        
        int modelRow = table.convertRowIndexToModel(row);
        Integer id = (Integer) tableModel.getValueAt(modelRow, 0);
        BienTheSanPham bts = dao.findByIdWithDetails(id);

        if (bts != null) {
            txtId.setText(String.valueOf(bts.getId()));
            txtSoLuong.setText(String.valueOf(bts.getSoLuong()));
            txtGiaBan.setText(bts.getGiaBan().toString());
            selectedProduct = bts.getMaSP(); // Cập nhật selectedProduct
            txtSelectedProduct.setText(bts.getMaSP() != null ? bts.getMaSP().getTenSP() : "N/A");
            cbMauSac.setSelectedItem(bts.getMaMau());
            cbKichThuoc.setSelectedItem(bts.getMaSize());
        }
    }

    private void filterTable() {
        String text = txtSearchField.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            // Create a custom RowFilter that supports Vietnamese search
            rowSorter.setRowFilter(new VietnameseRowFilter(text));
        }
    }
    
    /**
     * Removes Vietnamese diacritics (accent marks) from text
     * Converts "áàảãạ" to "a", "éèẻẽẹ" to "e", etc.
     */
    private String removeVietnameseDiacritics(String text) {
        if (text == null) return "";
        
        // Normalize to decomposed form (NFD) - separates base characters from diacritics
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        
        // Remove diacritical marks (combining diacritical marks Unicode category)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutDiacritics = pattern.matcher(normalized).replaceAll("");
        
        // Handle special Vietnamese characters that don't decompose properly
        withoutDiacritics = withoutDiacritics
            .replace("đ", "d").replace("Đ", "D")  // Handle đ/Đ specifically
            .replace("ư", "u").replace("Ư", "U")  // Handle ư/Ư specifically  
            .replace("ơ", "o").replace("Ơ", "O"); // Handle ơ/Ơ specifically
            
        return withoutDiacritics;
    }
    
    /**
     * Custom RowFilter that supports Vietnamese diacritic-insensitive search
     */
    private class VietnameseRowFilter extends RowFilter<DefaultTableModel, Object> {
        private final String searchText;
        private final String searchTextNoDiacritics;
        
        public VietnameseRowFilter(String searchText) {
            this.searchText = searchText.toLowerCase();
            this.searchTextNoDiacritics = removeVietnameseDiacritics(this.searchText);
        }
        
        @Override
        public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
            // Check all columns in the row
            for (int i = 0; i < entry.getValueCount(); i++) {
                String value = entry.getStringValue(i);
                if (value != null) {
                    String valueLower = value.toLowerCase();
                    String valueNoDiacritics = removeVietnameseDiacritics(valueLower);
                    
                    // Match if either:
                    // 1. Original text contains search term (for exact matches)
                    // 2. Text without diacritics contains search term without diacritics
                    if (valueLower.contains(searchText) || 
                        valueNoDiacritics.contains(searchTextNoDiacritics)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void updateStatistics() {
        // Use eager fetch for statistics as well
        List<BienTheSanPham> allItems = dao.findAllWithDetails();
        int totalItems = allItems.size();
        BigDecimal totalValue = BigDecimal.ZERO;
        int lowStockCount = 0;

        for (BienTheSanPham bts : allItems) {
            if (bts.getSoLuong() != null && bts.getGiaBan() != null) {
                totalValue = totalValue.add(bts.getGiaBan().multiply(new BigDecimal(bts.getSoLuong())));
                if (bts.getSoLuong() <= 10) {
                    lowStockCount++;
                }
            }
        }

        lblTotalItems.setText("Tổng số mặt hàng: " + totalItems);
        lblTotalValue.setText("Tổng giá trị kho: " + String.format("%,.0f VNĐ", totalValue));
        lblLowStockAlert.setText("Cảnh báo hết hàng: " + lowStockCount + " sản phẩm");
    }

    private void addBienThe() {
        try {
            if (!validateInput()) return;

            int soLuong = Integer.parseInt(txtSoLuong.getText());
            BigDecimal giaBan = new BigDecimal(txtGiaBan.getText());
            MauSac mauSac = (MauSac) cbMauSac.getSelectedItem();
            KichThuoc kichThuoc = (KichThuoc) cbKichThuoc.getSelectedItem();

            BienTheSanPham bts = new BienTheSanPham(soLuong, giaBan, selectedProduct, kichThuoc, mauSac);
            dao.insert(bts);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm vào kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBienThe() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!validateInput()) return;

            int id = Integer.parseInt(txtId.getText());
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            BigDecimal giaBan = new BigDecimal(txtGiaBan.getText());

            BienTheSanPham bts = dao.findById(id);
            if (bts != null) {
                bts.setSoLuong(soLuong);
                bts.setGiaBan(giaBan);
                bts.setMaSP(selectedProduct);
                bts.setMaMau((MauSac) cbMauSac.getSelectedItem());
                bts.setMaSize((KichThuoc) cbKichThuoc.getSelectedItem());

                dao.update(bts);
                loadTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBienThe() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(txtId.getText());
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm này khỏi kho?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                dao.delete(id);
                loadTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm khỏi kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stockInDialog() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để nhập kho!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Nhập số lượng cần nhập kho:", "Nhập Kho", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(input.trim());
                if (quantity > 0) {
                    int id = Integer.parseInt(txtId.getText());
                    BienTheSanPham bts = dao.findById(id);
                    if (bts != null) {
                        bts.increaseStock(quantity);
                        dao.update(bts);
                        loadTable();
                        updateStatistics();
                        JOptionPane.showMessageDialog(this,
                            String.format("Đã nhập %d sản phẩm vào kho thành công!", quantity),
                            "Nhập kho thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void stockOutDialog() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xuất kho!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        // Ensure associations are initialized for form binding
        BienTheSanPham bts = dao.findByIdWithDetails(id);
        if (bts == null) return;

        String input = JOptionPane.showInputDialog(this,
            String.format("Số lượng hiện tại: %d\nNhập số lượng cần xuất kho:", bts.getSoLuong()),
            "Xuất Kho",
            JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(input.trim());
                if (quantity > 0) {
                    if (bts.isAvailable(quantity)) {
                        bts.decreaseStock(quantity);
                        dao.update(bts);
                        loadTable();
                        updateStatistics();
                        JOptionPane.showMessageDialog(this,
                            String.format("Đã xuất %d sản phẩm khỏi kho thành công!", quantity),
                            "Xuất kho thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            String.format("Không đủ hàng trong kho! Số lượng hiện tại: %d", bts.getSoLuong()),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showLowStockItems() {
        StringBuilder lowStockList = new StringBuilder();
        List<BienTheSanPham> allItems = dao.findAllWithDetails();
        int count = 0;

        lowStockList.append("DANH SÁCH SẢN PHẨM SẮP HẾT HÀNG (≤ 10 sản phẩm):\n\n");

        for (BienTheSanPham bts : allItems) {
            if (bts.getSoLuong() != null && bts.getSoLuong() <= 10) {
                count++;
                String sanPhamName = bts.getMaSP() != null ? bts.getMaSP().getTenSP() : "N/A";
                String mauSacName = bts.getMaMau() != null ? bts.getMaMau().getTenMau() : "N/A";
                String kichThuocName = bts.getMaSize() != null ? bts.getMaSize().getTenSize() : "N/A";

                lowStockList.append(String.format("%d. %s - %s - %s: %d sản phẩm\n",
                    count, sanPhamName, mauSacName, kichThuocName, bts.getSoLuong()));
            }
        }

        if (count == 0) {
            lowStockList.append("Không có sản phẩm nào sắp hết hàng.");
        }

        JTextArea textArea = new JTextArea(lowStockList.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Cảnh Báo Hết Hàng", JOptionPane.WARNING_MESSAGE);
    }

    private void selectProductDialog() {
        SanPham product = ProductSelectionDialog.showDialog(this);
        if (product != null) {
            selectedProduct = product;
            txtSelectedProduct.setText(product.getTenSP());
        }
    }

    private boolean validateInput() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cbMauSac.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn màu sắc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cbKichThuoc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kích thước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtSoLuong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtGiaBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá bán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            BigDecimal giaBan = new BigDecimal(txtGiaBan.getText().trim());
            if (giaBan.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá bán không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtSoLuong.setText("");
        txtGiaBan.setText("");
        txtSelectedProduct.setText("");
        cbMauSac.setSelectedIndex(0);
        cbKichThuoc.setSelectedIndex(0);
        table.clearSelection();
        selectedProduct = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BienTheSanPhamUI().setVisible(true));
    }
}

