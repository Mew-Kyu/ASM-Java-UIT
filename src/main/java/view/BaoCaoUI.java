package view;

import service.interfaces.IBaoCaoService;
import service.impl.BaoCaoServiceImpl;
import dao.impl.BaoCaoDAO;
import model.ThongKeSanPham;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.time.ZoneId;

/**
 * UI for Reports & Analytics Management
 */
public class BaoCaoUI extends BaseAuthenticatedUI {
    
    private IBaoCaoService baoCaoService;
    private JTabbedPane tabbedPane;
    
    // Revenue report components
    private JDateChooser txtTuNgay, txtDenNgay;
    private JTextArea txtBaoCaoDoanhThu;
    
    // Product report components
    private JDateChooser txtTuNgaySeProduct, txtDenNgayProduct;
    private JTable tblTopSanPham;
    private DefaultTableModel topSanPhamModel;
    
    // Inventory report components
    private JTable tblTonKho;
    private DefaultTableModel tonKhoModel;
    private JLabel lblTongGiaTriTonKho, lblTongSoLuongTonKho;
    
    // Employee report components
    private JDateChooser txtTuNgayNhanVien, txtDenNgayNhanVien;
    private JTable tblHieuSuatNhanVien;
    private DefaultTableModel hieuSuatNhanVienModel;
    
    public BaoCaoUI() {
        super();
        if (!RoleManager.canAccessReports()) {
            RoleManager.showAccessDeniedMessage(this, "Báo cáo & Thống kê", "Manager hoặc Admin");
            dispose();
            return;
        }
        
        this.baoCaoService = new BaoCaoServiceImpl(new BaoCaoDAO());
        initComponents();
        loadInitialData();
    }
    
    protected void initComponents() {
        setTitle("Quản Lý Báo Cáo & Thống Kê");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Báo Cáo Doanh Thu", createRevenueReportPanel());
        tabbedPane.addTab("Báo Cáo Sản Phẩm", createProductReportPanel());
        tabbedPane.addTab("Báo Cáo Tồn Kho", createInventoryReportPanel());
        tabbedPane.addTab("Báo Cáo Nhân Viên", createEmployeeReportPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add control panel at bottom
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> refreshCurrentTab());
        
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        
        controlPanel.add(btnRefresh);
        controlPanel.add(btnClose);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createRevenueReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JDateChooser();
        txtTuNgay.setDateFormatString("yyyy-MM-dd");
        txtTuNgay.setDate(java.sql.Date.valueOf(LocalDate.of(2025, 7, 24)));
        txtTuNgay.setPreferredSize(new Dimension(120, 25));
        controlPanel.add(txtTuNgay);
        
        controlPanel.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JDateChooser();
        txtDenNgay.setDateFormatString("yyyy-MM-dd");
        txtDenNgay.setDate(java.sql.Date.valueOf(LocalDate.of(2025, 8, 23)));
        txtDenNgay.setPreferredSize(new Dimension(120, 25));
        controlPanel.add(txtDenNgay);
        
        JButton btnTaoBaoCao = new JButton("Tạo Báo Cáo");
        btnTaoBaoCao.addActionListener(this::taoBaoCaoDoanhThu);
        controlPanel.add(btnTaoBaoCao);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Report content
        txtBaoCaoDoanhThu = new JTextArea();
        txtBaoCaoDoanhThu.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtBaoCaoDoanhThu.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtBaoCaoDoanhThu);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProductReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Từ ngày:"));
        txtTuNgaySeProduct = new JDateChooser();
        txtTuNgaySeProduct.setDateFormatString("yyyy-MM-dd");
        txtTuNgaySeProduct.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        txtTuNgaySeProduct.setPreferredSize(new Dimension(120, 25));
        controlPanel.add(txtTuNgaySeProduct);
        
        controlPanel.add(new JLabel("Đến ngày:"));
        txtDenNgayProduct = new JDateChooser();
        txtDenNgayProduct.setDateFormatString("yyyy-MM-dd");
        txtDenNgayProduct.setDate(java.sql.Date.valueOf(LocalDate.now()));
        txtDenNgayProduct.setPreferredSize(new Dimension(120, 25));
        controlPanel.add(txtDenNgayProduct);
        
        JButton btnTopSanPham = new JButton("Top Sản Phẩm Bán Chạy");
        btnTopSanPham.addActionListener(this::loadTopSanPhamBanChay);
        controlPanel.add(btnTopSanPham);
        
