package view;

import model.PhieuDoiTra;
import model.ChiTietPhieuDoiTra;
import model.HoaDon;
import model.KhachHang;
import dao.impl.HoaDonDAO;
import service.impl.PhieuDoiTraServiceImpl;
import service.interfaces.IPhieuDoiTraService;
import exception.BusinessException;
import exception.ValidationException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for adding/editing Returns & Exchanges
 */
public class ThemPhieuDoiTraDialog extends JDialog {
    
    private final IPhieuDoiTraService phieuDoiTraService;
    private final HoaDonDAO hoaDonDAO;
    
    private PhieuDoiTra phieuDoiTra;
    private List<ChiTietPhieuDoiTra> danhSachChiTiet;
    private boolean isEditMode;
    
    // UI Components
    private JTextField txtMaHD;
    private JComboBox<String> cmbLoaiPhieu;
    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextArea txtLyDo;
    private JTextField txtGhiChu;
    
    // Chi tiết table
    private JTable tableChiTiet;
    private DefaultTableModel tableModel;
    
    // Tính toán
    private JLabel lblTongGiaTriTra;
    private JLabel lblTongGiaTriDoi;
    private JLabel lblSoTienHoanLai;
    private JLabel lblSoTienBoSung;
    
    private boolean saved = false;
    
    public ThemPhieuDoiTraDialog(JFrame parent) {
        this(parent, null);
    }
    
    public ThemPhieuDoiTraDialog(JFrame parent, PhieuDoiTra phieu) {
        super(parent, "Quản lý phiếu đổi trả", true);
        
        this.phieuDoiTraService = new PhieuDoiTraServiceImpl();
        this.hoaDonDAO = new HoaDonDAO();
        
        this.phieuDoiTra = phieu;
        this.isEditMode = (phieu != null);
        this.danhSachChiTiet = new ArrayList<>();
        
        initComponents();
        
        if (isEditMode) {
            loadData();
        }
    }
    
