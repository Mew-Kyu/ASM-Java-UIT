package view;

import model.NhanVien;
import controller.NhanVienController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class NhanVienUI extends JFrame {
    private NhanVienController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtHoTen, txtGioiTinh, txtNgaySinh, txtDienThoai, txtEmail, txtChucVu;
    private JCheckBox chkTrangThai;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

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
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Mã NV", "Họ Tên", "Giới Tính", "Ngày Sinh", "Điện Thoại", "Email", "Chức Vụ", "Trạng Thái"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 4));
        inputPanel.add(new JLabel("Mã NV:"));
        txtId = new JTextField();
        txtId.setEnabled(false);
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("Họ Tên:"));
        txtHoTen = new JTextField();
        inputPanel.add(txtHoTen);
        inputPanel.add(new JLabel("Giới Tính:"));
        txtGioiTinh = new JTextField();
        inputPanel.add(txtGioiTinh);
        inputPanel.add(new JLabel("Ngày Sinh (yyyy-MM-dd):"));
        txtNgaySinh = new JTextField();
        inputPanel.add(txtNgaySinh);
        inputPanel.add(new JLabel("Điện Thoại:"));
        txtDienThoai = new JTextField();
        inputPanel.add(txtDienThoai);
        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);
        inputPanel.add(new JLabel("Chức Vụ:"));
        txtChucVu = new JTextField();
        inputPanel.add(txtChucVu);
        inputPanel.add(new JLabel("Trạng Thái:"));
        chkTrangThai = new JCheckBox("Đang làm việc");
        inputPanel.add(chkTrangThai);
        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        btnAdd.addActionListener(e -> {
            try {
                NhanVien nv = new NhanVien();
                nv.setHoTen(txtHoTen.getText().trim());
                nv.setGioiTinh(txtGioiTinh.getText().trim());
                nv.setNgaySinh(LocalDate.parse(txtNgaySinh.getText().trim()));
                nv.setDienThoai(txtDienThoai.getText().trim());
                nv.setEmail(txtEmail.getText().trim());
                nv.setChucVu(txtChucVu.getText().trim());
                nv.setTrangThai(chkTrangThai.isSelected());
                controller.addNhanVien(nv);
                loadTable();
                clearInput();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(NhanVienUI.this, "Dữ liệu không hợp lệ!");
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    NhanVien nv = new NhanVien();
                    nv.setId(Integer.parseInt(txtId.getText()));
                    nv.setHoTen(txtHoTen.getText().trim());
                    nv.setGioiTinh(txtGioiTinh.getText().trim());
                    nv.setNgaySinh(LocalDate.parse(txtNgaySinh.getText().trim()));
                    nv.setDienThoai(txtDienThoai.getText().trim());
                    nv.setEmail(txtEmail.getText().trim());
                    nv.setChucVu(txtChucVu.getText().trim());
                    nv.setTrangThai(chkTrangThai.isSelected());
                    controller.updateNhanVien(nv);
                    loadTable();
                    clearInput();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(NhanVienUI.this, "Dữ liệu không hợp lệ!");
                }
            } else {
                JOptionPane.showMessageDialog(NhanVienUI.this, "Chọn một dòng để sửa!");
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
            loadTable();
            clearInput();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
                txtGioiTinh.setText(tableModel.getValueAt(row, 2).toString());
                txtNgaySinh.setText(tableModel.getValueAt(row, 3).toString());
                txtDienThoai.setText(tableModel.getValueAt(row, 4).toString());
                txtEmail.setText(tableModel.getValueAt(row, 5).toString());
                txtChucVu.setText(tableModel.getValueAt(row, 6).toString());
                chkTrangThai.setSelected("true".equals(tableModel.getValueAt(row, 7).toString()));
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<NhanVien> list = controller.getAllNhanVien();
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{nv.getId(), nv.getHoTen(), nv.getGioiTinh(), nv.getNgaySinh(), nv.getDienThoai(), nv.getEmail(), nv.getChucVu(), nv.getTrangThai()});
        }
    }

    private void clearInput() {
        txtId.setText("");
        txtHoTen.setText("");
        txtGioiTinh.setText("");
        txtNgaySinh.setText("");
        txtDienThoai.setText("");
        txtEmail.setText("");
        txtChucVu.setText("");
        chkTrangThai.setSelected(false);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanVienUI().setVisible(true));
    }
}
