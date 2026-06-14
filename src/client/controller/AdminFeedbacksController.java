package client.controller;

import client.view.panel.AdminFeedbacksPanel;
import client.service.FeedbackService;
import javax.swing.*;
import java.util.List;

public class AdminFeedbacksController {
    private final AdminFeedbacksPanel view;

    public AdminFeedbacksController(AdminFeedbacksPanel view) {
        this.view = view;
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnResolve().addActionListener(e -> updateStatus("Đã xử lý"));
    }

    private int lastKnownCount = -1;
    private Runnable onNewData;

    public void setOnNewData(Runnable onNewData) {
        this.onNewData = onNewData;
    }

    public void loadData() {
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                return FeedbackService.getAllFeedbacks();
            }
            @Override
            protected void done() {
                try {
                    List<String[]> list = get();
                    int newCount = list != null ? list.size() : 0;
                    if (lastKnownCount != -1 && newCount > lastKnownCount) {
                        if (onNewData != null) onNewData.run();
                    }
                    lastKnownCount = newCount;
                    
                    view.getTableModel().setRowCount(0);
                    if (list == null) return;
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
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một góp ý!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(view.getTableModel().getValueAt(row, 0).toString());
        String currentStatus = view.getTableModel().getValueAt(row, 5).toString();

        if (currentStatus.equals(newStatus)) return;

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return FeedbackService.updateFeedbackStatus(id, newStatus);
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
