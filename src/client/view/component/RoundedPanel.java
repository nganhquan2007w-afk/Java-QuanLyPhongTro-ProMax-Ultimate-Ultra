package client.view.component;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public final class RoundedPanel extends JPanel {
    private int cornerRadius = 15;
    private Color startColor;
    private Color endColor;
    private boolean isGradient = false;
    private Color borderColor;
    private int borderWidth = 0;

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bg) {
        this.cornerRadius = radius;
        setBackground(bg);
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color startColor, Color endColor) {
        this.cornerRadius = radius;
        this.startColor = startColor;
        this.endColor = endColor;
        this.isGradient = true;
        setOpaque(false);
    }

    public void setBorderColor(Color color, int width) {
        this.borderColor = color;
        this.borderWidth = width;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (isGradient && startColor != null && endColor != null) {
            GradientPaint gp = new GradientPaint(0, 0, startColor, width, height, endColor);
            g2.setPaint(gp);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        if (borderWidth > 0 && borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.drawRoundRect(borderWidth / 2, borderWidth / 2, width - borderWidth, height - borderWidth, cornerRadius, cornerRadius);
        }
        g2.dispose();
    }
}
