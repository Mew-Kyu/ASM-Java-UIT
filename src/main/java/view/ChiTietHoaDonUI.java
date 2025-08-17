package view;

import dao.impl.ChiTietHoaDonDAO;
import model.ChiTietHoaDon;
import model.ChiTietHoaDonId;
import model.BienTheSanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class ChiTietHoaDonUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHD, txtMaBienThe, txtSoLuong, txtDonGia;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    public ChiTietHoaDonUI() {
        setTitle("Quản lý Chi Tiết Hóa Đơn");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Mã HĐ", "Mã Biến Thể", "Số Lượng", "Đơn Giá"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form input
        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 10, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết hóa đơn"));
        txtMaHD = new JTextField();
        txtMaBienThe = new JTextField();
        txtSoLuong = new JTextField();
        txtDonGia = new JTextField();
        pnlInput.add(new JLabel("Mã HĐ:"));
        pnlInput.add(txtMaHD);
        pnlInput.add(new JLabel("Mã Biến Thể:"));
        pnlInput.add(txtMaBienThe);
        pnlInput.add(new JLabel("Số Lượng:"));
        pnlInput.add(txtSoLuong);
        pnlInput.add(new JLabel("Đơn Giá:"));
        pnlInput.add(txtDonGia);
        add(pnlInput, BorderLayout.NORTH);

        // Buttons
        JPanel pnlButton = new JPanel();
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");
        pnlButton.add(btnThem);
        pnlButton.add(btnSua);
        pnlButton.add(btnXoa);
        pnlButton.add(btnLamMoi);
        add(pnlButton, BorderLayout.SOUTH);

        // Load data
        loadTable();

        // Button actions
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themChiTietHoaDon();
            }
        });
        btnSua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaChiTietHoaDon();
            }
        });
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaChiTietHoaDon();
            }
        });
        btnLamMoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaHD.setText(tableModel.getValueAt(row, 0).toString());
                txtMaBienThe.setText(tableModel.getValueAt(row, 1).toString());
                txtSoLuong.setText(tableModel.getValueAt(row, 2).toString());
                txtDonGia.setText(tableModel.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<ChiTietHoaDon> list = dao.findAll();
        for (ChiTietHoaDon ct : list) {
            tableModel.addRow(new Object[]{
                    ct.getId().getMaHD(),
                    ct.getId().getMaBienThe(),
                    ct.getSoLuong(),
                    ct.getDonGia()
            });
        }
    }

    private void themChiTietHoaDon() {
        try {
            int maHD = Integer.parseInt(txtMaHD.getText());
            int maBienThe = Integer.parseInt(txtMaBienThe.getText());
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            BigDecimal donGia = new BigDecimal(txtDonGia.getText());
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHD, maBienThe);
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setId(id);
            ct.setSoLuong(soLuong);
            ct.setDonGia(donGia);
            // Set BienTheSanPham (chỉ cần id)
            BienTheSanPham bienThe = new BienTheSanPham();
            bienThe.setMaBienThe(maBienThe);
            ct.setMaBienThe(bienThe);
            dao.insert(ct);
            loadTable();
            lamMoiForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void suaChiTietHoaDon() {
        try {
            int maHD = Integer.parseInt(txtMaHD.getText());
            int maBienThe = Integer.parseInt(txtMaBienThe.getText());
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            BigDecimal donGia = new BigDecimal(txtDonGia.getText());
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHD, maBienThe);
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setId(id);
            ct.setSoLuong(soLuong);
            ct.setDonGia(donGia);
            BienTheSanPham bienThe = new BienTheSanPham();
            bienThe.setMaBienThe(maBienThe);
            ct.setMaBienThe(bienThe);
            dao.update(ct);
            loadTable();
            lamMoiForm();
            JOptionPane.showMessageDialog(this, "Sửa thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void xoaChiTietHoaDon() {
        try {
            int maHD = Integer.parseInt(txtMaHD.getText());
            int maBienThe = Integer.parseInt(txtMaBienThe.getText());
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHD, maBienThe);
            dao.delete(id);
            loadTable();
            lamMoiForm();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void lamMoiForm() {
        txtMaHD.setText("");
        txtMaBienThe.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChiTietHoaDonUI().setVisible(true));
    }
}

