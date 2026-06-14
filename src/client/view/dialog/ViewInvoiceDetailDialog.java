package client.view.dialog;

import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ViewInvoiceDetailDialog - Premium dialog for viewing invoice billing details.
 */
@SuppressWarnings("serial")
public class ViewInvoiceDetailDialog extends JDialog {
    
    public ViewInvoiceDetailDialog(Frame owner, String id, String room, String tenant, String date, String deadline, String status, String paymentDate, long rentPrice, double elecKwh, double waterM3, long otherPrice, long elecUnitPrice, long waterUnitPrice) {
        super(owner, "Chi Tiết Hóa Đơn - " + id, true);
        setSize(480, 640);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(25, 30, 25, 30));
        container.setBackground(Color.WHITE);

        // Header Title
        JLabel lblTitle = new JLabel("CHI TIẾT HÓA ĐƠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42)); // Slate 900
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);

        JLabel lblSub = new JLabel("Mã Hóa Đơn: " + id);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139)); // Slate 500
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblSub);
        
        container.add(Box.createVerticalStrut(20));

        // Group 1: General Info Card
        RoundedPanel generalCard = new RoundedPanel(12, new Color(248, 250, 252));
        generalCard.setLayout(new GridLayout(3, 2, 10, 10));
        generalCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        generalCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        generalCard.setMaximumSize(new Dimension(420, 180));

        addInfoItem(generalCard, "Phòng Trọ:", room);
        addInfoItem(generalCard, "Khách Thuê:", tenant);
        addInfoItem(generalCard, "Ngày Lập:", date);
        addInfoItem(generalCard, "Hạn Nộp:", deadline);
        addInfoItem(generalCard, "Trạng Thái:", status);
        addInfoItem(generalCard, "Ngày Thu:", "-".equals(paymentDate) || paymentDate.isEmpty() ? "Chưa thu" : paymentDate);
        container.add(generalCard);

        container.add(Box.createVerticalStrut(20));

        // Cost breakdown title
        JLabel lblCostTitle = new JLabel("Chi Tiết Các Khoản Chi Phí");
        lblCostTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCostTitle.setForeground(new Color(15, 23, 42));
        lblCostTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblCostTitle);
        container.add(Box.createVerticalStrut(10));

        // Cost breakdown panel
        JPanel costPanel = new JPanel();
        costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.Y_AXIS));
        costPanel.setOpaque(false);
        costPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price calculations
        long elecCost = (long) (elecKwh * elecUnitPrice);
        long waterCost = (long) (waterM3 * waterUnitPrice);
        long totalCost = rentPrice + elecCost + waterCost + otherPrice;

        addCostRow(costPanel, "1. Tiền thuê phòng:", formatMoney(rentPrice));
        addCostRow(costPanel, String.format("2. Tiền điện (%,.1f kWh x %,dđ):", elecKwh, elecUnitPrice), formatMoney(elecCost));
        addCostRow(costPanel, String.format("3. Tiền nước (%,.1f m3 x %,dđ):", waterM3, waterUnitPrice), formatMoney(waterCost));
        addCostRow(costPanel, "4. Chi phí dịch vụ khác:", formatMoney(otherPrice));

        costPanel.add(Box.createVerticalStrut(10));
        costPanel.add(JSeparatorUtils.createSeparator());
        costPanel.add(Box.createVerticalStrut(10));

        // Total Cost Row
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(420, 30));
        
        JLabel lblTotalLabel = new JLabel("TỔNG CỘNG:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalLabel.setForeground(new Color(15, 23, 42));
        totalRow.add(lblTotalLabel, BorderLayout.WEST);

        JLabel lblTotalVal = new JLabel(formatMoney(totalCost));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalVal.setForeground(new Color(239, 68, 68)); // Red 500
        totalRow.add(lblTotalVal, BorderLayout.EAST);
        costPanel.add(totalRow);

        container.add(costPanel);
        container.add(Box.createVerticalStrut(30));

        // Close Button
        ModernButton btnClose = new ModernButton("Đóng Cửa Sổ");
        btnClose.setColors(new Color(71, 85, 105), new Color(100, 116, 139), new Color(51, 65, 85));
        btnClose.setPreferredSize(new Dimension(150, 38));
        btnClose.setMaximumSize(new Dimension(150, 38));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnClose.addActionListener(e -> dispose());
        container.add(btnClose);

        add(container, BorderLayout.CENTER);
    }

    private void addInfoItem(JPanel panel, String label, String value) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));
        item.add(lbl, BorderLayout.NORTH);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 12));
        val.setForeground(new Color(15, 23, 42));
        item.add(val, BorderLayout.CENTER);

        panel.add(item);
    }

    private void addCostRow(JPanel panel, String labelText, String valText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(420, 25));
        row.setPreferredSize(new Dimension(400, 25));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(71, 85, 105));
        row.add(lbl, BorderLayout.WEST);

        JLabel val = new JLabel(valText);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(new Color(15, 23, 42));
        row.add(val, BorderLayout.EAST);

        panel.add(row);
        panel.add(Box.createVerticalStrut(6));
    }

    private String formatMoney(long amount) {
        return String.format("%,d", amount).replace(',', '.') + " VND";
    }

    private static class JSeparatorUtils {
        public static JComponent createSeparator() {
            return new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(new Color(226, 232, 240)); // Slate 200
                    g.drawLine(0, 0, getWidth(), 0);
                }
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(100, 1);
                }
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(2000, 1);
                }
            };
        }
    }
}
