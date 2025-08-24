package view;

import model.PhieuDoiTra;
import service.interfaces.IPhieuDoiTraService;
import service.impl.PhieuDoiTraServiceImpl;
import exception.BusinessException;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Optional;

/**
 * Form để hoàn thành xử lý phiếu đổi trả
 */
public class PhieuDoiTraCompleteUI extends JDialog {
    
    private final IPhieuDoiTraService phieuDoiTraService;
    private int maPhieuDT;
    private PhieuDoiTra phieuDoiTra;
    
    // Components
    private JLabel lblMaPhieu;
    private JLabel lblLoaiPhieu;
    private JLabel lblTongGiaTriTra;
    private JLabel lblTongGiaTriDoi;
    private JLabel lblSoTienHoanLai;
    private JLabel lblSoTienBoSung;
    
    private JComboBox<String> cmbHinhThucHoanTien;
    private JPanel pnlThongTinTaiKhoan;
    private JTextField txtSoTaiKhoan;
    private JTextField txtTenChuTaiKhoan;
    private JTextField txtNganHang;
    private JTextArea txtGhiChuHoanTien;
    
    private boolean dataSaved = false;
    
    public PhieuDoiTraCompleteUI(JFrame parent, int maPhieuDT) {
        super(parent, "Hoàn Thành Phiếu Đổi Trả", true);
        this.maPhieuDT = maPhieuDT;
        this.phieuDoiTraService = new PhieuDoiTraServiceImpl();
        
        loadData();
        initComponents();
        setupEventHandlers();
    }
    
    private void loadData() {
        try {
            Optional<PhieuDoiTra> optional = phieuDoiTraService.layPhieuTheoId(maPhieuDT);
            if (optional.isPresent()) {
                phieuDoiTra = optional.get();
                
                // Kiểm tra trạng thái phiếu
                if (!"APPROVED".equals(phieuDoiTra.getTrangThai())) {
                    JOptionPane.showMessageDialog(this, 
                        "Chỉ có thể hoàn thành phiếu đã được phê duyệt", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    dispose();
                    return;
                }
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
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        populateData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("HOÀN THÀNH PHIẾU ĐỔI TRẢ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Thông tin phiếu
        JPanel infoPanel = createInfoPanel();
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Form hoàn tiền
        JPanel refundPanel = createRefundPanel();
        panel.add(refundPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mã phiếu
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã phiếu:"), gbc);
        gbc.gridx = 1;
        lblMaPhieu = new JLabel();
        lblMaPhieu.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblMaPhieu, gbc);
        
        // Loại phiếu
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Loại phiếu:"), gbc);
        gbc.gridx = 1;
        lblLoaiPhieu = new JLabel();
        panel.add(lblLoaiPhieu, gbc);
        
        // Tổng giá trị trả
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tổng giá trị trả:"), gbc);
        gbc.gridx = 1;
        lblTongGiaTriTra = new JLabel();
        lblTongGiaTriTra.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongGiaTriTra.setForeground(new Color(0, 128, 0));
        panel.add(lblTongGiaTriTra, gbc);
        
        // Tổng giá trị đổi
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tổng giá trị đổi:"), gbc);
        gbc.gridx = 1;
        lblTongGiaTriDoi = new JLabel();
        lblTongGiaTriDoi.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongGiaTriDoi.setForeground(new Color(255, 140, 0));
        panel.add(lblTongGiaTriDoi, gbc);
        
        // Số tiền hoàn lại
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Số tiền hoàn lại:"), gbc);
        gbc.gridx = 1;
        lblSoTienHoanLai = new JLabel();
        lblSoTienHoanLai.setFont(new Font("Arial", Font.BOLD, 14));
        lblSoTienHoanLai.setForeground(new Color(0, 128, 0));
        panel.add(lblSoTienHoanLai, gbc);
        
        // Số tiền bổ sung
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Số tiền bổ sung:"), gbc);
        gbc.gridx = 1;
        lblSoTienBoSung = new JLabel();
        lblSoTienBoSung.setFont(new Font("Arial", Font.BOLD, 14));
        lblSoTienBoSung.setForeground(new Color(220, 53, 69));
        panel.add(lblSoTienBoSung, gbc);
        
        return panel;
    }
    
    private JPanel createRefundPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin hoàn tiền"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Hình thức hoàn tiền
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Hình thức hoàn tiền:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbHinhThucHoanTien = new JComboBox<>(new String[]{
            "CASH", "BANK_TRANSFER", "CREDIT_NOTE"
        });
        // Custom renderer to show Vietnamese text
        cmbHinhThucHoanTien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    String text = value.toString();
                    switch (text) {
                        case "CASH":
                            setText("Tiền mặt");
                            break;
                        case "BANK_TRANSFER":
                            setText("Chuyển khoản");
                            break;
                        case "CREDIT_NOTE":
                            setText("Ghi có tài khoản");
                            break;
                        default:
                            setText(text);
                    }
                }
                return c;
            }
        });
        formPanel.add(cmbHinhThucHoanTien, gbc);
        
        gbc.gridy++; gbc.fill = GridBagConstraints.NONE;
        
        // Panel thông tin tài khoản (chỉ hiện khi chọn chuyển khoản)
        gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlThongTinTaiKhoan = createAccountInfoPanel();
        pnlThongTinTaiKhoan.setVisible(false);
        formPanel.add(pnlThongTinTaiKhoan, gbc);
        
