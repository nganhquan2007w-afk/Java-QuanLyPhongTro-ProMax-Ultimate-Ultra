package client.controller;

import client.view.panel.ContractsPanel;
import client.view.dialog.AddContractDialog;
import common.model.Room;
import client.service.ContractService;
import client.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.util.List;

/**
 * ContractsController - Controller (MVC) cho module Quản Lý Hợp Đồng.
 *
 * Luồng: View → Controller → ContractService → Socket
 *        → ServerController → ContractDAO → Database
 */
public class ContractsController {
    private final ContractsPanel view;
    private Runnable onDataChanged;

    public ContractsController(ContractsPanel view) {
        this.view = view;
        initController();
        loadContractData();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void initController() {
        String role = "USER";
        if (common.model.UserSession.getInstance() != null) {
            role = common.model.UserSession.getInstance().getRole();
        }
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        
        view.getBtnAdd().setVisible(isAdmin);
        view.getBtnAdd().addActionListener(e -> openAddContractDialog());

        view.getCmbStatus().addActionListener(e -> {
            String selected = (String) view.getCmbStatus().getSelectedItem();
            filterByStatus(selected);
        });

        // Search theo text
        view.getTxtSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterBySearch(view.getTxtSearch().getText().trim());
            }
        });
    }

    // ─── Load dữ liệu từ DB ──────────────────────────────────────────────────
    public void loadContractData() {
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                // Controller → ContractService → Socket → ServerController
                //           → ContractDAO → SELECT FROM contracts → DB
                return ContractService.getContracts();
            }

            @Override
            protected void done() {
                try {
                    List<String[]> list = get();
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    
                    String role = "USER";
                    String roomId = "";
                    if (common.model.UserSession.getInstance() != null) {
                        role = common.model.UserSession.getInstance().getRole();
                        roomId = common.model.UserSession.getInstance().getRoomId();
                    }
                    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

                    for (String[] c : list) {
                        String code   = "HD-" + c[0];
                        String room   = c[1];
                        
                        if (!isAdmin && roomId != null && !roomId.isEmpty()) {
                            if (!room.equalsIgnoreCase(roomId)) continue;
                        }
                        
                        String tenant = c[2];
                        String start  = client.util.DateFormatter.toDisplay(c[3]);
                        String end    = client.util.DateFormatter.toDisplay(c[4]);
                        double depVal = 0.0;
                        try { depVal = Double.parseDouble(c[5]); } catch (Exception ignored) {}
                        String deposit = String.format("%,.0f", depVal).replace(',', '.') + " VND";
                        String status  = c[6];
                        model.addRow(new Object[]{code, room, tenant, start, end, deposit, status});
                    }
                } catch (Exception e) {
                    System.err.println("ContractsController: Lỗi load dữ liệu: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ─── Filter ──────────────────────────────────────────────────────────────
    private void filterByStatus(String status) {
        loadContractData(); // Load lại từ DB sau đó lọc phía client
        if ("Tất cả".equals(status)) return;
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = view.getTableModel();
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                String rowStatus = (String) model.getValueAt(i, 6);
                if (!rowStatus.equalsIgnoreCase(status)) model.removeRow(i);
            }
        });
    }

    private void filterBySearch(String keyword) {
        if (keyword.isEmpty()) { loadContractData(); return; }
        String kw = keyword.toLowerCase();
        DefaultTableModel model = view.getTableModel();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            boolean match = false;
            for (int j = 0; j <= 2; j++) {
                Object val = model.getValueAt(i, j);
                if (val != null && val.toString().toLowerCase().contains(kw)) { match = true; break; }
            }
            if (!match) model.removeRow(i);
        }
    }

    // ─── Thêm hợp đồng ───────────────────────────────────────────────────────
    /**
     * Mở dialog Tạo Hợp Đồng Mới.
     * Controller tải danh sách phòng từ RoomService, truyền vào dialog.
     */
    public void openAddContractDialog() {
        // Controller lấy room list từ Service, truyền vào Dialog (không để View tự gọi)
        String[] roomIds = loadRoomIds();

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        AddContractDialog dialog = new AddContractDialog(parent, roomIds);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            // Lấy dữ liệu từ View
            String room       = dialog.getRoom();
            String tenantName = dialog.getTenantName();
            String startDate  = dialog.getStartDate();
            String endDate    = dialog.getEndDate();
            String deposit    = dialog.getDeposit();

            // Validate phía client
            if (tenantName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập Tên khách thuê!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập Ngày bắt đầu và Ngày kết thúc!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double depVal = 0;
            try { depVal = Double.parseDouble(deposit.replaceAll("[^\\d.]", "")); } catch (Exception ignored) {}

            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang lưu...");
            final double finalDep = depVal;

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    // Controller → ContractService → Socket → ServerController
                    //           → ContractDAO.addContract() → INSERT INTO contracts → DB
                    return ContractService.addContract(room, tenantName, startDate, endDate, finalDep);
                }

                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Lưu Lại");
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            loadContractData();
                            if (onDataChanged != null) onDataChanged.run();
                            JOptionPane.showMessageDialog(dialog,
                                "✅ Đã tạo hợp đồng mới thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "❌ " + result, "Lỗi khi tạo hợp đồng", JOptionPane.ERROR_MESSAGE);
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

    // ─── Helpers ─────────────────────────────────────────────────────────────
    /**
     * Lấy danh sách mã phòng từ server để truyền vào dialog.
     * Controller đảm nhiệm, KHÔNG để View tự gọi Service.
     */
    private String[] loadRoomIds() {
        try {
            List<Room> rooms = RoomService.getRooms();
            if (rooms != null && !rooms.isEmpty()) {
                return rooms.stream().map(Room::getRoomId).toArray(String[]::new);
            }
        } catch (Exception e) {
            System.err.println("ContractsController: Không tải được danh sách phòng: " + e.getMessage());
        }
        return new String[0];
    }
}
