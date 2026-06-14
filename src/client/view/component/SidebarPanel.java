package client.view.component;

import common.model.UserSession;
import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

@SuppressWarnings("serial")
public class SidebarPanel extends JPanel {

    private String currentRole = "ADMIN";
    private JPanel sidebarMenuContainer;
    private JLabel lblUserName;
    private JLabel lblUserRole;

    private SidebarButton btnAdminHome;
    private SidebarButton btnRooms;
    private SidebarButton btnTenants;
    private SidebarButton btnContracts;
    private SidebarButton btnInvoices;
    private SidebarButton btnServices;
    private SidebarButton btnAdminSubscriptions;
    private SidebarButton btnMaintenance;
    private SidebarButton btnAdminFeedbacks;
    private SidebarButton btnStatistics;
    private SidebarButton btnSettings;
    private SidebarButton btnNotifications;

    private SidebarButton btnUserHome;
    private SidebarButton btnMyRoom;
    private SidebarButton btnMyContract;
    private SidebarButton btnMyInvoices;
    private SidebarButton btnUserServices;
    private SidebarButton btnSupport;
    private SidebarButton btnFeedback;
    private SidebarButton btnUserSettings;
    private SidebarButton btnUserNotifications;
    private SidebarButton btnLogout;

    private BiConsumer<String, String> onPageRequested;

