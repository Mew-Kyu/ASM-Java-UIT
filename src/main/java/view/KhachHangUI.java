package view;

import controller.KhachHangController;
import model.KhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class KhachHangUI extends BaseAuthenticatedUI {
    private KhachHangController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtHoTen, txtDienThoai, txtDiaChi;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    public KhachHangUI() {
        controller = new KhachHangController();
        setTitle("Quản lý Khách Hàng");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        // Search panel
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSearch.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        panelSearch.add(new JLabel("Từ khóa:"));
        txtTimKiem = new JTextField(20);
        panelSearch.add(txtTimKiem);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(90, 25));
        panelSearch.add(btnTimKiem);

        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "👥 Thông tin khách hàng",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(51, 102, 153)),
            BorderFactory.createEmptyBorder(15, 20, 20, 20)
        ));
        panelInput.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Create styled labels with icons
        JLabel lblId = new JLabel("🏷️ Mã khách hàng:");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblId.setForeground(new Color(68, 68, 68));

        JLabel lblHoTen = new JLabel("👤 Họ và tên:");
        lblHoTen.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblHoTen.setForeground(new Color(68, 68, 68));

        JLabel lblDienThoai = new JLabel("📞 Điện thoại:");
        lblDienThoai.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDienThoai.setForeground(new Color(68, 68, 68));

        JLabel lblDiaChi = new JLabel("🏠 Địa chỉ:");
        lblDiaChi.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDiaChi.setForeground(new Color(68, 68, 68));

        // Style text fields
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        txtId.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtId.setToolTipText("Mã khách hàng sẽ được tự động tạo");
        txtId.setPreferredSize(new Dimension(150, 30));

        txtHoTen = new JTextField();
        txtHoTen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtHoTen.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtHoTen.setToolTipText("Nhập họ và tên đầy đủ của khách hàng (bắt buộc)");
        txtHoTen.setPreferredSize(new Dimension(200, 30));

        txtDienThoai = new JTextField();
        txtDienThoai.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtDienThoai.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDienThoai.setToolTipText("Nhập số điện thoại (bắt buộc)");
        txtDienThoai.setPreferredSize(new Dimension(150, 30));

        txtDiaChi = new JTextField();
        txtDiaChi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtDiaChi.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDiaChi.setToolTipText("Nhập địa chỉ của khách hàng (tùy chọn)");
        txtDiaChi.setPreferredSize(new Dimension(200, 30));

        // Row 1 - Mã KH và Họ tên
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelInput.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.4;
        panelInput.add(txtId, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        panelInput.add(lblHoTen, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(8, 8, 8, 8);
        panelInput.add(txtHoTen, gbc);

        // Row 2 - Điện thoại và Địa chỉ
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelInput.add(lblDienThoai, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.4;
        panelInput.add(txtDienThoai, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 20, 8, 8);
        panelInput.add(lblDiaChi, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(8, 8, 8, 8);
        panelInput.add(txtDiaChi, gbc);

        // Add visual separation
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        gbc.weightx = 1.0;
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(220, 220, 220));
        panelInput.add(separator, gbc);

        // Add instruction label
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.weightx = 0;
        JLabel instructionLabel = new JLabel("💡 Chọn một dòng trong bảng để chỉnh sửa hoặc nhập thông tin mới để thêm khách hàng");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructionLabel.setForeground(new Color(102, 102, 102));
        panelInput.add(instructionLabel, gbc);

        // Top panel containing search and input panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelSearch, BorderLayout.NORTH);
        topPanel.add(panelInput, BorderLayout.CENTER);

        JPanel panelButton = new JPanel();
        btnThem = new JButton("Thêm");
        btnThem.setPreferredSize(new Dimension(80, 30));
        btnSua = new JButton("Sửa");
        btnSua.setPreferredSize(new Dimension(80, 30));
        btnXoa = new JButton("Xóa");
        btnXoa.setPreferredSize(new Dimension(80, 30));
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setPreferredSize(new Dimension(100, 30));
        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnXoa);
        panelButton.add(btnLamMoi);

        tableModel = new DefaultTableModel(new Object[]{"Mã KH", "Họ tên", "Điện thoại", "Địa chỉ"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);

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

        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KhachHang kh = new KhachHang();
                kh.setHoTen(txtHoTen.getText());
                kh.setDienThoai(txtDienThoai.getText());
                kh.setDiaChi(txtDiaChi.getText());
                controller.themKhachHang(kh);
                loadTable();
                clearInput();
            }
        });
        btnSua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtId.getText().isEmpty()) return;
                KhachHang kh = new KhachHang();
                kh.setId(Integer.parseInt(txtId.getText()));
                kh.setHoTen(txtHoTen.getText());
                kh.setDienThoai(txtDienThoai.getText());
                kh.setDiaChi(txtDiaChi.getText());
                controller.suaKhachHang(kh);
                loadTable();
                clearInput();
            }
        });
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtId.getText().isEmpty()) return;
                int id = Integer.parseInt(txtId.getText());
                controller.xoaKhachHang(id);
                loadTable();
                clearInput();
            }
        });
        btnLamMoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInput();
                txtTimKiem.setText(""); // Clear search field
                loadTable(); // Reload all data
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
                txtDienThoai.setText(tableModel.getValueAt(row, 2).toString());
                txtDiaChi.setText(tableModel.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<KhachHang> ds = controller.layDanhSachKhachHang();
        for (KhachHang kh : ds) {
            tableModel.addRow(new Object[]{kh.getId(), kh.getHoTen(), kh.getDienThoai(), kh.getDiaChi()});
        }
    }

    private void clearInput() {
        txtId.setText("");
        txtHoTen.setText("");
        txtDienThoai.setText("");
        txtDiaChi.setText("");
        table.clearSelection();
    }

    private void performSearch() {
        String keyword = txtTimKiem.getText();
        tableModel.setRowCount(0);
        List<KhachHang> ds = controller.timKiemKhachHang(keyword);
        for (KhachHang kh : ds) {
            tableModel.addRow(new Object[]{kh.getId(), kh.getHoTen(), kh.getDienThoai(), kh.getDiaChi()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KhachHangUI().setVisible(true));
    }
}
