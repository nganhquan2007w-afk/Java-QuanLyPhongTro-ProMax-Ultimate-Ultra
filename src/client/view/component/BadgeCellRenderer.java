package client.view.component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

@SuppressWarnings("serial")
public final class BadgeCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        String text = (value != null) ? value.toString() : "";
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        container.setOpaque(true);
        container.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        label.setOpaque(false);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // Gán màu sắc động dựa trên trạng thái thực tế
        if (text.equalsIgnoreCase("Đã thanh toán") || text.equalsIgnoreCase("Còn hiệu lực") || text.equalsIgnoreCase("Đã hoàn thành") || text.equalsIgnoreCase("Đang trống") || text.equalsIgnoreCase("Còn trống")) {
            label.setText("● " + text + "  ");
            label.setBackground(new Color(220, 252, 231)); // Emerald 100
            label.setForeground(new Color(5, 150, 105));   // Emerald 600
        } else if (text.equalsIgnoreCase("Chưa thanh toán") || text.equalsIgnoreCase("Hết hiệu lực") || text.equalsIgnoreCase("Từ chối") || text.equalsIgnoreCase("Hết hạn") || text.equalsIgnoreCase("Đã hủy") || text.equalsIgnoreCase("Đã đầy") || text.equalsIgnoreCase("Đầy")) {
            label.setText("● " + text + "  ");
            label.setBackground(new Color(254, 226, 226)); // Red 100
            label.setForeground(new Color(220, 38, 38));   // Red 600
        } else if (text.equalsIgnoreCase("Sắp hết hạn") || text.equalsIgnoreCase("Trống") || text.equalsIgnoreCase("Chờ xử lý") || text.equalsIgnoreCase("Chưa duyệt")) {
            label.setText("● " + text + "  ");
            label.setBackground(new Color(254, 240, 138)); // Yellow 200
            label.setForeground(new Color(161, 98, 7));    // Yellow 700
        } else if (text.equalsIgnoreCase("Đang sửa chữa") || text.equalsIgnoreCase("Bảo trì") || text.equalsIgnoreCase("Đang thuê") || text.equalsIgnoreCase("Đã thuê") || text.equalsIgnoreCase("Đang ở")) {
            label.setText("● " + text + "  ");
            label.setBackground(new Color(219, 234, 254)); // Blue 100
            label.setForeground(new Color(37, 99, 235));   // Blue 600
        } else {
            label.setText("● " + text + "  ");
            label.setBackground(new Color(241, 245, 249)); // Slate 100
            label.setForeground(new Color(71, 85, 105));   // Slate 600
        }

        container.add(label);
        return container;
    }


// <editor-fold defaultstate="collapsed" desc="Generated Code">
private void initComponents() {

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 300, Short.MAX_VALUE)
    );
}// </editor-fold>


// Variables declaration - do not modify
// End of variables declaration
}
