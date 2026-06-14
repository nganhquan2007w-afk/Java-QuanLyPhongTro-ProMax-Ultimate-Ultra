package client.view.component;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public final class SidebarButton extends JButton {
    private boolean selected = false;
    private boolean showRedDot = false;
    private Color hoverBg = new Color(30, 41, 59); // Slate 800
    private Color selectedBg = new Color(30, 41, 59); // Slate 800
    private Color activeIndicatorColor = new Color(59, 130, 246); // Sky Blue
    private String symbolPrefix = "";
    private Image customIcon = null;

    public void setCustomIcon(Image img) {
        this.customIcon = img;
        this.symbolPrefix = "";
        repaint();
    }

    public SidebarButton(String text, String unicodeSymbol) {
        super("      " + text);
        this.symbolPrefix = unicodeSymbol;

        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(new Color(148, 163, 184)); // Slate 400
        setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        setMaximumSize(new Dimension(260, 46));
        setPreferredSize(new Dimension(260, 46));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void setSelectedButton(boolean selected) {
        this.selected = selected;
        if (selected) {
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            this.showRedDot = false; // Directly set field to bypass the !selected check
        } else {
            setForeground(new Color(148, 163, 184));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        repaint();
    }
    
    public void setShowRedDot(boolean showRedDot) {
        if (!this.selected) { // Only show red dot if not currently selected
            this.showRedDot = showRedDot;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        ButtonModel model = getModel();
        if (selected) {
            g2.setColor(selectedBg);
            g2.fillRoundRect(12, 2, width - 24, height - 4, 10, 10);

            g2.setColor(activeIndicatorColor);
            g2.fillRoundRect(16, 12, 4, height - 24, 2, 2);
        } else if (model.isRollover()) {
            g2.setColor(hoverBg);
            g2.fillRoundRect(12, 2, width - 24, height - 4, 10, 10);
            setForeground(Color.WHITE);
        } else {
            setForeground(new Color(148, 163, 184));
        }

        // Paint Icon explicitly on the left
        if (customIcon != null) {
            int iconSize = 20;
            g2.drawImage(customIcon, 24, (height - iconSize) / 2, iconSize, iconSize, null);
        } else {
            g2.setColor(selected ? Color.WHITE : new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            g2.drawString(symbolPrefix, 26, height / 2 + 6);
        }

        // Paint Red Dot if active
        if (showRedDot) {
            g2.setColor(new Color(239, 68, 68)); // Red 500
            g2.fillOval(width - 40, height / 2 - 4, 8, 8);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
