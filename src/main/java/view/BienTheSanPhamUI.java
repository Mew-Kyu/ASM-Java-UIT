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

public class BienTheSanPhamUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtId, txtSoLuong, txtGiaBan, txtSearchField;
    private JComboBox<SanPham> cbSanPham;
    private JComboBox<MauSac> cbMauSac;
    private JComboBox<KichThuoc> cbKichThuoc;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnStockIn, btnStockOut, btnLowStock;
    private JLabel lblTotalItems, lblTotalValue, lblLowStockAlert;

    private final BienTheSanPhamDAO dao = new BienTheSanPhamDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final MauSacDAO mauSacDAO = new MauSacDAO();
    private final KichThuocDAO kichThuocDAO = new KichThuocDAO();

    public BienTheSanPhamUI() {
        setTitle("Quản Lý Kho Hàng - Inventory Management");
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

        // Form fields
        txtId = new JTextField();
        txtId.setEditable(false);
        txtSoLuong = new JTextField();
        txtGiaBan = new JTextField();
        txtSearchField = new JTextField(20);

        cbSanPham = new JComboBox<>();
        cbMauSac = new JComboBox<>();
        cbKichThuoc = new JComboBox<>();

        // Buttons with icons and enhanced functionality
        btnAdd = new JButton("Thêm Sản Phẩm");
        btnUpdate = new JButton("Cập Nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm Mới");
        btnStockIn = new JButton("Nhập Kho");
        btnStockOut = new JButton("Xuất Kho");
        btnLowStock = new JButton("Hàng Sắp Hết");

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
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Sản Phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã Biến Thể:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Sản Phẩm:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cbSanPham, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Màu Sắc:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbMauSac, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Kích Thước:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cbKichThuoc, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số Lượng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSoLuong, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Giá Bán:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtGiaBan, gbc);

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
        // Load SanPham
        cbSanPham.removeAllItems();
        cbSanPham.addItem(null);
        List<SanPham> sanPhams = sanPhamDAO.findAll();
        for (SanPham sp : sanPhams) {
            cbSanPham.addItem(sp);
        }

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
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<BienTheSanPham> list = dao.findAll();
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
        int modelRow = table.convertRowIndexToModel(row);
        Integer id = (Integer) tableModel.getValueAt(modelRow, 0);
        BienTheSanPham bts = dao.findById(id);

        if (bts != null) {
            txtId.setText(String.valueOf(bts.getId()));
            txtSoLuong.setText(String.valueOf(bts.getSoLuong()));
            txtGiaBan.setText(bts.getGiaBan().toString());
            cbSanPham.setSelectedItem(bts.getMaSP());
            cbMauSac.setSelectedItem(bts.getMaMau());
            cbKichThuoc.setSelectedItem(bts.getMaSize());
        }
    }

    private void filterTable() {
        String text = txtSearchField.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void updateStatistics() {
        List<BienTheSanPham> allItems = dao.findAll();
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
            SanPham sanPham = (SanPham) cbSanPham.getSelectedItem();
            MauSac mauSac = (MauSac) cbMauSac.getSelectedItem();
            KichThuoc kichThuoc = (KichThuoc) cbKichThuoc.getSelectedItem();

            BienTheSanPham bts = new BienTheSanPham(soLuong, giaBan, sanPham, kichThuoc, mauSac);
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
                bts.setMaSP((SanPham) cbSanPham.getSelectedItem());
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
                        loadSelectedItem(table.getSelectedRow());
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
        BienTheSanPham bts = dao.findById(id);
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
                        loadSelectedItem(table.getSelectedRow());
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
        List<BienTheSanPham> allItems = dao.findAll();
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
            lowStockList.append("Không có s���n phẩm nào sắp hết hàng.");
        }

        JTextArea textArea = new JTextArea(lowStockList.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Cảnh Báo Hết Hàng", JOptionPane.WARNING_MESSAGE);
    }

    private boolean validateInput() {
        if (cbSanPham.getSelectedItem() == null) {
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
        cbSanPham.setSelectedIndex(0);
        cbMauSac.setSelectedIndex(0);
        cbKichThuoc.setSelectedIndex(0);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BienTheSanPhamUI().setVisible(true));
    }
}

