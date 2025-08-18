package view;

import model.MauSac;
import controller.MauSacController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MauSacUI extends JFrame {
    private MauSacController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTenMau;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    public MauSacUI() {
        // Check authentication and authorization
        if (!SessionManager.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            this.dispose();
            return;
        }

        if (!RoleManager.canAccessProductConfiguration()) {
            RoleManager.showAccessDeniedMessage(null, "Manager hoặc Admin");
            this.dispose();
            return;
        }

        controller = new MauSacController();
        setTitle("Quản Lý Màu Sắc - " + SessionManager.getInstance().getCurrentUsername());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Mã Màu", "Tên Màu"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Mã Màu:"));
        txtId = new JTextField();
        txtId.setEnabled(false);
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("Tên Màu:"));
        txtTenMau = new JTextField();
        inputPanel.add(txtTenMau);
        panel.add(inputPanel, BorderLayout.NORTH);

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

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tenMau = txtTenMau.getText().trim();
                if (!tenMau.isEmpty()) {
                    controller.addMauSac(tenMau);
                    loadTable();
                    clearInput();
                } else {
                    JOptionPane.showMessageDialog(MauSacUI.this, "Tên Màu không được để trống!");
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = Integer.parseInt(txtId.getText());
                    String tenMau = txtTenMau.getText().trim();
                    controller.updateMauSac(id, tenMau);
                    loadTable();
                    clearInput();
                } else {
                    JOptionPane.showMessageDialog(MauSacUI.this, "Chọn một dòng để sửa!");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = Integer.parseInt(txtId.getText());
                    controller.deleteMauSac(id);
                    loadTable();
                    clearInput();
                } else {
                    JOptionPane.showMessageDialog(MauSacUI.this, "Chọn một dòng để xóa!");
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTable();
                clearInput();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtTenMau.setText(tableModel.getValueAt(row, 1).toString());
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<MauSac> list = controller.getAllMauSac();
        for (MauSac ms : list) {
            tableModel.addRow(new Object[]{ms.getId(), ms.getTenMau()});
        }
    }

    private void clearInput() {
        txtId.setText("");
        txtTenMau.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MauSacUI().setVisible(true));
    }
}
