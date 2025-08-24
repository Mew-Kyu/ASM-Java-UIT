package view;

import model.PhieuDoiTra;
import model.ChiTietPhieuDoiTra;
import model.HoaDon;
import model.ChiTietHoaDon;
import model.BienTheSanPham;
import dao.impl.HoaDonDAO;
import dao.impl.BienTheSanPhamDAO;
import dao.impl.ChiTietPhieuDoiTraDAO;
import service.interfaces.IPhieuDoiTraService;
import service.impl.PhieuDoiTraServiceImpl;


import exception.BusinessException;
import exception.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Form để thêm mới hoặc chỉnh sửa phiếu đổi trả
 */
public class PhieuDoiTraFormUI extends JDialog {
    
    private final IPhieuDoiTraService phieuDoiTraService;
    private final HoaDonDAO hoaDonDAO;

    private final BienTheSanPhamDAO bienTheSanPhamDAO;
    
    private PhieuDoiTra phieuHienTai;
    private boolean isEditMode = false;
    
    // Components
    private JTextField txtMaHoaDon;
    private JButton btnTimHoaDon;
    private JLabel lblThongTinHoaDon;
    private JComboBox<String> cmbLoaiPhieu;
    private JTextArea txtLyDo;
    private JTextField txtGhiChu;
    
    // Table for original invoice items
    private JTable tableHoaDon;
    private DefaultTableModel modelHoaDon;
    
    // Table for return/exchange items
    private JTable tableDoiTra;
    private DefaultTableModel modelDoiTra;
    
    // Calculation labels
    private JLabel lblTongGiaTriTra;
    private JLabel lblTongGiaTriDoi;
    private JLabel lblSoTienHoanLai;
    private JLabel lblSoTienBoSung;
    
    private boolean dataSaved = false;
    
    public PhieuDoiTraFormUI(JFrame parent) {
        super(parent, "Thêm Phiếu Đổi Trả Mới", true);
        this.phieuDoiTraService = new PhieuDoiTraServiceImpl();
        this.hoaDonDAO = new HoaDonDAO();
        this.bienTheSanPhamDAO = new BienTheSanPhamDAO();
        
        initComponents();
        setupEventHandlers();
    }
    
    public PhieuDoiTraFormUI(JFrame parent, PhieuDoiTra phieu) {
        super(parent, "Chỉnh Sửa Phiếu Đổi Trả", true);
        this.phieuDoiTraService = new PhieuDoiTraServiceImpl();
        this.hoaDonDAO = new HoaDonDAO();
        this.bienTheSanPhamDAO = new BienTheSanPhamDAO();
        this.phieuHienTai = phieu;
        this.isEditMode = true;
        
        initComponents();
        setupEventHandlers();
        loadPhieuData();
    }
    
    private void initComponents() {
        setSize(1200, 800);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Mã hóa đơn
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Mã hóa đơn:"), gbc);
        
        gbc.gridx = 1;
        txtMaHoaDon = new JTextField(15);
        txtMaHoaDon.setEnabled(!isEditMode);
        panel.add(txtMaHoaDon, gbc);
        
        gbc.gridx = 2;
        btnTimHoaDon = new JButton("Tìm hóa đơn");
        btnTimHoaDon.setEnabled(!isEditMode);
        panel.add(btnTimHoaDon, gbc);
        
        // Thông tin hóa đơn
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblThongTinHoaDon = new JLabel("Chưa có thông tin hóa đơn");
        lblThongTinHoaDon.setFont(new Font("Arial", Font.ITALIC, 12));
        lblThongTinHoaDon.setForeground(Color.GRAY);
        panel.add(lblThongTinHoaDon, gbc);
        
        // Loại phiếu
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Loại phiếu:"), gbc);
        
        gbc.gridx = 1;
        cmbLoaiPhieu = new JComboBox<>(new String[]{"DOI", "TRA"});
        panel.add(cmbLoaiPhieu, gbc);
        
        // Lý do
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Lý do:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        txtLyDo = new JTextArea(3, 30);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        panel.add(scrollLyDo, gbc);
        
        // Ghi chú
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Ghi chú:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtGhiChu = new JTextField();
        panel.add(txtGhiChu, gbc);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Split pane for two tables
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.35); // Give more space to bottom panel
        splitPane.setOneTouchExpandable(true);
        
        // Upper panel - Original invoice items
        JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.setBorder(BorderFactory.createTitledBorder("Sản phẩm trong hóa đơn gốc"));
        upperPanel.setMinimumSize(new Dimension(0, 200));
        
