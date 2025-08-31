package view;

import controller.BienTheSanPhamController;
import controller.ChiTietHoaDonController;
import controller.HoaDonController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class ThemSanPhamDialog extends JDialog {
    private HoaDon hoaDon;
    private BienTheSanPhamController bienTheController;
    private ChiTietHoaDonController chiTietController;
    private HoaDonController hoaDonController;

    private JTable tableBienThe;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem, txtSoLuong;
    private JButton btnThem, btnDong, btnTimKiem;
    private JLabel lblThongTin;

    public ThemSanPhamDialog(JFrame parent, HoaDon hoaDon) {
        super(parent, "Thêm Sản Phẩm Vào Hóa Đơn #" + hoaDon.getId(), true);
        this.hoaDon = hoaDon;
        this.bienTheController = new BienTheSanPhamController();
        this.chiTietController = new ChiTietHoaDonController();
        this.hoaDonController = new HoaDonController();

        initComponents();
        loadData();

        setSize(900, 600);
        setLocationRelativeTo(parent);
    }

    // Overloaded constructor to accept JDialog as parent
    public ThemSanPhamDialog(JDialog parent, HoaDon hoaDon) {
        super(parent, "Thêm Sản Phẩm Vào Hóa Đơn #" + hoaDon.getId(), true);
        this.hoaDon = hoaDon;
        this.bienTheController = new BienTheSanPhamController();
        this.chiTietController = new ChiTietHoaDonController();
        this.hoaDonController = new HoaDonController();

        initComponents();
        loadData();

        setSize(900, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - Search and info
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel - Product table
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Input and buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setupEventHandlers();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm sản phẩm"));

        searchPanel.add(new JLabel("Tên sản phẩm:"));
        txtTimKiem = new JTextField(20);
        searchPanel.add(txtTimKiem);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(btnTimKiem);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Info panel
        lblThongTin = new JLabel("<html><b>Hóa đơn #" + hoaDon.getId() + "</b> - " +
                                "Khách hàng: " + (hoaDon.getMaKH() != null ? hoaDon.getMaKH().getHoTen() : "Khách lẻ") +
                                "</html>");
        lblThongTin.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(lblThongTin, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách biến thể sản phẩm"));

        tableModel = new DefaultTableModel(
            new Object[]{"Mã BT", "Sản phẩm", "Màu sắc", "Size", "Tồn kho", "Giá bán"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBienThe = new JTable(tableModel);
        tableBienThe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableBienThe.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        tableBienThe.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã BT
        tableBienThe.getColumnModel().getColumn(1).setPreferredWidth(200); // Sản phẩm
        tableBienThe.getColumnModel().getColumn(2).setPreferredWidth(100); // Màu sắc
        tableBienThe.getColumnModel().getColumn(3).setPreferredWidth(80);  // Size
        tableBienThe.getColumnModel().getColumn(4).setPreferredWidth(80);  // Tồn kho
        tableBienThe.getColumnModel().getColumn(5).setPreferredWidth(120); // Giá bán

        JScrollPane scrollPane = new JScrollPane(tableBienThe);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thêm vào hóa đơn"));

        inputPanel.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField(10);
        txtSoLuong.setText("1");
        inputPanel.add(txtSoLuong);

        btnThem = new JButton("Thêm vào hóa đơn");
        btnThem.setIcon(new ImageIcon());
        btnThem.setPreferredSize(new Dimension(160, 30));
        inputPanel.add(btnThem);

        panel.add(inputPanel, BorderLayout.WEST);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnDong = new JButton("Đóng");
        btnDong.setIcon(new ImageIcon());
        btnDong.setPreferredSize(new Dimension(80, 30));
        buttonPanel.add(btnDong);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void setupEventHandlers() {
        btnDong.addActionListener(e -> dispose());
        btnTimKiem.addActionListener(e -> timKiemSanPham());
        btnThem.addActionListener(e -> themVaoHoaDon());

        // Enter key for search
        txtTimKiem.addActionListener(e -> timKiemSanPham());

        // Enter key for quantity
        txtSoLuong.addActionListener(e -> themVaoHoaDon());

        // Double-click to add product
        tableBienThe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    themVaoHoaDon();
                }
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            // Eager fetch product/color/size to avoid lazy init when rendering
            List<BienTheSanPham> list = bienTheController.getAllWithDetails();

            for (BienTheSanPham bt : list) {
                if (bt.getSoLuong() > 0) { // Only show products in stock
                    String tenSP = bt.getMaSP() != null ? bt.getMaSP().getTenSP() : "N/A";
                    String mauSac = bt.getMaMau() != null ? bt.getMaMau().getTenMau() : "N/A";
                    String size = bt.getMaSize() != null ? bt.getMaSize().getTenSize() : "N/A";

                    Object[] row = {
                        bt.getId(),
                        tenSP,
                        mauSac,
                        size,
                        bt.getSoLuong(),
                        String.format("%,.0f VNĐ", bt.getGiaBan())
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void timKiemSanPham() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        try {
            List<BienTheSanPham> list = bienTheController.getAllWithDetails();

            for (BienTheSanPham bt : list) {
                if (bt.getSoLuong() > 0) {
                    String tenSP = bt.getMaSP() != null ? bt.getMaSP().getTenSP().toLowerCase() : "";
                    String mauSac = bt.getMaMau() != null ? bt.getMaMau().getTenMau().toLowerCase() : "";
                    String size = bt.getMaSize() != null ? bt.getMaSize().getTenSize().toLowerCase() : "";

                    if (keyword.isEmpty() || tenSP.contains(keyword) ||
                        mauSac.contains(keyword) || size.contains(keyword)) {

                        Object[] row = {
                            bt.getId(),
                            bt.getMaSP() != null ? bt.getMaSP().getTenSP() : "N/A",
                            bt.getMaMau() != null ? bt.getMaMau().getTenMau() : "N/A",
                            bt.getMaSize() != null ? bt.getMaSize().getTenSize() : "N/A",
                            bt.getSoLuong(),
                            String.format("%,.0f VNĐ", bt.getGiaBan())
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void themVaoHoaDon() {
        int selectedRow = tableBienThe.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để thêm!");
            return;
        }

        try {
            int maBienThe = (Integer) tableModel.getValueAt(selectedRow, 0);
            int soLuongThem = Integer.parseInt(txtSoLuong.getText().trim());
            int tonKho = (Integer) tableModel.getValueAt(selectedRow, 4);

            if (soLuongThem <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }

            if (soLuongThem > tonKho) {
                JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho (" + tonKho + ")!");
                return;
            }

            // Get BienTheSanPham
            BienTheSanPham bienThe = bienTheController.getByIdWithDetails(maBienThe);
            if (bienThe == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy biến thể sản phẩm!");
                return;
            }

            // Check if already exists in invoice
            ChiTietHoaDonId existingId = new ChiTietHoaDonId(hoaDon.getId(), maBienThe);
            ChiTietHoaDon existing = chiTietController.getById(existingId);

            if (existing != null) {
                // Update existing quantity
                int newQuantity = existing.getSoLuong() + soLuongThem;
                if (newQuantity > tonKho) {
                    JOptionPane.showMessageDialog(this,
                        "Tổng số lượng (" + newQuantity + ") vượt quá tồn kho (" + tonKho + ")!");
                    return;
                }
                existing.setSoLuong(newQuantity);
                chiTietController.update(existing);
                JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng sản phẩm trong hóa đơn!");
            } else {
                // Create new detail
                ChiTietHoaDon chiTiet = new ChiTietHoaDon(hoaDon, bienThe, soLuongThem, bienThe.getGiaBan());
                chiTietController.add(chiTiet);
                JOptionPane.showMessageDialog(this, "Đã thêm sản phẩm vào hóa đơn!");
            }
            
            // IMPORTANT: Recalculate and update invoice total on a fully initialized entity
            HoaDon refreshed = hoaDonController.getHoaDonByIdWithDetails(hoaDon.getId());
            if (refreshed != null) {
                refreshed.calculateTongTien();
                hoaDonController.updateHoaDon(refreshed);
                // keep local reference updated
                this.hoaDon = refreshed;
            }

            // Update stock in BienTheSanPham
            bienThe.decreaseStock(soLuongThem);
            bienTheController.update(bienThe);

            // Refresh table
            if (txtTimKiem.getText().trim().isEmpty()) {
                loadData();
            } else {
                timKiemSanPham();
            }

            // Reset quantity input
            txtSoLuong.setText("1");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
}