    public SidebarPanel(BiConsumer<String, String> onPageRequested, Runnable onLogoutRequested) {
        this.onPageRequested = onPageRequested;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        setPreferredSize(new Dimension(260, 800));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        initTopPanel();
        initNavigationButtons(onLogoutRequested);
        initScrollPane();
        initFooterPanel();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        brandPanel.setOpaque(false);
        brandPanel.setMaximumSize(new Dimension(260, 80));

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    java.net.URL logoUrl = getClass().getResource("/Image/LogoSmartLiving.png");
                    if (logoUrl != null) {
                        Image img = new ImageIcon(logoUrl).getImage();
                        g.drawImage(img, 0, 0, 36, 36, this);
                    } else {
                        // Fallback nếu không có ảnh
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(59, 130, 246));
                        g2.fillOval(0, 0, 36, 36);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                        g2.drawString("🏠", 8, 25);
                        g2.dispose();
                    }
                } catch (Exception e) {}
            }
        };
        logoPanel.setPreferredSize(new Dimension(36, 36));
        logoPanel.setOpaque(false);
        brandPanel.add(logoPanel);

        JPanel brandTitlePanel = new JPanel();
        brandTitlePanel.setLayout(new BoxLayout(brandTitlePanel, BoxLayout.Y_AXIS));
        brandTitlePanel.setOpaque(false);

        JLabel lblBrandName = new JLabel("LIVING SMART");
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBrandName.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("Quản lý phòng trọ");
        lblBrandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBrandSub.setForeground(new Color(148, 163, 184));

        brandTitlePanel.add(lblBrandName);
        brandTitlePanel.add(lblBrandSub);
        brandPanel.add(brandTitlePanel);
        topPanel.add(brandPanel);

        topPanel.add(Box.createVerticalStrut(10));

        RoundedPanel profileCard = new RoundedPanel(12, new Color(30, 41, 59));
        profileCard.setLayout(new BorderLayout(12, 0));
        profileCard.setMaximumSize(new Dimension(230, 68));
        profileCard.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        Image avatarImage = null;
        try {
            java.net.URL logoUrl = getClass().getResource("/Image/LogoUser.png");
            if (logoUrl != null) {
                avatarImage = new ImageIcon(logoUrl).getImage();
            }
        } catch (Exception e) {}

        CircularAvatar avatarPanel = (avatarImage != null) ? new CircularAvatar(avatarImage) : new CircularAvatar("AD");
        profileCard.add(avatarPanel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(Box.createVerticalGlue());

        String displayName = "Người dùng";
        if (UserSession.getInstance() != null) {
            displayName = UserSession.getInstance().getFullName();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = UserSession.getInstance().getUsername();
            }
        }

        lblUserName = new JLabel(displayName);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);

        lblUserRole = new JLabel("Quản trị viên");
        lblUserRole.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUserRole.setForeground(new Color(59, 130, 246));

        textPanel.add(lblUserName);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblUserRole);
        textPanel.add(Box.createVerticalGlue());

        profileCard.add(textPanel, BorderLayout.CENTER);
        topPanel.add(profileCard);

        topPanel.add(Box.createVerticalStrut(20));
        add(topPanel, BorderLayout.NORTH);
    }

    private void initScrollPane() {
        sidebarMenuContainer = new JPanel();
        sidebarMenuContainer.setLayout(new BoxLayout(sidebarMenuContainer, BoxLayout.Y_AXIS));
        sidebarMenuContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(sidebarMenuContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollPane.getVerticalScrollBar().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void initFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        footerPanel.add(btnLogout);
        footerPanel.add(Box.createVerticalStrut(10));

        JLabel lblVersion = new JLabel("LivingSmart v1.2.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(71, 85, 105));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(lblVersion);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private SidebarButton createBtn(String text, String emoji, String iconPath) {
        SidebarButton btn = new SidebarButton(text, emoji);
        try {
            java.net.URL url = getClass().getResource(iconPath);
            if (url != null) btn.setCustomIcon(new ImageIcon(url).getImage());
        } catch (Exception e) {}
        return btn;
    }

    private void initNavigationButtons(Runnable onLogoutRequested) {
        btnAdminHome = createBtn("Trang Chủ", "🏠", "/Image/LogoTrangChu.png");
        btnRooms = createBtn("Quản Lý Phòng Trọ", "🚪", "/Image/QLPhongTro.png");
        btnTenants = createBtn("Quản Lý Khách Thuê", "👥", "/Image/LogoKhachThue.png");
        btnContracts = createBtn("Quản Lý Hợp Đồng", "📄", "/Image/HopDong.png");
        btnInvoices = createBtn("Hóa Đơn & Chi Phí", "🧾", "/Image/ChiPhi.png");
        btnServices = createBtn("Dịch Vụ & Điện Nước", "⚡", "/Image/DienNuoc.png");
        btnAdminSubscriptions = createBtn("Đăng Ký Dịch Vụ", "📝", "/Image/HopDong.png");
        btnMaintenance = createBtn("Yêu Cầu Sửa Chữa", "🛠️", "/Image/SuaChua.png");
        btnAdminFeedbacks = createBtn("Ý Kiến Phản Hồi", "💬", "/Image/ThongBao.png");
        btnStatistics = createBtn("Thống Kê Báo Cáo", "📊", "/Image/ThongKe.png");
        btnSettings = createBtn("Cài Đặt Hệ Thống", "⚙️", "/Image/settings.png");
        btnNotifications = createBtn("Thông Báo", "🔔", "/Image/ThongBao.png");

        btnAdminHome.addActionListener(e -> requestPage("ADMIN_HOME", btnAdminHome, "Tổng Quan Hệ Thống"));
        btnRooms.addActionListener(e -> requestPage("ROOMS", btnRooms, "Quản Lý Danh Sách Phòng Trọ"));
        btnTenants.addActionListener(e -> requestPage("TENANTS", btnTenants, "Quản Lý Danh Sách Khách Thuê"));
        btnContracts.addActionListener(e -> requestPage("CONTRACTS", btnContracts, "Quản Lý Hợp Đồng Thuê"));
        btnInvoices.addActionListener(e -> requestPage("INVOICES", btnInvoices, "Quản Lý Hóa Đơn & Chi Phí"));
        btnServices.addActionListener(e -> requestPage("SERVICES", btnServices, "Quản Lý Dịch Vụ & Điện Nước"));
        btnAdminSubscriptions.addActionListener(e -> requestPage("ADMIN_SUBSCRIPTIONS", btnAdminSubscriptions, "Duyệt Đăng Ký Dịch Vụ"));
        btnMaintenance.addActionListener(e -> requestPage("MAINTENANCE", btnMaintenance, "Quản Lý Yêu Cầu Sửa Chữa"));
        btnAdminFeedbacks.addActionListener(e -> requestPage("ADMIN_FEEDBACKS", btnAdminFeedbacks, "Quản Lý Phản Hồi Từ Khách"));
        btnStatistics.addActionListener(e -> requestPage("STATISTICS", btnStatistics, "Thống Kê & Báo Cáo Doanh Thu"));
        btnSettings.addActionListener(e -> requestPage("SETTINGS", btnSettings, "Cài Đặt Hệ Thống"));
        btnNotifications.addActionListener(e -> requestPage("NOTIFICATIONS", btnNotifications, "Quản Lý & Phát Thông Báo"));

        btnUserHome = createBtn("Trang Chủ", "🏠", "/Image/LogoTrangChu.png");
        btnMyRoom = createBtn("Phòng Của Tôi", "🚪", "/Image/QLPhongTro.png");
        btnMyContract = createBtn("Hợp Đồng Của Tôi", "📄", "/Image/HopDong.png");
        btnMyInvoices = createBtn("Hóa Đơn & Thanh Toán", "🧾", "/Image/ChiPhi.png");
        btnUserServices = createBtn("Đăng Ký Dịch Vụ", "⚡", "/Image/DienNuoc.png");
        btnSupport = createBtn("Gửi Yêu Cầu & Báo Lỗi", "🛠️", "/Image/SuaChua.png");
        btnFeedback = createBtn("Góp Ý & Phản Hồi", "💬", "/Image/ThongBao.png");
        btnUserSettings = createBtn("Cài Đặt Cá Nhân", "⚙️", "/Image/settings.png");
        btnUserNotifications = createBtn("Thông Báo Mới", "🔔", "/Image/ThongBao.png");

        btnUserHome.addActionListener(e -> requestPage("USER_HOME", btnUserHome, "Thông Tin Phòng Trọ Của Bạn"));
        btnMyRoom.addActionListener(e -> requestPage("ROOMS", btnMyRoom, "Chi Tiết Phòng Đang Thuê"));
        btnMyContract.addActionListener(e -> requestPage("CONTRACTS", btnMyContract, "Hợp Đồng Của Tôi"));
        btnMyInvoices.addActionListener(e -> requestPage("INVOICES", btnMyInvoices, "Lịch Sử Hóa Đơn & Thanh Toán"));
        btnUserServices.addActionListener(e -> requestPage("USER_SERVICES", btnUserServices, "Đăng Ký Dịch Vụ & Tiện Ích"));
        btnSupport.addActionListener(e -> requestPage("MAINTENANCE", btnSupport, "Gửi Yêu Cầu Hỗ Trợ"));
        btnFeedback.addActionListener(e -> requestPage("FEEDBACK", btnFeedback, "Góp Ý & Phản Hồi"));
        btnUserSettings.addActionListener(e -> requestPage("SETTINGS", btnUserSettings, "Cài Đặt Tài Khoản"));
        btnUserNotifications.addActionListener(e -> requestPage("NOTIFICATIONS", btnUserNotifications, "Thông Báo Từ Chủ Nhà"));

        btnLogout = createBtn("Đăng Xuất", "🚪", "/Image/Logout.png");
        btnLogout.addActionListener(e -> {
            if (onLogoutRequested != null) onLogoutRequested.run();
        });
    }

    public void requestPage(String pageKey, SidebarButton btn, String title) {
        selectSidebarButton(btn);
        if (onPageRequested != null) {
            onPageRequested.accept(pageKey, title);
        }
    }

    private JLabel createSidebarHeader(String title) {
        JLabel lbl = new JLabel(title.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 26, 6, 26));
        lbl.setMaximumSize(new Dimension(260, 32));
        lbl.setPreferredSize(new Dimension(260, 32));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    public void switchToRole(String role) {
        this.currentRole = role;
        sidebarMenuContainer.removeAll();

        boolean isAdmin = role != null && role.equalsIgnoreCase("ADMIN");

        if (isAdmin) {
            lblUserRole.setText("Quản trị viên");
            sidebarMenuContainer.add(createSidebarHeader("Tổng Quan"));
            sidebarMenuContainer.add(btnAdminHome);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnStatistics);

            sidebarMenuContainer.add(createSidebarHeader("Phòng / Khu Trọ"));
            sidebarMenuContainer.add(btnRooms);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnTenants);

            sidebarMenuContainer.add(createSidebarHeader("Hợp Đồng & Thanh Toán"));
            sidebarMenuContainer.add(btnContracts);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnInvoices);

            sidebarMenuContainer.add(createSidebarHeader("Dịch Vụ & Tiện Ích"));
            sidebarMenuContainer.add(btnServices);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnAdminSubscriptions);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnMaintenance);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnAdminFeedbacks);

            sidebarMenuContainer.add(createSidebarHeader("Hệ Thống"));
            sidebarMenuContainer.add(btnNotifications);

            sidebarMenuContainer.add(createSidebarHeader("Tài Khoản"));
            sidebarMenuContainer.add(btnSettings);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));

            requestPage("ADMIN_HOME", btnAdminHome, "Tổng Quan Hệ Thống");
        } else {
            lblUserRole.setText("Khách hàng");
            sidebarMenuContainer.add(createSidebarHeader("Tổng Quan"));
            sidebarMenuContainer.add(btnUserHome);

            sidebarMenuContainer.add(createSidebarHeader("Phòng Của Tôi"));
            sidebarMenuContainer.add(btnMyRoom);

            sidebarMenuContainer.add(createSidebarHeader("Hợp Đồng & Thanh Toán"));
            sidebarMenuContainer.add(btnMyContract);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnMyInvoices);

            sidebarMenuContainer.add(createSidebarHeader("Dịch Vụ & Tiện Ích"));
            sidebarMenuContainer.add(btnUserServices);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnSupport);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));
            sidebarMenuContainer.add(btnFeedback);

            sidebarMenuContainer.add(createSidebarHeader("Hệ Thống"));
            sidebarMenuContainer.add(btnUserNotifications);

            sidebarMenuContainer.add(createSidebarHeader("Tài Khoản"));
            sidebarMenuContainer.add(btnUserSettings);
            sidebarMenuContainer.add(Box.createVerticalStrut(2));

            requestPage("USER_HOME", btnUserHome, "Thông Tin Phòng Trọ Của Bạn");
        }

        sidebarMenuContainer.revalidate();
        sidebarMenuContainer.repaint();
    }

    public void selectSidebarButton(SidebarButton button) {
        SidebarButton[] buttons = {
            btnAdminHome, btnRooms, btnTenants, btnContracts, btnInvoices, btnServices, btnAdminSubscriptions, btnMaintenance, btnAdminFeedbacks, btnStatistics, btnSettings, btnNotifications,
            btnUserHome, btnMyRoom, btnMyContract, btnMyInvoices, btnUserServices, btnSupport, btnFeedback, btnUserSettings, btnUserNotifications, btnLogout
        };
        for (SidebarButton b : buttons) {
            if (b != null) {
                b.setSelectedButton(b == button);
            }
        }
    }

    public void setUserName(String name) {
        if (lblUserName != null) lblUserName.setText(name);
    }

    public SidebarButton getBtnAdminHome() { return btnAdminHome; }
    public SidebarButton getBtnRooms() { return btnRooms; }
    public SidebarButton getBtnTenants() { return btnTenants; }
    public SidebarButton getBtnInvoices() { return btnInvoices; }
    public SidebarButton getBtnContracts() { return btnContracts; }
    public SidebarButton getBtnServices() { return btnServices; }
    public SidebarButton getBtnMaintenance() { return btnMaintenance; }
    public SidebarButton getBtnNotifications() { return btnNotifications; }
    public SidebarButton getBtnSettings() { return btnSettings; }
    public SidebarButton getBtnUserNotifications() { return btnUserNotifications; }
    public SidebarButton getBtnAdminFeedbacks() { return btnAdminFeedbacks; }
    public SidebarButton getBtnSupport() { return btnSupport; }
    public SidebarButton getBtnFeedback() { return btnFeedback; }
    public SidebarButton getBtnLogout() { return btnLogout; }
}
