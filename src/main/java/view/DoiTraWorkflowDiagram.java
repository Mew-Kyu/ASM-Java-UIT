package view;

import javax.swing.*;
import java.awt.*;

/**
 * Class tạo sơ đồ quy trình đổi trả hàng
 */
public class DoiTraWorkflowDiagram {
    
    public static void showWorkflowDiagram(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Quy trình đổi trả hàng", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawWorkflow(g);
            }
        };
        
        panel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(panel);
        dialog.add(scrollPane);
        
        JPanel buttonPanel = new JPanel();
        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private static void drawWorkflow(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.BLACK);
        g2d.drawString("QUY TRÌNH ĐỔI TRẢ HÀNG", 250, 30);
        
        // Step boxes
        int boxWidth = 150;
        int boxHeight = 60;
        int startX = 50;
        int startY = 60;
        int stepY = 100;
        
        // Step 1: Tạo phiếu
        drawStep(g2d, startX, startY, boxWidth, boxHeight, 
                "1. TẠO PHIẾU", "Staff tạo phiếu đổi/trả", new Color(173, 216, 230));
        
        // Step 2: Kiểm tra
        drawStep(g2d, startX + 200, startY, boxWidth, boxHeight,
                "2. KIỂM TRA", "Validate hóa đơn & điều kiện", new Color(255, 255, 224));
        
        // Step 3: Phê duyệt
        drawStep(g2d, startX + 400, startY, boxWidth, boxHeight,
                "3. PHÊ DUYỆT", "Manager phê duyệt/từ chối", new Color(255, 218, 185));
        
        // Step 4: Xử lý
        drawStep(g2d, startX + 600, startY, boxWidth, boxHeight,
                "4. XỬ LÝ", "Hoàn tiền & cập nhật kho", new Color(144, 238, 144));
        
        // Arrows
        drawArrow(g2d, startX + boxWidth, startY + boxHeight/2, 
                 startX + 200, startY + boxHeight/2);
        drawArrow(g2d, startX + 200 + boxWidth, startY + boxHeight/2,
                 startX + 400, startY + boxHeight/2);
        drawArrow(g2d, startX + 400 + boxWidth, startY + boxHeight/2,
                 startX + 600, startY + boxHeight/2);
        
        // Status flow
        int statusY = startY + stepY + 50;
        
        // PENDING
        drawStatusBox(g2d, startX, statusY, "PENDING", "Chờ xử lý", Color.YELLOW);
        
        // APPROVED/REJECTED
        drawStatusBox(g2d, startX + 250, statusY, "APPROVED", "Đã duyệt", Color.GREEN);
        drawStatusBox(g2d, startX + 250, statusY + 80, "REJECTED", "Từ chối", Color.RED);
        
        // COMPLETED
        drawStatusBox(g2d, startX + 500, statusY, "COMPLETED", "Hoàn thành", Color.BLUE);
        
        // Status arrows
        drawArrow(g2d, startX + 120, statusY + 25, startX + 250, statusY + 25);
        drawArrow(g2d, startX + 120, statusY + 25, startX + 250, statusY + 80 + 25);
        drawArrow(g2d, startX + 250 + 120, statusY + 25, startX + 500, statusY + 25);
        
        // Legend
        int legendY = statusY + 150;
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("QUYỀN TRUY CẬP:", startX, legendY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("• Staff: Tạo, sửa, xóa phiếu PENDING", startX, legendY + 25);
        g2d.drawString("• Manager: Phê duyệt, từ chối, hoàn thành", startX, legendY + 45);
        g2d.drawString("• Admin: Tất cả quyền", startX, legendY + 65);
        
        // Rules
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("QUY TẮC NGHIỆP VỤ:", startX + 400, legendY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("• Chỉ đổi trả trong 30 ngày", startX + 400, legendY + 25);
        g2d.drawString("• Hóa đơn phải đã thanh toán", startX + 400, legendY + 45);
        g2d.drawString("• Tự động tính toán giá trị", startX + 400, legendY + 65);
        g2d.drawString("• Cập nhật tồn kho real-time", startX + 400, legendY + 85);
    }
    
    private static void drawStep(Graphics2D g2d, int x, int y, int width, int height,
                                String title, String desc, Color bgColor) {
        // Background
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, width, height, 10, 10);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, x + (width - titleWidth) / 2, y + 20);
        
        // Description
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        fm = g2d.getFontMetrics();
        int descWidth = fm.stringWidth(desc);
        g2d.drawString(desc, x + (width - descWidth) / 2, y + 40);
    }
    
    private static void drawStatusBox(Graphics2D g2d, int x, int y, String status, String desc, Color color) {
        int width = 100;
        int height = 50;
        
        // Background
        g2d.setColor(color);
        g2d.fillOval(x, y, width, height);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y, width, height);
        
        // Text
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int statusWidth = fm.stringWidth(status);
        g2d.drawString(status, x + (width - statusWidth) / 2, y + 20);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        fm = g2d.getFontMetrics();
        int descWidth = fm.stringWidth(desc);
        g2d.drawString(desc, x + (width - descWidth) / 2, y + 35);
    }
    
    private static void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x1, y1, x2, y2);
        
        // Arrow head
        int arrowLength = 10;
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
        int x3 = (int) (x2 - arrowLength * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - arrowLength * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (x2 - arrowLength * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - arrowLength * Math.sin(angle + Math.PI / 6));
        
        g2d.drawLine(x2, y2, x3, y3);
        g2d.drawLine(x2, y2, x4, y4);
    }
}
