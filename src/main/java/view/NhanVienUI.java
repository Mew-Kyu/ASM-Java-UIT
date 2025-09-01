package view;

import model.NhanVien;
import controller.NhanVienController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import com.toedter.calendar.JDateChooser;

public class NhanVienUI extends JFrame {
    private NhanVienController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtHoTen, txtDienThoai, txtEmail, txtChucVu;
    private JTextField txtTimKiem;
    private JComboBox<String> cmbGioiTinh;
    private JDateChooser dateChooserNgaySinh;
    private JCheckBox chkTrangThai;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnTimKiem;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public NhanVienUI() {
        // Check authentication and authorization
        if (!SessionManager.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            this.dispose();
            return;
        }

        if (!RoleManager.canAccessEmployeeManagement()) {
            RoleManager.showAccessDeniedMessage(null, "Admin");
            this.dispose();
            return;
        }

        controller = new NhanVienController();
        setTitle("Quản Lý Nhân Viên - " + SessionManager.getInstance().getCurrentUsername());
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSearch.setBorder(BorderFactory.createTitledBorder("Tìm kiếm nhân viên"));
        panelSearch.add(new JLabel("Từ khóa:"));
        txtTimKiem = new JTextField(20);
        panelSearch.add(txtTimKiem);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(90, 25));
        panelSearch.add(btnTimKiem);

        tableModel = new DefaultTableModel(new Object[]{"Mã NV", "Họ Tên", "Giới Tính", "Ngày Sinh", "Điện Thoại", "Email", "Chức Vụ", "Trạng Thái"}, 0);
        table = new JTable(tableModel);
        
