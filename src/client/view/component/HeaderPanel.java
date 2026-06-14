package client.view.component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("serial")
public class HeaderPanel extends JPanel {

    private JLabel lblHeaderTitle;
    private JLabel lblDate;
    private JLabel lblTime;
    private Timer clockTimer;

    public HeaderPanel(Runnable onToggleSidebar) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 70));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 25, 0, 25)
        ));

        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setOpaque(false);

        lblHeaderTitle = new JLabel("Tổng Quan Hệ Thống");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeaderTitle.setForeground(new Color(15, 23, 42));
        lblHeaderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateTimePanel.setOpaque(false);
        dateTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblDate = new JLabel("Thứ Năm, 21/05/2026");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(100, 116, 139));

        lblTime = new JLabel("  |  00:00:00");
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTime.setForeground(new Color(59, 130, 246));

        dateTimePanel.add(lblDate);
        dateTimePanel.add(lblTime);

        leftHeader.add(Box.createVerticalStrut(5));
        leftHeader.add(lblHeaderTitle);
        leftHeader.add(Box.createVerticalStrut(2));
        leftHeader.add(dateTimePanel);

        JPanel headerWestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerWestPanel.setOpaque(false);
        
        JButton btnToggleSidebar = new JButton("");
        try {
            java.net.URL url = getClass().getResource("/Image/menu.png");
            if (url != null) {
                btnToggleSidebar.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
            } else {
                btnToggleSidebar.setText("☰");
            }
        } catch (Exception e) {
            btnToggleSidebar.setText("☰");
        }
        btnToggleSidebar.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnToggleSidebar.setForeground(new Color(15, 23, 42));
        btnToggleSidebar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnToggleSidebar.setFocusPainted(false);
        btnToggleSidebar.setContentAreaFilled(false);
        btnToggleSidebar.setBorderPainted(false);
        btnToggleSidebar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));
        
        btnToggleSidebar.addActionListener(e -> {
            if (onToggleSidebar != null) onToggleSidebar.run();
        });
        
        headerWestPanel.add(btnToggleSidebar);
        headerWestPanel.add(leftHeader);
        
        add(headerWestPanel, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); // Chỉnh lại margin một chút
        rightHeader.setOpaque(false);

        JLabel lblTopRightLogo = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/Image/LogoVKUV2.png");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                // Giữ nguyên tỷ lệ, cố định chiều cao là 40px
                int origW = originalIcon.getIconWidth();
                int origH = originalIcon.getIconHeight();
                int targetH = 40;
                int targetW = (origH > 0) ? (int)((double)origW / origH * targetH) : 80;
                
                Image img = originalIcon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblTopRightLogo.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}

        rightHeader.add(lblTopRightLogo);
        add(rightHeader, BorderLayout.EAST);

        startClock();
    }

    public void setTitle(String title) {
        if (lblHeaderTitle != null) {
            lblHeaderTitle.setText(title);
        }
    }

    private void startClock() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("  |  HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

        clockTimer = new Timer(1000, e -> {
            Date now = new Date();
            lblTime.setText(timeFormat.format(now));
            lblDate.setText(dateFormat.format(now));
        });
        clockTimer.start();
    }

    public void stopClock() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
    }
}
