package view;

import model.PhieuDoiTra;
import model.ChiTietPhieuDoiTra;

import dao.impl.ChiTietPhieuDoiTraDAO;
import service.interfaces.IPhieuDoiTraService;
import service.impl.PhieuDoiTraServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Form để xem chi tiết phiếu đổi trả
 */
public class PhieuDoiTraDetailUI extends JDialog {
    
    private final IPhieuDoiTraService phieuDoiTraService;

    private final ChiTietPhieuDoiTraDAO chiTietDAO;
    
    private int maPhieuDT;
    private PhieuDoiTra phieuDoiTra;
    
    // Components
    private JLabel lblMaPhieu;
    private JLabel lblMaHoaDon;
    private JLabel lblLoaiPhieu;
    private JLabel lblNgayTao;
    private JLabel lblTrangThai;
    private JLabel lblNhanVien;
    private JLabel lblKhachHang;
    private JTextArea txtLyDo;
    private JTextArea txtGhiChu;
    
    // Approval info
    private JLabel lblNgayDuyet;
    private JLabel lblNguoiDuyet;
    private JTextArea txtGhiChuDuyet;
    private JTextArea txtLyDoTuChoi;
    
    // Financial info
    private JLabel lblTongGiaTriTra;
    private JLabel lblTongGiaTriDoi;
    private JLabel lblSoTienHoanLai;
    private JLabel lblSoTienBoSung;
    private JLabel lblHinhThucHoanTien;
    private JLabel lblSoTaiKhoan;
    private JLabel lblTenChuTaiKhoan;
    private JLabel lblNgayHoanTien;
    
    // Table for details
    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    
    public PhieuDoiTraDetailUI(JFrame parent, int maPhieuDT) {
        super(parent, "Chi Tiết Phiếu Đổi Trả", true);
        this.maPhieuDT = maPhieuDT;
        this.phieuDoiTraService = new PhieuDoiTraServiceImpl();

        this.chiTietDAO = new ChiTietPhieuDoiTraDAO();
        
        loadData();
        initComponents();
    }
    
