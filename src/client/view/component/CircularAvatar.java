package client.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

@SuppressWarnings("serial")
public final class CircularAvatar extends JPanel {
    private Image image;
    private String initials = "AD";

    public CircularAvatar(Image img) {
        this.image = img;
        setOpaque(false);
        setPreferredSize(new Dimension(46, 46));
    }

    public CircularAvatar(String initials) {
        this.initials = initials;
        setOpaque(false);
        setPreferredSize(new Dimension(46, 46));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        if (image != null) {
            Area clip = new Area(new Ellipse2D.Double(x, y, size, size));
            g2.setClip(clip);
            g2.drawImage(image, x, y, size, size, null);
        } else {
            g2.setColor(new Color(59, 130, 246));
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (size - fm.stringWidth(initials)) / 2;
            int ty = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(initials, tx, ty);
        }

        g2.setClip(null);
        g2.setColor(new Color(255, 255, 255, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, size, size);

        g2.dispose();
    }
}
