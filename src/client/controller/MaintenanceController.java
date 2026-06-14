package client.controller;

import client.view.panel.MaintenancePanel;
import client.view.dialog.ReportMaintenanceDialog;
import common.model.UserSession;
import client.service.MaintenanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.util.List;

/**
 * MaintenanceController - Controller (MVC) cho module Yêu Cầu Sửa Chữa.
 *
 * Luồng: View → Controller → MaintenanceService → Socket
 *        → ServerController → MaintenanceDAO → Database
 *
 * - Admin: xem tất cả, duyệt/từ chối yêu cầu
 * - USER/Tenant: chỉ xem và gửi yêu cầu của phòng mình
 */
public class MaintenanceController {
    private final MaintenancePanel view;

    // Cache ID của dòng đang chọn để gọi updateStatus đúng bản ghi DB
    private int selectedRequestId = -1;
    private Runnable onDataChanged;

    public MaintenanceController(MaintenancePanel view) {
        this.view = view;
        initController();
        loadMaintenanceData();
        
        // Auto refresh every 10 seconds
        javax.swing.Timer timer = new javax.swing.Timer(10000, e -> loadMaintenanceData());
        timer.start();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void initController() {
        // Nút báo cáo sự cố mới (User)
        view.getBtnAdd().addActionListener(e -> handleReportIssue());

        // Nút làm mới
        view.getBtnRefresh().addActionListener(e -> {
            view.getCmbStatus().setSelectedIndex(0);
            view.getTxtSearch().setText("");
            loadMaintenanceData();
        });

        // Nút Admin duyệt / từ chối / hoàn thành
        view.getBtnApprove().addActionListener(e -> handleUpdateStatus("Đang sửa chữa"));
        view.getBtnReject().addActionListener(e -> handleUpdateStatus("Từ chối"));
        view.getBtnComplete().addActionListener(e -> handleUpdateStatus("Đã hoàn thành"));

        // Filter theo trạng thái
        view.getCmbStatus().addActionListener(e -> {
            String selected = (String) view.getCmbStatus().getSelectedItem();
            filterByStatus(selected);
        });

        // Lắng nghe search
        view.getTxtSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterBySearch(view.getTxtSearch().getText().trim());
            }
        });

        // Ghi nhớ ID khi click chọn dòng
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                try {
                    Object idObj = view.getTableModel().getValueAt(row, 0);
                    if (idObj != null) {
                        String idStr = idObj.toString().replaceAll("[^\\d]", "");
                        selectedRequestId = idStr.isEmpty() ? -1 : Integer.parseInt(idStr);
                    }
                } catch (Exception ignored) {
                    selectedRequestId = -1;
                }
            }
        });
    }

    private int lastKnownCount = -1;
    private Runnable onNewData;
    
    public void setOnNewData(Runnable onNewData) {
        this.onNewData = onNewData;
    }

    // ─── Load dữ liệu từ DB ──────────────────────────────────────────────────
    public void loadMaintenanceData() {
        String role   = "USER";
        String roomId = "";
        if (UserSession.getInstance() != null) {
            role   = UserSession.getInstance().getRole();
            roomId = UserSession.getInstance().getRoomId();
            if (roomId == null) roomId = "";
        }
        final String finalRole   = role;
        final String finalRoomId = roomId;

        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                // Controller → MaintenanceService → Socket → ServerController
                //           → MaintenanceDAO.getAllRequests/getByRoom() → DB
                return MaintenanceService.getMaintenanceList(finalRole, finalRoomId);
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
                    
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    if (list == null) return;
                    for (String[] r : list) {
                        if (!"ADMIN".equalsIgnoreCase(finalRole) && finalRoomId != null && !finalRoomId.isEmpty()) {
                            if (!r[1].equalsIgnoreCase(finalRoomId)) continue;
                        }
                        // r: [id, room_id, description, priority, report_date, status]
                        String code = "SC-" + r[0];
                        model.addRow(new Object[]{code, r[1], r[2], r[3], r[4], r[5]});
                    }
                } catch (Exception e) {
                    System.err.println("MaintenanceController: Lỗi load: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ─── Filter ──────────────────────────────────────────────────────────────
    private void filterByStatus(String status) {
        loadMaintenanceData();
        if ("Tất cả".equals(status)) return;
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = view.getTableModel();
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                String s = (String) model.getValueAt(i, 5);
                if (!s.equalsIgnoreCase(status)) model.removeRow(i);
            }
        });
    }

    private void filterBySearch(String keyword) {
        if (keyword.isEmpty()) { loadMaintenanceData(); return; }
        String kw = keyword.toLowerCase();
        DefaultTableModel model = view.getTableModel();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            boolean match = false;
            for (int j = 0; j <= 2; j++) {
                Object v = model.getValueAt(i, j);
                if (v != null && v.toString().toLowerCase().contains(kw)) { match = true; break; }
            }
            if (!match) model.removeRow(i);
        }
    }

    // ─── Duyệt / Từ chối (Admin) ─────────────────────────────────────────────
    private void handleUpdateStatus(String newStatus) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow == -1 || selectedRequestId <= 0) {
            JOptionPane.showMessageDialog(view,
                "Vui lòng chọn một yêu cầu từ danh sách để cập nhật trạng thái!",
                "Chưa chọn yêu cầu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) view.getTableModel().getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(view,
            "Chuyển trạng thái yêu cầu " + code + " thành '" + newStatus + "'?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        view.getBtnApprove().setEnabled(false);
        view.getBtnReject().setEnabled(false);
        final int finalId = selectedRequestId;

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                // Controller → MaintenanceService → Socket → ServerController
                //           → MaintenanceDAO.updateStatus() → UPDATE maintenance_requests → DB
                return MaintenanceService.updateMaintenanceStatus(finalId, newStatus);
            }

            @Override
            protected void done() {
                view.getBtnApprove().setEnabled(true);
                view.getBtnReject().setEnabled(true);
                try {
                    String result = get();
                    if ("SUCCESS".equals(result)) {
                        loadMaintenanceData();
                        if (onDataChanged != null) onDataChanged.run();
                        JOptionPane.showMessageDialog(view,
                            "✅ Đã cập nhật trạng thái thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(view,
                            "❌ " + result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view,
                        "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ─── Báo cáo sự cố mới (User) ────────────────────────────────────────────
    private void handleReportIssue() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        
        // Fetch room list to pass to dialog
        String[] roomList = {};
        try {
            java.util.List<common.model.Room> rooms = client.service.RoomService.getRooms();
            if (rooms != null && !rooms.isEmpty()) {
                roomList = rooms.stream().map(common.model.Room::getRoomId).toArray(String[]::new);
            }
        } catch (Exception ignored) {}
        
        if (roomList.length == 0) {
            roomList = new String[]{"P.101", "P.102", "P.103", "P.201", "P.202", "P.204", "P.302"};
        }

        ReportMaintenanceDialog dialog = new ReportMaintenanceDialog(parent, roomList);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            // Lấy dữ liệu từ View
            String room  = dialog.getRoom();
            String desc  = dialog.getDesc();
            String prio  = dialog.getPriority();
            String date  = dialog.getDate();

            // Validate phía client
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập Nội dung sự cố!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang gửi...");

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    // Controller → MaintenanceService → Socket → ServerController
                    //           → MaintenanceDAO.addRequest() → INSERT INTO maintenance_requests → DB
                    return MaintenanceService.addMaintenance(room, desc, prio, date);
                }

                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Lưu Lại");
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            loadMaintenanceData();
                            if (onDataChanged != null) onDataChanged.run();
                            JOptionPane.showMessageDialog(dialog,
                                "✅ Đã gửi báo cáo sự cố thành công!\nBan quản lý sẽ xem xét và phản hồi sớm.",
                                "Gửi thành công", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "❌ " + result, "Lỗi khi gửi báo cáo", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                            "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        dialog.setVisible(true);
    }
}
