package view;

import model.PhieuDoiTra;
import dao.impl.PhieuDoiTraDAO;
import service.impl.PhieuDoiTraServiceImpl;
import service.interfaces.IPhieuDoiTraService;
import util.RoleManager;
import util.SessionManager;
import exception.BusinessException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * UI for Returns & Exchanges Management - Full CRUD implementation
 */
public class PhieuDoiTraUI extends JFrame {
    
    private final PhieuDoiTraDAO phieuDoiTraDAO;
    private final IPhieuDoiTraService phieuDoiTraService;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JComboBox<String> cmbTrangThai;
    private JComboBox<String> cmbLoaiPhieu;
    private JTextField txtTimKiem;
    
    public PhieuDoiTraUI() {
        this.phieuDoiTraDAO = new PhieuDoiTraDAO();
        this.phieuDoiTraService = new PhieuDoiTraServiceImpl();
        
        if (!RoleManager.canAccessReturns()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý đổi trả hàng", "Staff trở lên");
            dispose();
            return;
        }
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setTitle("Quản Lý Đổi Trả Hàng - Phiên bản đầy đủ");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.WEST);
        
        // Table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Footer with buttons
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ ĐỔI TRẢ HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));
        panel.setPreferredSize(new Dimension(200, 0));
        
        // Trạng thái filter
        panel.add(new JLabel("Trạng thái:"));
        cmbTrangThai = new JComboBox<>(new String[]{
            "Tất cả", "PENDING", "APPROVED", "REJECTED", "COMPLETED"
        });
        cmbTrangThai.addActionListener(e -> filterData());
        panel.add(cmbTrangThai);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Loại phiếu filter
        panel.add(new JLabel("Loại phiếu:"));
        cmbLoaiPhieu = new JComboBox<>(new String[]{
            "Tất cả", "DOI", "TRA"
        });
        cmbLoaiPhieu.addActionListener(e -> filterData());
        panel.add(cmbLoaiPhieu);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Tìm kiếm
        panel.add(new JLabel("Tìm kiếm (Mã HĐ):"));
        txtTimKiem = new JTextField();
        txtTimKiem.addActionListener(e -> filterData());
        panel.add(txtTimKiem);
        
        panel.add(Box.createVerticalStrut(10));
        
        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> filterData());
        panel.add(btnTimKiem);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table
        String[] columns = {
            "Mã phiếu", "Mã HĐ", "Loại", "Ngày tạo", "Khách hàng", 
            "Lý do", "Trạng thái", "GT Trả", "GT Đổi", "Hoàn lại", "Bổ sung"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        // Double click to view details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    xemChiTietPhieu();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        // Thêm mới
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> themPhieuDoiTra());
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        panel.add(btnAdd);
        
        // Sửa
        JButton btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(e -> suaPhieuDoiTra());
        btnEdit.setBackground(new Color(255, 193, 7));
        panel.add(btnEdit);
        
        // Xóa
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> xoaPhieuDoiTra());
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        panel.add(btnDelete);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Phê duyệt
        JButton btnApprove = new JButton("Phê duyệt");
        btnApprove.addActionListener(e -> pheDuyetPhieu());
        btnApprove.setBackground(new Color(40, 167, 69));
        btnApprove.setForeground(Color.WHITE);
        if (!RoleManager.canApproveReturns()) {
            btnApprove.setEnabled(false);
        }
        panel.add(btnApprove);
        
        // Từ chối
        JButton btnReject = new JButton("Từ chối");
        btnReject.addActionListener(e -> tuChoiPhieu());
        btnReject.setBackground(new Color(220, 53, 69));
        btnReject.setForeground(Color.WHITE);
        if (!RoleManager.canApproveReturns()) {
            btnReject.setEnabled(false);
        }
        panel.add(btnReject);
        
        // Hoàn thành
        JButton btnComplete = new JButton("Hoàn thành");
        btnComplete.addActionListener(e -> hoanThanhPhieu());
        btnComplete.setBackground(new Color(23, 162, 184));
        btnComplete.setForeground(Color.WHITE);
        if (!RoleManager.canProcessRefunds()) {
            btnComplete.setEnabled(false);
        }
        panel.add(btnComplete);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Xem chi tiết
        JButton btnDetail = new JButton("Xem chi tiết");
        btnDetail.addActionListener(e -> xemChiTietPhieu());
        panel.add(btnDetail);
        
        // Làm mới
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        panel.add(btnRefresh);
        
        // Đóng
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);
        
        return panel;
    }
    
    private void loadData() {
        try {
            List<PhieuDoiTra> list = phieuDoiTraDAO.findAll();
            displayData(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayData(List<PhieuDoiTra> list) {
        tableModel.setRowCount(0);
        
        for (PhieuDoiTra phieu : list) {
            tableModel.addRow(new Object[]{
                phieu.getMaPhieuDT(),
                phieu.getMaHD(),
                phieu.getLoaiPhieu().equals("DOI") ? "Đổi" : "Trả",
                phieu.getNgayTao(),
                phieu.getTenKhachHang() != null ? phieu.getTenKhachHang() : "Khách lẻ",
                phieu.getLyDo().length() > 30 ? phieu.getLyDo().substring(0, 30) + "..." : phieu.getLyDo(),
                getTrangThaiText(phieu.getTrangThai()),
                String.format("%,.0f", phieu.getTongGiaTriTra()),
                String.format("%,.0f", phieu.getTongGiaTriDoi()),
                String.format("%,.0f", phieu.getSoTienHoanLai()),
                String.format("%,.0f", phieu.getSoTienBoSung())
            });
        }
    }
    
    private String getTrangThaiText(String trangThai) {
        switch (trangThai) {
            case "PENDING": return "Chờ xử lý";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Từ chối";
            case "COMPLETED": return "Hoàn thành";
            default: return trangThai;
        }
    }
    
    private void filterData() {
        try {
            List<PhieuDoiTra> list;
            
            String trangThai = (String) cmbTrangThai.getSelectedItem();
            String loaiPhieu = (String) cmbLoaiPhieu.getSelectedItem();
            String timKiem = txtTimKiem.getText().trim();
            
            // Apply filters
            if (!"Tất cả".equals(trangThai)) {
                list = phieuDoiTraService.layPhieuTheoTrangThai(trangThai);
            } else if (!"Tất cả".equals(loaiPhieu)) {
                list = phieuDoiTraService.layPhieuTheoLoai(loaiPhieu);
            } else if (!timKiem.isEmpty()) {
                try {
                    int maHD = Integer.parseInt(timKiem);
                    list = phieuDoiTraService.layPhieuTheoHoaDon(maHD);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Mã hóa đơn phải là số", 
                                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                list = phieuDoiTraService.layDanhSachPhieu();
            }
            
            displayData(list);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc dữ liệu: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void themPhieuDoiTra() {
        PhieuDoiTraFormUI formUI = new PhieuDoiTraFormUI(this);
        formUI.setVisible(true);
        
        if (formUI.isDataSaved()) {
            loadData(); // Reload table data
        }
    }
    
    private void suaPhieuDoiTra() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần sửa", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String trangThai = (String) tableModel.getValueAt(selectedRow, 6);
        if (!"Chờ xử lý".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể sửa phiếu ở trạng thái chờ xử lý", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maPhieuDT = (Integer) tableModel.getValueAt(selectedRow, 0);
            java.util.Optional<PhieuDoiTra> optional = phieuDoiTraService.layPhieuTheoId(maPhieuDT);
            
            if (optional.isPresent()) {
                PhieuDoiTraFormUI formUI = new PhieuDoiTraFormUI(this, optional.get());
                formUI.setVisible(true);
                
                if (formUI.isDataSaved()) {
                    loadData(); // Reload table data
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đổi trả", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mở form sửa: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaPhieuDoiTra() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xóa", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maPhieuDT = (Integer) tableModel.getValueAt(selectedRow, 0);
            String trangThai = (String) tableModel.getValueAt(selectedRow, 6);
            
            if (!"Chờ xử lý".equals(trangThai)) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể xóa phiếu ở trạng thái chờ xử lý", 
                                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn xóa phiếu đổi trả này?", 
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                phieuDoiTraService.xoaPhieuDoiTra(maPhieuDT);
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa phiếu đổi trả thành công!", 
                                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa phiếu: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void pheDuyetPhieu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần phê duyệt", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maPhieuDT = (Integer) tableModel.getValueAt(selectedRow, 0);
            String trangThai = (String) tableModel.getValueAt(selectedRow, 6);
            
            if (!"Chờ xử lý".equals(trangThai)) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể phê duyệt phiếu ở trạng thái chờ xử lý", 
                                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String ghiChu = JOptionPane.showInputDialog(this, "Ghi chú phê duyệt (không bắt buộc):");
            if (ghiChu == null) return;
            
            int nguoiDuyet = SessionManager.getInstance().getCurrentUser().getMaNV().getId();
            phieuDoiTraService.pheDuyetPhieu(maPhieuDT, nguoiDuyet, ghiChu);
            
            loadData();
            JOptionPane.showMessageDialog(this, "Phê duyệt phiếu đổi trả thành công!", 
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi phê duyệt: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tuChoiPhieu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần từ chối", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maPhieuDT = (Integer) tableModel.getValueAt(selectedRow, 0);
            String trangThai = (String) tableModel.getValueAt(selectedRow, 6);
            
            if (!"Chờ xử lý".equals(trangThai)) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể từ chối phiếu ở trạng thái chờ xử lý", 
                                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String lyDoTuChoi = JOptionPane.showInputDialog(this, "Lý do từ chối:");
            if (lyDoTuChoi == null || lyDoTuChoi.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do từ chối", 
                                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int nguoiDuyet = SessionManager.getInstance().getCurrentUser().getMaNV().getId();
            phieuDoiTraService.tuChoiPhieu(maPhieuDT, nguoiDuyet, lyDoTuChoi);
            
            loadData();
            JOptionPane.showMessageDialog(this, "Từ chối phiếu đổi trả thành công!", 
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi từ chối: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hoanThanhPhieu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần hoàn thành", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String trangThai = (String) tableModel.getValueAt(selectedRow, 6);
        if (!"Đã duyệt".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hoàn thành phiếu đã được phê duyệt", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maPhieuDT = (Integer) tableModel.getValueAt(selectedRow, 0);
            PhieuDoiTraCompleteUI completeUI = new PhieuDoiTraCompleteUI(this, maPhieuDT);
            completeUI.setVisible(true);
            
            if (completeUI.isDataSaved()) {
                loadData(); // Reload table data
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mở form hoàn thành: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xemChiTietPhieu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xem", 
                                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maPhieuDT = (Integer) tableModel.getValueAt(selectedRow, 0);
            PhieuDoiTraDetailUI detailUI = new PhieuDoiTraDetailUI(this, maPhieuDT);
            detailUI.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mở form chi tiết: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
