package view;

import model.TheThanThiet;
import model.LichSuDiem;
import model.KhachHang;
import dao.impl.TheThanThietDAO;
import dao.impl.LichSuDiemDAO;
import dao.impl.KhachHangDAO;
import util.RoleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * UI for Loyalty Card Management - Basic implementation
 */
public class TheThanThietUI extends JFrame {
    
    private TheThanThietDAO theThanThietDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    
    public TheThanThietUI() {
        if (!RoleManager.canAccessLoyalty()) {
            RoleManager.showAccessDeniedMessage(this, "Quản lý khách hàng thân thiết", "Staff trở lên");
            dispose();
            return;
        }
        
        this.theThanThietDAO = new TheThanThietDAO();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setTitle("Quản Lý Khách Hàng Thân Thiết");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("QUẢN LÝ KHÁCH HÀNG THÂN THIẾT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Mã thẻ", "Mã KH", "Số thẻ", "Loại thẻ", "Điểm tích lũy", "Trạng thái", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with buttons
        JPanel footerPanel = new JPanel(new FlowLayout());
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        footerPanel.add(btnRefresh);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        footerPanel.add(closeButton);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadData() {
        try {
            List<TheThanThiet> list = theThanThietDAO.findAll();
            tableModel.setRowCount(0);
            
            for (TheThanThiet the : list) {
                tableModel.addRow(new Object[]{
                    the.getMaThe(),
                    the.getMaKH(),
                    the.getSoThe(),
                    the.getLoaiThe(),
                    the.getDiemTichLuy(),
                    the.getTrangThai(),
                    the.getNgayTao()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
