package view;

import model.SanPham;
import dao.impl.SanPhamDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Dialog popup để chọn sản phẩm thay vì sử dụng combobox
 */
public class ProductSelectionDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;
    private JButton btnSelect, btnCancel;
    private SanPham selectedProduct = null;
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public ProductSelectionDialog(Frame parent) {
        super(parent, "Chọn Sản Phẩm", true);
        initializeComponents();
        setupLayout();
        loadProductData();
        setupEventHandlers();

        setSize(800, 600);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Tạo bảng hiển thị sản phẩm
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "Mô Tả"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        // Thiết lập row sorter cho tìm kiếm
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Tạo các component khác
        txtSearch = new JTextField(20);
        btnSelect = new JButton("Chọn");
        btnCancel = new JButton("Hủy");

        // Thiết lập style cho buttons
        btnSelect.setPreferredSize(new Dimension(80, 30));
        btnCancel.setPreferredSize(new Dimension(80, 30));
        btnSelect.setBackground(new Color(76, 175, 80));
        btnSelect.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(244, 67, 54));
        btnCancel.setForeground(Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm Kiếm"));
        searchPanel.add(new JLabel("Tên sản phẩm:"));
        searchPanel.add(txtSearch);

        // Panel bảng
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Sản Phẩm"));

        // Panel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSelect);
        buttonPanel.add(btnCancel);

        // Thêm instruction label
        JLabel instructionLabel = new JLabel("<html><i>Double-click vào sản phẩm để chọn hoặc nhấn nút 'Chọn'</i></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(instructionLabel.getFont().deriveFont(Font.ITALIC, 12f));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(instructionLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Padding cho dialog
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        List<SanPham> products = sanPhamDAO.findAll();

        for (SanPham product : products) {
            Object[] rowData = {
                product.getId(),
                product.getTenSP(),
                product.getDanhMuc() != null ? product.getDanhMuc().getTenDM(): "N/A",
                product.getMoTa()
            };
            tableModel.addRow(rowData);
        }
    }

    private void setupEventHandlers() {
        // Xử lý tìm kiếm real-time
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });

        // Double-click để chọn sản phẩm
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectProduct();
                }
            }
        });

        // Xử lý button chọn
        btnSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectProduct();
            }
        });

        // Xử lý button hủy
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedProduct = null;
                dispose();
            }
        });

        // Xử lý ESC key để đóng dialog
        getRootPane().registerKeyboardAction(
            e -> {
                selectedProduct = null;
                dispose();
            },
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Set Enter key cho nút chọn
        getRootPane().setDefaultButton(btnSelect);
    }

    private void filterTable() {
        String searchText = txtSearch.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void selectProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một sản phẩm!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy row index trong model (không phải view vì có filter)
        int modelRow = table.convertRowIndexToModel(selectedRow);
        Integer maSanPhamObj = (Integer) tableModel.getValueAt(modelRow, 0);

        if (maSanPhamObj == null) {
            JOptionPane.showMessageDialog(this,
                "Dữ liệu sản phẩm không hợp lệ!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int maSanPham = maSanPhamObj.intValue(); // Convert Integer to int safely

        // Tìm sản phẩm từ database
        selectedProduct = sanPhamDAO.findById(maSanPham);

        if (selectedProduct != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy sản phẩm!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy sản phẩm đã được chọn
     * @return SanPham đã chọn hoặc null nếu không chọn
     */
    public SanPham getSelectedProduct() {
        return selectedProduct;
    }

    /**
     * Hiển thị dialog và trả về sản phẩm được chọn
     * @param parent Frame cha
     * @return SanPham được chọn hoặc null nếu hủy
     */
    public static SanPham showDialog(Frame parent) {
        ProductSelectionDialog dialog = new ProductSelectionDialog(parent);
        dialog.setVisible(true);
        return dialog.getSelectedProduct();
    }
}
