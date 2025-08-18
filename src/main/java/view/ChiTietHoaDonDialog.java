package view;

import controller.ChiTietHoaDonController;
import controller.HoaDonController;
import model.ChiTietHoaDon;
import model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChiTietHoaDonDialog extends JDialog {
    private HoaDon hoaDon;
    private ChiTietHoaDonController controller;
    private HoaDonController hoaDonController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblHoaDonInfo;
    private JButton btnClose, btnDelete, btnRefresh;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ChiTietHoaDonDialog(JFrame parent, HoaDon hoaDon) {
        super(parent, "Chi Tiết Hóa Đơn #" + hoaDon.getId(), true);
        this.hoaDon = hoaDon;
        this.controller = new ChiTietHoaDonController();
        this.hoaDonController = new HoaDonController();

        initComponents();
        loadData();

        setSize(800, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - Invoice information
        JPanel topPanel = createInfoPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel - Details table
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Buttons
        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setupEventHandlers();
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        lblHoaDonInfo = new JLabel();
        lblHoaDonInfo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.add(lblHoaDonInfo, BorderLayout.CENTER);

        updateHoaDonInfo();

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết sản phẩm"));

        tableModel = new DefaultTableModel(
            new Object[]{"Sản phẩm", "Màu sắc", "Size", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Sản phẩm
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Màu sắc
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Size
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Đơn giá
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Thành tiền

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        btnDelete = new JButton("Xóa sản phẩm");
        btnDelete.setIcon(new ImageIcon());
        btnDelete.setPreferredSize(new Dimension(130, 30));

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new ImageIcon());
        btnRefresh.setPreferredSize(new Dimension(100, 30));

        btnClose = new JButton("Đóng");
        btnClose.setIcon(new ImageIcon());
        btnClose.setPreferredSize(new Dimension(80, 30));

        panel.add(btnDelete);
        panel.add(btnRefresh);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnClose);

        return panel;
    }

    private void setupEventHandlers() {
        btnClose.addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> deleteChiTiet());

        // Double-click to edit quantity
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editQuantity();
                }
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<ChiTietHoaDon> details = controller.getByHoaDonId(hoaDon.getId());

            for (ChiTietHoaDon ct : details) {
                if (ct.getMaBienThe() != null) {
                    String tenSP = ct.getMaBienThe().getMaSP() != null ?
                                   ct.getMaBienThe().getMaSP().getTenSP() : "N/A";
                    String mauSac = ct.getMaBienThe().getMaMau() != null ?
                                    ct.getMaBienThe().getMaMau().getTenMau() : "N/A";
                    String size = ct.getMaBienThe().getMaSize() != null ?
                                  ct.getMaBienThe().getMaSize().getTenSize() : "N/A";

                    Object[] row = {
                        tenSP,
                        mauSac,
                        size,
                        ct.getSoLuong(),
                        String.format("%,.0f VNĐ", ct.getDonGia()),
                        String.format("%,.0f VNĐ", ct.getThanhTien())
                    };
                    tableModel.addRow(row);
                }
            }

            updateHoaDonInfo();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết: " + e.getMessage());
        }
    }

    private void updateHoaDonInfo() {
        StringBuilder info = new StringBuilder("<html>");
        info.append("<b>Mã hóa đơn:</b> ").append(hoaDon.getId()).append(" | ");
        info.append("<b>Ngày lập:</b> ").append(hoaDon.getNgayLap().format(dateFormatter)).append(" | ");
        info.append("<b>Khách hàng:</b> ").append(hoaDon.getMaKH() != null ? hoaDon.getMaKH().getHoTen() : "Khách lẻ").append(" | ");
        info.append("<b>Nhân viên:</b> ").append(hoaDon.getMaNV() != null ? hoaDon.getMaNV().getHoTen() : "N/A").append("<br>");
        info.append("<b>Tổng số sản phẩm:</b> ").append(hoaDon.getTotalItems()).append(" | ");
        info.append("<b>Tổng tiền:</b> <span style='color: red; font-size: 14px;'><b>").append(String.format("%,.0f VNĐ", hoaDon.getTotalAmount())).append("</b></span>");
        info.append("</html>");

        lblHoaDonInfo.setText(info.toString());
    }

    private void deleteChiTiet() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        try {
            List<ChiTietHoaDon> details = controller.getByHoaDonId(hoaDon.getId());
            if (selectedRow < details.size()) {
                ChiTietHoaDon selectedDetail = details.get(selectedRow);

                int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa sản phẩm này khỏi hóa đơn?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.delete(selectedDetail.getId());
                    
                    // Recalculate and update invoice total
                    hoaDon.calculateTongTien();
                    hoaDonController.updateHoaDon(hoaDon);
                    
                    JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm khỏi hóa đơn!");
                    loadData();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
        }
    }

    private void editQuantity() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        try {
            List<ChiTietHoaDon> details = controller.getByHoaDonId(hoaDon.getId());
            if (selectedRow < details.size()) {
                ChiTietHoaDon selectedDetail = details.get(selectedRow);

                String input = JOptionPane.showInputDialog(this,
                    "Nhập số lượng mới:",
                    selectedDetail.getSoLuong());

                if (input != null && !input.trim().isEmpty()) {
                    int newQuantity = Integer.parseInt(input.trim());
                    if (newQuantity > 0) {
                        selectedDetail.setSoLuong(newQuantity);
                        controller.update(selectedDetail);
                        
                        // Recalculate and update invoice total
                        hoaDon.calculateTongTien();
                        hoaDonController.updateHoaDon(hoaDon);
                        
                        JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + e.getMessage());
        }
    }
}