        String[] hoaDonColumns = {"Mã biến thể", "Tên sản phẩm", "Màu sắc", "Kích thước", "SL", "Đơn giá", "Thành tiền"};
        modelHoaDon = new DefaultTableModel(hoaDonColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableHoaDon = new JTable(modelHoaDon);
        tableHoaDon.setRowHeight(25);
        JScrollPane scrollHoaDon = new JScrollPane(tableHoaDon);
        scrollHoaDon.setPreferredSize(new Dimension(0, 150));
        upperPanel.add(scrollHoaDon, BorderLayout.CENTER);
        
        // Add button to add items to return/exchange
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnThemVaoDoiTra = new JButton("Thêm vào đổi/trả");
        btnThemVaoDoiTra.addActionListener(e -> themSanPhamVaoDoiTra());
        buttonPanel.add(btnThemVaoDoiTra);
        upperPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        splitPane.setTopComponent(upperPanel);
        
        // Lower panel with calculation - Return/Exchange items
        JPanel lowerMainPanel = new JPanel(new BorderLayout());
        
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.setBorder(BorderFactory.createTitledBorder("Sản phẩm đổi/trả"));
        lowerPanel.setMinimumSize(new Dimension(0, 250));
        
        String[] doiTraColumns = {"Mã biến thể", "Tên sản phẩm", "Loại", "SL", "Đơn giá", "Thành tiền", "Tình trạng", "Ghi chú"};
        modelDoiTra = new DefaultTableModel(doiTraColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 6 || column == 7; // SL, Tình trạng, Ghi chú
            }
        };
        tableDoiTra = new JTable(modelDoiTra);
        tableDoiTra.setRowHeight(25);
        
