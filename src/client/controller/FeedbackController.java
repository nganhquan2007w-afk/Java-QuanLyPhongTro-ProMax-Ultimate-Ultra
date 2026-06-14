package client.controller;

import client.view.panel.FeedbackPanel;
import client.service.FeedbackService;
import common.model.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FeedbackController {
    private final FeedbackPanel view;

    public FeedbackController(FeedbackPanel view) {
        this.view = view;
        initController();
        loadFeedbacks();
    }

    private void initController() {
        view.getBtnSubmit().addActionListener(e -> handleSubmit());
    }

    public void loadFeedbacks() {
        String roomId = getUserRoomId();
        if (roomId == null) return;

        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                return FeedbackService.getMyFeedbacks(roomId);
            }

            @Override
            protected void done() {
                try {
                    List<String[]> list = get();
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    for (String[] f : list) {
                        // [id, title, content, rating, status, createdAt]
                        model.addRow(new Object[]{f[1], f[2], f[3] + " sao", f[4], f[5]});
                    }
                } catch (Exception e) {}
            }
        }.execute();
    }

    private void handleSubmit() {
        String roomId = getUserRoomId();
        if (roomId == null) {
            JOptionPane.showMessageDialog(view, "Bạn chưa được gán phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = view.getTxtTitle().getText().trim();
        String content = view.getTxtContent().getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập tiêu đề và nội dung!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rating = 5 - view.getCmbRating().getSelectedIndex(); // "5 - Rất tốt" is index 0

        view.getBtnSubmit().setEnabled(false);
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return FeedbackService.addFeedback(roomId, title, content, rating);
            }

            @Override
            protected void done() {
                view.getBtnSubmit().setEnabled(true);
                try {
                    String res = get();
                    if ("SUCCESS".equals(res)) {
                        JOptionPane.showMessageDialog(view, "Gửi phản hồi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        view.getTxtTitle().setText("");
                        view.getTxtContent().setText("");
                        view.getCmbRating().setSelectedIndex(0);
                        loadFeedbacks();
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
