package client.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public final class RoundTextField extends JTextField {
    private final String placeholder;
    private final int radius;
    private final boolean showSearchIcon;
    private Image searchIconImg;

    public RoundTextField(int columns) {
        this("Tìm kiếm nhanh...", columns, 12, true);
    }

    public RoundTextField(String placeholder, int columns) {
        this(placeholder, columns, 12, false);
    }

    public RoundTextField(String placeholder, int columns, int radius, boolean showSearchIcon) {
        super(columns);
        this.placeholder = placeholder;
        this.radius = radius;
        this.showSearchIcon = showSearchIcon;
        
        // Use FlatLaf properties instead of custom painting
        putClientProperty("JTextField.placeholderText", placeholder);
        putClientProperty("JComponent.roundRect", true);
        
        if (showSearchIcon) {
            putClientProperty("JTextField.leadingIcon", new ImageIcon(getClass().getResource("/Image/search.png")));
        }

        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(new Color(51, 65, 85));
    }
}
