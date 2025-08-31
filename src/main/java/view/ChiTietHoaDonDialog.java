package view;

import controller.ChiTietHoaDonController;
import controller.HoaDonController;
import model.ChiTietHoaDon;
import model.HoaDon;
import util.PDFInvoiceGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChiTietHoaDonDialog extends JDialog {
    private HoaDon hoaDon;
    private ChiTietHoaDonController controller;
    private HoaDonController hoaDonController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblHoaDonInfo;
    private JButton btnClose, btnDelete, btnRefresh, btnPrintPDF, btnAddProduct;
    private JFrame parentFrame;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ChiTietHoaDonDialog(JFrame parent, HoaDon hoaDon) {
        super(parent, "Chi Tiết Hóa Đơn #" + hoaDon.getId(), true);
        this.hoaDon = hoaDon;
        this.parentFrame = parent;
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons with basic colors and no icons
        btnAddProduct = createStyledButton("Thêm sản phẩm", Color.WHITE, Color.BLACK, 160);
        btnDelete = createStyledButton("Xóa sản phẩm", Color.LIGHT_GRAY, Color.BLACK, 150);
        btnRefresh = createStyledButton("Làm mới", Color.GRAY, Color.WHITE, 120);
        btnPrintPDF = createStyledButton("In PDF", Color.DARK_GRAY, Color.WHITE, 120);
        btnClose = createStyledButton("Đóng", Color.BLACK, Color.WHITE, 100);

        // Add buttons to panel with proper spacing
        panel.add(btnAddProduct);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnDelete);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(btnRefresh);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnPrintPDF);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(btnClose);

        return panel;
    }

    // Helper method to create consistently styled buttons
    private JButton createStyledButton(String text, Color backgroundColor, Color textColor, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 35));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add simple hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalBackground = backgroundColor;
            private Color originalText = textColor;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (backgroundColor == Color.WHITE) {
                    button.setBackground(new Color(240, 240, 240));
                } else if (backgroundColor == Color.LIGHT_GRAY) {
                    button.setBackground(new Color(200, 200, 200));
                } else if (backgroundColor == Color.GRAY) {
                    button.setBackground(new Color(100, 100, 100));
                } else if (backgroundColor == Color.DARK_GRAY) {
                    button.setBackground(new Color(60, 60, 60));
                } else if (backgroundColor == Color.BLACK) {
                    button.setBackground(new Color(40, 40, 40));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBackground);
                button.setForeground(originalText);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (backgroundColor == Color.WHITE) {
                    button.setBackground(new Color(220, 220, 220));
                } else if (backgroundColor == Color.LIGHT_GRAY) {
                    button.setBackground(new Color(180, 180, 180));
                } else if (backgroundColor == Color.GRAY) {
                    button.setBackground(new Color(80, 80, 80));
                } else if (backgroundColor == Color.DARK_GRAY) {
                    button.setBackground(new Color(40, 40, 40));
                } else if (backgroundColor == Color.BLACK) {
                    button.setBackground(new Color(20, 20, 20));
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBackground);
            }
        });

        return button;
    }

    private void setupEventHandlers() {
        btnClose.addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> deleteChiTiet());
        btnPrintPDF.addActionListener(e -> printInvoiceToPDF());
        btnAddProduct.addActionListener(e -> addProductToInvoice());

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

        // Check if there are any items to delete
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có sản phẩm nào để xóa!");
            return;
        }

        try {
            List<ChiTietHoaDon> details = controller.getByHoaDonId(hoaDon.getId());
            if (selectedRow < details.size()) {
                ChiTietHoaDon selectedDetail = details.get(selectedRow);

                // Show product info in confirmation dialog
                String productInfo = String.format(
                    "Sản phẩm: %s\nMàu sắc: %s\nSize: %s\nSố lượng: %d\nĐơn giá: %,.0f VNĐ",
                    selectedDetail.getMaBienThe().getMaSP().getTenSP(),
                    selectedDetail.getMaBienThe().getMaMau().getTenMau(),
                    selectedDetail.getMaBienThe().getMaSize().getTenSize(),
                    selectedDetail.getSoLuong(),
                    selectedDetail.getDonGia()
                );

                int confirm = JOptionPane.showConfirmDialog(this,
                    productInfo + "\n\nBạn có chắc muốn xóa sản phẩm này khỏi hóa đơn?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Delete from database first
                    controller.delete(selectedDetail.getId());

                    // Remove from the entity's collection to keep it in sync
                    hoaDon.getChiTietHoaDons().removeIf(ct ->
                        ct.getId().equals(selectedDetail.getId()));

                    // Recalculate and update invoice total
                    hoaDon.calculateTongTien();
                    hoaDonController.updateHoaDon(hoaDon);

                    // Refresh the invoice object from database to ensure consistency
                    hoaDon = hoaDonController.getHoaDonByIdWithDetails(hoaDon.getId());

                    JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm khỏi hóa đơn!");
                    loadData();
                    refreshParentWindow();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm được chọn!");
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

                        // Update the entity's collection to keep it in sync
                        hoaDon.getChiTietHoaDons().stream()
                            .filter(ct -> ct.getId().equals(selectedDetail.getId()))
                            .findFirst()
                            .ifPresent(ct -> ct.setSoLuong(newQuantity));

                        // Recalculate and update invoice total
                        hoaDon.calculateTongTien();
                        hoaDonController.updateHoaDon(hoaDon);

                        // Refresh the invoice object from database to ensure consistency
                        hoaDon = hoaDonController.getHoaDonByIdWithDetails(hoaDon.getId());

                        JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng!");
                        loadData();
                        refreshParentWindow();
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

    private void printInvoiceToPDF() {
        try {
            // Open file chooser for save location
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
            fileChooser.setSelectedFile(new File("HoaDon_" + hoaDon.getId() + ".pdf"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                // Ensure file has .pdf extension
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Generate PDF
                PDFInvoiceGenerator.generateInvoicePDF(hoaDon, filePath);

                JOptionPane.showMessageDialog(this,
                    "Đã in hóa đơn thành công!\nFile được lưu tại: " + filePath,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

                // Ask if user wants to open the PDF
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn mở file PDF vừa tạo không?",
                    "Mở file",
                    JOptionPane.YES_NO_OPTION);

                if (openFile == JOptionPane.YES_OPTION) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(new File(filePath));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể mở file PDF. Vui lòng mở thủ công tại: " + filePath,
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProductToInvoice() {
        try {
            // Open product selection dialog
            ThemSanPhamDialog dialog = new ThemSanPhamDialog(this, hoaDon);
            dialog.setVisible(true);

            // Refresh the invoice object from database to get updated details
            hoaDon = hoaDonController.getHoaDonByIdWithDetails(hoaDon.getId());

            // Refresh the data in the table and update the invoice info
            loadData();
            refreshParentWindow();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm: " + ex.getMessage());
        }
    }

    /**
     * Refresh parent window if it's HoaDonUI to update the invoice list
     */
    private void refreshParentWindow() {
        if (parentFrame instanceof view.HoaDonUI) {
            // Call refresh method on parent if available
            try {
                java.lang.reflect.Method refreshMethod = parentFrame.getClass().getDeclaredMethod("loadHoaDonTable");
                refreshMethod.setAccessible(true);
                refreshMethod.invoke(parentFrame);
            } catch (Exception e) {
                // Silently ignore if refresh method is not available
                System.out.println("Could not refresh parent window: " + e.getMessage());
            }
        }
    }
}
