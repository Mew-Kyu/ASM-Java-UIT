package view;

import model.KichThuoc;
import controller.KichThuocController;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class KichThuocUI extends JFrame {
    private KichThuocController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTenSize;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    public KichThuocUI() {
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

        controller = new KichThuocController();
        setTitle("Quản Lý Kích Thước - " + SessionManager.getInstance().getCurrentUsername());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel panelTop = new JPanel(new GridLayout(2, 2, 10, 10));
        panelTop.setBorder(BorderFactory.createTitledBorder("Thông tin Kích Thước"));
        panelTop.add(new JLabel("Mã Size:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelTop.add(txtId);
        panelTop.add(new JLabel("Tên Size:"));
        txtTenSize = new JTextField();
        panelTop.add(txtTenSize);

        JPanel panelButtons = new JPanel();
        btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(new Dimension(80, 30));
        btnUpdate = new JButton("Sửa");
        btnUpdate.setPreferredSize(new Dimension(80, 30));
        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(new Dimension(80, 30));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnRefresh);

        tableModel = new DefaultTableModel(new Object[]{"Mã Size", "Tên Size"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout(10, 10));
        add(panelTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tenSize = txtTenSize.getText().trim();
                if (!tenSize.isEmpty()) {
                    controller.addKichThuoc(tenSize);
                    loadTable();
                    clearInput();
                } else {
                    JOptionPane.showMessageDialog(KichThuocUI.this, "Tên Size không được để trống!");
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = Integer.parseInt(txtId.getText());
                    String tenSize = txtTenSize.getText().trim();
                    controller.updateKichThuoc(id, tenSize);
                    loadTable();
                    clearInput();
                } else {
                    JOptionPane.showMessageDialog(KichThuocUI.this, "Chọn một dòng để sửa!");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = Integer.parseInt(txtId.getText());
                    controller.deleteKichThuoc(id);
                    loadTable();
                    clearInput();
                } else {
                    JOptionPane.showMessageDialog(KichThuocUI.this, "Chọn một dòng để xóa!");
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
                txtTenSize.setText(tableModel.getValueAt(row, 1).toString());
            }
        });
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<KichThuoc> list = controller.getAllKichThuoc();
        for (KichThuoc kt : list) {
            tableModel.addRow(new Object[]{kt.getId(), kt.getTenSize()});
        }
    }

    private void clearInput() {
        txtId.setText("");
        txtTenSize.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KichThuocUI().setVisible(true));
    }
}