    private void loadData() {
        try {
            Optional<PhieuDoiTra> optional = phieuDoiTraService.layPhieuTheoId(maPhieuDT);
            if (optional.isPresent()) {
                phieuDoiTra = optional.get();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đổi trả", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private void initComponents() {
        setSize(900, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Thông tin chung
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("Thông tin chung", infoPanel);
        
        // Tab 2: Chi tiết sản phẩm
        JPanel detailPanel = createDetailPanel();
        tabbedPane.addTab("Chi tiết sản phẩm", detailPanel);
        
        // Tab 3: Thông tin phê duyệt & hoàn tiền
        JPanel approvalPanel = createApprovalPanel();
        tabbedPane.addTab("Phê duyệt & Hoàn tiền", approvalPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load data to components
        populateData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("CHI TIẾT PHIẾU ĐỔI TRẢ #" + maPhieuDT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Mã phiếu
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Mã phiếu:"), gbc);
        gbc.gridx = 1;
        lblMaPhieu = new JLabel();
        lblMaPhieu.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblMaPhieu, gbc);
        
        // Mã hóa đơn
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Mã hóa đơn:"), gbc);
        gbc.gridx = 3;
        lblMaHoaDon = new JLabel();
        lblMaHoaDon.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblMaHoaDon, gbc);
        
        row++;
        
        // Loại phiếu
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Loại phiếu:"), gbc);
        gbc.gridx = 1;
        lblLoaiPhieu = new JLabel();
        panel.add(lblLoaiPhieu, gbc);
        
        // Trạng thái
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3;
        lblTrangThai = new JLabel();
        panel.add(lblTrangThai, gbc);
        
        row++;
        
        // Ngày tạo
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày tạo:"), gbc);
        gbc.gridx = 1;
        lblNgayTao = new JLabel();
        panel.add(lblNgayTao, gbc);
        
        // Nhân viên
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Nhân viên xử lý:"), gbc);
        gbc.gridx = 3;
        lblNhanVien = new JLabel();
        panel.add(lblNhanVien, gbc);
        
        row++;
        
        // Khách hàng
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        lblKhachHang = new JLabel();
        panel.add(lblKhachHang, gbc);
        
        row++; gbc.gridwidth = 1;
        
        // Lý do
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Lý do:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        txtLyDo = new JTextArea(3, 40);
        txtLyDo.setEditable(false);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBackground(panel.getBackground());
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        scrollLyDo.setPreferredSize(new Dimension(400, 60));
        panel.add(scrollLyDo, gbc);
        
        row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Ghi chú
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        txtGhiChu = new JTextArea(2, 40);
        txtGhiChu.setEditable(false);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBackground(panel.getBackground());
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(400, 40));
        panel.add(scrollGhiChu, gbc);
        
        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for product details
        String[] columns = {"Mã biến thể", "Tên sản phẩm", "Màu sắc", "Kích thước", 
                          "Loại", "SL", "Đơn giá", "Thành tiền", "Tình trạng", "Ghi chú"};
        modelChiTiet = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tổng kết tài chính"));
        
        panel.add(new JLabel("Tổng giá trị trả:"));
        lblTongGiaTriTra = new JLabel();
        lblTongGiaTriTra.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTongGiaTriTra);
        
        panel.add(new JLabel("Tổng giá trị đổi:"));
        lblTongGiaTriDoi = new JLabel();
        lblTongGiaTriDoi.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTongGiaTriDoi);
        
        panel.add(new JLabel("Số tiền hoàn lại:"));
        lblSoTienHoanLai = new JLabel();
        lblSoTienHoanLai.setFont(new Font("Arial", Font.BOLD, 12));
        lblSoTienHoanLai.setForeground(new Color(0, 128, 0));
        panel.add(lblSoTienHoanLai);
        
        panel.add(new JLabel("Số tiền bổ sung:"));
        lblSoTienBoSung = new JLabel();
        lblSoTienBoSung.setFont(new Font("Arial", Font.BOLD, 12));
        lblSoTienBoSung.setForeground(new Color(220, 53, 69));
        panel.add(lblSoTienBoSung);
        
        return panel;
    }
    
    private JPanel createApprovalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Approval section
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JLabel approvalTitle = new JLabel("THÔNG TIN PHÊ DUYỆT");
        approvalTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(approvalTitle, gbc);
        
        row++; gbc.gridwidth = 1;
        
        // Ngày duyệt
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày duyệt:"), gbc);
        gbc.gridx = 1;
        lblNgayDuyet = new JLabel("Chưa duyệt");
        panel.add(lblNgayDuyet, gbc);
        
        // Người duyệt
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Người duyệt:"), gbc);
        gbc.gridx = 3;
        lblNguoiDuyet = new JLabel("-");
        panel.add(lblNguoiDuyet, gbc);
        
        row++;
        
        // Ghi chú duyệt
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Ghi chú duyệt:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        txtGhiChuDuyet = new JTextArea(2, 40);
        txtGhiChuDuyet.setEditable(false);
        txtGhiChuDuyet.setLineWrap(true);
        txtGhiChuDuyet.setWrapStyleWord(true);
        txtGhiChuDuyet.setBackground(panel.getBackground());
        JScrollPane scrollGhiChuDuyet = new JScrollPane(txtGhiChuDuyet);
        scrollGhiChuDuyet.setPreferredSize(new Dimension(400, 40));
        panel.add(scrollGhiChuDuyet, gbc);
        
        row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Lý do từ chối
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Lý do từ chối:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        txtLyDoTuChoi = new JTextArea(2, 40);
        txtLyDoTuChoi.setEditable(false);
        txtLyDoTuChoi.setLineWrap(true);
        txtLyDoTuChoi.setWrapStyleWord(true);
        txtLyDoTuChoi.setBackground(panel.getBackground());
        JScrollPane scrollLyDoTuChoi = new JScrollPane(txtLyDoTuChoi);
        scrollLyDoTuChoi.setPreferredSize(new Dimension(400, 40));
        panel.add(scrollLyDoTuChoi, gbc);
        
