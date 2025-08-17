package view;

import controller.HoaDonController;
import model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HoaDonUI extends JFrame {
    private JTextField txtMaHD, txtNgayLap, txtTongTien;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;
    private HoaDonController controller;

    public HoaDonUI() {
        controller = new HoaDonController();
        setTitle("Quản Lý Hóa Đơn");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        panelInput.add(new JLabel("Mã HĐ:"));
        txtMaHD = new JTextField();
        txtMaHD.setEditable(false);
        panelInput.add(txtMaHD);
        panelInput.add(new JLabel("Ngày lập (yyyy-MM-dd):"));
        txtNgayLap = new JTextField();
        panelInput.add(txtNgayLap);
        panelInput.add(new JLabel("Tổng tiền:"));
        txtTongTien = new JTextField();
        panelInput.add(txtTongTien);

        JPanel panelButtons = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnRefresh);

        tableModel = new DefaultTableModel(new Object[]{"Mã HĐ", "Ngày lập", "Tổng tiền"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout(5, 5));
        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Event listeners
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addHoaDon();
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateHoaDon();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteHoaDon();
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
                loadTable();
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<HoaDon> list = controller.getAllHoaDon();
        for (HoaDon hd : list) {
            tableModel.addRow(new Object[]{hd.getId(), hd.getNgayLap(), hd.getTongTien()});
        }
    }

    private void addHoaDon() {
        try {
            LocalDate ngayLap = LocalDate.parse(txtNgayLap.getText().trim());
            BigDecimal tongTien = new BigDecimal(txtTongTien.getText().trim());
            HoaDon hd = new HoaDon();
            hd.setNgayLap(ngayLap);
            hd.setTongTien(tongTien);
            controller.addHoaDon(hd);
            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
            clearFields();
            loadTable();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày lập không hợp lệ! (yyyy-MM-dd)");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tổng tiền không hợp lệ!");
        }
    }

    private void updateHoaDon() {
        try {
            int id = Integer.parseInt(txtMaHD.getText().trim());
            LocalDate ngayLap = LocalDate.parse(txtNgayLap.getText().trim());
            BigDecimal tongTien = new BigDecimal(txtTongTien.getText().trim());
            HoaDon hd = new HoaDon();
            hd.setId(id);
            hd.setNgayLap(ngayLap);
            hd.setTongTien(tongTien);
            controller.updateHoaDon(hd);
            JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!");
            clearFields();
            loadTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để cập nhật!");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày lập không hợp lệ! (yyyy-MM-dd)");
        }
    }

    private void deleteHoaDon() {
        try {
            int id = Integer.parseInt(txtMaHD.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hóa đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteHoaDon(id);
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
                clearFields();
                loadTable();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xóa!");
        }
    }

    private void clearFields() {
        txtMaHD.setText("");
        txtNgayLap.setText("");
        txtTongTien.setText("");
        table.clearSelection();
    }

    private void fillFieldsFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaHD.setText(table.getValueAt(row, 0).toString());
            txtNgayLap.setText(table.getValueAt(row, 1).toString());
            txtTongTien.setText(table.getValueAt(row, 2).toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HoaDonUI().setVisible(true));
    }
}

