package view;

import model.NhanVien;
import dao.impl.NhanVienDAO;

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
 * Dialog popup để chọn nhân viên thay vì sử dụng combobox
 */
public class EmployeeSelectionDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;
    private JButton btnSelect, btnCancel;
    private NhanVien selectedEmployee = null;
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public EmployeeSelectionDialog(Frame parent) {
        super(parent, "Chọn Nhân Viên", true);
        initializeComponents();
        setupLayout();
        loadEmployeeData();
        setupEventHandlers();

        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Tạo bảng hiển thị nhân viên
        String[] columns = {"Mã NV", "Họ Tên", "Chức Vụ", "Điện Thoại", "Email"};
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
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã NV
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Họ Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Chức Vụ
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Điện Thoại
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Email

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
        searchPanel.add(new JLabel("Tên nhân viên:"));
        searchPanel.add(txtSearch);

        // Panel bảng
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Nhân Viên"));

        // Panel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSelect);
        buttonPanel.add(btnCancel);

        // Thêm instruction label
        JLabel instructionLabel = new JLabel("<html><i>Double-click vào nhân viên để chọn hoặc nhấn nút 'Chọn'</i></html>");
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

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<NhanVien> employees = nhanVienDAO.findAll();

        for (NhanVien employee : employees) {
            Object[] rowData = {
                employee.getId(),
                employee.getHoTen(),
                employee.getChucVu(),
                employee.getDienThoai() != null ? employee.getDienThoai() : "N/A",
                employee.getEmail() != null ? employee.getEmail() : "N/A"
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

        // Double-click để chọn nhân viên
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectEmployee();
                }
            }
        });

        // Xử lý button chọn
        btnSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectEmployee();
            }
        });

        // Xử lý button hủy
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedEmployee = null;
                dispose();
            }
        });

        // Xử lý ESC key để đóng dialog
        getRootPane().registerKeyboardAction(
            e -> {
                selectedEmployee = null;
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

    private void selectEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một nhân viên!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy row index trong model (không phải view vì có filter)
        int modelRow = table.convertRowIndexToModel(selectedRow);
        Integer maNhanVienObj = (Integer) tableModel.getValueAt(modelRow, 0);

        if (maNhanVienObj == null) {
            JOptionPane.showMessageDialog(this,
                "Dữ liệu nhân viên không hợp lệ!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int maNhanVien = maNhanVienObj.intValue(); // Convert Integer to int safely

        // Tìm nhân viên từ database
        selectedEmployee = nhanVienDAO.findById(maNhanVien);

        if (selectedEmployee != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy nhân viên!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy nhân viên đã được chọn
     * @return NhanVien đã chọn hoặc null nếu không chọn
     */
    public NhanVien getSelectedEmployee() {
        return selectedEmployee;
    }

    /**
     * Hiển thị dialog và trả về nhân viên được chọn
     * @param parent Frame cha
     * @return NhanVien được chọn hoặc null nếu hủy
     */
    public static NhanVien showDialog(Frame parent) {
        EmployeeSelectionDialog dialog = new EmployeeSelectionDialog(parent);
        dialog.setVisible(true);
        return dialog.getSelectedEmployee();
    }
}
