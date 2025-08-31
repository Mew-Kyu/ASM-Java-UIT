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

        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        panelInput.add(new JLabel("Mã KH:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelInput.add(txtId);
        panelInput.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        panelInput.add(txtHoTen);
        panelInput.add(new JLabel("Điện thoại:"));
        txtDienThoai = new JTextField();
        panelInput.add(txtDienThoai);
        panelInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        panelInput.add(txtDiaChi);

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
