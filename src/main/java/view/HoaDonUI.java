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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import util.PDFInvoiceGenerator;
import com.toedter.calendar.JDateChooser; // Added for date picker
import java.util.Date; // for JDateChooser value
import java.time.ZoneId; // for conversion to LocalDate
import java.util.ArrayList; // pagination list

public class HoaDonUI extends JFrame {
    private JTextField txtMaHD, txtTongTien, txtSelectedEmployee, txtSelectedCustomer, txtCustomerName;
    // Date choosers replacing text fields
    private JDateChooser txtNgayLap;
    private JDateChooser txtSearchFromDate, txtSearchToDate;
    // Search fields (non-date)
    private JTextField txtSearchMaHD, txtSearchCustomer, txtSearchEmployee;
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
    // Pagination state
    private List<HoaDon> allHoaDonCache = new ArrayList<>();
    private List<HoaDon> filteredHoaDon = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 30;
    private int totalPages = 1;
    private JLabel lblPageInfo;
    private JButton btnFirstPage, btnPrevPage, btnNextPage, btnLastPage;
    private JComboBox<Integer> cboPageSize;

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
        setSize(1200, 1000);
        setMinimumSize(new Dimension(1200, 1000)); // Set minimum size to ensure search panel is fully visible
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
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Tìm kiếm hóa đơn",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13)
        ));
        mainPanel.setPreferredSize(new Dimension(280, 0));

        // Create search criteria panel
        JPanel criteriaPanel = createSearchCriteriaPanel();
        
        // Create search actions panel
        JPanel actionsPanel = createSearchActionsPanel();
        
        mainPanel.add(criteriaPanel, BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }

    private JPanel createSearchCriteriaPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 5, 8));
        
        // Create sub-panels for better organization
        JPanel basicSearchPanel = createBasicSearchPanel();
        JPanel dateRangePanel = createDateRangePanel();
        JPanel statusPanel = createStatusSearchPanel();
        
        // Arrange panels vertically
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 0, 3, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;

        // Basic search panel (top)
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(basicSearchPanel, gbc);

        // Date range panel (middle)
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(dateRangePanel, gbc);

        // Status panel (bottom)
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(statusPanel, gbc);

        // Add spacer to push everything to top
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(Box.createVerticalGlue(), gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createBasicSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin cơ bản",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.PLAIN, 11)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Invoice ID search
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblSearchMaHD = new JLabel("Mã hóa đơn:");
        lblSearchMaHD.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblSearchMaHD, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtSearchMaHD = new JTextField();
        txtSearchMaHD.setPreferredSize(new Dimension(220, 26));
        txtSearchMaHD.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSearchMaHD.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        panel.add(txtSearchMaHD, gbc);

        // Customer search
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(10, 8, 6, 8);
        JLabel lblSearchCustomer = new JLabel("Khách hàng:");
        lblSearchCustomer.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblSearchCustomer, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 8, 6, 8);
        txtSearchCustomer = new JTextField();
        txtSearchCustomer.setPreferredSize(new Dimension(220, 26));
        txtSearchCustomer.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSearchCustomer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        panel.add(txtSearchCustomer, gbc);

        // Employee search
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(10, 8, 6, 8);
        JLabel lblSearchEmployee = new JLabel("Nhân viên:");
        lblSearchEmployee.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblSearchEmployee, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 8, 8, 8);
        txtSearchEmployee = new JTextField();
        txtSearchEmployee.setPreferredSize(new Dimension(220, 26));
        txtSearchEmployee.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSearchEmployee.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        panel.add(txtSearchEmployee, gbc);

        return panel;
    }

    private JPanel createDateRangePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Khoảng thời gian",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.PLAIN, 11)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // From date
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFromDate = new JLabel("Từ ngày:");
        lblFromDate.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblFromDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtSearchFromDate = new JDateChooser();
        txtSearchFromDate.setDateFormatString("dd/MM/yyyy");
        txtSearchFromDate.setPreferredSize(new Dimension(220, 26));
        txtSearchFromDate.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSearchFromDate.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        panel.add(txtSearchFromDate, gbc);

        // To date
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(10, 8, 6, 8);
        JLabel lblToDate = new JLabel("Đến ngày:");
        lblToDate.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblToDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 8, 8, 8);
        txtSearchToDate = new JDateChooser();
        txtSearchToDate.setDateFormatString("dd/MM/yyyy");
        txtSearchToDate.setPreferredSize(new Dimension(220, 26));
        txtSearchToDate.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSearchToDate.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        panel.add(txtSearchToDate, gbc);

        return panel;
    }

    private JPanel createStatusSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Trạng thái",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.PLAIN, 11)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Payment status
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblPaymentStatus = new JLabel("Trạng thái thanh toán:");
        lblPaymentStatus.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblPaymentStatus, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboSearchPaymentStatus = new JComboBox<>();
        cboSearchPaymentStatus.setPreferredSize(new Dimension(220, 28));
        cboSearchPaymentStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        cboSearchPaymentStatus.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        panel.add(cboSearchPaymentStatus, gbc);

        return panel;
    }

    private JPanel createSearchActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Search button
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(100, 32));
        btnSearch.setFont(new Font("Arial", Font.BOLD, 11));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 86, 179)),
            BorderFactory.createEmptyBorder(6, 15, 6, 15)
        ));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect for search button
        btnSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSearch.setBackground(new Color(0, 86, 179));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnSearch.setBackground(new Color(0, 123, 255));
            }
        });

        // Clear button
        btnClearSearch = new JButton("Xóa bộ lọc");
        btnClearSearch.setPreferredSize(new Dimension(100, 32));
        btnClearSearch.setFont(new Font("Arial", Font.PLAIN, 11));
        btnClearSearch.setBackground(new Color(248, 249, 250));
        btnClearSearch.setForeground(new Color(108, 117, 125));
        btnClearSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(6, 15, 6, 15)
        ));
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect for clear button
        btnClearSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClearSearch.setBackground(new Color(233, 236, 239));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnClearSearch.setBackground(new Color(248, 249, 250));
            }
        });

        panel.add(btnSearch);
        panel.add(btnClearSearch);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Thông tin hóa đơn",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // Create sub-panels for better organization
        JPanel basicInfoPanel = createBasicInfoPanel();
        JPanel customerInfoPanel = createCustomerInfoPanel();
        JPanel paymentInfoPanel = createPaymentInfoPanel();

        // Arrange panels in a grid
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Basic info panel (top)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(basicInfoPanel, gbc);

        // Customer info panel (middle left)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        contentPanel.add(customerInfoPanel, gbc);

        // Payment info panel (middle right)
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1;
        contentPanel.add(paymentInfoPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Thông tin cơ bản",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.PLAIN, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Invoice ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblMaHD = new JLabel("Mã hóa đơn:");
        lblMaHD.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblMaHD, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        txtMaHD = new JTextField(12);
        txtMaHD.setEditable(false);
        txtMaHD.setBackground(new Color(245, 245, 245));
        txtMaHD.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        txtMaHD.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtMaHD, gbc);

        // Date
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 10);
        JLabel lblNgayLap = new JLabel("Ngày lập:");
        lblNgayLap.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblNgayLap, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        gbc.insets = new Insets(8, 10, 8, 10);
        txtNgayLap = new JDateChooser();
        txtNgayLap.setDateFormatString("dd/MM/yyyy");
        txtNgayLap.setDate(java.sql.Date.valueOf(LocalDate.now()));
        txtNgayLap.setPreferredSize(new Dimension(140, 30));
        txtNgayLap.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtNgayLap, gbc);

        // Total amount
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 10);
        JLabel lblTongTien = new JLabel("Tổng tiền:");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTongTien, gbc);

        gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.4;
        gbc.insets = new Insets(8, 10, 8, 10);
        txtTongTien = new JTextField(15);
        txtTongTien.setEditable(false);
        txtTongTien.setBackground(new Color(240, 255, 240));
        txtTongTien.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 0)),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        txtTongTien.setFont(new Font("Arial", Font.BOLD, 12));
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtTongTien, gbc);

        return panel;
    }

    private JPanel createCustomerInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Thông tin khách hàng & nhân viên",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.PLAIN, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Customer type selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel lblLoaiKH = new JLabel("Loại khách hàng:");
        lblLoaiKH.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblLoaiKH, gbc);

        // Radio buttons panel
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioKhachLe = new JRadioButton("Khách lẻ");
        radioKhachLe.setSelected(true);
        radioKhachLe.setFont(new Font("Arial", Font.PLAIN, 11));
        radioHoiVien = new JRadioButton("Hội viên");
        radioHoiVien.setFont(new Font("Arial", Font.PLAIN, 11));

        customerTypeGroup = new ButtonGroup();
        customerTypeGroup.add(radioKhachLe);
        customerTypeGroup.add(radioHoiVien);

        radioPanel.add(radioKhachLe);
        radioPanel.add(radioHoiVien);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(radioPanel, gbc);

        // Customer selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel lblKhachHang = new JLabel("Khách hàng:");
        lblKhachHang.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblKhachHang, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtSelectedCustomer = new JTextField();
        txtSelectedCustomer.setEditable(false);
        txtSelectedCustomer.setPreferredSize(new Dimension(200, 28));
        txtSelectedCustomer.setText("Khách lẻ");
        txtSelectedCustomer.setBackground(new Color(250, 250, 250));
        txtSelectedCustomer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        txtSelectedCustomer.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtSelectedCustomer, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(8, 5, 8, 10);
        btnSelectCustomer = new JButton("Chọn");
        btnSelectCustomer.setPreferredSize(new Dimension(80, 28));
        btnSelectCustomer.setFont(new Font("Arial", Font.PLAIN, 11));
        btnSelectCustomer.setVisible(false);
        panel.add(btnSelectCustomer, gbc);

        // Employee selection
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 10, 8, 10);
        JLabel lblNhanVien = new JLabel("Nhân viên:");
        lblNhanVien.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblNhanVien, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtSelectedEmployee = new JTextField();
        txtSelectedEmployee.setEditable(false);
        txtSelectedEmployee.setPreferredSize(new Dimension(200, 28));
        txtSelectedEmployee.setBackground(new Color(250, 250, 250));
        txtSelectedEmployee.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        txtSelectedEmployee.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtSelectedEmployee, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(8, 5, 8, 10);
        btnSelectEmployee = new JButton("Chọn");
        btnSelectEmployee.setPreferredSize(new Dimension(80, 28));
        btnSelectEmployee.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(btnSelectEmployee, gbc);

        return panel;
    }

    private JPanel createPaymentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Thông tin thanh toán",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.PLAIN, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Payment method
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblHinhThuc = new JLabel("Hình thức thanh toán:");
        lblHinhThuc.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblHinhThuc, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboHinhThucThanhToan = new JComboBox<>();
        cboHinhThucThanhToan.setPreferredSize(new Dimension(200, 30));
        cboHinhThucThanhToan.setFont(new Font("Arial", Font.PLAIN, 12));
        cboHinhThucThanhToan.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof HinhThucThanhToan) {
                    HinhThucThanhToan httt = (HinhThucThanhToan) value;
                    setText(httt.getTenHTTT());
                } else if (value == null) {
                    setText("-- Chọn hình thức thanh toán --");
                }
                return this;
            }
        });
        panel.add(cboHinhThucThanhToan, gbc);

        // Payment status
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 10, 5, 10);
        JLabel lblTrangThai = new JLabel("Trạng thái thanh toán:");
        lblTrangThai.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 10, 10, 10);
        cboTrangThaiThanhToan = new JComboBox<>();
        cboTrangThaiThanhToan.setPreferredSize(new Dimension(200, 30));
        cboTrangThaiThanhToan.setFont(new Font("Arial", Font.PLAIN, 12));
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

        // Pagination controls panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnFirstPage = new JButton("|<");
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        btnLastPage = new JButton(">|");
        stylePaginationButton(btnFirstPage);
        stylePaginationButton(btnPrevPage);
        stylePaginationButton(btnNextPage);
        stylePaginationButton(btnLastPage);

        lblPageInfo = new JLabel("Page 1/1 (0 records)");
        lblPageInfo.setFont(new Font("Arial", Font.PLAIN, 11));

        cboPageSize = new JComboBox<>(new Integer[]{10, 30, 50, 100});
        cboPageSize.setSelectedItem(30);
        cboPageSize.setFont(new Font("Arial", Font.PLAIN, 11));
        cboPageSize.setPreferredSize(new Dimension(70, 26));

        paginationPanel.add(new JLabel("Rows:"));
        paginationPanel.add(cboPageSize);
        paginationPanel.add(btnFirstPage);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNextPage);
        paginationPanel.add(btnLastPage);

        panel.add(paginationPanel, BorderLayout.SOUTH);

        // Add listeners for pagination
        btnFirstPage.addActionListener(e -> {
            if (currentPage != 1) {
                currentPage = 1;
                renderCurrentPage();
            }
        });
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderCurrentPage();
            }
        });
        btnNextPage.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                renderCurrentPage();
            }
        });
        btnLastPage.addActionListener(e -> {
            if (currentPage != totalPages) {
                currentPage = totalPages;
                renderCurrentPage();
            }
        });
        cboPageSize.addActionListener(e -> {
            Integer sel = (Integer) cboPageSize.getSelectedItem();
            if (sel != null && sel != pageSize) {
                pageSize = sel;
                currentPage = 1;
                recalcPagination();
                renderCurrentPage();
            }
        });

        return panel;
    }

    private void stylePaginationButton(JButton btn) {
        btn.setMargin(new Insets(2, 8, 2, 8));
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
    }

    private void recalcPagination() {
        int total = filteredHoaDon.size();
        totalPages = total == 0 ? 1 : (int) Math.ceil(total / (double) pageSize);
        if (currentPage > totalPages) currentPage = totalPages;
        updatePaginationControls();
    }

    private void updatePaginationControls() {
        int totalRecords = filteredHoaDon.size();
        lblPageInfo.setText("Page " + currentPage + "/" + totalPages + " (" + totalRecords + " records)");
        btnFirstPage.setEnabled(currentPage > 1);
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);
        btnLastPage.setEnabled(currentPage < totalPages);
    }

    private void renderCurrentPage() {
        tableModelHoaDon.setRowCount(0);
        if (filteredHoaDon.isEmpty()) {
            updatePaginationControls();
            return;
        }
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredHoaDon.size());
        for (int i = start; i < end; i++) {
            HoaDon hd = filteredHoaDon.get(i);
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
        updatePaginationControls();
    }

    private void loadHoaDonTable() {
        try {
            allHoaDonCache = hoaDonController.getAllHoaDonWithDetails();
            filteredHoaDon = new ArrayList<>(allHoaDonCache);
            currentPage = 1;
            recalcPagination();
            renderCurrentPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
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
            // Ensure we have the latest data (optional – can comment out if not desired)
            allHoaDonCache = hoaDonController.getAllHoaDonWithDetails();

            String maHD = txtSearchMaHD.getText().trim();
            String khachHang = txtSearchCustomer.getText().trim();
            String nhanVien = txtSearchEmployee.getText().trim();
            LocalDate startDate = getLocalDateFromChooser(txtSearchFromDate);
            LocalDate endDate = getLocalDateFromChooser(txtSearchToDate);
            String trangThai = (String) cboSearchPaymentStatus.getSelectedItem();

            if ("Tất cả".equals(trangThai)) {
                trangThai = null;
            } else if (trangThai != null) {
                trangThai = DISPLAY_TO_DB.get(trangThai);
            }

            filteredHoaDon = new ArrayList<>();
            int resultCount = 0;
            for (HoaDon hd : allHoaDonCache) {
                boolean matches = true;
                if (!maHD.isEmpty() && matches) {
                    if (!String.valueOf(hd.getId()).contains(maHD)) matches = false;
                }
                if (!khachHang.isEmpty() && matches) {
                    String customerName = hd.getMaKH() != null ? hd.getMaKH().getHoTen() : "Khách lẻ";
                    if (!customerName.toLowerCase().contains(khachHang.toLowerCase())) matches = false;
                }
                if (!nhanVien.isEmpty() && matches) {
                    String employeeName = hd.getMaNV() != null ? hd.getMaNV().getHoTen() : "";
                    if (!employeeName.toLowerCase().contains(nhanVien.toLowerCase())) matches = false;
                }
                if (startDate != null && matches) {
                    if (hd.getNgayLap().isBefore(startDate)) matches = false;
                }
                if (endDate != null && matches) {
                    if (hd.getNgayLap().isAfter(endDate)) matches = false;
                }
                if (trangThai != null && matches) {
                    if (!trangThai.equals(hd.getTrangThaiThanhToan())) matches = false;
                }
                if (matches) {
                    filteredHoaDon.add(hd);
                    resultCount++;
                }
            }
            currentPage = 1;
            recalcPagination();
            renderCurrentPage();
            JOptionPane.showMessageDialog(this, "Tìm thấy " + resultCount + " kết quả phù hợp.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearchFields() {
        txtSearchMaHD.setText("");
        txtSearchCustomer.setText("");
        txtSearchEmployee.setText("");
        txtSearchFromDate.setDate(null);
        txtSearchToDate.setDate(null);
        cboSearchPaymentStatus.setSelectedIndex(0);
        // Reset filtered list to all data and re-render
        filteredHoaDon = new ArrayList<>(allHoaDonCache);
        currentPage = 1;
        recalcPagination();
        renderCurrentPage();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Helper method to create buttons with consistent styling
        btnAdd = createStyledButton("Thêm mới", Color.WHITE, Color.BLACK, 120);
        btnUpdate = createStyledButton("Cập nhật", Color.WHITE, Color.BLACK, 120);
        btnDelete = createStyledButton("Xóa", Color.LIGHT_GRAY, Color.BLACK, 100);

        btnViewDetails = createStyledButton("Xem chi tiết", Color.WHITE, Color.BLACK, 140);
        btnAddDetail = createStyledButton("Thêm sản phẩm", Color.WHITE, Color.BLACK, 160);

        btnRefresh = createStyledButton("Làm mới", Color.GRAY, Color.WHITE, 120);
        btnPrintPDF = createStyledButton("In PDF", Color.DARK_GRAY, Color.WHITE, 120);

        // Group buttons logically
        JPanel mainActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        mainActionsPanel.add(btnAdd);
        mainActionsPanel.add(btnUpdate);
        mainActionsPanel.add(btnDelete);

        JPanel detailActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        detailActionsPanel.add(btnViewDetails);
        detailActionsPanel.add(btnAddDetail);

        JPanel utilityActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        utilityActionsPanel.add(btnRefresh);
        utilityActionsPanel.add(btnPrintPDF);

        // Add groups to main panel
        panel.add(mainActionsPanel);
        panel.add(createSeparator());
        panel.add(detailActionsPanel);
        panel.add(createSeparator());
        panel.add(utilityActionsPanel);

        return panel;
    }

    // Helper method to create consistently styled buttons
    private JButton createStyledButton(String text, Color backgroundColor, Color textColor, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add simple hover effect
        button.addMouseListener(new MouseAdapter() {
            private Color originalBackground = backgroundColor;
            private Color originalText = textColor;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (backgroundColor == Color.WHITE) {
                    button.setBackground(new Color(240, 240, 240));
                } else if (backgroundColor == Color.LIGHT_GRAY) {
                    button.setBackground(new Color(200, 200, 200));
                } else if (backgroundColor == Color.GRAY) {
                    button.setBackground(new Color(100, 100, 100));
                } else if (backgroundColor == Color.DARK_GRAY) {
                    button.setBackground(new Color(60, 60, 60));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBackground);
                button.setForeground(originalText);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (backgroundColor == Color.WHITE) {
                    button.setBackground(new Color(220, 220, 220));
                } else if (backgroundColor == Color.LIGHT_GRAY) {
                    button.setBackground(new Color(180, 180, 180));
                } else if (backgroundColor == Color.GRAY) {
                    button.setBackground(new Color(80, 80, 80));
                } else if (backgroundColor == Color.DARK_GRAY) {
                    button.setBackground(new Color(40, 40, 40));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(originalBackground);
            }
        });

        return button;
    }

    // Helper method to create visual separators between button groups
    private JPanel createSeparator() {
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(2, 30));
        separator.setBackground(new Color(220, 220, 220));
        separator.setBorder(BorderFactory.createEtchedBorder());
        return separator;
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
            LocalDate ngayLap = getLocalDateFromChooser(txtNgayLap);
            if (ngayLap == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày lập!");
                return;
            }

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            HoaDon hd = new HoaDon(ngayLap, selectedCustomer, selectedEmployee);
            hoaDonController.addHoaDon(hd);

            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
            loadHoaDonTable();

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

            LocalDate ngayLap = getLocalDateFromChooser(txtNgayLap);
            if (ngayLap == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày lập!");
                return;
            }

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
            // Load with details to avoid lazy initialization issues in the dialog
            HoaDon hoaDon = hoaDonController.getHoaDonByIdWithDetails(id);

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
        txtNgayLap.setDate(java.sql.Date.valueOf(LocalDate.now()));
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
                    txtNgayLap.setDate(java.sql.Date.valueOf(hd.getNgayLap()));
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

    private LocalDate getLocalDateFromChooser(JDateChooser chooser) {
        Date date = chooser.getDate();
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