        gbc.gridy++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        
        // Ghi chú
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        txtGhiChuHoanTien = new JTextArea(3, 20);
        txtGhiChuHoanTien.setLineWrap(true);
        txtGhiChuHoanTien.setWrapStyleWord(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChuHoanTien);
        formPanel.add(scrollGhiChu, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAccountInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Số tài khoản
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Số tài khoản:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSoTaiKhoan = new JTextField(20);
        panel.add(txtSoTaiKhoan, gbc);
        
        // Tên chủ tài khoản
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Tên chủ tài khoản:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTenChuTaiKhoan = new JTextField(20);
        panel.add(txtTenChuTaiKhoan, gbc);
        
        // Ngân hàng
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Ngân hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNganHang = new JTextField(20);
        panel.add(txtNganHang, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnHoanThanh = new JButton("Hoàn thành");
        btnHoanThanh.setBackground(new Color(40, 167, 69));
        btnHoanThanh.setForeground(Color.WHITE);
        btnHoanThanh.setFont(new Font("Arial", Font.BOLD, 12));
        btnHoanThanh.addActionListener(e -> hoanThanhPhieu());
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        
        panel.add(btnHoanThanh);
        panel.add(btnHuy);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        cmbHinhThucHoanTien.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selected = (String) e.getItem();
                    boolean needAccountInfo = "BANK_TRANSFER".equals(selected);
                    pnlThongTinTaiKhoan.setVisible(needAccountInfo);
                    pack(); // Resize dialog to fit content
                }
            }
        });
    }
    
    private void populateData() {
        if (phieuDoiTra == null) return;
        
        lblMaPhieu.setText("#" + phieuDoiTra.getMaPhieuDT());
        lblLoaiPhieu.setText(phieuDoiTra.getLoaiPhieuText());
        lblTongGiaTriTra.setText(String.format("%,.0f VNĐ", phieuDoiTra.getTongGiaTriTra().doubleValue()));
        lblTongGiaTriDoi.setText(String.format("%,.0f VNĐ", phieuDoiTra.getTongGiaTriDoi().doubleValue()));
        lblSoTienHoanLai.setText(String.format("%,.0f VNĐ", phieuDoiTra.getSoTienHoanLai().doubleValue()));
        lblSoTienBoSung.setText(String.format("%,.0f VNĐ", phieuDoiTra.getSoTienBoSung().doubleValue()));
        
        // Hiển thị thông báo nếu có số tiền bổ sung
        if (phieuDoiTra.getSoTienBoSung().doubleValue() > 0) {
            JLabel lblThongBao = new JLabel("* Khách hàng cần thanh toán thêm số tiền bổ sung");
            lblThongBao.setForeground(Color.RED);
            lblThongBao.setFont(new Font("Arial", Font.ITALIC, 11));
        }
        
        // Mặc định chọn tiền mặt nếu chỉ có hoàn tiền
        if (phieuDoiTra.getSoTienHoanLai().doubleValue() > 0 && 
            phieuDoiTra.getSoTienBoSung().doubleValue() == 0) {
            cmbHinhThucHoanTien.setSelectedItem("CASH");
        }
    }
    
    private void hoanThanhPhieu() {
        try {
            // Validate
            String hinhThucHoanTien = (String) cmbHinhThucHoanTien.getSelectedItem();
            
            if ("BANK_TRANSFER".equals(hinhThucHoanTien)) {
                if (txtSoTaiKhoan.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập số tài khoản", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtSoTaiKhoan.requestFocus();
                    return;
                }
                
                if (txtTenChuTaiKhoan.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chủ tài khoản", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtTenChuTaiKhoan.requestFocus();
                    return;
                }
            }
            
            // Xác nhận
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn hoàn thành phiếu đổi trả này?\n" +
                "Sau khi hoàn thành sẽ không thể chỉnh sửa.", 
                "Xác nhận hoàn thành", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Thực hiện hoàn thành
            String soTaiKhoan = "BANK_TRANSFER".equals(hinhThucHoanTien) ? 
                txtSoTaiKhoan.getText().trim() : null;
            String tenChuTaiKhoan = "BANK_TRANSFER".equals(hinhThucHoanTien) ? 
                txtTenChuTaiKhoan.getText().trim() : null;
            
            phieuDoiTraService.hoanThanhPhieu(maPhieuDT, hinhThucHoanTien, 
                soTaiKhoan, tenChuTaiKhoan);
            
            dataSaved = true;
            
            // Thông báo thành công
            String message = "Hoàn thành phiếu đổi trả thành công!";
            if (phieuDoiTra.getSoTienHoanLai().doubleValue() > 0) {
                message += "\n\nSố tiền hoàn lại: " + 
                    String.format("%,.0f VNĐ", phieuDoiTra.getSoTienHoanLai().doubleValue());
                
                if ("BANK_TRANSFER".equals(hinhThucHoanTien)) {
                    message += "\nHình thức: Chuyển khoản";
                    message += "\nSố TK: " + soTaiKhoan;
                    message += "\nChủ TK: " + tenChuTaiKhoan;
                } else if ("CASH".equals(hinhThucHoanTien)) {
                    message += "\nHình thức: Tiền mặt";
                } else {
                    message += "\nHình thức: Ghi có tài khoản";
                }
            }
            
            if (phieuDoiTra.getSoTienBoSung().doubleValue() > 0) {
                message += "\n\nSố tiền khách cần thanh toán thêm: " + 
                    String.format("%,.0f VNĐ", phieuDoiTra.getSoTienBoSung().doubleValue());
            }
            
            JOptionPane.showMessageDialog(this, message, 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isDataSaved() {
        return dataSaved;
    }
}
