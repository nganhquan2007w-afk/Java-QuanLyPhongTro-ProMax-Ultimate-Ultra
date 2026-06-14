package client.view.component;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public final class ProgressRing extends JComponent {
    private int progress = 71;

    public ProgressRing(int progress, String title) {
        this.progress = progress;
        setPreferredSize(new Dimension(140, 140));
    }

    public void setProgress(int progress) {
        this.progress = progress;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 16;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int strokeWidth = 10;

        g2.setColor(new Color(241, 245, 249));
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(x + strokeWidth/2, y + strokeWidth/2, size - strokeWidth, size - strokeWidth);

        g2.setColor(new Color(16, 185, 129));
        int angle = (int) (360 * (progress / 100.0));
        g2.drawArc(x + strokeWidth/2, y + strokeWidth/2, size - strokeWidth, size - strokeWidth, 90, -angle);

        g2.setColor(new Color(15, 23, 42));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String text = progress + "%";
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (size - fm.stringWidth(text)) / 2;
        int ty = y + (size / 2) + fm.getAscent() / 3;
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}
