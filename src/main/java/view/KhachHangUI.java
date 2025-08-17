package view;

import controller.KhachHangController;
import model.KhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class KhachHangUI extends BaseAuthenticatedUI {
    private KhachHangController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtHoTen, txtDienThoai, txtDiaChi;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    public KhachHangUI() {
        controller = new KhachHangController();
        setTitle("Quản lý Khách Hàng");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadTable();
    }

    private void initComponents() {
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

        JPanel panelButton = new JPanel();
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");
        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnXoa);
        panelButton.add(btnLamMoi);

        tableModel = new DefaultTableModel(new Object[]{"Mã KH", "Họ tên", "Điện thoại", "Địa chỉ"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout(5, 5));
        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KhachHangUI().setVisible(true));
    }
}
