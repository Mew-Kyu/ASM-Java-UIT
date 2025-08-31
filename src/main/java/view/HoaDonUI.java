package view;

import controller.BienTheSanPhamController;
import controller.ChiTietHoaDonController;
import controller.HoaDonController;
import controller.KhachHangController;
import controller.NhanVienController;
import controller.HinhThucThanhToanController;
import model.*;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import util.PDFInvoiceGenerator;

public class HoaDonUI extends JFrame {
    private JTextField txtMaHD, txtNgayLap, txtTongTien, txtSelectedEmployee, txtSelectedCustomer, txtCustomerName;
    // Search fields
    private JTextField txtSearchMaHD, txtSearchCustomer, txtSearchEmployee;
    private JTextField txtSearchFromDate, txtSearchToDate;
    private JComboBox<String> cboSearchPaymentStatus;
    private JButton btnSearch, btnClearSearch;
    private JButton btnSelectEmployee, btnSelectCustomer;
    private JRadioButton radioKhachLe, radioHoiVien;
    private ButtonGroup customerTypeGroup;
    private JComboBox<HinhThucThanhToan> cboHinhThucThanhToan;
    private JComboBox<String> cboTrangThaiThanhToan;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnViewDetails, btnAddDetail, btnPrintPDF;
    private JTable tableHoaDon;
    private DefaultTableModel tableModelHoaDon;
    private NhanVien selectedEmployee = null;
    private KhachHang selectedCustomer = null;

    private HoaDonController hoaDonController;
    private KhachHangController khachHangController;
    private NhanVienController nhanVienController;
    private ChiTietHoaDonController chiTietController;
    private BienTheSanPhamController bienTheController;
    private HinhThucThanhToanController hinhThucThanhToanController;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Payment status mapping
    private static final Map<String, String> DB_TO_DISPLAY = new HashMap<>();
    private static final Map<String, String> DISPLAY_TO_DB = new HashMap<>();

    static {
        // Map database values to display values
        DB_TO_DISPLAY.put("PENDING", "Chờ thanh toán");
        DB_TO_DISPLAY.put("PROCESSING", "Đang xử lý");
        DB_TO_DISPLAY.put("COMPLETED", "Đã hoàn tất");
        DB_TO_DISPLAY.put("FAILED", "Thất bại");
        DB_TO_DISPLAY.put("REFUNDED", "Đã hoàn tiền");
        DB_TO_DISPLAY.put("CANCELLED", "Đã hủy");

        // Map display values to database values
        DISPLAY_TO_DB.put("Chờ thanh toán", "PENDING");
        DISPLAY_TO_DB.put("Đang xử lý", "PROCESSING");
        DISPLAY_TO_DB.put("Đã hoàn tất", "COMPLETED");
        DISPLAY_TO_DB.put("Thất bại", "FAILED");
        DISPLAY_TO_DB.put("Đã hoàn tiền", "REFUNDED");
        DISPLAY_TO_DB.put("Đã hủy", "CANCELLED");
    }

    public HoaDonUI() {
        initControllers();
        setTitle("Quản Lý Hóa Đơn");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData();
    }