        row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Spacing
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(Box.createVerticalStrut(20), gbc);
        
        row++; gbc.gridwidth = 1;
        
        // Refund section
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JLabel refundTitle = new JLabel("THÔNG TIN HOÀN TIỀN");
        refundTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(refundTitle, gbc);
        
        row++; gbc.gridwidth = 1;
        
        // Hình thức hoàn tiền
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Hình thức hoàn tiền:"), gbc);
        gbc.gridx = 1;
        lblHinhThucHoanTien = new JLabel("Chưa hoàn tiền");
        panel.add(lblHinhThucHoanTien, gbc);
        
        // Ngày hoàn tiền
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Ngày hoàn tiền:"), gbc);
        gbc.gridx = 3;
        lblNgayHoanTien = new JLabel("-");
        panel.add(lblNgayHoanTien, gbc);
        
        row++;
        
        // Số tài khoản
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Số tài khoản:"), gbc);
        gbc.gridx = 1;
        lblSoTaiKhoan = new JLabel("-");
        panel.add(lblSoTaiKhoan, gbc);
        
        // Tên chủ tài khoản
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Tên chủ TK:"), gbc);
        gbc.gridx = 3;
        lblTenChuTaiKhoan = new JLabel("-");
        panel.add(lblTenChuTaiKhoan, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnInPhieu = new JButton("In phiếu");
        btnInPhieu.addActionListener(e -> inPhieu());
        
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        
        panel.add(btnInPhieu);
        panel.add(btnDong);
        
        return panel;
    }
    
    private void populateData() {
        if (phieuDoiTra == null) return;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Basic info
        lblMaPhieu.setText(String.valueOf(phieuDoiTra.getMaPhieuDT()));
        lblMaHoaDon.setText(String.valueOf(phieuDoiTra.getMaHD()));
        lblLoaiPhieu.setText(phieuDoiTra.getLoaiPhieuText());
        lblNgayTao.setText(phieuDoiTra.getNgayTao().format(formatter));
        lblTrangThai.setText(phieuDoiTra.getTrangThaiText());
        lblNhanVien.setText(phieuDoiTra.getTenNhanVien() != null ? phieuDoiTra.getTenNhanVien() : "N/A");
        lblKhachHang.setText(phieuDoiTra.getTenKhachHang() != null ? phieuDoiTra.getTenKhachHang() : "Khách lẻ");
        txtLyDo.setText(phieuDoiTra.getLyDo());
        txtGhiChu.setText(phieuDoiTra.getGhiChu() != null ? phieuDoiTra.getGhiChu() : "");
        
        // Financial info
        lblTongGiaTriTra.setText(String.format("%,.0f VNĐ", phieuDoiTra.getTongGiaTriTra().doubleValue()));
        lblTongGiaTriDoi.setText(String.format("%,.0f VNĐ", phieuDoiTra.getTongGiaTriDoi().doubleValue()));
        lblSoTienHoanLai.setText(String.format("%,.0f VNĐ", phieuDoiTra.getSoTienHoanLai().doubleValue()));
        lblSoTienBoSung.setText(String.format("%,.0f VNĐ", phieuDoiTra.getSoTienBoSung().doubleValue()));
        
        // Approval info
        if (phieuDoiTra.getNgayDuyet() != null) {
            lblNgayDuyet.setText(phieuDoiTra.getNgayDuyet().format(formatter));
        }
        if (phieuDoiTra.getTenNguoiDuyet() != null) {
            lblNguoiDuyet.setText(phieuDoiTra.getTenNguoiDuyet());
        }
        txtGhiChuDuyet.setText(phieuDoiTra.getGhiChu() != null ? phieuDoiTra.getGhiChu() : "");
        txtLyDoTuChoi.setText(phieuDoiTra.getLyDoTuChoi() != null ? phieuDoiTra.getLyDoTuChoi() : "");
        
        // Refund info
        if (phieuDoiTra.getHinhThucHoanTien() != null) {
            lblHinhThucHoanTien.setText(phieuDoiTra.getHinhThucHoanTienText());
        }
        if (phieuDoiTra.getNgayHoanTien() != null) {
            lblNgayHoanTien.setText(phieuDoiTra.getNgayHoanTien().format(formatter));
        }
        if (phieuDoiTra.getSoTaiKhoanHoan() != null) {
            lblSoTaiKhoan.setText(phieuDoiTra.getSoTaiKhoanHoan());
        }
        if (phieuDoiTra.getTenChuTaiKhoan() != null) {
            lblTenChuTaiKhoan.setText(phieuDoiTra.getTenChuTaiKhoan());
        }
        
        // Load chi tiết
        loadChiTietData();
    }
    