    private void initComponents() {
        setSize(900, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Thông tin phiếu", createInfoPanel());
        tabbedPane.addTab("Chi tiết sản phẩm", createDetailPanel());
        tabbedPane.addTab("Tính toán", createCalculationPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(70, 130, 180));
        
        JLabel title = new JLabel(isEditMode ? "SỬA PHIẾU ĐỔI TRẢ" : "TẠO PHIẾU ĐỔI TRẢ MỚI");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        panel.add(title);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mã hóa đơn
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã hóa đơn *:"), gbc);
        gbc.gridx = 1;
        txtMaHD = new JTextField(15);
        txtMaHD.addActionListener(this::timHoaDon);
        panel.add(txtMaHD, gbc);
        
        gbc.gridx = 2;
        JButton btnTimHD = new JButton("Tìm");
        btnTimHD.addActionListener(this::timHoaDon);
        panel.add(btnTimHD, gbc);
        
        // Loại phiếu
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Loại phiếu *:"), gbc);
        gbc.gridx = 1;
        cmbLoaiPhieu = new JComboBox<>(new String[]{"DOI", "TRA"});
        panel.add(cmbLoaiPhieu, gbc);
        
        // Thông tin khách hàng
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Mã khách hàng:"), gbc);
        gbc.gridx = 1;
        txtMaKH = new JTextField(15);
        txtMaKH.setEditable(false);
        panel.add(txtMaKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTenKH = new JTextField(30);
        txtTenKH.setEditable(false);
        panel.add(txtTenKH, gbc);
        
        // Lý do
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Lý do *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtLyDo = new JTextArea(3, 30);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        panel.add(scrollLyDo, gbc);
        
        // Ghi chú
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtGhiChu = new JTextField(30);
        panel.add(txtGhiChu, gbc);
        
        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Table
        String[] columns = {"Mã biến thể", "Tên sản phẩm", "Màu sắc", "Kích thước", 
                           "Loại", "Số lượng", "Đơn giá", "Thành tiền", "Tình trạng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChiTiet = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnThemSanPham = new JButton("Thêm sản phẩm");
        btnThemSanPham.addActionListener(this::themSanPham);
        buttonPanel.add(btnThemSanPham);
        
        JButton btnSuaSanPham = new JButton("Sửa");
        btnSuaSanPham.addActionListener(this::suaSanPham);
        buttonPanel.add(btnSuaSanPham);
        
        JButton btnXoaSanPham = new JButton("Xóa");
        btnXoaSanPham.addActionListener(this::xoaSanPham);
        buttonPanel.add(btnXoaSanPham);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Tổng giá trị trả
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel label1 = new JLabel("Tổng giá trị trả:");
        label1.setFont(labelFont);
        panel.add(label1, gbc);
        gbc.gridx = 1;
        lblTongGiaTriTra = new JLabel("0 VNĐ");
        lblTongGiaTriTra.setFont(labelFont);
        lblTongGiaTriTra.setForeground(Color.RED);
        panel.add(lblTongGiaTriTra, gbc);
        
        // Tổng giá trị đổi
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel label2 = new JLabel("Tổng giá trị đổi:");
        label2.setFont(labelFont);
        panel.add(label2, gbc);
        gbc.gridx = 1;
        lblTongGiaTriDoi = new JLabel("0 VNĐ");
        lblTongGiaTriDoi.setFont(labelFont);
        lblTongGiaTriDoi.setForeground(Color.BLUE);
        panel.add(lblTongGiaTriDoi, gbc);
        
        // Số tiền hoàn lại
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel label3 = new JLabel("Số tiền hoàn lại:");
        label3.setFont(labelFont);
        panel.add(label3, gbc);
        gbc.gridx = 1;
        lblSoTienHoanLai = new JLabel("0 VNĐ");
        lblSoTienHoanLai.setFont(labelFont);
        lblSoTienHoanLai.setForeground(new Color(0, 150, 0));
        panel.add(lblSoTienHoanLai, gbc);
        
        // Số tiền bổ sung
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel label4 = new JLabel("Số tiền bổ sung:");
        label4.setFont(labelFont);
        panel.add(label4, gbc);
        gbc.gridx = 1;
        lblSoTienBoSung = new JLabel("0 VNĐ");
        lblSoTienBoSung.setFont(labelFont);
        lblSoTienBoSung.setForeground(Color.ORANGE);
        panel.add(lblSoTienBoSung, gbc);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnLuu = new JButton("Lưu");
        btnLuu.addActionListener(this::luuPhieu);
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        panel.add(btnLuu);
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        panel.add(btnHuy);
        
        return panel;
    }
    
    private void timHoaDon(ActionEvent e) {
        try {
            String maHDText = txtMaHD.getText().trim();
            if (maHDText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn",
                                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate input is numeric
            int maHD;
            try {
                maHD = Integer.parseInt(maHDText);
                if (maHD <= 0) {
                    JOptionPane.showMessageDialog(this, "Mã hóa đơn phải là số dương",
                                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn phải là số hợp lệ",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Clear previous data
            clearCustomerInfo();

            // Find invoice
            HoaDon hoaDon = hoaDonDAO.findByIdWithDetails(maHD);

            if (hoaDon == null) {
                JOptionPane.showMessageDialog(this,
                    "Hóa đơn với mã " + maHD + " không tồn tại trong hệ thống",
                    "Không tìm thấy hóa đơn", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if invoice can be returned/exchanged
            try {
                if (!phieuDoiTraService.kiemTraHoaDonCoTheDoisTra(maHD)) {
                    JOptionPane.showMessageDialog(this,
                        "Hóa đơn này không thể đổi trả (có thể đã quá thời hạn hoặc không có sản phẩm)",
                        "Không thể đổi trả", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi kiểm tra điều kiện đổi trả: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Display customer information
            displayCustomerInfo(hoaDon);

            JOptionPane.showMessageDialog(this,
                String.format("Tìm thấy hóa đơn hợp lệ!\nMã hóa đơn: %d\nNgày lập: %s\nTổng tiền: %,.0f VNĐ",
                    hoaDon.getId(),
                    hoaDon.getNgayLap().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    hoaDon.getTotalAmount()),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace(); // For debugging
            JOptionPane.showMessageDialog(this,
                "Lỗi không xác định khi tìm hóa đơn: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearCustomerInfo() {
        txtMaKH.setText("");
        txtTenKH.setText("");
    }

    private void displayCustomerInfo(HoaDon hoaDon) {
        if (hoaDon.getMaKH() != null) {
            KhachHang khachHang = hoaDon.getMaKH();
            txtMaKH.setText(String.valueOf(khachHang.getId()));
            txtTenKH.setText(khachHang.getHoTen());
        } else {
            txtMaKH.setText("");
            txtTenKH.setText("Khách lẻ");
        }
    }
    
    private void themSanPham(ActionEvent e) {
        ChiTietSanPhamDialog dialog = new ChiTietSanPhamDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            ChiTietPhieuDoiTra chiTiet = dialog.getChiTiet();
            danhSachChiTiet.add(chiTiet);
            refreshTable();
            tinhToanLai();
        }
    }
    
    private void suaSanPham(ActionEvent e) {
        int selectedRow = tableChiTiet.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ChiTietPhieuDoiTra chiTiet = danhSachChiTiet.get(selectedRow);
        ChiTietSanPhamDialog dialog = new ChiTietSanPhamDialog(this, chiTiet);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            danhSachChiTiet.set(selectedRow, dialog.getChiTiet());
            refreshTable();
            tinhToanLai();
        }
    }
    
    private void xoaSanPham(ActionEvent e) {
        int selectedRow = tableChiTiet.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa sản phẩm này?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            danhSachChiTiet.remove(selectedRow);
            refreshTable();
            tinhToanLai();
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (ChiTietPhieuDoiTra chiTiet : danhSachChiTiet) {
            tableModel.addRow(new Object[]{
                chiTiet.getMaBienThe(),
                chiTiet.getTenSanPham(),
                chiTiet.getTenMauSac(),
                chiTiet.getTenKichThuoc(),
                chiTiet.getLoaiChiTiet(),
                chiTiet.getSoLuong(),
                String.format("%,.0f", chiTiet.getDonGia()),
                String.format("%,.0f", chiTiet.getThanhTien()),
                chiTiet.getTinhTrangSanPham()
            });
        }
    }
    
    private void tinhToanLai() {
        if (phieuDoiTra == null) {
            phieuDoiTra = new PhieuDoiTra();
        }
        
        phieuDoiTraService.tinhToanLaiGiaTri(phieuDoiTra, danhSachChiTiet);
        
        lblTongGiaTriTra.setText(String.format("%,.0f VNĐ", phieuDoiTra.getTongGiaTriTra()));
        lblTongGiaTriDoi.setText(String.format("%,.0f VNĐ", phieuDoiTra.getTongGiaTriDoi()));
        lblSoTienHoanLai.setText(String.format("%,.0f VNĐ", phieuDoiTra.getSoTienHoanLai()));
        lblSoTienBoSung.setText(String.format("%,.0f VNĐ", phieuDoiTra.getSoTienBoSung()));
    }
    
    private void luuPhieu(ActionEvent e) {
        try {
            // Validate input
            if (txtMaHD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (txtLyDo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do đổi trả", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (danhSachChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo/cập nhật phiếu
            if (phieuDoiTra == null) {
                phieuDoiTra = new PhieuDoiTra();
            }
            
            phieuDoiTra.setMaHD(Integer.parseInt(txtMaHD.getText().trim()));
            phieuDoiTra.setLoaiPhieu((String) cmbLoaiPhieu.getSelectedItem());
            phieuDoiTra.setLyDo(txtLyDo.getText().trim());
            phieuDoiTra.setGhiChu(txtGhiChu.getText().trim());
            
            String maKHText = txtMaKH.getText().trim();
            if (!maKHText.isEmpty()) {
                phieuDoiTra.setMaKH(Integer.parseInt(maKHText));
            } else {
                phieuDoiTra.setMaKH(null);
            }
            
            if (isEditMode) {
                phieuDoiTraService.capNhatPhieuDoiTra(phieuDoiTra);
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu đổi trả thành công!", 
                                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                phieuDoiTra = phieuDoiTraService.taoPhieuDoiTra(phieuDoiTra, danhSachChiTiet);
                JOptionPane.showMessageDialog(this, "Tạo phiếu đổi trả thành công!", 
                                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            
            saved = true;
            dispose();
            
        } catch (ValidationException ex) {
            StringBuilder msg = new StringBuilder("Lỗi validation:\n");
            for (String error : ex.getErrors()) {
                msg.append("- ").append(error).append("\n");
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadData() {
        if (phieuDoiTra != null) {
            txtMaHD.setText(String.valueOf(phieuDoiTra.getMaHD()));
            cmbLoaiPhieu.setSelectedItem(phieuDoiTra.getLoaiPhieu());
            txtLyDo.setText(phieuDoiTra.getLyDo());
            txtGhiChu.setText(phieuDoiTra.getGhiChu());
            
            if (phieuDoiTra.getMaKH() != null) {
                txtMaKH.setText(String.valueOf(phieuDoiTra.getMaKH()));
            }
            
            // Load chi tiết
            // TODO: Load từ database
            
            tinhToanLai();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public PhieuDoiTra getPhieuDoiTra() {
        return phieuDoiTra;
    }
}

// Inner class for product detail dialog
class ChiTietSanPhamDialog extends JDialog {
    private ChiTietPhieuDoiTra chiTiet;
    private boolean saved = false;
    
    private JTextField txtMaBienThe;
    private JComboBox<String> cmbLoaiChiTiet;
    private JSpinner spnSoLuong;
    private JTextField txtDonGia;
    private JComboBox<String> cmbTinhTrang;
    private JTextField txtLyDoChiTiet;
    
    public ChiTietSanPhamDialog(JDialog parent, ChiTietPhieuDoiTra chiTiet) {
        super(parent, "Chi tiết sản phẩm", true);
        this.chiTiet = chiTiet;
        
        initComponents();
        
        if (chiTiet != null) {
            loadData();
        }
    }
    
    private void initComponents() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mã biến thể
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã biến thể *:"), gbc);
        gbc.gridx = 1;
        txtMaBienThe = new JTextField(15);
        panel.add(txtMaBienThe, gbc);
        
        // Loại chi tiết
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Loại *:"), gbc);
        gbc.gridx = 1;
        cmbLoaiChiTiet = new JComboBox<>(new String[]{"TRA", "DOI"});
        panel.add(cmbLoaiChiTiet, gbc);
        
        // Số lượng
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Số lượng *:"), gbc);
        gbc.gridx = 1;
        spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        panel.add(spnSoLuong, gbc);
        
        // Đơn giá
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Đơn giá *:"), gbc);
        gbc.gridx = 1;
        txtDonGia = new JTextField(15);
        panel.add(txtDonGia, gbc);
        
        // Tình trạng
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Tình trạng:"), gbc);
        gbc.gridx = 1;
        cmbTinhTrang = new JComboBox<>(new String[]{"MOI", "DA_SU_DUNG", "LOI", "HONG"});
        panel.add(cmbTinhTrang, gbc);
        
        // Lý do chi tiết
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Lý do chi tiết:"), gbc);
        gbc.gridx = 1;
        txtLyDoChiTiet = new JTextField(15);
        panel.add(txtLyDoChiTiet, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnLuu = new JButton("Lưu");
        btnLuu.addActionListener(this::luu);
        buttonPanel.add(btnLuu);
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        buttonPanel.add(btnHuy);
        
        panel.add(buttonPanel, gbc);
        
        add(panel);
    }
    
    private void luu(ActionEvent e) {
        try {
            // Validate
            if (txtMaBienThe.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã biến thể");
                return;
            }
            
            if (txtDonGia.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đơn giá");
                return;
            }
            
            // Create/update chi tiết
            if (chiTiet == null) {
                chiTiet = new ChiTietPhieuDoiTra();
            }
            
            chiTiet.setMaBienThe(Integer.parseInt(txtMaBienThe.getText().trim()));
            chiTiet.setLoaiChiTiet((String) cmbLoaiChiTiet.getSelectedItem());
            chiTiet.setSoLuong((Integer) spnSoLuong.getValue());
            chiTiet.setDonGia(new BigDecimal(txtDonGia.getText().trim()));
            chiTiet.setTinhTrangSanPham((String) cmbTinhTrang.getSelectedItem());
            chiTiet.setLyDoChiTiet(txtLyDoChiTiet.getText().trim());
            
            chiTiet.calculateThanhTien();
            
            saved = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu số không hợp lệ");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private void loadData() {
        if (chiTiet != null) {
            txtMaBienThe.setText(String.valueOf(chiTiet.getMaBienThe()));
            cmbLoaiChiTiet.setSelectedItem(chiTiet.getLoaiChiTiet());
            spnSoLuong.setValue(chiTiet.getSoLuong());
            txtDonGia.setText(chiTiet.getDonGia().toString());
            cmbTinhTrang.setSelectedItem(chiTiet.getTinhTrangSanPham());
            txtLyDoChiTiet.setText(chiTiet.getLyDoChiTiet());
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public ChiTietPhieuDoiTra getChiTiet() {
        return chiTiet;
    }
}
