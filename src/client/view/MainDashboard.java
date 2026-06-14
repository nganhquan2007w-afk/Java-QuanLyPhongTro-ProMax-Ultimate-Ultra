package client.view;

import client.view.component.HeaderPanel;
import client.view.component.SidebarPanel;
import client.view.panel.StatisticsPanel;
import client.view.panel.SettingsPanel;
import client.view.panel.InvoicesPanel;
import client.view.panel.AdminHomePanel;
import client.view.panel.ContractsPanel;
import client.view.panel.TenantsPanel;
import client.view.panel.MaintenancePanel;
import client.view.panel.UserHomePanel;
import client.view.panel.ServicesPanel;
import client.view.panel.RoomsPanel;
import client.view.panel.NotificationsPanel;
import client.view.panel.UserServicesPanel;
import client.view.panel.FeedbackPanel;
import client.view.panel.AdminSubscriptionsPanel;
import client.view.panel.AdminFeedbacksPanel;
import client.view.component.SidebarButton;
import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public final class MainDashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainDashboard.class.getName());

    private SidebarPanel sidebarPanel;
    private HeaderPanel headerPanel;
    private javax.swing.JPanel cardContainer;
    private java.awt.CardLayout cardLayout;

    // Sub-panels
    private AdminHomePanel adminHomePanel;
    private UserHomePanel userHomePanel;
    private RoomsPanel roomsPanel;
    private TenantsPanel tenantsPanel;
    private InvoicesPanel invoicesPanel;
    private ServicesPanel servicesPanel;
    private SettingsPanel settingsPanel;
    private ContractsPanel contractsPanel;
    private MaintenancePanel maintenancePanel;
    private NotificationsPanel notificationsPanel;
    private StatisticsPanel statisticsPanel;
    private UserServicesPanel userServicesPanel;
    private FeedbackPanel feedbackPanel;
    private AdminSubscriptionsPanel adminSubscriptionsPanel;
    private AdminFeedbacksPanel adminFeedbacksPanel;

    // Getters for panels
    public AdminHomePanel getAdminHomePanel() { return adminHomePanel; }
    public UserHomePanel getUserHomePanel() { return userHomePanel; }
    public RoomsPanel getRoomsPanel() { return roomsPanel; }
    public TenantsPanel getTenantsPanel() { return tenantsPanel; }
    public InvoicesPanel getInvoicesPanel() { return invoicesPanel; }
    public ServicesPanel getServicesPanel() { return servicesPanel; }
    public SettingsPanel getSettingsPanel() { return settingsPanel; }
    public ContractsPanel getContractsPanel() { return contractsPanel; }
    public MaintenancePanel getMaintenancePanel() { return maintenancePanel; }
    public NotificationsPanel getNotificationsPanel() { return notificationsPanel; }
    public StatisticsPanel getStatisticsPanel() { return statisticsPanel; }
    public UserServicesPanel getUserServicesPanel() { return userServicesPanel; }
    public FeedbackPanel getFeedbackPanel() { return feedbackPanel; }
    public AdminSubscriptionsPanel getAdminSubscriptionsPanel() { return adminSubscriptionsPanel; }
    public AdminFeedbacksPanel getAdminFeedbacksPanel() { return adminFeedbacksPanel; }

    // Delegated Getters for Buttons
    public SidebarButton getBtnAdminHome() { return sidebarPanel.getBtnAdminHome(); }
    public SidebarButton getBtnRooms() { return sidebarPanel.getBtnRooms(); }
    public SidebarButton getBtnTenants() { return sidebarPanel.getBtnTenants(); }
    public SidebarButton getBtnInvoices() { return sidebarPanel.getBtnInvoices(); }
    public SidebarButton getBtnContracts() { return sidebarPanel.getBtnContracts(); }
    public SidebarButton getBtnServices() { return sidebarPanel.getBtnServices(); }
    public SidebarButton getBtnMaintenance() { return sidebarPanel.getBtnMaintenance(); }
    public SidebarButton getBtnNotifications() { return sidebarPanel.getBtnNotifications(); }
    public SidebarButton getBtnSettings() { return sidebarPanel.getBtnSettings(); }
    public SidebarButton getBtnUserNotifications() { return sidebarPanel.getBtnUserNotifications(); }
    public SidebarButton getBtnAdminFeedbacks() { return sidebarPanel.getBtnAdminFeedbacks(); }
    public SidebarButton getBtnSupport() { return sidebarPanel.getBtnSupport(); }
    public SidebarButton getBtnFeedback() { return sidebarPanel.getBtnFeedback(); }
    public SidebarButton getBtnLogout() { return sidebarPanel.getBtnLogout(); }

    public void setUserName(String name) {
        if (sidebarPanel != null) {
            sidebarPanel.setUserName(name);
        }
    }

    public MainDashboard() {
        super();
        initComponentsCustom();
        switchToRole("ADMIN");
    }

    private void initComponentsCustom() {
        setTitle("LIVING SMART - Dashboard Hệ Thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1152, 720));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(248, 250, 252));
        setContentPane(mainPanel);

        // Init Components
        sidebarPanel = new SidebarPanel(this::handlePageRequest, this::handleLogout);
        headerPanel = new HeaderPanel(() -> sidebarPanel.setVisible(!sidebarPanel.isVisible()));

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 0));
        contentWrapper.setOpaque(false);
        contentWrapper.add(headerPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        // Instantiate Sub-panels
        adminHomePanel = new AdminHomePanel();
        userHomePanel = new UserHomePanel();
        roomsPanel = new RoomsPanel();
        tenantsPanel = new TenantsPanel();
        invoicesPanel = new InvoicesPanel();
        servicesPanel = new ServicesPanel();
        settingsPanel = new SettingsPanel();
        contractsPanel = new ContractsPanel();
        maintenancePanel = new MaintenancePanel();
        notificationsPanel = new NotificationsPanel();
        statisticsPanel = new StatisticsPanel();
        userServicesPanel = new UserServicesPanel();
        feedbackPanel = new FeedbackPanel();
        adminSubscriptionsPanel = new AdminSubscriptionsPanel();
        adminFeedbacksPanel = new AdminFeedbacksPanel();

        // Add to CardLayout
        cardContainer.add(adminHomePanel, "ADMIN_HOME");
        cardContainer.add(userHomePanel, "USER_HOME");
        cardContainer.add(roomsPanel, "ROOMS");
        cardContainer.add(tenantsPanel, "TENANTS");
        cardContainer.add(invoicesPanel, "INVOICES");
        cardContainer.add(servicesPanel, "SERVICES");
        cardContainer.add(settingsPanel, "SETTINGS");
        cardContainer.add(statisticsPanel, "STATISTICS");
        cardContainer.add(contractsPanel, "CONTRACTS");
        cardContainer.add(maintenancePanel, "MAINTENANCE");
        cardContainer.add(notificationsPanel, "NOTIFICATIONS");
        cardContainer.add(userServicesPanel, "USER_SERVICES");
        cardContainer.add(feedbackPanel, "FEEDBACK");
        cardContainer.add(adminSubscriptionsPanel, "ADMIN_SUBSCRIPTIONS");
        cardContainer.add(adminFeedbacksPanel, "ADMIN_FEEDBACKS");

        contentWrapper.add(cardContainer, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
    }

    private void handlePageRequest(String pageKey, String headerTitle) {
        if (headerPanel != null) {
            headerPanel.setTitle(headerTitle);
        }
        cardLayout.show(cardContainer, pageKey);
    }

    private void handleLogout() {
        // Log out is handled by DashboardController, which listens to the btnLogout.
        // We do nothing here, the ActionEvent from btnLogout will be caught there.
    }

    public void switchToRole(String role) {
        boolean isAdmin = role != null && role.equalsIgnoreCase("ADMIN");
        
        if (roomsPanel != null) roomsPanel.setAdminMode(isAdmin);
        if (maintenancePanel != null) maintenancePanel.setAdminMode(isAdmin);
        if (notificationsPanel != null) notificationsPanel.setAdminMode(isAdmin);
        if (servicesPanel != null) servicesPanel.setAdminMode(isAdmin);

        if (sidebarPanel != null) {
            sidebarPanel.switchToRole(role);
        }
    }

    public void switchPage(String pageKey) {
        if ("LOGOUT".equals(pageKey)) return;
        cardLayout.show(cardContainer, pageKey);
    }

    public void switchToInvoices() {
        if (sidebarPanel != null) {
            sidebarPanel.requestPage("INVOICES", sidebarPanel.getBtnInvoices(), "Quản Lý Hóa Đơn & Thu Phí");
        }
    }

    public static void main(String[] args) {
        LoginFrame.main(args);
    }
}