    private void initControllers() {
        hoaDonController = new HoaDonController();
        khachHangController = new KhachHangController();
        nhanVienController = new NhanVienController();
        chiTietController = new ChiTietHoaDonController();
        bienTheController = new BienTheSanPhamController();
        hinhThucThanhToanController = new HinhThucThanhToanController();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = createInputPanel();
        add(topPanel, BorderLayout.NORTH);

        // Add search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.WEST);

        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setupEventHandlers();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm hóa đơn"));
        panel.setPreferredSize(new Dimension(250, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1 - Invoice ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Mã HĐ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtSearchMaHD = new JTextField(15);
        panel.add(txtSearchMaHD, gbc);

        // Row 2 - Customer
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtSearchCustomer = new JTextField(15);
        panel.add(txtSearchCustomer, gbc);

        // Row 3 - Employee
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtSearchEmployee = new JTextField(15);
        panel.add(txtSearchEmployee, gbc);

        // Row 4 - From Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtSearchFromDate = new JTextField(15);
        txtSearchFromDate.setToolTipText("dd/MM/yyyy");
        panel.add(txtSearchFromDate, gbc);

        // Row 5 - To Date
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtSearchToDate = new JTextField(15);
        txtSearchToDate.setToolTipText("dd/MM/yyyy");
        panel.add(txtSearchToDate, gbc);

        // Row 6 - Payment Status
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("Trạng thái TT:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cboSearchPaymentStatus = new JComboBox<>();
        cboSearchPaymentStatus.setPreferredSize(new Dimension(150, 25));
        panel.add(cboSearchPaymentStatus, gbc);

        // Row 7 - Buttons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        panel.add(btnSearch, gbc);

        gbc.gridx = 1; gbc.gridwidth = 1;
        btnClearSearch = new JButton("Xóa bộ lọc");
        btnClearSearch.setPreferredSize(new Dimension(100, 30));
        panel.add(btnClearSearch, gbc);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Mã HĐ:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMaHD = new JTextField(10);
        txtMaHD.setEditable(false);
        panel.add(txtMaHD, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Ngày lập:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNgayLap = new JTextField(10);
        txtNgayLap.setText(LocalDate.now().format(dateFormatter));
        panel.add(txtNgayLap, gbc);

        // Row 2 - Customer selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSelectedCustomer = new JTextField();
        txtSelectedCustomer.setEditable(false);
        txtSelectedCustomer.setPreferredSize(new Dimension(150, 25));
        txtSelectedCustomer.setText("Khách lẻ");
        panel.add(txtSelectedCustomer, gbc);

        // Radio buttons for customer type
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        radioKhachLe = new JRadioButton("Khách lẻ");
        radioKhachLe.setSelected(true);
        panel.add(radioKhachLe, gbc);

        gbc.gridx = 3;
        radioHoiVien = new JRadioButton("Hội viên");
        panel.add(radioHoiVien, gbc);

        // Customer selection button
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE;
        btnSelectCustomer = new JButton("Chọn KH");
        btnSelectCustomer.setPreferredSize(new Dimension(90, 25));
        panel.add(btnSelectCustomer, gbc);
        btnSelectCustomer.setVisible(false);

        // Row 3 - Employee selection
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSelectedEmployee = new JTextField();
        txtSelectedEmployee.setEditable(false);
        txtSelectedEmployee.setPreferredSize(new Dimension(150, 25));
        panel.add(txtSelectedEmployee, gbc);

        customerTypeGroup = new ButtonGroup();
        customerTypeGroup.add(radioKhachLe);
        customerTypeGroup.add(radioHoiVien);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnSelectEmployee = new JButton("Chọn NV");
        btnSelectEmployee.setPreferredSize(new Dimension(90, 25));
        panel.add(btnSelectEmployee, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTongTien = new JTextField(10);
        txtTongTien.setEditable(false);
        txtTongTien.setBackground(Color.LIGHT_GRAY);
        panel.add(txtTongTien, gbc);

        // Row 4 - Payment method and status
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Hình thức TT:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        cboHinhThucThanhToan = new JComboBox<>();
        cboHinhThucThanhToan.setPreferredSize(new Dimension(150, 25));
        cboHinhThucThanhToan.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof HinhThucThanhToan) {
                    HinhThucThanhToan httt = (HinhThucThanhToan) value;
                    setText(httt.getTenHTTT());
                } else if (value == null) {
                    setText("");
                }
                return this;
            }
        });
        panel.add(cboHinhThucThanhToan, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Trạng thái TT:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        cboTrangThaiThanhToan = new JComboBox<>();
        cboTrangThaiThanhToan.setPreferredSize(new Dimension(150, 25));
        panel.add(cboTrangThaiThanhToan, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        tableModelHoaDon = new DefaultTableModel(
                new Object[]{"Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền", "Số items"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHoaDon = new JTable(tableModelHoaDon);
        tableHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHoaDon.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        tableHoaDon.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableHoaDon.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableHoaDon.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableHoaDon.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableHoaDon.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableHoaDon.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        btnAdd = new JButton("Thêm mới");
        btnAdd.setPreferredSize(new Dimension(110, 30));
        btnUpdate = new JButton("Cập nhật");
        btnUpdate.setPreferredSize(new Dimension(100, 30));
        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(80, 30));
        btnViewDetails = new JButton("Xem chi tiết");
        btnViewDetails.setPreferredSize(new Dimension(120, 30));
        btnAddDetail = new JButton("Thêm sản phẩm");
        btnAddDetail.setPreferredSize(new Dimension(140, 30));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnPrintPDF = new JButton("In PDF");
        btnPrintPDF.setPreferredSize(new Dimension(100, 30));

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnViewDetails);
        panel.add(btnAddDetail);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnRefresh);
        panel.add(btnPrintPDF);

        return panel;
    }

    private void setupEventHandlers() {
        btnAdd.addActionListener(e -> addHoaDon());
        btnUpdate.addActionListener(e -> updateHoaDon());
        btnDelete.addActionListener(e -> deleteHoaDon());
        btnViewDetails.addActionListener(e -> viewHoaDonDetails());
        btnAddDetail.addActionListener(e -> addProductToHoaDon());
        btnRefresh.addActionListener(e -> {
            clearFields();
            loadData();
        });
        btnPrintPDF.addActionListener(e -> printInvoiceToPDF());
        btnSelectEmployee.addActionListener(e -> selectEmployeeDialog());
        btnSelectCustomer.addActionListener(e -> selectCustomerDialog());

        // Add search event handlers
        btnSearch.addActionListener(e -> searchHoaDon());
        btnClearSearch.addActionListener(e -> {
            clearSearchFields();
            loadHoaDonTable(); // Reload all data when clearing search
        });

        // Add radio button event handlers
        radioKhachLe.addActionListener(e -> {
            if (radioKhachLe.isSelected()) {
                selectedCustomer = null;
                txtSelectedCustomer.setText("Khách lẻ");
                btnSelectCustomer.setVisible(false);
            }
        });

        radioHoiVien.addActionListener(e -> {
            if (radioHoiVien.isSelected()) {
                btnSelectCustomer.setVisible(true);
                if (selectedCustomer == null) {
                    txtSelectedCustomer.setText("");
                }
            }
        });

        tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });

        // Add double-click listener to table
        tableHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableHoaDon.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tableHoaDon.setRowSelectionInterval(row, row);
                        viewHoaDonDetails();
                    }
                }
            }
        });
    }

    private void loadData() {
        loadHoaDonTable();
        loadComboBoxes();
        loadSearchComboBoxes();
    }

    private void loadSearchComboBoxes() {
        // Load search payment status combo box
        DefaultComboBoxModel<String> searchPaymentStatusModel = new DefaultComboBoxModel<>();
        cboSearchPaymentStatus.setModel(searchPaymentStatusModel);
        searchPaymentStatusModel.addElement("Tất cả"); // Add "All" option
        searchPaymentStatusModel.addElement("Chờ thanh toán");
        searchPaymentStatusModel.addElement("Đang xử lý");
        searchPaymentStatusModel.addElement("Đã hoàn tất");
        searchPaymentStatusModel.addElement("Thất bại");
        searchPaymentStatusModel.addElement("Đã hoàn tiền");
        searchPaymentStatusModel.addElement("Đã hủy");
        cboSearchPaymentStatus.setSelectedIndex(0);
    }

    private void loadHoaDonTable() {
        tableModelHoaDon.setRowCount(0);
        try {
            // Use eager fetch to avoid lazy initialization when rendering table
            List<HoaDon> list = hoaDonController.getAllHoaDonWithDetails();
            for (HoaDon hd : list) {
                Object[] row = {
                        hd.getId(),
                        hd.getNgayLap().format(dateFormatter),
                        hd.getMaKH() != null ? hd.getMaKH().getHoTen() : "Khách lẻ",
                        hd.getMaNV() != null ? hd.getMaNV().getHoTen() : "N/A",
                        String.format("%,.0f VNĐ", hd.getTongTien()),
                        hd.getTotalItems()
                };
                tableModelHoaDon.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadComboBoxes() {
        try {
            // Set current user as default employee
            try {
                TaiKhoan currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser != null && currentUser.getMaNV() != null) {
                    selectedEmployee = currentUser.getMaNV();
                    txtSelectedEmployee.setText(selectedEmployee.getHoTen() + " - " + selectedEmployee.getChucVu());
                }
            } catch (Exception ex) {
                // If there's an issue with current user, just continue without setting default
            }

            // Load payment methods
            DefaultComboBoxModel<HinhThucThanhToan> paymentMethodModel = new DefaultComboBoxModel<>();
            cboHinhThucThanhToan.setModel(paymentMethodModel);
            paymentMethodModel.addElement(null); // Add empty option

            List<HinhThucThanhToan> paymentMethods = hinhThucThanhToanController.getActiveHinhThucThanhToan();
            for (HinhThucThanhToan method : paymentMethods) {
                paymentMethodModel.addElement(method);
            }
            cboHinhThucThanhToan.setSelectedIndex(0); // Select empty option

            // Load payment statuses
            DefaultComboBoxModel<String> paymentStatusModel = new DefaultComboBoxModel<>();
            cboTrangThaiThanhToan.setModel(paymentStatusModel);
            paymentStatusModel.addElement("Chờ thanh toán");
            paymentStatusModel.addElement("Đang xử lý");
            paymentStatusModel.addElement("Đã hoàn tất");
            paymentStatusModel.addElement("Thất bại");
            paymentStatusModel.addElement("Đã hoàn tiền");
            paymentStatusModel.addElement("Đã hủy");
            cboTrangThaiThanhToan.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải combo box: " + e.getMessage());
        }
    }

    private void searchHoaDon() {
        try {
            String maHD = txtSearchMaHD.getText().trim();
            String khachHang = txtSearchCustomer.getText().trim();
            String nhanVien = txtSearchEmployee.getText().trim();
            String fromDate = txtSearchFromDate.getText().trim();
            String toDate = txtSearchToDate.getText().trim();
            String trangThai = (String) cboSearchPaymentStatus.getSelectedItem();

            // Convert "Tất cả" to null for search
            if ("Tất cả".equals(trangThai)) {
                trangThai = null;
            } else if (trangThai != null) {
                // Convert display value to database value
                trangThai = DISPLAY_TO_DB.get(trangThai);
            }

            LocalDate startDate = null;
            LocalDate endDate = null;

            if (!fromDate.isEmpty()) {
                startDate = parseDate(fromDate);
            }

            if (!toDate.isEmpty()) {
                endDate = parseDate(toDate);
            }

            // Perform search using the existing table data for client-side filtering
            List<HoaDon> allInvoices = hoaDonController.getAllHoaDonWithDetails();
            tableModelHoaDon.setRowCount(0);
            int resultCount = 0;

            for (HoaDon hd : allInvoices) {
                boolean matches = true;

                // Filter by invoice ID
                if (!maHD.isEmpty()) {
                    if (!String.valueOf(hd.getId()).contains(maHD)) {
                        matches = false;
                    }
                }

                // Filter by customer name
                if (!khachHang.isEmpty() && matches) {
                    String customerName = hd.getMaKH() != null ? hd.getMaKH().getHoTen() : "Khách lẻ";
                    if (!customerName.toLowerCase().contains(khachHang.toLowerCase())) {
                        matches = false;
                    }
                }

                // Filter by employee name
                if (!nhanVien.isEmpty() && matches) {
                    String employeeName = hd.getMaNV() != null ? hd.getMaNV().getHoTen() : "";
                    if (!employeeName.toLowerCase().contains(nhanVien.toLowerCase())) {
                        matches = false;
                    }
                }

                // Filter by date range
                if (startDate != null && matches) {
                    if (hd.getNgayLap().isBefore(startDate)) {
                        matches = false;
                    }
                }

                if (endDate != null && matches) {
                    if (hd.getNgayLap().isAfter(endDate)) {
                        matches = false;
                    }
                }

                // Filter by payment status
                if (trangThai != null && matches) {
                    if (!trangThai.equals(hd.getTrangThaiThanhToan())) {
                        matches = false;
                    }
                }

                if (matches) {
                    Object[] row = {
                            hd.getId(),
                            hd.getNgayLap().format(dateFormatter),
                            hd.getMaKH() != null ? hd.getMaKH().getHoTen() : "Khách lẻ",
                            hd.getMaNV() != null ? hd.getMaNV().getHoTen() : "N/A",
                            String.format("%,.0f VNĐ", hd.getTongTien()),
                            hd.getTotalItems()
                    };
                    tableModelHoaDon.addRow(row);
                    resultCount++;
                }
            }

            JOptionPane.showMessageDialog(this, "Tìm thấy " + resultCount + " kết quả phù hợp.");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng sử dụng dd/MM/yyyy", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearchFields() {
        txtSearchMaHD.setText("");
        txtSearchCustomer.setText("");
        txtSearchEmployee.setText("");
        txtSearchFromDate.setText("");
        txtSearchToDate.setText("");
        cboSearchPaymentStatus.setSelectedIndex(0);
    }

    // Rest of the methods remain the same...
    private void selectEmployeeDialog() {
        NhanVien employee = EmployeeSelectionDialog.showDialog(this);
        if (employee != null) {
            selectedEmployee = employee;
            txtSelectedEmployee.setText(employee.getHoTen() + " - " + employee.getChucVu());
        }
    }

    private void selectCustomerDialog() {
        KhachHang customer = CustomerSelectionDialog.showDialog(this);
        if (customer != null) {
            selectedCustomer = customer;
            txtSelectedCustomer.setText(customer.getHoTen());
        }
    }

    private void setGuestCustomer() {
        selectedCustomer = null;
        txtSelectedCustomer.setText("Khách lẻ");
    }

    private void addHoaDon() {
        try {
            LocalDate ngayLap = parseDate(txtNgayLap.getText().trim());

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            HoaDon hd = new HoaDon(ngayLap, selectedCustomer, selectedEmployee);
            hoaDonController.addHoaDon(hd);

            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
            loadHoaDonTable();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày lập không hợp lệ! Định dạng: dd/MM/yyyy");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateHoaDon() {
        try {
            if (txtMaHD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để cập nhật!");
                return;
            }

            int id = Integer.parseInt(txtMaHD.getText().trim());
            HoaDon hd = hoaDonController.getHoaDonById(id);

            if (hd == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
                return;
            }

            // Set payment method and status
            HinhThucThanhToan selectedPaymentMethod = (HinhThucThanhToan) cboHinhThucThanhToan.getSelectedItem();
            if (selectedPaymentMethod != null) {
                hd.setMaHTTT(selectedPaymentMethod.getMaHTTT());
            }

            String selectedPaymentStatus = (String) cboTrangThaiThanhToan.getSelectedItem();
            if (selectedPaymentStatus != null) {
                // Convert display value to database value
                String dbValue = DISPLAY_TO_DB.get(selectedPaymentStatus);
                hd.setTrangThaiThanhToan(dbValue != null ? dbValue : selectedPaymentStatus);
            }

            LocalDate ngayLap = parseDate(txtNgayLap.getText().trim());

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            hd.setNgayLap(ngayLap);
            hd.setMaKH(selectedCustomer);
            hd.setMaNV(selectedEmployee);

            hoaDonController.updateHoaDon(hd);

            JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!");
            clearFields();
            loadHoaDonTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày lập không hợp lệ! Định dạng: dd/MM/yyyy");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteHoaDon() {
        try {
            if (txtMaHD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xóa!");
                return;
            }

            int id = Integer.parseInt(txtMaHD.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa hóa đơn này?\nToàn bộ chi tiết hóa đơn cũng sẽ bị xóa!",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                hoaDonController.deleteHoaDon(id);
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
                clearFields();
                loadHoaDonTable();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void viewHoaDonDetails() {
        try {
            if (txtMaHD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết!");
                return;
            }

            int id = Integer.parseInt(txtMaHD.getText().trim());
            // Load with details to avoid lazy initialization issues in the dialog
            HoaDon hoaDon = hoaDonController.getHoaDonByIdWithDetails(id);

            if (hoaDon == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
                return;
            }

            // Open detail dialog
            ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(this, hoaDon);
            dialog.setVisible(true);

            // Refresh table after dialog closes
            loadHoaDonTable();
            fillFieldsFromTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void addProductToHoaDon() {
        try {
            if (txtMaHD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để thêm sản phẩm!");
                return;
            }

            int id = Integer.parseInt(txtMaHD.getText().trim());
            HoaDon hoaDon = hoaDonController.getHoaDonById(id);

            if (hoaDon == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
                return;
            }

            // Open product selection dialog
            ThemSanPhamDialog dialog = new ThemSanPhamDialog(this, hoaDon);
            dialog.setVisible(true);

            // Refresh table after dialog closes
            loadHoaDonTable();
            fillFieldsFromTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtMaHD.setText("");
        txtNgayLap.setText(LocalDate.now().format(dateFormatter));
        txtTongTien.setText("");
        selectedCustomer = null;
        txtSelectedCustomer.setText("Khách lẻ");

        try {
            TaiKhoan currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getMaNV() != null) {
                selectedEmployee = currentUser.getMaNV();
                txtSelectedEmployee.setText(selectedEmployee.getHoTen() + " - " + selectedEmployee.getChucVu());
            } else {
                selectedEmployee = null;
                txtSelectedEmployee.setText("");
            }
        } catch (Exception ex) {
            selectedEmployee = null;
            txtSelectedEmployee.setText("");
        }
        radioKhachLe.setSelected(true);
        btnSelectCustomer.setVisible(false);

        tableHoaDon.clearSelection();
    }

    private void fillFieldsFromTable() {
        int row = tableHoaDon.getSelectedRow();
        if (row >= 0) {
            try {
                int id = (Integer) tableModelHoaDon.getValueAt(row, 0);
                // Use detailed fetch for binding and later actions
                HoaDon hd = hoaDonController.getHoaDonByIdWithDetails(id);

                if (hd != null) {
                    txtMaHD.setText(String.valueOf(hd.getId()));
                    txtNgayLap.setText(hd.getNgayLap().format(dateFormatter));
                    txtTongTien.setText(String.format("%,.0f VNĐ", hd.getTongTien()));

                    selectedCustomer = hd.getMaKH();
                    if (selectedCustomer != null) {
                        txtSelectedCustomer.setText(selectedCustomer.getHoTen());
                        // Customer exists - set to "Hội viên"
                        radioHoiVien.setSelected(true);
                        btnSelectCustomer.setVisible(true);
                    } else {
                        txtSelectedCustomer.setText("Khách lẻ");
                        // No customer - set to "Khách lẻ"
                        radioKhachLe.setSelected(true);
                        btnSelectCustomer.setVisible(false);
                    }

                    // Set payment method by ID
                    if (hd.getMaHTTT() != null && hd.getMaHTTT() != 0) {
                        // Find and select the payment method by ID
                        for (int i = 0; i < cboHinhThucThanhToan.getItemCount(); i++) {
                            HinhThucThanhToan item = cboHinhThucThanhToan.getItemAt(i);
                            if (item != null && item.getMaHTTT() == hd.getMaHTTT().intValue()) {
                                cboHinhThucThanhToan.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        cboHinhThucThanhToan.setSelectedIndex(0); // Select null option
                    }

                    // Set payment status by string value - convert from DB to display
                    String trangThaiThanhToan = hd.getTrangThaiThanhToan();
                    // Convert database value to display value
                    String displayValue = DB_TO_DISPLAY.get(trangThaiThanhToan);
                    if (displayValue != null && ((DefaultComboBoxModel<String>) cboTrangThaiThanhToan.getModel()).getIndexOf(displayValue) != -1) {
                        cboTrangThaiThanhToan.setSelectedItem(displayValue);
                    } else {
                        cboTrangThaiThanhToan.setSelectedIndex(0);
                    }

                    selectedEmployee = hd.getMaNV();
                    if (selectedEmployee != null) {
                        txtSelectedEmployee.setText(selectedEmployee.getHoTen() + " - " + selectedEmployee.getChucVu());
                    } else {
                        txtSelectedEmployee.setText("");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin hóa đơn: " + e.getMessage());
            }
        }
    }

    private LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr, dateFormatter);
    }

    private void printInvoiceToPDF() {
        try {
            if (txtMaHD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để in!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int hoaDonId = Integer.parseInt(txtMaHD.getText().trim());
            // Use the method that loads all details to avoid lazy initialization errors
            HoaDon hoaDon = hoaDonController.getHoaDonByIdWithDetails(hoaDonId);

            if (hoaDon == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Open file chooser for save location
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
            fileChooser.setSelectedFile(new File("HoaDon_" + hoaDonId + ".pdf"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                // Ensure file has .pdf extension
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Generate PDF
                PDFInvoiceGenerator.generateInvoicePDF(hoaDon, filePath);

                JOptionPane.showMessageDialog(this,
                    "Đã in hóa đơn thành công!\nFile được lưu tại: " + filePath,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

                // Ask if user wants to open the PDF
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn mở file PDF vừa tạo không?",
                    "Mở file",
                    JOptionPane.YES_NO_OPTION);

                if (openFile == JOptionPane.YES_OPTION) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(new File(filePath));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể mở file PDF. Vui lòng mở thủ công tại: " + filePath,
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HoaDonUI().setVisible(true));
    }
}
