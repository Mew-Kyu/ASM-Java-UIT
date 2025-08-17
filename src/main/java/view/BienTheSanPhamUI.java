package view;

import model.BienTheSanPham;
import dao.impl.BienTheSanPhamDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class BienTheSanPhamUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtSoLuong, txtGiaBan;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private BienTheSanPhamDAO dao = new BienTheSanPhamDAO();

    public BienTheSanPhamUI() {
        setTitle("Quản lý Biến Thể Sản Phẩm");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Mã Biến Thể", "Số Lượng", "Giá Bán"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.add(new JLabel("Mã Biến Thể:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        formPanel.add(txtId);

        formPanel.add(new JLabel("Số Lượng:"));
        txtSoLuong = new JTextField();
        formPanel.add(txtSoLuong);

        formPanel.add(new JLabel("Giá Bán:"));
        txtGiaBan = new JTextField();
        formPanel.add(txtGiaBan);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.SOUTH);

        // Load data
        loadTable();

        // Events
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtSoLuong.setText(tableModel.getValueAt(row, 1).toString());
                    txtGiaBan.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });

        btnAdd.addActionListener(e -> addBienThe());
        btnUpdate.addActionListener(e -> updateBienThe());
        btnDelete.addActionListener(e -> deleteBienThe());
        btnRefresh.addActionListener(e -> clearForm());
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<BienTheSanPham> list = dao.findAll();
        for (BienTheSanPham bts : list) {
            tableModel.addRow(new Object[]{
                bts.getId(),
                bts.getSoLuong(),
                bts.getGiaBan()
            });
        }
    }

    private void addBienThe() {
        try {
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            BigDecimal giaBan = new BigDecimal(txtGiaBan.getText());
            BienTheSanPham bts = new BienTheSanPham();
            bts.setSoLuong(soLuong);
            bts.setGiaBan(giaBan);
            dao.insert(bts);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateBienThe() {
        try {
            int id = Integer.parseInt(txtId.getText());
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            BigDecimal giaBan = new BigDecimal(txtGiaBan.getText());
            BienTheSanPham bts = dao.findById(id);
            if (bts != null) {
                bts.setSoLuong(soLuong);
                bts.setGiaBan(giaBan);
                dao.update(bts);
                loadTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteBienThe() {
        try {
            int id = Integer.parseInt(txtId.getText());
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa biến thể này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.delete(id);
                loadTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtSoLuong.setText("");
        txtGiaBan.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BienTheSanPhamUI().setVisible(true));
    }
}

