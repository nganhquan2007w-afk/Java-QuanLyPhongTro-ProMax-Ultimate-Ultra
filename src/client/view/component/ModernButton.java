package client.view.component;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public final class ModernButton extends JButton {
    private Color baseColor = new Color(59, 130, 246);
    private Color hoverColor;
    private Color pressedColor;
    private int radius = 10;

    public ModernButton(String text) {
        super(text);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setBackground(baseColor);
        
        // Use FlatLaf roundRect style
        putClientProperty("JButton.buttonType", "roundRect");
    }

    public void setColors(Color base, Color hover, Color pressed) {
        this.baseColor = base;
        setBackground(base);
    }
}
