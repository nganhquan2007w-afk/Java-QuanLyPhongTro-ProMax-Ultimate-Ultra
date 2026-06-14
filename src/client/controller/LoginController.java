package client.controller;

import client.view.MainDashboard;
import client.view.LoginFrame;
import client.service.LoginService;
import common.model.UserSession;

import javax.swing.*;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Controller chịu trách nhiệm điều khiển logic xác thực đăng nhập từ LoginFrame
 */
public class LoginController {
    private final LoginFrame view;

    public LoginController(LoginFrame view) {
        this.view = view;
        initController();
    }

    private void initController() {
        // Bắt sự kiện click nút đăng nhập
        view.getBtnLogin().addActionListener(e -> performLogin());

        // Bắt sự kiện nhấn Enter trên ô tài khoản hoặc mật khẩu để đăng nhập nhanh
        KeyAdapter enterSubmit = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        view.getTxtUsername().addKeyListener(enterSubmit);
        view.getTxtPassword().addKeyListener(enterSubmit);
    }

    private void performLogin() {
        if (view.isLoading()) return;

        String username = view.getUsername();
        String password = view.getPassword();

        view.clearError();
        view.setLoadingState(true);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return LoginService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    view.setLoadingState(false);

                    if ("SUCCESS".equals(result)) {
                        String role = UserSession.getInstance().getRole();
                        EventQueue.invokeLater(() -> {
                            MainDashboard dashboard = new MainDashboard();
                            new DashboardController(dashboard);
                            dashboard.switchToRole(role);
                            dashboard.setVisible(true);
                            view.dispose();
                        });
                    } else {
                        view.setError(result);
                    }
                } catch (Exception ex) {
                    view.setLoadingState(false);
                    view.setError("Gặp lỗi bất ngờ: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
