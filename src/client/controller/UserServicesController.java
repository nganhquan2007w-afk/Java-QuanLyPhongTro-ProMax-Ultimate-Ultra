package client.controller;

import client.view.panel.UserServicesPanel;
import client.service.ServiceSubscriptionService;
import client.service.ServicesService;
import common.model.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UserServicesController {
    private final UserServicesPanel view;

    public UserServicesController(UserServicesPanel view) {
        this.view = view;
        initController();
        loadServicesList();
        loadSubscriptions();
    }

    private void initController() {
        view.getBtnSubscribe().addActionListener(e -> {
            try {
                handleSubscribe();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Lỗi khi xử lý: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadServicesList() {
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                return ServicesService.getServices();
            }

            @Override
            protected void done() {
                try {
                    List<String[]> svcs = get();
                    JComboBox<String> cmb = view.getCmbServices();
                    cmb.removeAllItems();
                    if (svcs != null) {
                        for (String[] s : svcs) {
                            cmb.addItem(s[1] + " (" + s[2] + "đ/" + s[3] + ")");
                        }
                    }
                } catch (Exception e) {}
            }
        }.execute();
    }

    public void loadSubscriptions() {
        String roomId = getUserRoomId();
        if (roomId == null) return;

        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                return ServiceSubscriptionService.getSubscriptions(roomId);
            }

            @Override
            protected void done() {
                try {
                    List<String[]> subs = get();
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    for (String[] s : subs) {
                        model.addRow(s);
                    }
                } catch (Exception e) {}
            }
        }.execute();
    }

    private void handleSubscribe() {
        Object selectedObj = view.getCmbServices().getSelectedItem();
        if (selectedObj == null) {
            JOptionPane.showMessageDialog(view, "Chưa có dịch vụ nào để đăng ký! Vui lòng thử lại sau.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selected = selectedObj.toString();
        if (selected.isEmpty()) return;

        String serviceName = selected.split(" \\(")[0];
        String roomId = getUserRoomId();
        if (roomId == null) {
            JOptionPane.showMessageDialog(view, "Bạn chưa được gán phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận đăng ký dịch vụ: " + serviceName + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        view.getBtnSubscribe().setEnabled(false);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return ServiceSubscriptionService.addSubscription(roomId, serviceName);
            }

            @Override
            protected void done() {
                view.getBtnSubscribe().setEnabled(true);
                try {
                    String res = get();
                    if ("SUCCESS".equals(res)) {
                        JOptionPane.showMessageDialog(view, "Đã gửi yêu cầu đăng ký dịch vụ!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadSubscriptions();
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi: " + res, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {}
            }
        }.execute();
    }

    private String getUserRoomId() {
        if (UserSession.getInstance() == null) return null;
        return UserSession.getInstance().getRoomId();
    }
}
