package client.controller;

import client.view.panel.NotificationsPanel;
import client.socket.SocketClient;
import server.protocol.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

/**
 * NotificationsController — Quản lý thông báo qua JSON protocol.
 */
public class NotificationsController {
    private final NotificationsPanel view;

    public NotificationsController(NotificationsPanel view) {
        this.view = view;
        initController();
        loadNotifications();
    }

    private void initController() {
        view.getBtnSend().addActionListener(e -> handleSendNotification());
    }

    private int lastKnownCount = -1;
    private Runnable onNewData;

    public void setOnNewData(Runnable onNewData) {
        this.onNewData = onNewData;
    }

    public void loadNotifications() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        String role = "USER";
        String roomId = "";
        if (common.model.UserSession.getInstance() != null) {
            role = common.model.UserSession.getInstance().getRole();
            roomId = common.model.UserSession.getInstance().getRoomId();
            if (roomId == null) roomId = "";
        }
        final String finalRole = role;
        final String finalRoomId = roomId;

        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() {
                return SocketClient.send("GET_NOTIFICATIONS", 
                    "role", finalRole, 
                    "roomId", finalRoomId);
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) {
                        if (lastKnownCount == -1) lastKnownCount = 0;
                        return;
                    }

                    List<Map<String, Object>> items = resp.getList();
                    if (items == null) return;
                    
                    int newCount = items.size();
                    if (lastKnownCount != -1 && newCount > lastKnownCount) {
                        if (onNewData != null) {
                            onNewData.run();
                        }
                    }
                    lastKnownCount = newCount;

                    // Hiển thị mới nhất trước (không reverse vì server đã sắp xếp DESC)
                    for (int i = 0; i < items.size(); i++) {
                        Map<String, Object> item = items.get(i);
                        model.addRow(new Object[]{
                            str(item, "title"),
                            str(item, "content"),
                            str(item, "date")
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi tải thông báo: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleSendNotification() {
        String title   = view.getTxtTitle().getText().trim();
        String content = view.getTxtContent().getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                "Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo!",
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.getBtnSend().setEnabled(false);

        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() {
                return SocketClient.send("SEND_NOTIFICATION",
                    "title",   title,
                    "content", content
                );
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    view.getBtnSend().setEnabled(true);

                    if (resp.isSuccess()) {
                        JOptionPane.showMessageDialog(view, "Gửi thông báo thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        view.getTxtTitle().setText("");
                        view.getTxtContent().setText("");
                        loadNotifications();
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi: " + resp.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    view.getBtnSend().setEnabled(true);
                    JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }
}
