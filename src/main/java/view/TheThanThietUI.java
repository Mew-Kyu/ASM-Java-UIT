package view;

import model.TheThanThiet;
import dao.impl.TheThanThietDAO;
import util.RoleManager;
import util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI for Loyalty Card Management - Basic implementation
 */
public class TheThanThietUI extends JFrame {
    
    private TheThanThietDAO theThanThietDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private List<TheThanThiet> allThe = new ArrayList<>();
    private List<TheThanThiet> filteredThe = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 30;
    private int totalPages = 1;
    private JLabel lblPageInfo;
    private JButton btnFirstPage, btnPrevPage, btnNextPage, btnLastPage;
    private JComboBox<Integer> cboPageSize;

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

        // Footer with buttons & pagination
        JPanel footerPanel = new JPanel(new BorderLayout());
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> { loadData(); });
        actionPanel.add(btnRefresh);
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        actionPanel.add(closeButton);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnFirstPage = new JButton("|<");
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        btnLastPage = new JButton(">|");
        lblPageInfo = new JLabel("Page 1/1 (0 records)");
        cboPageSize = new JComboBox<>(new Integer[]{10,30,50,100});
        cboPageSize.setSelectedItem(30);
        paginationPanel.add(new JLabel("Rows:"));
        paginationPanel.add(cboPageSize);
        paginationPanel.add(btnFirstPage);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNextPage);
        paginationPanel.add(btnLastPage);

        footerPanel.add(actionPanel, BorderLayout.WEST);
        footerPanel.add(paginationPanel, BorderLayout.EAST);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Pagination listeners
        btnFirstPage.addActionListener(e -> { if(currentPage!=1){currentPage=1; renderCurrentPage();}});
        btnPrevPage.addActionListener(e -> { if(currentPage>1){currentPage--; renderCurrentPage();}});
        btnNextPage.addActionListener(e -> { if(currentPage< totalPages){currentPage++; renderCurrentPage();}});
        btnLastPage.addActionListener(e -> { if(currentPage!= totalPages){currentPage= totalPages; renderCurrentPage();}});
        cboPageSize.addActionListener(e -> { Integer sel=(Integer)cboPageSize.getSelectedItem(); if(sel!=null && sel!=pageSize){pageSize=sel; currentPage=1; recalcPagination(); renderCurrentPage();}});
    }

    private void loadData() {
        try {
            allThe = theThanThietDAO.findAll();
            filteredThe = new ArrayList<>(allThe);
            currentPage = 1;
            recalcPagination();
            renderCurrentPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recalcPagination(){
        int total = filteredThe.size();
        totalPages = total==0?1:(int)Math.ceil(total/(double)pageSize);
        if(currentPage>totalPages) currentPage= totalPages;
        updatePaginationControls();
    }
    private void updatePaginationControls(){
        if(lblPageInfo!=null){
            lblPageInfo.setText("Page "+currentPage+"/"+totalPages+" ("+filteredThe.size()+" records)");
            btnFirstPage.setEnabled(currentPage>1);
            btnPrevPage.setEnabled(currentPage>1);
            btnNextPage.setEnabled(currentPage< totalPages);
            btnLastPage.setEnabled(currentPage< totalPages);
        }
    }
    private void renderCurrentPage(){
        tableModel.setRowCount(0);
        if(filteredThe.isEmpty()){ updatePaginationControls(); return; }
        int start = (currentPage-1)*pageSize;
        int end = Math.min(start+pageSize, filteredThe.size());
        for(int i=start;i<end;i++){
            TheThanThiet the = filteredThe.get(i);
            tableModel.addRow(new Object[]{
                the.getMaThe(),
                the.getMaKH(),
                the.getSoThe(),
                the.getLoaiThe(),
                the.getDiemTichLuy(),
                the.getTrangThai(),
                DateUtils.formatDate(the.getNgayTao())
            });
        }
        updatePaginationControls();
    }
}
