package client.controller;

import client.view.panel.RoomsPanel;
import common.model.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * RoomsController - Coordinates room management events and table mock datasets.
 */
public class RoomsController {
    private final RoomsPanel view;
    private Runnable onDataChanged;
    private java.util.List<common.model.Room> cachedRooms;

    public RoomsController(RoomsPanel view) {
        this.view = view;
        initController();
        loadRoomData();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private javax.swing.table.TableRowSorter<DefaultTableModel> rowSorter;

    private void initController() {
        boolean isAdmin = true;
        if (UserSession.getInstance() != null) {
            isAdmin = "ADMIN".equalsIgnoreCase(UserSession.getInstance().getRole());
        }
        view.getBtnAdd().setVisible(isAdmin);
        view.getBtnEdit().setVisible(isAdmin);
        view.getBtnDelete().setVisible(isAdmin);
        
        // Cần đảm bảo các nút này không null (đã khởi tạo trong view)
        if (view.getBtnExport() != null) {
            view.getBtnExport().setVisible(isAdmin);
            view.getBtnExport().addActionListener(e -> handleExportCSV());
        }
        if (view.getBtnImport() != null) {
            view.getBtnImport().setVisible(isAdmin);
            view.getBtnImport().addActionListener(e -> handleImportCSV());
        }

        view.getBtnAdd().addActionListener(e -> openAddRoomDialog());
        view.getBtnEdit().addActionListener(e -> handleEditRoom());
        view.getBtnDelete().addActionListener(e -> handleDeleteRoom());
        
        if (view.getBtnMaintenance() != null) {
            view.getBtnMaintenance().setVisible(isAdmin);
            view.getBtnMaintenance().addActionListener(e -> handleMaintenanceRoom());
        }

        // Khởi tạo RowSorter để hỗ trợ tìm kiếm và lọc
        rowSorter = new javax.swing.table.TableRowSorter<>(view.getTableModel());
        view.getTable().setRowSorter(rowSorter);

        view.getCmbStatus().addActionListener(e -> applyFilters());
        
        view.getTxtSearch().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });
    }

    private void applyFilters() {
        String status = (String) view.getCmbStatus().getSelectedItem();
        String searchText = view.getTxtSearch().getText().trim().toLowerCase();

        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        // Bộ lọc trạng thái
        if (status != null && !"Tất cả".equals(status)) {
            filters.add(RowFilter.regexFilter("^" + status + "$", 5));
        }

        // Bộ lọc tìm kiếm (lọc theo nhiều cột như Mã phòng, Loại phòng, Mô tả)
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    public void loadRoomData() {
        boolean isAdmin = true;
        String userRoom = null;
        if (UserSession.getInstance() != null) {
            isAdmin = "ADMIN".equalsIgnoreCase(UserSession.getInstance().getRole());
            userRoom = UserSession.getInstance().getRoomId();
        }

        final boolean finalIsAdmin = isAdmin;
        final String finalUserRoom = userRoom;

        new javax.swing.SwingWorker<Void, Void>() {
            java.util.List<String[]> tenants;
            java.util.Map<String, Integer> roomTenantCount = new java.util.HashMap<>();
            java.util.List<common.model.Room> fetchedRooms;

            @Override
            protected Void doInBackground() {
                tenants = client.service.TenantService.getTenants();
                if (tenants != null) {
                    for (String[] t : tenants) {
                        String rId = t[7];
                        if (rId != null && !rId.isEmpty() && !"Chưa có".equals(rId)) {
                            roomTenantCount.put(rId, roomTenantCount.getOrDefault(rId, 0) + 1);
                        }
                    }
                }
                fetchedRooms = client.service.RoomService.getRooms();
                return null;
            }

            @Override
            protected void done() {
                try {
                    cachedRooms = fetchedRooms;
                    DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
                    model.setRowCount(0);

                    if (cachedRooms == null) return;

                    for (common.model.Room r : cachedRooms) {
                        if (finalIsAdmin || (finalUserRoom != null && r.getRoomId().equalsIgnoreCase(finalUserRoom))) {
                            String area = r.getZone();
                            String type = r.getType();
                            int maxCap = r.getCapacity();
                            String capacity = String.valueOf(maxCap);
                            String cleanDesc = r.getDescription();
                            
                            int currentCount = roomTenantCount.getOrDefault(r.getRoomId(), 0);
                            
                            String dynamicStatus = r.getStatus();
                            if ("Đang bảo trì".equalsIgnoreCase(dynamicStatus) || "Bảo trì".equalsIgnoreCase(dynamicStatus)) {
                                // Tôn trọng trạng thái bảo trì
                            } else if (maxCap > 0 && currentCount >= maxCap) {
                                dynamicStatus = "Đã đầy";
                            } else if (currentCount > 0) {
                                dynamicStatus = "Đang thuê";
                            } else {
                                dynamicStatus = "Trống";
                            }
                            r.setStatus(dynamicStatus); // Update cache for other uses

                            String formattedPrice = String.format("%,.0f", r.getPrice()).replace(',', '.');

                            String displayCode = r.getRoomId();
                            if (displayCode.contains("-")) {
                                displayCode = displayCode.substring(displayCode.indexOf("-") + 1);
                            }

                            model.addRow(new Object[]{
                                displayCode,
                                area,
                                type,
                                formattedPrice,
                                capacity,
                                r.getStatus(),
                                cleanDesc
                            });
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Mở dialog Thêm Phòng Mới.
     * Được gọi từ nút trong RoomsPanel VÀ từ AdminHomeController (nút lối tắt).
     */
    public void openAddRoomDialog() {
        client.controller.handler.RoomActionHandler.openAddRoomDialog(
            view, cachedRooms, this::reloadAfterAction
        );
    }

    private void handleEditRoom() {
        client.controller.handler.RoomActionHandler.handleEditRoom(
            view, cachedRooms, this::reloadAfterAction
        );
    }

    private void handleDeleteRoom() {
        client.controller.handler.RoomActionHandler.handleDeleteRoom(
            view, cachedRooms, this::reloadAfterAction
        );
    }

    private void handleMaintenanceRoom() {
        client.controller.handler.RoomActionHandler.handleMaintenanceRoom(
            view, cachedRooms, this::reloadAfterAction
        );
    }

    private void reloadAfterAction() {
        loadRoomData();
        if (onDataChanged != null) onDataChanged.run();
    }

    public void handleExportCSV() {
        client.util.CSVHelper.exportTableToCSV(view, view.getTable(), "DanhSachPhong.csv");
    }

    public void handleImportCSV() {
        String encodedCsv = client.util.CSVHelper.readCSVToBase64(view);
        if (encodedCsv == null) return; // User cancelled or error
        
        view.getBtnImport().setEnabled(false);
        view.getBtnImport().setText("Đang xử lý...");
        
        new javax.swing.SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return client.service.RoomService.importRoomsBatch(encodedCsv);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    if (result != null && result.startsWith("SUCCESS")) {
                        javax.swing.JOptionPane.showMessageDialog(view, "Nhập dữ liệu thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        reloadAfterAction();
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(view, "Lỗi khi nhập dữ liệu: " + (result != null ? result : "Không xác định"), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                } finally {
                    view.getBtnImport().setEnabled(true);
                    view.getBtnImport().setText("📤 Nhập CSV");
                }
            }
        }.execute();
    }
}
