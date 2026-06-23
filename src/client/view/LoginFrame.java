package client.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 * Giao diện Đăng nhập hệ thống (View lớp trong mô hình MVC)
 */
@SuppressWarnings("serial")
public final class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private boolean isLoading = false;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        // Cấu hình cửa sổ chính
        setTitle("VKU LIVING SMART - Đăng Nhập Hệ Thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 520);
        setResizable(false);
        setLocationRelativeTo(null);

        // Panel nền phân bổ dạng lưới ngang (1 hàng, 2 cột)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // ==========================================
        // 1. LEFT PANEL: BRANDING & GRADIENT (Chứa Logo + Slogan)
        // ==========================================
        JPanel leftPanel = new JPanel() {
            private Image bgImage;
            {
                try {
                    java.net.URL imgUrl = getClass().getResource("/Image/LoginQLTro.jpg");
                    if (imgUrl != null) {
                        bgImage = new ImageIcon(imgUrl).getImage();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    int panelW = getWidth();
                    int panelH = getHeight();
                    int imgW = bgImage.getWidth(null);
                    int imgH = bgImage.getHeight(null);

                    if (imgW > 0 && imgH > 0) {
                        double panelAspect = (double) panelW / panelH;
                        double imgAspect = (double) imgW / imgH;

                        int drawW = panelW;
                        int drawH = panelH;
                        int x = 0;
                        int y = 0;

                        if (panelAspect > imgAspect) {
                            // Panel rộng hơn tỷ lệ ảnh -> Scale theo width
                            drawH = (int) (panelW / imgAspect);
                            // Cố định y = 0 (kéo ảnh xuống hết mức không hở viền) hoặc offset
                            y = 0;
                        } else {
                            // Panel hẹp hơn tỷ lệ ảnh -> Scale theo height
                            drawW = (int) (panelH * imgAspect);
                            x = (panelW - drawW) / 2; // Căn giữa chiều ngang
                        }
                        g.drawImage(bgImage, x, y, drawW, drawH, this);
                    } else {
                        g.drawImage(bgImage, 0, 0, panelW, panelH, this);
                    }
                }
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Tạo thẻ (card) trong suốt cho cụm Text để tôn vẻ đẹp
        JPanel textWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Nền kính mờ
                g2.setColor(new Color(15, 23, 42, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                // Viền sáng bóng bẩy
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        textWrapper.setOpaque(false);
        textWrapper.setLayout(new javax.swing.BoxLayout(textWrapper, javax.swing.BoxLayout.Y_AXIS));
        textWrapper.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Nhãn Logo VKU
        JLabel lblIcon = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/Image/LogoVKU.png");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                // Lấy tỷ lệ gốc để scale không bị méo (stretched)
                int origW = originalIcon.getIconWidth();
                int origH = originalIcon.getIconHeight();
                int targetW = 160;
                int targetH = (origW > 0) ? (int) ((double) origH / origW * targetW) : 80;

                Image img = originalIcon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblIcon.setIcon(new ImageIcon(img));
            } else {
                lblIcon.setText("VKU");
                lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 42));
                lblIcon.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            lblIcon.setText("VKU");
        }
        lblIcon.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Nhãn tên ứng dụng
        JLabel lblAppName = new JLabel("LIVING SMART");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Nhãn khẩu hiệu (Slogan)
        JLabel lblSlogan = new JLabel("Quản Lý Phòng Trọ");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSlogan.setForeground(new Color(226, 232, 240));
        lblSlogan.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        textWrapper.add(lblIcon);
        textWrapper.add(Box.createVerticalStrut(20)); // Tăng khoảng cách giữa logo và chữ
        textWrapper.add(lblAppName);
        textWrapper.add(Box.createVerticalStrut(10)); // Tăng khoảng cách giữa tên và slogan
        textWrapper.add(lblSlogan);

        // Đưa Wrapper vào chính giữa Left Panel
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0;
        gbcL.gridy = 0;
        gbcL.weightx = 1.0;
        gbcL.weighty = 1.0;
        gbcL.fill = GridBagConstraints.NONE;
        gbcL.anchor = GridBagConstraints.CENTER;

        leftPanel.add(textWrapper, gbcL);

        mainPanel.add(leftPanel);

        // ==========================================
        // 2. RIGHT PANEL: LOGIN FORM (Giao diện nhập liệu)
        // ==========================================
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null); // Absolute Layout cho căn chỉnh cực kỳ chính xác
        rightPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Welcome Back!");
        lblTitle.setBounds(40, 45, 340, 35);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(15, 23, 42)); // Slate 900
        rightPanel.add(lblTitle);

        JLabel lblSubTitle = new JLabel("Vui lòng đăng nhập để truy cập hệ thống.");
        lblSubTitle.setBounds(40, 85, 340, 20);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(new Color(100, 116, 139)); // Slate 500
        rightPanel.add(lblSubTitle);

        // Trường nhập tài khoản
        JLabel lblUserLabel = new JLabel("TÊN ĐĂNG NHẬP");
        lblUserLabel.setBounds(40, 135, 340, 20);
        lblUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserLabel.setForeground(new Color(100, 116, 139)); // Slate 500
        rightPanel.add(lblUserLabel);

        txtUsername = new client.view.component.RoundTextField("Nhập tên đăng nhập của bạn...", 20);
        txtUsername.setBounds(40, 160, 340, 44);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(txtUsername);

        // Trường nhập mật khẩu
        JLabel lblPassLabel = new JLabel("MẬT KHẨU");
        lblPassLabel.setBounds(40, 225, 340, 20);
        lblPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassLabel.setForeground(new Color(100, 116, 139)); // Slate 500
        rightPanel.add(lblPassLabel);

        txtPassword = new client.view.component.RoundPasswordField("Nhập mật khẩu...", 20, 12, true);
        txtPassword.setBounds(40, 250, 340, 44);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(txtPassword);

        // Nhãn hiển thị thông báo lỗi
        lblError = new JLabel("");
        lblError.setBounds(40, 305, 340, 25);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblError.setForeground(new Color(239, 68, 68)); // Red 500
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblError);

        // Nút đăng nhập ModernButton màu Xanh nhạt
        client.view.component.ModernButton mBtnLogin = new client.view.component.ModernButton("ĐĂNG NHẬP");
        mBtnLogin.setBounds(40, 320, 340, 46);
        mBtnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        mBtnLogin.setColors(
                new Color(96, 165, 250), // Blue 400 (Default)
                new Color(59, 130, 246), // Blue 500 (Hover)
                new Color(37, 99, 235) // Blue 600 (Pressed)
        );
        mBtnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin = mBtnLogin; // Gán lại cho biến interface
        rightPanel.add(btnLogin);

        // Thêm bản quyền chân trang
        JLabel lblFooter = new JLabel("<html><div style='text-align: center; line-height: 1.4;'>"
                + "© 2026 <b>VKU LivingSmart</b> - Smart & Secure<br>"
                + "<span style='font-size: 10px; color: #94a3b8;'>"
                + "Nguyễn Anh Quân - 25CEB047 • Vương Gia Kiệt - 25CEB031"
                + "</span></div></html>");
        lblFooter.setBounds(20, 395, 380, 60);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(148, 163, 184)); // Slate 400
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblFooter);

        mainPanel.add(rightPanel);
    }

    // ==========================================
    // GETTERS & SETTERS (Exposing interface hooks to Controller)
    // ==========================================

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setError(String errorMessage) {
        lblError.setText("⚠️ " + errorMessage);
    }

    public void clearError() {
        lblError.setText("");
    }

    public void setLoadingState(boolean loading) {
        this.isLoading = loading;
        txtUsername.setEnabled(!loading);
        txtPassword.setEnabled(!loading);
        btnLogin.setEnabled(!loading);

        if (loading) {
            btnLogin.setText("ĐANG XÁC THỰC...");
        } else {
            btnLogin.setText("ĐĂNG NHẬP");
        }
    }

    /**
     * Điểm chạy chính thức của ứng dụng Client (Login Frame làm điểm bắt đầu)
     */
    public static void main(String[] args) {
        try {
            // Khởi tạo FlatLaf Mac Light Theme cho giao diện hiện đại và cao cấp
            FlatMacLightLaf.setup();
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 12);
        } catch (Exception ex) {
            System.err.println("Không thể thiết lập FlatLaf.");
        }

        EventQueue.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            // Khởi tạo LoginController để điều khiển khung đăng nhập này
            new client.controller.LoginController(frame);
            frame.setVisible(true);
        });
    }
}
