package client.controller;

import client.view.panel.AdminSubscriptionsPanel;
import client.service.ServiceSubscriptionService;
import javax.swing.*;
import java.util.List;

public class AdminSubscriptionsController {
    private final AdminSubscriptionsPanel view;

    public AdminSubscriptionsController(AdminSubscriptionsPanel view) {
        this.view = view;
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnApprove().addActionListener(e -> updateStatus("Đã duyệt"));
        view.getBtnReject().addActionListener(e -> updateStatus("Bị từ chối"));
    }

    public void loadData() {
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                return ServiceSubscriptionService.getAllSubscriptions();
            }
            @Override
            protected void done() {
                try {
                    List<String[]> list = get();
                    view.getTableModel().setRowCount(0);
                    for (String[] r : list) {
                        view.getTableModel().addRow(r);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateStatus(String newStatus) {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một yêu cầu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(view.getTableModel().getValueAt(row, 0).toString());
        String currentStatus = view.getTableModel().getValueAt(row, 3).toString();

        if (currentStatus.equals(newStatus)) return;

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return ServiceSubscriptionService.updateSubscriptionStatus(id, newStatus);
            }
            @Override
            protected void done() {
                try {
                    String res = get();
                    if ("SUCCESS".equals(res)) {
                        JOptionPane.showMessageDialog(view, "Đã cập nhật trạng thái!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi: " + res, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