        JButton btnSanPhamSapHet = new JButton("Sản Phẩm Sắp Hết");
        btnSanPhamSapHet.addActionListener(this::loadSanPhamSapHet);
        controlPanel.add(btnSanPhamSapHet);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "SL Bán", "Doanh Thu", "SL Tồn", "Giá Trị Tồn"};
        topSanPhamModel = new DefaultTableModel(columns, 0);
        tblTopSanPham = new JTable(topSanPhamModel);
        JScrollPane scrollPane = new JScrollPane(tblTopSanPham);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInventoryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout());
        summaryPanel.add(new JLabel("Tổng giá trị tồn kho:"));
        lblTongGiaTriTonKho = new JLabel("0 VNĐ");
        lblTongGiaTriTonKho.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(lblTongGiaTriTonKho);
        
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(new JLabel("Tổng số lượng:"));
        lblTongSoLuongTonKho = new JLabel("0");
        lblTongSoLuongTonKho.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(lblTongSoLuongTonKho);
        
        JButton btnLoadTonKho = new JButton("Tải Dữ Liệu Tồn Kho");
        btnLoadTonKho.addActionListener(this::loadBaoCaoTonKho);
        summaryPanel.add(btnLoadTonKho);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "SL Tồn", "Giá Trị Tồn"};
        tonKhoModel = new DefaultTableModel(columns, 0);
        tblTonKho = new JTable(tonKhoModel);
        JScrollPane scrollPane = new JScrollPane(tblTonKho);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEmployeeReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Từ ngày:"));
        txtTuNgayNhanVien = new JDateChooser();
        txtTuNgayNhanVien.setDateFormatString("yyyy-MM-dd");
        txtTuNgayNhanVien.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        txtTuNgayNhanVien.setPreferredSize(new Dimension(120, 25));
        controlPanel.add(txtTuNgayNhanVien);
        
        controlPanel.add(new JLabel("Đến ngày:"));
        txtDenNgayNhanVien = new JDateChooser();
        txtDenNgayNhanVien.setDateFormatString("yyyy-MM-dd");
        txtDenNgayNhanVien.setDate(java.sql.Date.valueOf(LocalDate.now()));
        txtDenNgayNhanVien.setPreferredSize(new Dimension(120, 25));
        controlPanel.add(txtDenNgayNhanVien);
        
        JButton btnHieuSuat = new JButton("Xem Hiệu Suất");
        btnHieuSuat.addActionListener(this::loadHieuSuatNhanVien);
        controlPanel.add(btnHieuSuat);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Mã NV", "Họ Tên", "Số Đơn Hàng", "Tổng Doanh Thu", "SL Sản Phẩm", "Doanh Thu TB"};
        hieuSuatNhanVienModel = new DefaultTableModel(columns, 0);
        tblHieuSuatNhanVien = new JTable(hieuSuatNhanVienModel);
        JScrollPane scrollPane = new JScrollPane(tblHieuSuatNhanVien);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void taoBaoCaoDoanhThu(ActionEvent e) {
        try {
            LocalDate tuNgay = getLocalDateFromChooser(txtTuNgay);
            LocalDate denNgay = getLocalDateFromChooser(txtDenNgay);

            if (tuNgay == null || denNgay == null) {
                showError("Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!");
                return;
            }

            String baoCao = baoCaoService.taoBaoCaoDoanhThu(tuNgay, denNgay, "NGAY");
            txtBaoCaoDoanhThu.setText(baoCao);
            
        } catch (Exception ex) {
            showError("Lỗi tạo báo cáo: " + ex.getMessage());
        }
    }
    
    private void loadTopSanPhamBanChay(ActionEvent e) {
        try {
            LocalDate tuNgay = getLocalDateFromChooser(txtTuNgaySeProduct);
            LocalDate denNgay = getLocalDateFromChooser(txtDenNgayProduct);

            if (tuNgay == null || denNgay == null) {
                showError("Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!");
                return;
            }

            List<ThongKeSanPham> data = baoCaoService.layTopSanPhamBanChay(tuNgay, denNgay, 20);
            
            topSanPhamModel.setRowCount(0);
            for (ThongKeSanPham item : data) {
                topSanPhamModel.addRow(new Object[]{
                    item.getMaSP(),
                    item.getTenSP(),
                    item.getTenDanhMuc(),
                    item.getSoLuongBan(),
                    formatCurrency(item.getDoanhThu()),
                    item.getSoLuongTon(),
                    formatCurrency(item.getGiaTriTon())
                });
            }
            
        } catch (Exception ex) {
            showError("Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private void loadSanPhamSapHet(ActionEvent e) {
        try {
            List<ThongKeSanPham> data = baoCaoService.laySanPhamSapHet(10);
            
            topSanPhamModel.setRowCount(0);
            for (ThongKeSanPham item : data) {
                topSanPhamModel.addRow(new Object[]{
                    item.getMaSP(),
                    item.getTenSP(),
                    item.getTenDanhMuc(),
                    item.getSoLuongBan(),
                    formatCurrency(item.getDoanhThu()),
                    item.getSoLuongTon(),
                    formatCurrency(item.getGiaTriTon())
                });
            }
            
        } catch (Exception ex) {
            showError("Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private void loadBaoCaoTonKho(ActionEvent e) {
        try {
            // Load summary data
            BigDecimal tongGiaTriTonKho = baoCaoService.tinhTongGiaTriTonKho();
            int tongSoLuongTonKho = baoCaoService.demTongSoLuongTonKho();
            
            lblTongGiaTriTonKho.setText(formatCurrency(tongGiaTriTonKho));
            lblTongSoLuongTonKho.setText(String.valueOf(tongSoLuongTonKho));
            
            // Load detail data
            List<ThongKeSanPham> data = baoCaoService.laySanPhamTonKho();
            
            tonKhoModel.setRowCount(0);
            for (ThongKeSanPham item : data) {
                tonKhoModel.addRow(new Object[]{
                    item.getMaSP(),
                    item.getTenSP(),
                    item.getTenDanhMuc(),
                    item.getSoLuongTon(),
                    formatCurrency(item.getGiaTriTon())
                });
            }
            
        } catch (Exception ex) {
            showError("Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private void loadHieuSuatNhanVien(ActionEvent e) {
        try {
            LocalDate tuNgay = getLocalDateFromChooser(txtTuNgayNhanVien);
            LocalDate denNgay = getLocalDateFromChooser(txtDenNgayNhanVien);

            if (tuNgay == null || denNgay == null) {
                showError("Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!");
                return;
            }

            List<Object[]> data = baoCaoService.thongKeHieuSuatNhanVien(tuNgay, denNgay);
            
            hieuSuatNhanVienModel.setRowCount(0);
            for (Object[] row : data) {
                hieuSuatNhanVienModel.addRow(new Object[]{
                    row[0], // MaNV
                    row[1], // HoTen
                    row[2], // SoDonHang
                    formatCurrency((BigDecimal) row[3]), // TongDoanhThu
                    row[4], // TongSoLuongBan
                    formatCurrency((BigDecimal) row[5]) // DoanhThuTrungBinh
                });
            }
            
        } catch (Exception ex) {
            showError("Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private void refreshCurrentTab() {
        int selectedTab = tabbedPane.getSelectedIndex();
        switch (selectedTab) {
            case 0: // Revenue report - no auto refresh
                break;
            case 1: // Product report
                loadTopSanPhamBanChay(null);
                break;
            case 2: // Inventory report
                loadBaoCaoTonKho(null);
                break;
            case 3: // Employee report
                loadHieuSuatNhanVien(null);
                break;
        }
    }
    
    private void loadInitialData() {
        // Load initial reports on startup
        SwingUtilities.invokeLater(() -> {
            // Auto-load revenue report for demo range (2025-07-24 to 2025-08-23)
            try {
                String baoCao = baoCaoService.taoBaoCaoDoanhThu(
                    LocalDate.of(2025, 7, 24), 
                    LocalDate.of(2025, 8, 23), 
                    "NGAY"
                );
                txtBaoCaoDoanhThu.setText(baoCao);
            } catch (Exception e) {
                txtBaoCaoDoanhThu.setText("Lỗi tải dữ liệu ban đầu: " + e.getMessage());
            }
            
            // Load inventory summary
            loadBaoCaoTonKho(null);
        });
    }
    
    private LocalDate getLocalDateFromChooser(JDateChooser chooser) {
        Date date = chooser.getDate();
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        return String.format("%,.0f VNĐ", amount);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
