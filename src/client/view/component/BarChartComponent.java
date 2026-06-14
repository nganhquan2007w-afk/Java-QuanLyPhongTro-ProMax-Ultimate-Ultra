package client.view.component;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public final class BarChartComponent extends JComponent {
    private int[] data = {0, 0, 0, 0, 0, 0}; // Revenue in millions
    private String[] labels = {"T12", "T1", "T2", "T3", "T4", "T5"};
    private Color startColor = new Color(59, 130, 246); // Blue Accent
    private Color endColor = new Color(99, 102, 241); // Indigo Accent

    public BarChartComponent() {
        setPreferredSize(new Dimension(400, 220));
    }

    public void setData(int[] data, String[] labels) {
        this.data = data;
        this.labels = labels;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 30;
        int chartHeight = height - 2 * padding;
        int chartWidth = width - 2 * padding;

        // Find max data for scaling
        int max = 0;
        for (int v : data) {
            if (v > max) max = v;
        }
        max = (int) (max * 1.15); // Add headroom
        if (max <= 0) max = 10; // Avoid division by zero

        // Draw grid lines
        g2.setColor(new Color(241, 245, 249)); // light gray grid
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 4; i++) {
            int gy = padding + chartHeight - (i * chartHeight / 4);
            g2.drawLine(padding, gy, padding + chartWidth, gy);

            // Draw axis label
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String valStr = (max * i / 4) + "Tr";
            g2.drawString(valStr, 4, gy + 4);
        }

        // Draw columns
        int barGap = 16;
        int barWidth = (chartWidth / data.length) - barGap;
        for (int i = 0; i < data.length; i++) {
            int val = data[i];
            int barHeight = (int) ((double) val / max * chartHeight);
            int bx = padding + i * (chartWidth / data.length) + (barGap / 2);
            int by = padding + chartHeight - barHeight;

            // Gradient Paint
            GradientPaint gp = new GradientPaint(bx, by, startColor, bx + barWidth, by + barHeight, endColor);
            g2.setPaint(gp);
            g2.fillRoundRect(bx, by, barWidth, barHeight, 8, 8);

            // Draw value above bar
            g2.setColor(new Color(15, 23, 42)); // Slate 900
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String valText = val + "Tr";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(valText, bx + (barWidth - fm.stringWidth(valText)) / 2, by - 5);

            // Draw label below bar
            g2.setColor(new Color(100, 116, 139)); // Slate 500
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.drawString(labels[i], bx + (barWidth - g2.getFontMetrics().stringWidth(labels[i])) / 2, padding + chartHeight + 16);
        }
        g2.dispose();
    }
}
