package client.controller;

import client.view.MainDashboard;
import client.view.LoginFrame;
import client.service.LoginService;

import javax.swing.*;
import java.awt.EventQueue;

/**
 * Controller chịu trách nhiệm điều khiển toàn bộ Dashboard và điều phối các Controller con
 */
public class DashboardController {
    private final MainDashboard view;

    public DashboardController(MainDashboard view) {
        this.view = view;
        initController();
    }

    private void initController() {
        // Bắt sự kiện click nút đăng xuất
        view.getBtnLogout().addActionListener(e -> handleLogout());

        // ── Khởi tạo các Controller chuyên dụng cho từng Panel ──────────────
        // Lưu vào biến để truyền vào AdminHomeController (tái sử dụng, không duplicate logic)
        RoomsController    roomsCtrl    = new RoomsController(view.getRoomsPanel());
        TenantsController  tenantsCtrl  = new TenantsController(view.getTenantsPanel());
        InvoicesController invoicesCtrl = new InvoicesController(view.getInvoicesPanel());

        ServicesController servicesCtrl   = new ServicesController(view.getServicesPanel());
        ContractsController contractsCtrl = new ContractsController(view.getContractsPanel());
        MaintenanceController maintCtrl   = new MaintenanceController(view.getMaintenancePanel());
        maintCtrl.setOnNewData(() -> {
            boolean isAdmin = common.model.UserSession.getInstance() != null && "ADMIN".equalsIgnoreCase(common.model.UserSession.getInstance().getRole());
            if (isAdmin) {
                if (view.getBtnMaintenance() != null) view.getBtnMaintenance().setShowRedDot(true);
            }
        });

        new SettingsController(view.getSettingsPanel());
        
        NotificationsController notifCtrl = new NotificationsController(view.getNotificationsPanel());
        notifCtrl.setOnNewData(() -> {
            boolean isAdmin = common.model.UserSession.getInstance() != null && "ADMIN".equalsIgnoreCase(common.model.UserSession.getInstance().getRole());
            if (!isAdmin) {
                if (view.getBtnUserNotifications() != null) view.getBtnUserNotifications().setShowRedDot(true);
            }
        });
        
        StatisticsController statCtrl = new StatisticsController(view.getStatisticsPanel());
        
        UserServicesController userServicesCtrl = new UserServicesController(view.getUserServicesPanel());
        FeedbackController feedbackCtrl = new FeedbackController(view.getFeedbackPanel());

        AdminSubscriptionsController adminSubsCtrl = new AdminSubscriptionsController(view.getAdminSubscriptionsPanel());
        AdminFeedbacksController adminFeedbacksCtrl = new AdminFeedbacksController(view.getAdminFeedbacksPanel());
        adminFeedbacksCtrl.setOnNewData(() -> {
            if (view.getBtnAdminFeedbacks() != null) view.getBtnAdminFeedbacks().setShowRedDot(true);
        });

        // ── AdminHomeController chỉ delegate tới 3 controller trên ───────────
        // Không tự xử lý business logic — nguyên tắc Single Responsibility
        AdminHomeController adminHomeCtrl = new AdminHomeController(
            view.getAdminHomePanel(),
            roomsCtrl,
            tenantsCtrl,
            invoicesCtrl
        );

        // Global refresh callback
        UserHomeController[] userHomeCtrlHolder = new UserHomeController[1];
        
        Runnable refreshAll = () -> {
            adminHomeCtrl.loadDashboardData();
            roomsCtrl.loadRoomData();
            tenantsCtrl.loadTenantData();
            invoicesCtrl.loadInvoiceData();
            contractsCtrl.loadContractData();
            maintCtrl.loadMaintenanceData();
            servicesCtrl.loadIndexData(); // Updates rooms dropdown for indexes
            statCtrl.loadStatistics();
            userServicesCtrl.loadSubscriptions();
            feedbackCtrl.loadFeedbacks();
            adminSubsCtrl.loadData();
            adminFeedbacksCtrl.loadData();
            notifCtrl.loadNotifications();
            if (userHomeCtrlHolder[0] != null) {
                userHomeCtrlHolder[0].loadData();
            }
        };

        roomsCtrl.setOnDataChanged(refreshAll);
        tenantsCtrl.setOnDataChanged(refreshAll);
        invoicesCtrl.setOnDataChanged(refreshAll);
        contractsCtrl.setOnDataChanged(refreshAll);
        maintCtrl.setOnDataChanged(refreshAll);
        servicesCtrl.setOnDataChanged(refreshAll);

        // Removed automatic invoice creation as requested

        userHomeCtrlHolder[0] = new UserHomeController(view.getUserHomePanel(), refreshAll);

        // Bắt đầu Timer reload dữ liệu định kỳ mỗi 5 giây (5000ms) để đồng bộ hệ thống
        Timer syncTimer = new Timer(5000, e -> {
            refreshAll.run();
        });
        syncTimer.start();
    }


    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            view, 
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", 
            "Xác Nhận Đăng Xuất", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            LoginService.logout();
            EventQueue.invokeLater(() -> {
                LoginFrame loginView = new LoginFrame();
                new LoginController(loginView);
                loginView.setVisible(true);
            });
            view.dispose();
        }
    }
}