    private void loadChiTietData() {
        try {
            List<ChiTietPhieuDoiTra> chiTietList = chiTietDAO.findByPhieuDoiTra(maPhieuDT);
            modelChiTiet.setRowCount(0);
            
            for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
                modelChiTiet.addRow(new Object[]{
                    chiTiet.getMaBienThe(),
                    chiTiet.getTenSanPham(),
                    chiTiet.getTenMauSac(),
                    chiTiet.getTenKichThuoc(),
                    chiTiet.getLoaiChiTiet().equals("DOI") ? "Đổi" : "Trả",
                    chiTiet.getSoLuong(),
                    String.format("%,.0f", chiTiet.getDonGia().doubleValue()),
                    String.format("%,.0f", chiTiet.getThanhTien().doubleValue()),
                    getTinhTrangText(chiTiet.getTinhTrangSanPham()),
                    chiTiet.getGhiChu() != null ? chiTiet.getGhiChu() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getTinhTrangText(String tinhTrang) {
        if (tinhTrang == null) return "";
        switch (tinhTrang) {
            case "MOI": return "Mới";
            case "DA_SU_DUNG": return "Đã sử dụng";
            case "LOI": return "Lỗi";
            case "HONG": return "Hỏng";
            default: return tinhTrang;
        }
    }
    
    private void inPhieu() {
        try {
            // Validate phieu data
            if (phieuDoiTra == null) {
                JOptionPane.showMessageDialog(this, "Không có thông tin phiếu để in!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if phieu has sufficient data
            if (phieuDoiTra.getLyDo() == null || phieuDoiTra.getLyDo().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phiếu chưa có đủ thông tin để in (thiếu lý do)!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create file chooser
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file PDF");

            // Set default filename
            String defaultFileName = String.format("PhieuDoiTra_%d_%s.pdf",
                phieuDoiTra.getMaPhieuDT(),
                phieuDoiTra.getNgayTao().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")));
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));

            // Set file filter for PDF
            javax.swing.filechooser.FileNameExtensionFilter filter =
                new javax.swing.filechooser.FileNameExtensionFilter("PDF files", "pdf");
            fileChooser.setFileFilter(filter);

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                // Ensure file has .pdf extension
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Generate PDF
                util.PDFPhieuDoiTraGenerator.generatePhieuDoiTraPDF(phieuDoiTra, filePath);

                JOptionPane.showMessageDialog(this,
                    "Đã in phiếu đổi trả thành công!\nFile được lưu tại: " + filePath,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

                // Ask if user wants to open the PDF
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn mở file PDF vừa tạo không?",
                    "Mở file",
                    JOptionPane.YES_NO_OPTION);

                if (openFile == JOptionPane.YES_OPTION) {
                    try {
                        java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể mở file PDF. Vui lòng mở thủ công tại: " + filePath,
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tạo file PDF: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi không xác định: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