        // Configure column widths for better display
        setupColumnWidths();
        
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "👥 Thông tin nhân viên",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(51, 102, 153)),
            BorderFactory.createEmptyBorder(15, 20, 20, 20)
        ));
        inputPanel.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Create styled labels with icons
        JLabel lblId = new JLabel("🏷️ Mã nhân viên:");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblId.setForeground(new Color(68, 68, 68));

        JLabel lblHoTen = new JLabel("👤 Họ và tên:");
        lblHoTen.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblHoTen.setForeground(new Color(68, 68, 68));

        JLabel lblGioiTinh = new JLabel("⚧ Giới tính:");
        lblGioiTinh.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblGioiTinh.setForeground(new Color(68, 68, 68));

        JLabel lblNgaySinh = new JLabel("🎂 Ngày sinh:");
        lblNgaySinh.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNgaySinh.setForeground(new Color(68, 68, 68));

        JLabel lblDienThoai = new JLabel("📞 Điện thoại:");
        lblDienThoai.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDienThoai.setForeground(new Color(68, 68, 68));

        JLabel lblEmail = new JLabel("📧 Email:");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEmail.setForeground(new Color(68, 68, 68));

        JLabel lblChucVu = new JLabel("💼 Chức vụ:");
        lblChucVu.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblChucVu.setForeground(new Color(68, 68, 68));

        JLabel lblTrangThai = new JLabel("✅ Trạng thái:");
        lblTrangThai.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTrangThai.setForeground(new Color(68, 68, 68));

        // Style text fields
        txtId = new JTextField();
        txtId.setEnabled(false);
        txtId.setBackground(new Color(240, 240, 240));
        txtId.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtId.setPreferredSize(new Dimension(120, 30));

        txtHoTen = new JTextField();
        txtHoTen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtHoTen.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtHoTen.setToolTipText("Nhập họ và tên đầy đủ (bắt buộc)");
        txtHoTen.setPreferredSize(new Dimension(180, 30));

        txtDienThoai = new JTextField();
        txtDienThoai.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtDienThoai.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDienThoai.setToolTipText("Nhập số điện thoại (bắt buộc)");
        txtDienThoai.setPreferredSize(new Dimension(140, 30));

        txtEmail = new JTextField();
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtEmail.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtEmail.setToolTipText("Nhập địa chỉ email (bắt buộc)");
        txtEmail.setPreferredSize(new Dimension(180, 30));

        txtChucVu = new JTextField();
        txtChucVu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtChucVu.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtChucVu.setToolTipText("Nhập chức vụ (bắt buộc)");
        txtChucVu.setPreferredSize(new Dimension(140, 30));

        // Style combo box and other components
        cmbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cmbGioiTinh.setBorder(null);
        cmbGioiTinh.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cmbGioiTinh.setBackground(Color.WHITE);
        cmbGioiTinh.setToolTipText("Chọn giới tính");
        cmbGioiTinh.setPreferredSize(new Dimension(120, 30));

        dateChooserNgaySinh = new JDateChooser();
        dateChooserNgaySinh.setDateFormatString("yyyy-MM-dd");
        dateChooserNgaySinh.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        dateChooserNgaySinh.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateChooserNgaySinh.setToolTipText("Chọn ngày sinh (bắt buộc)");
        dateChooserNgaySinh.setPreferredSize(new Dimension(140, 30));

        chkTrangThai = new JCheckBox("Đang làm việc");
        chkTrangThai.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkTrangThai.setBackground(new Color(248, 249, 250));
        chkTrangThai.setToolTipText("Tích để đánh dấu nhân viên đang làm việc");

        // Row 1 - Mã NV và Họ tên
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(txtId, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        inputPanel.add(lblHoTen, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 8, 8, 8);
        inputPanel.add(txtHoTen, gbc);

        // Row 2 - Giới tính và Ngày sinh
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(lblGioiTinh, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(cmbGioiTinh, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        inputPanel.add(lblNgaySinh, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 8, 8, 8);
        inputPanel.add(dateChooserNgaySinh, gbc);

        // Row 3 - Điện thoại và Email
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(lblDienThoai, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(txtDienThoai, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        inputPanel.add(lblEmail, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 8, 8, 8);
        inputPanel.add(txtEmail, gbc);

        // Row 4 - Chức vụ và Trạng thái
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(lblChucVu, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(txtChucVu, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        inputPanel.add(lblTrangThai, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 8, 8, 8);
        inputPanel.add(chkTrangThai, gbc);

        // Add visual separation
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        gbc.weightx = 1.0;
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(220, 220, 220));
        inputPanel.add(separator, gbc);

        // Add instruction label
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.weightx = 0;
        JLabel instructionLabel = new JLabel("💡 Chọn một dòng trong bảng để chỉnh sửa hoặc nhập thông tin mới để thêm nhân viên");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructionLabel.setForeground(new Color(102, 102, 102));
        inputPanel.add(instructionLabel, gbc);

        // Top panel containing search and input panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelSearch, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(new Dimension(80, 30));
        btnUpdate = new JButton("Sửa");
        btnUpdate.setPreferredSize(new Dimension(80, 30));
        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(80, 30));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Search functionality - only when button is clicked or Enter key pressed
        btnTimKiem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // Allow Enter key to trigger search
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        btnAdd.addActionListener(e -> {
            if (validateInput()) {
                try {
                    NhanVien nv = new NhanVien();
                    nv.setHoTen(txtHoTen.getText().trim());
                    nv.setGioiTinh((String) cmbGioiTinh.getSelectedItem());

                    // Convert Date to LocalDate
                    if (dateChooserNgaySinh.getDate() != null) {
                        java.util.Date selectedDate = dateChooserNgaySinh.getDate();
                        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
                        nv.setNgaySinh(localDate);
                    }
                    
                    nv.setDienThoai(txtDienThoai.getText().trim());
                    nv.setEmail(txtEmail.getText().trim());
                    nv.setChucVu(txtChucVu.getText().trim());
                    nv.setTrangThai(chkTrangThai.isSelected());
                    
                    controller.addNhanVien(nv);
                    loadTable();
                    clearInput();
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                if (validateInput()) {
                    try {
                        NhanVien nv = new NhanVien();
                        nv.setId(Integer.parseInt(txtId.getText()));
                        nv.setHoTen(txtHoTen.getText().trim());
                        nv.setGioiTinh((String) cmbGioiTinh.getSelectedItem());
                        
                        // Convert Date to LocalDate
                        if (dateChooserNgaySinh.getDate() != null) {
                            java.util.Date selectedDate = dateChooserNgaySinh.getDate();
                            LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
                            nv.setNgaySinh(localDate);
                        }

                        nv.setDienThoai(txtDienThoai.getText().trim());
                        nv.setEmail(txtEmail.getText().trim());
                        nv.setChucVu(txtChucVu.getText().trim());
                        nv.setTrangThai(chkTrangThai.isSelected());
                        
                        controller.updateNhanVien(nv);
                        loadTable();
                        clearInput();
                        JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(txtId.getText());
                controller.deleteNhanVien(id);
                loadTable();
                clearInput();
            } else {
                JOptionPane.showMessageDialog(NhanVienUI.this, "Chọn một dòng để xóa!");
            }
        });

        btnRefresh.addActionListener(e -> {
            clearInput();
            txtTimKiem.setText(""); // Clear search field
            loadTable(); // Reload all data
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtHoTen.setText(tableModel.getValueAt(row, 1).toString());

                // Set gender combobox
                String gioiTinh = tableModel.getValueAt(row, 2).toString();
                cmbGioiTinh.setSelectedItem(gioiTinh);

                // Set date picker
                try {
                    String ngaySinhStr = tableModel.getValueAt(row, 3).toString();
                    LocalDate localDate = LocalDate.parse(ngaySinhStr);
                    java.util.Date utilDate = java.sql.Date.valueOf(localDate);
                    dateChooserNgaySinh.setDate(utilDate);
                } catch (Exception ex) {
                    dateChooserNgaySinh.setDate(null);
                }

                txtDienThoai.setText(tableModel.getValueAt(row, 4).toString());
                txtEmail.setText(tableModel.getValueAt(row, 5).toString());
                txtChucVu.setText(tableModel.getValueAt(row, 6).toString());

                // Convert text status back to boolean for checkbox
                String trangThaiText = tableModel.getValueAt(row, 7).toString();
                chkTrangThai.setSelected(parseTrangThai(trangThaiText));
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<NhanVien> list = controller.getAllNhanVien();
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                nv.getId(),
                nv.getHoTen(),
                nv.getGioiTinh(), 
                nv.getNgaySinh(), 
                nv.getDienThoai(), 
                nv.getEmail(), 
                nv.getChucVu(), 
                formatTrangThai(nv.getTrangThai())
            });
        }
    }

    private void clearInput() {
        txtId.setText("");
        txtHoTen.setText("");
        cmbGioiTinh.setSelectedIndex(0);
        dateChooserNgaySinh.setDate(null);
        txtDienThoai.setText("");
        txtEmail.setText("");
        txtChucVu.setText("");
        chkTrangThai.setSelected(false);
        table.clearSelection();
    }
    
    /**
     * Configures column widths for optimal display
     */
    private void setupColumnWidths() {
        // Set specific widths for each column
        // Index: 0=Mã NV, 1=Họ Tên, 2=Giới Tính, 3=Ngày Sinh, 4=Điện Thoại, 5=Email, 6=Chức Vụ, 7=Trạng Thái
        
        int[] columnWidths = {
            60,   // Mã NV - smaller width (only numbers)
            120,  // Họ Tên 
            80,   // Giới Tính
            100,  // Ngày Sinh
            110,  // Điện Thoại
            150,  // Email 
            100,  // Chức Vụ
            140   // Trạng Thái - wider to fit "Không còn làm việc"
        };
        
        for (int i = 0; i < columnWidths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            
            // Set minimum width to prevent columns from becoming too small
            if (i == 0) { // Mã NV
                table.getColumnModel().getColumn(i).setMinWidth(50);
                table.getColumnModel().getColumn(i).setMaxWidth(80);
            } else if (i == 7) { // Trạng Thái
                table.getColumnModel().getColumn(i).setMinWidth(130);
            }
        }

        // Enable auto-resize mode and other table improvements
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Improve table appearance
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
    }

    /**
     * Gets actual text from field, ignoring placeholder text
     */
    private String getActualText(JTextField textField) {
        return textField.getForeground().equals(Color.GRAY) ? "" : textField.getText().trim();
    }

    /**
     * Checks if the field contains placeholder text
     */
    private boolean isPlaceholderText(JTextField textField, String placeholder) {
        return textField.getText().equals(placeholder) || textField.getForeground().equals(Color.GRAY);
    }

    /**
     * Validates email format using regex
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Converts boolean status to meaningful Vietnamese text
     */
    private String formatTrangThai(Boolean trangThai) {
        return (trangThai != null && trangThai) ? "Đang làm việc" : "Không còn làm việc";
    }
    
    /**
     * Converts Vietnamese text status back to boolean
     */
    private boolean parseTrangThai(String trangThaiText) {
        return "Đang làm việc".equals(trangThaiText);
    }

    /**
     * Validates all input fields
     */
    private boolean validateInput() {
        // Check required fields
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return false;
        }

        if (dateChooserNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dateChooserNgaySinh.requestFocus();
            return false;
        }

        if (txtDienThoai.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtDienThoai.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }

        // Validate email format
        if (!isValidEmail(txtEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this,
                "Email không đúng định dạng!\nVí dụ: example@domain.com",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        if (txtChucVu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập chức vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtChucVu.requestFocus();
            return false;
        }
        
        // Validate date not in future
        java.util.Date selectedDate = dateChooserNgaySinh.getDate();
        if (selectedDate.after(new java.util.Date())) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không thể là ngày trong tương lai!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dateChooserNgaySinh.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Performs the search operation based on the keyword
     */
    private void performSearch() {
        String keyword = txtTimKiem.getText().trim();
        tableModel.setRowCount(0);
        List<NhanVien> list = controller.searchNhanVien(keyword);
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                nv.getId(),
                nv.getHoTen(),
                nv.getGioiTinh(),
                nv.getNgaySinh(),
                nv.getDienThoai(),
                nv.getEmail(),
                nv.getChucVu(),
                formatTrangThai(nv.getTrangThai())
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanVienUI().setVisible(true));
    }
}

