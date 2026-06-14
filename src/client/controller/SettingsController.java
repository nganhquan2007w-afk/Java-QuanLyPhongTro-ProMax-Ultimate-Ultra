package client.controller;

import client.view.MainDashboard;
import client.view.panel.SettingsPanel;
import client.service.LoginService;
import common.model.UserSession;

import javax.swing.*;

/**
 * SettingsController - Coordinates SettingsPanel events.
 */
public class SettingsController {
    private final SettingsPanel view;

    public SettingsController(SettingsPanel view) {
        this.view = view;
        initController();
        initData();
    }

    private void initController() {
        view.getBtnSaveProfile().addActionListener(e -> handleSaveProfile());
        view.getBtnSavePassword().addActionListener(e -> handleSavePassword());
    }

    private void initData() {
        if (UserSession.getInstance() != null) {
            view.setFullName(UserSession.getInstance().getFullName());
            view.setPhone(UserSession.getInstance().getPhone());
        }
    }

    private void handleSaveProfile() {
        String name = view.getFullName();
        String phone = view.getPhone();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Họ tên và số điện thoại không được để trống!",
                "Thông Báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        view.setProfileSavingState(true);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return LoginService.updateProfile(name, phone);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    view.setProfileSavingState(false);

                    if ("SUCCESS".equals(result)) {
                        // Cập nhật lại UserSession
                        if (UserSession.getInstance() != null) {
                            UserSession.getInstance().setFullName(name);
                            UserSession.getInstance().setPhone(phone);
                        }
                        // Cập nhật giao diện MainDashboard
                        java.awt.Window win = SwingUtilities.getWindowAncestor(view);
                        if (win instanceof MainDashboard) {
                            ((MainDashboard) win).setUserName(name);
                        }

                        JOptionPane.showMessageDialog(
                            view,
                            "Cập nhật thông tin cá nhân thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            view,
                            "Lỗi: " + result,
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    view.setProfileSavingState(false);
                    JOptionPane.showMessageDialog(
                        view,
                        "Lỗi hệ thống: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void handleSavePassword() {
        String oldPass = view.getOldPassword();
        String newPass = view.getNewPassword();

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Vui lòng điền đầy đủ cả mật khẩu cũ và mới!",
                "Thông Báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        view.setPasswordSavingState(true);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                // Short simulation of network latency
                Thread.sleep(800);
                return LoginService.changePassword(oldPass, newPass);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    view.setPasswordSavingState(false);
                    view.clearPasswordFields();

                    if ("SUCCESS".equals(result)) {
                        JOptionPane.showMessageDialog(
                            view,
                            "Thay đổi mật khẩu thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            view,
                            "Lỗi đổi mật khẩu: " + result,
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    view.setPasswordSavingState(false);
                    JOptionPane.showMessageDialog(
                        view,
                        "Lỗi hệ thống: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
}