        // Combo box cho cột tình trạng
        JComboBox<String> cmbTinhTrang = new JComboBox<>(new String[]{"MOI", "DA_SU_DUNG", "LOI", "HONG"});
        tableDoiTra.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(cmbTinhTrang));
        
        JScrollPane scrollDoiTra = new JScrollPane(tableDoiTra);
        scrollDoiTra.setPreferredSize(new Dimension(0, 200));
        lowerPanel.add(scrollDoiTra, BorderLayout.CENTER);
        
        // Button panel for return/exchange table
        JPanel doiTraButtonPanel = new JPanel(new FlowLayout());
        JButton btnXoaKhoiDoiTra = new JButton("Xóa khỏi danh sách");
        btnXoaKhoiDoiTra.addActionListener(e -> xoaSanPhamKhoiDoiTra());
        JButton btnTinhToan = new JButton("Tính toán lại");
        btnTinhToan.addActionListener(e -> tinhToanLaiGiaTri());
        doiTraButtonPanel.add(btnXoaKhoiDoiTra);
        doiTraButtonPanel.add(btnTinhToan);
        lowerPanel.add(doiTraButtonPanel, BorderLayout.SOUTH);
        
        lowerMainPanel.add(lowerPanel, BorderLayout.CENTER);
        
        // Calculation panel
        JPanel calcPanel = createCalculationPanel();
        lowerMainPanel.add(calcPanel, BorderLayout.SOUTH);
        
        splitPane.setBottomComponent(lowerMainPanel);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tính toán"));
        
        panel.add(new JLabel("Tổng giá trị trả:"));
        lblTongGiaTriTra = new JLabel("0 VNĐ");
        lblTongGiaTriTra.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTongGiaTriTra);
        
        panel.add(new JLabel("Tổng giá trị đổi:"));
        lblTongGiaTriDoi = new JLabel("0 VNĐ");
        lblTongGiaTriDoi.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTongGiaTriDoi);
        
        panel.add(new JLabel("Số tiền hoàn lại:"));
        lblSoTienHoanLai = new JLabel("0 VNĐ");
        lblSoTienHoanLai.setFont(new Font("Arial", Font.BOLD, 12));
        lblSoTienHoanLai.setForeground(new Color(0, 128, 0));
        panel.add(lblSoTienHoanLai);
        
        panel.add(new JLabel("Số tiền bổ sung:"));
        lblSoTienBoSung = new JLabel("0 VNĐ");
        lblSoTienBoSung.setFont(new Font("Arial", Font.BOLD, 12));
        lblSoTienBoSung.setForeground(new Color(220, 53, 69));
        panel.add(lblSoTienBoSung);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.addActionListener(e -> luuPhieu());
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        
        panel.add(btnLuu);
        panel.add(btnHuy);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        btnTimHoaDon.addActionListener(e -> timHoaDon());
        cmbLoaiPhieu.addActionListener(e -> capNhatLoaiChiTiet());
    }
    
    private void timHoaDon() {
        String maHDText = txtMaHoaDon.getText().trim();
        if (maHDText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maHD = Integer.parseInt(maHDText);
            
            // Kiểm tra hóa đơn có thể đổi trả không
            if (!phieuDoiTraService.kiemTraHoaDonCoTheDoisTra(maHD)) {
                JOptionPane.showMessageDialog(this, "Hóa đơn này không thể đổi trả (có thể đã quá thời hạn hoặc không tồn tại)", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Load thông tin hóa đơn
            HoaDon hoaDon = hoaDonDAO.findById(maHD);
            if (hoaDon != null) {
                hienThiThongTinHoaDon(hoaDon);
                loadSanPhamHoaDon(maHD);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hienThiThongTinHoaDon(HoaDon hoaDon) {
        String thongTin = String.format("HĐ #%d - Ngày: %s - Khách hàng: %s - Tổng tiền: %,.0f VNĐ", 
            hoaDon.getId(), 
            hoaDon.getNgayLap(),
            hoaDon.getMaKH() != null && hoaDon.getMaKH().getHoTen() != null ? hoaDon.getMaKH().getHoTen() : "Khách lẻ",
            hoaDon.getTongTien().doubleValue());
        
        lblThongTinHoaDon.setText(thongTin);
        lblThongTinHoaDon.setForeground(Color.BLACK);
    }
    
    private void loadSanPhamHoaDon(int maHD) {
        try {
            HoaDon hoaDonInfo = hoaDonDAO.findById(maHD);
            if (hoaDonInfo != null) {
                List<ChiTietHoaDon> chiTietList = new ArrayList<>(hoaDonInfo.getChiTietHoaDons());
                modelHoaDon.setRowCount(0);
                
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    BienTheSanPham bienThe = chiTiet.getMaBienThe();
                    if (bienThe != null) {
                        String tenSanPham = bienThe.getMaSP() != null ? bienThe.getMaSP().getTenSP() : "N/A";
                        String tenMauSac = bienThe.getMaMau() != null ? bienThe.getMaMau().getTenMau() : "N/A";
                        String tenKichThuoc = bienThe.getMaSize() != null ? bienThe.getMaSize().getTenSize() : "N/A";
                        
                        BigDecimal thanhTien = chiTiet.getDonGia().multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
                        
                        modelHoaDon.addRow(new Object[]{
                            bienThe.getId(),
                            tenSanPham,
                            tenMauSac,
                            tenKichThuoc,
                            chiTiet.getSoLuong(),
                            String.format("%,.0f", chiTiet.getDonGia().doubleValue()),
                            String.format("%,.0f", thanhTien.doubleValue())
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải sản phẩm: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void themSanPhamVaoDoiTra() {
        int selectedRow = tableHoaDon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm từ hóa đơn gốc", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get product info from selected row
        int maBienThe = (Integer) modelHoaDon.getValueAt(selectedRow, 0);
        String tenSanPham = (String) modelHoaDon.getValueAt(selectedRow, 1);
        String mauSac = (String) modelHoaDon.getValueAt(selectedRow, 2);
        String kichThuoc = (String) modelHoaDon.getValueAt(selectedRow, 3);
        int soLuongGoc = (Integer) modelHoaDon.getValueAt(selectedRow, 4);
        String donGiaStr = (String) modelHoaDon.getValueAt(selectedRow, 5);
        BigDecimal donGia = new BigDecimal(donGiaStr.replaceAll("[,.]", ""));
        
        // Ask for quantity
        String soLuongStr = JOptionPane.showInputDialog(this, 
            "Nhập số lượng đổi/trả (tối đa " + soLuongGoc + "):", "1");
        
        if (soLuongStr == null || soLuongStr.trim().isEmpty()) return;
        
        try {
            int soLuong = Integer.parseInt(soLuongStr);
            if (soLuong <= 0 || soLuong > soLuongGoc) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String loaiChiTiet = (String) cmbLoaiPhieu.getSelectedItem();
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
            
            modelDoiTra.addRow(new Object[]{
                maBienThe,
                tenSanPham + " - " + mauSac + " - " + kichThuoc,
                loaiChiTiet,
                soLuong,
                String.format("%,.0f", donGia.doubleValue()),
                String.format("%,.0f", thanhTien.doubleValue()),
                "MOI",
                ""
            });
            
            tinhToanLaiGiaTri();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaSanPhamKhoiDoiTra() {
        int selectedRow = tableDoiTra.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        modelDoiTra.removeRow(selectedRow);
        tinhToanLaiGiaTri();
    }
    
    private void capNhatLoaiChiTiet() {
        String loaiPhieu = (String) cmbLoaiPhieu.getSelectedItem();
        
        // Update all items in return/exchange table
        for (int i = 0; i < modelDoiTra.getRowCount(); i++) {
            modelDoiTra.setValueAt(loaiPhieu, i, 2);
        }
        
        tinhToanLaiGiaTri();
    }
    
    private void tinhToanLaiGiaTri() {
        BigDecimal tongGiaTriTra = BigDecimal.ZERO;
        BigDecimal tongGiaTriDoi = BigDecimal.ZERO;
        
        for (int i = 0; i < modelDoiTra.getRowCount(); i++) {
            String loaiChiTiet = (String) modelDoiTra.getValueAt(i, 2);
            String thanhTienStr = (String) modelDoiTra.getValueAt(i, 5);
            BigDecimal thanhTien = new BigDecimal(thanhTienStr.replaceAll("[,.]", ""));
            
            if ("TRA".equals(loaiChiTiet)) {
                tongGiaTriTra = tongGiaTriTra.add(thanhTien);
            } else if ("DOI".equals(loaiChiTiet)) {
                tongGiaTriDoi = tongGiaTriDoi.add(thanhTien);
            }
        }
        
        BigDecimal soTienHoanLai = tongGiaTriTra.subtract(tongGiaTriDoi);
        BigDecimal soTienBoSung = BigDecimal.ZERO;
        
        if (soTienHoanLai.compareTo(BigDecimal.ZERO) < 0) {
            soTienBoSung = soTienHoanLai.negate();
            soTienHoanLai = BigDecimal.ZERO;
        }
        
        lblTongGiaTriTra.setText(String.format("%,.0f VNĐ", tongGiaTriTra.doubleValue()));
        lblTongGiaTriDoi.setText(String.format("%,.0f VNĐ", tongGiaTriDoi.doubleValue()));
        lblSoTienHoanLai.setText(String.format("%,.0f VNĐ", soTienHoanLai.doubleValue()));
        lblSoTienBoSung.setText(String.format("%,.0f VNĐ", soTienBoSung.doubleValue()));
    }
    
    private void loadPhieuData() {
        if (phieuHienTai == null) return;
        
        txtMaHoaDon.setText(String.valueOf(phieuHienTai.getMaHD()));
        cmbLoaiPhieu.setSelectedItem(phieuHienTai.getLoaiPhieu());
        txtLyDo.setText(phieuHienTai.getLyDo());
        txtGhiChu.setText(phieuHienTai.getGhiChu() != null ? phieuHienTai.getGhiChu() : "");
        
        timHoaDon();
        loadChiTietPhieuDoiTra();
    }
    
    private void loadChiTietPhieuDoiTra() {
        if (phieuHienTai == null) return;
        
        try {
            // Load chi tiết từ database
            ChiTietPhieuDoiTraDAO chiTietDAO = new ChiTietPhieuDoiTraDAO();
            List<ChiTietPhieuDoiTra> chiTietList = chiTietDAO.findByPhieuDoiTra(phieuHienTai.getMaPhieuDT());
            
            modelDoiTra.setRowCount(0);
            
            for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                // Get product info
                BienTheSanPham bienThe = bienTheSanPhamDAO.findById(chiTiet.getMaBienThe());
                if (bienThe != null) {
                    String tenSanPham = bienThe.getMaSP() != null ? bienThe.getMaSP().getTenSP() : "N/A";
                    String tenMauSac = bienThe.getMaMau() != null ? bienThe.getMaMau().getTenMau() : "N/A";
                    String tenKichThuoc = bienThe.getMaSize() != null ? bienThe.getMaSize().getTenSize() : "N/A";
                    
                    modelDoiTra.addRow(new Object[]{
                        chiTiet.getMaBienThe(),
                        tenSanPham + " - " + tenMauSac + " - " + tenKichThuoc,
                        chiTiet.getLoaiChiTiet(),
                        chiTiet.getSoLuong(),
                        String.format("%,.0f", chiTiet.getDonGia().doubleValue()),
                        String.format("%,.0f", chiTiet.getThanhTien().doubleValue()),
                        chiTiet.getTinhTrangSanPham() != null ? chiTiet.getTinhTrangSanPham() : "MOI",
                        chiTiet.getGhiChu() != null ? chiTiet.getGhiChu() : ""
                    });
                }
            }
            
            tinhToanLaiGiaTri();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết phiếu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void luuPhieu() {
        try {
            // Validate
            if (txtMaHoaDon.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (txtLyDo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do đổi/trả", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (modelDoiTra.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm đổi/trả", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create PhieuDoiTra
            PhieuDoiTra phieu = new PhieuDoiTra();
            phieu.setMaHD(Integer.parseInt(txtMaHoaDon.getText().trim()));
            phieu.setLoaiPhieu((String) cmbLoaiPhieu.getSelectedItem());
            phieu.setLyDo(txtLyDo.getText().trim());
            phieu.setGhiChu(txtGhiChu.getText().trim());
            
            // Create chi tiết list
            List<ChiTietPhieuDoiTra> chiTietList = new ArrayList<>();
            for (int i = 0; i < modelDoiTra.getRowCount(); i++) {
                ChiTietPhieuDoiTra chiTiet = new ChiTietPhieuDoiTra();
                chiTiet.setMaBienThe((Integer) modelDoiTra.getValueAt(i, 0));
                chiTiet.setLoaiChiTiet((String) modelDoiTra.getValueAt(i, 2));
                chiTiet.setSoLuong((Integer) modelDoiTra.getValueAt(i, 3));
                
                String donGiaStr = (String) modelDoiTra.getValueAt(i, 4);
                chiTiet.setDonGia(new BigDecimal(donGiaStr.replaceAll("[,.]", "")));
                
                chiTiet.setTinhTrangSanPham((String) modelDoiTra.getValueAt(i, 6));
                chiTiet.setGhiChu((String) modelDoiTra.getValueAt(i, 7));
                
                chiTietList.add(chiTiet);
            }
            
            if (isEditMode) {
                // Update existing phieu
                phieu.setMaPhieuDT(phieuHienTai.getMaPhieuDT());
                phieu.setNgayTao(phieuHienTai.getNgayTao()); // Keep original creation date
                phieu.setMaNV(phieuHienTai.getMaNV()); // Keep original employee
                phieu.setTrangThai(phieuHienTai.getTrangThai()); // Keep current status
                
                // Calculate values
                tinhToanLaiGiaTri();
                phieu.setTongGiaTriTra(getTongGiaTriTraFromLabel());
                phieu.setTongGiaTriDoi(getTongGiaTriDoiFromLabel());
                phieu.setSoTienHoanLai(getSoTienHoanLaiFromLabel());
                phieu.setSoTienBoSung(getSoTienBoSungFromLabel());
                
                phieuDoiTraService.capNhatPhieuDoiTra(phieu);
                
                // Update chi tiết - delete old and insert new
                ChiTietPhieuDoiTraDAO chiTietDAO = new ChiTietPhieuDoiTraDAO();
                chiTietDAO.deleteByPhieuDoiTra(phieu.getMaPhieuDT());
                
                for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                    chiTiet.setMaPhieuDT(phieu.getMaPhieuDT());
                    chiTiet.calculateThanhTien();
                    chiTietDAO.insert(chiTiet);
                }
            } else {
                // Create new phieu
                phieuDoiTraService.taoPhieuDoiTra(phieu, chiTietList);
            }
            
            dataSaved = true;
            JOptionPane.showMessageDialog(this, 
                isEditMode ? "Cập nhật phiếu đổi trả thành công!" : "Tạo phiếu đổi trả thành công!", 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, "Lỗi validation: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private BigDecimal getTongGiaTriTraFromLabel() {
        String text = lblTongGiaTriTra.getText().replace(" VNĐ", "").replace(",", "");
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private BigDecimal getTongGiaTriDoiFromLabel() {
        String text = lblTongGiaTriDoi.getText().replace(" VNĐ", "").replace(",", "");
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private BigDecimal getSoTienHoanLaiFromLabel() {
        String text = lblSoTienHoanLai.getText().replace(" VNĐ", "").replace(",", "");
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private BigDecimal getSoTienBoSungFromLabel() {
        String text = lblSoTienBoSung.getText().replace(" VNĐ", "").replace(",", "");
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    public boolean isDataSaved() {
        return dataSaved;
    }
}
