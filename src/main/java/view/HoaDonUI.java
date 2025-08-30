package view;

import controller.BienTheSanPhamController;
import controller.ChiTietHoaDonController;
import controller.HoaDonController;
import controller.KhachHangController;
import controller.NhanVienController;
import model.*;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.io.File;
import util.PDFInvoiceGenerator;

public class HoaDonUI extends JFrame {
    private JTextField txtMaHD, txtNgayLap, txtTongTien, txtSelectedEmployee, txtSelectedCustomer, txtCustomerName;
    private JButton btnSelectEmployee, btnSelectCustomer;
    private JRadioButton radioKhachLe, radioHoiVien;
    private ButtonGroup customerTypeGroup;
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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HoaDonUI() {
        initControllers();
        setTitle("Quản Lý Hóa Đơn");
        setSize(900, 600);
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
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = createInputPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setupEventHandlers();
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

        // Customer selection button - moved after radio buttons
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
        customerTypeGroup.add(btnSelectCustomer);

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

    }

    private void loadData() {
        loadHoaDonTable();
        loadComboBoxes();
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
            // Load customers
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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải combo box: " + e.getMessage());
        }
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
            LocalDate ngayLap = parseDate(txtNgayLap.getText().trim());

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            HoaDon hd = new HoaDon(ngayLap, selectedCustomer, selectedEmployee);
            hoaDonController.addHoaDon(hd);

            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
            clearFields();
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
            JOptionPane.showMessageDialog(this, "M�� hóa đơn không hợp lệ!");
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
            HoaDon hoaDon = hoaDonController.getHoaDonById(hoaDonId);

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
