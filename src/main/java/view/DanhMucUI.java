package view;

import controller.DanhMucController;
import model.DanhMuc;
import util.SessionManager;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DanhMucUI extends JFrame {
    private final DanhMucController controller = new DanhMucController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTenDM;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    public DanhMucUI() {
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

        setTitle("Quản Lý Danh Mục - " + SessionManager.getInstance().getCurrentUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel panelTop = new JPanel(new GridLayout(2, 2, 10, 10));
        panelTop.setBorder(BorderFactory.createTitledBorder("Thông tin Danh Mục"));
        panelTop.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelTop.add(txtId);
        panelTop.add(new JLabel("Tên Danh Mục:"));
        txtTenDM = new JTextField();
        panelTop.add(txtTenDM);

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

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tên Danh Mục"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout(10, 10));
        add(panelTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Event listeners
        btnAdd.addActionListener(e -> addDanhMuc());
        btnUpdate.addActionListener(e -> updateDanhMuc());
        btnDelete.addActionListener(e -> deleteDanhMuc());
        btnRefresh.addActionListener(e -> clearFields());
        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable());
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<DanhMuc> list = controller.getAllDanhMuc();
        for (DanhMuc dm : list) {
            tableModel.addRow(new Object[]{dm.getId(), dm.getTenDM()});
        }
    }

    private void addDanhMuc() {
        String ten = txtTenDM.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!");
            return;
        }
        DanhMuc dm = new DanhMuc();
        dm.setTenDM(ten);
        controller.addDanhMuc(dm);
        loadTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Thêm thành công!");
    }

    private void updateDanhMuc() {
        String idStr = txtId.getText().trim();
        String ten = txtTenDM.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để sửa!");
            return;
        }
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!");
            return;
        }
        int id = Integer.parseInt(idStr);
        DanhMuc dm = controller.getDanhMucById(id);
        if (dm == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy danh mục!");
            return;
        }
        dm.setTenDM(ten);
        controller.updateDanhMuc(dm);
        loadTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
    }

    private void deleteDanhMuc() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để xóa!");
            return;
        }
        int id = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteDanhMuc(id);
            loadTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtTenDM.setText("");
        table.clearSelection();
    }

    private void fillFieldsFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(table.getValueAt(row, 0).toString());
            txtTenDM.setText(table.getValueAt(row, 1).toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DanhMucUI().setVisible(true));
    }
}
