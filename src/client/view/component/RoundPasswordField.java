package client.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

@SuppressWarnings("serial")
public final class RoundPasswordField extends JPasswordField {
    private final String placeholder;
    private final int radius;
    private final boolean showToggleIcon;
    private boolean showPassword = false;
    private boolean hoverToggle = false;
    private Image iconHidden;
    private Image iconShown;

    public RoundPasswordField(String placeholder, int columns) {
        this(placeholder, columns, 12, false);
    }

    public RoundPasswordField(String placeholder, int columns, int radius, boolean showToggleIcon) {
        super(columns);
        this.placeholder = placeholder;
        this.radius = radius;
        this.showToggleIcon = showToggleIcon;
        
        // Use FlatLaf properties instead of custom painting
        putClientProperty("JTextField.placeholderText", placeholder);
        putClientProperty("JComponent.roundRect", true);
        
        if (showToggleIcon) {
            JToggleButton btnToggle = new JToggleButton();
            btnToggle.setFocusable(false);
            btnToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnToggle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 15));
            btnToggle.setContentAreaFilled(false);
            
            try {
                java.net.URL urlHidden = getClass().getResource("/Image/EyeLogin.png");
                java.net.URL urlShown = getClass().getResource("/Image/eye-invis.png");
                if (urlHidden != null) btnToggle.setIcon(new ImageIcon(new ImageIcon(urlHidden).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                if (urlShown != null) btnToggle.setSelectedIcon(new ImageIcon(new ImageIcon(urlShown).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            } catch (Exception e) {}
            
            btnToggle.addActionListener(e -> {
                if (btnToggle.isSelected()) {
                    setEchoChar((char) 0);
                } else {
                    setEchoChar('●');
                }
            });
            
            putClientProperty("JTextField.trailingComponent", btnToggle);
        }

        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(new Color(51, 65, 85));
    }
}
