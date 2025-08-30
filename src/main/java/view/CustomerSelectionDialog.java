package view;

import model.KhachHang;
import dao.impl.KhachHangDAO;

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
 * Dialog popup để chọn khách hàng từ danh sách hội viên
 */
public class CustomerSelectionDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;
    private JButton btnSelect, btnCancel;
    private KhachHang selectedCustomer = null;
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public CustomerSelectionDialog(Frame parent) {
        super(parent, "Chọn Khách Hàng", true);
        initializeComponents();
        setupLayout();
        loadCustomerData();
        setupEventHandlers();

        setSize(800, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Tạo bảng hiển thị khách hàng
        String[] columns = {"Mã KH", "Họ Tên", "Điện Thoại", "Email", "Địa Chỉ", "Điểm Tích Lũy"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        // Thiết lập độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã KH
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Họ Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Điện Thoại
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        table.getColumnModel().getColumn(4).setPreferredWidth(180); // Địa Chỉ
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Điểm Tích Lũy

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
        searchPanel.add(new JLabel("Tên khách hàng:"));
        searchPanel.add(txtSearch);

        // Panel bảng
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Khách Hàng"));

        // Panel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSelect);
        buttonPanel.add(btnCancel);

        // Thêm instruction label
        JLabel instructionLabel = new JLabel("<html><i>Double-click vào khách hàng để chọn hoặc nhấn nút 'Chọn'</i></html>");
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

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        List<KhachHang> customers = khachHangDAO.findAll();

        for (KhachHang customer : customers) {
            Object[] rowData = {
                customer.getId(),
                customer.getHoTen(),
                customer.getDienThoai() != null ? customer.getDienThoai() : "N/A",
                customer.getDiaChi() != null ? customer.getDiaChi() : "N/A",
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

        // Double-click để chọn khách hàng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectCustomer();
                }
            }
        });

        // Xử lý button chọn
        btnSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCustomer();
            }
        });

        // Xử lý button hủy
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCustomer = null;
                dispose();
            }
        });

        // Xử lý ESC key để đóng dialog
        getRootPane().registerKeyboardAction(
            e -> {
                selectedCustomer = null;
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

    private void selectCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một khách hàng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy row index trong model (không phải view vì có filter)
        int modelRow = table.convertRowIndexToModel(selectedRow);
        Integer maKhachHangObj = (Integer) tableModel.getValueAt(modelRow, 0);

        if (maKhachHangObj == null) {
            JOptionPane.showMessageDialog(this,
                "Dữ liệu khách hàng không hợp lệ!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int maKhachHang = maKhachHangObj.intValue(); // Convert Integer to int safely

        // Tìm khách hàng từ database
        selectedCustomer = khachHangDAO.findById(maKhachHang);

        if (selectedCustomer != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy khách hàng!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy khách hàng đã được chọn
     * @return KhachHang đã chọn hoặc null nếu không chọn
     */
    public KhachHang getSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * Hiển thị dialog và trả về khách hàng được chọn
     * @param parent Frame cha
     * @return KhachHang được chọn hoặc null nếu hủy
     */
    public static KhachHang showDialog(Frame parent) {
        CustomerSelectionDialog dialog = new CustomerSelectionDialog(parent);
        dialog.setVisible(true);
        return dialog.getSelectedCustomer();
    }
}
