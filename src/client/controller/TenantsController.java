package client.controller;


import client.view.panel.TenantsPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * TenantsController - Coordinates tenant management events and table mock datasets.
 */
public class TenantsController {
    private final TenantsPanel view;
    private Runnable onDataChanged;
    private java.util.function.BiConsumer<String, String> onTenantAdded;
    private java.util.Map<String, String> displayToRealRoomId = new java.util.HashMap<>();

    public TenantsController(TenantsPanel view) {
        this.view = view;
        initController();
        loadTenantData();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public void setOnTenantAdded(java.util.function.BiConsumer<String, String> callback) {
        this.onTenantAdded = callback;
    }

    private javax.swing.table.TableRowSorter<DefaultTableModel> rowSorter;
    private boolean isUpdatingCombo = false;

    private void initController() {
        view.getBtnAdd().addActionListener(e -> openAddTenantDialog());
        view.getBtnDelete().addActionListener(e -> handleDeleteTenant());
        view.getBtnEdit().addActionListener(e -> handleEditTenant());
        
        rowSorter = new javax.swing.table.TableRowSorter<>(view.getTableModel());
        view.getTable().setRowSorter(rowSorter);

        view.getCmbRooms().addActionListener(e -> {
            if (!isUpdatingCombo) applyFilters();
        });

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
        String roomFilter = (String) view.getCmbRooms().getSelectedItem();
        String searchText = view.getTxtSearch().getText().trim().toLowerCase();

        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        if (roomFilter != null && !"Tất cả phòng".equals(roomFilter)) {
            // Lọc chính xác theo tên phòng hiển thị ở cột 2
            filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(roomFilter) + "$", 2));
        }

        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    public void loadTenantData() {
        new javax.swing.SwingWorker<Void, Void>() {
            java.util.List<String[]> tenants;
            java.util.List<common.model.Room> rooms;

            @Override
            protected Void doInBackground() {
                tenants = client.service.TenantService.getTenants();
                rooms = client.service.RoomService.getRooms();
                return null;
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
                    model.setRowCount(0);

                    if (tenants == null) return;

                    for (String[] t : tenants) {
                        String id = "KH" + String.format("%03d", Integer.parseInt(t[0]));
                        String name = t[1];
                        String room = "Chưa có";
                        
                        if (t[7] != null && !t[7].trim().isEmpty()) {
                            String realRoomId = t[7].trim();
                            String displayRoom = realRoomId;
                            if (rooms != null) {
                                for (common.model.Room r : rooms) {
                                    if (r.getRoomId().equals(realRoomId)) {
                                        String area = "Khu nhà A";
                                        if (r.getDescription() != null && r.getDescription().contains("::")) {
                                            area = r.getDescription().split("::")[0];
                                        }
                                        String code = realRoomId.contains("-") ? realRoomId.substring(realRoomId.indexOf("-") + 1) : realRoomId;
                                        displayRoom = "P." + code + " - " + area;
                                        break;
                                    }
                                }
                            }
                            room = displayRoom;
                        }
                        
                        String phone = t[2];
                        String cccd = t[3];
                        
                        String startDate = client.util.DateFormatter.toDisplay(t[8]);
                        String duration = client.util.DateFormatter.formatDuration(t[8], t[9]);
                        String status = (t[7] != null && !t[7].trim().isEmpty()) ? "Đang ở" : "Đã chuyển đi";

                        model.addRow(new Object[]{id, name, room, phone, cccd, startDate, duration, status});
                    }

                    // Cập nhật Combobox Lọc Phòng một cách linh hoạt (Dynamic) từ CSDL Phòng
                    isUpdatingCombo = true;
                    String selectedRoom = (String) view.getCmbRooms().getSelectedItem();
                    view.getCmbRooms().removeAllItems();
                    view.getCmbRooms().addItem("Tất cả phòng");
                    
                    if (rooms != null) {
                        for (common.model.Room r : rooms) {
                            String realRoomId = r.getRoomId();
                            String area = "Khu nhà A";
                            if (r.getDescription() != null && r.getDescription().contains("::")) {
                                area = r.getDescription().split("::")[0];
                            }
                            String code = realRoomId.contains("-") ? realRoomId.substring(realRoomId.indexOf("-") + 1) : realRoomId;
                            String displayRoom = "P." + code + " - " + area;
                            view.getCmbRooms().addItem(displayRoom);
                        }
                    }
                    
                    if (selectedRoom != null) {
                        view.getCmbRooms().setSelectedItem(selectedRoom);
                    } else {
                        view.getCmbRooms().setSelectedIndex(0);
                    }
                    isUpdatingCombo = false;
                    applyFilters();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Mở dialog Đăng Ký Khách Thuê Mới.
     * Được gọi từ nút trong TenantsPanel VÀ từ AdminHomeController (nút lối tắt).
     */
    public void openAddTenantDialog() {
        client.controller.handler.TenantActionHandler.openAddTenantDialog(
            this, view, this::reloadAfterAction, onTenantAdded
        );
    }

    private void handleDeleteTenant() {
        client.controller.handler.TenantActionHandler.handleDeleteTenant(
            this, view, this::reloadAfterAction
        );
    }

    private void handleEditTenant() {
        client.controller.handler.TenantActionHandler.handleEditTenant(
            this, view, this::reloadAfterAction
        );
    }

    private void reloadAfterAction() {
        loadTenantData();
        if (onDataChanged != null) onDataChanged.run();
    }

    public String getRealRoomId(String display) {
        return displayToRealRoomId.getOrDefault(display, display.split(" - ")[0].trim());
    }

    public String[] loadRoomIdsWithStatus(String ignoreRoomId) {
        displayToRealRoomId.clear();
        try {
            java.util.List<common.model.Room> rooms = client.service.RoomService.getRooms();
            java.util.List<String[]> tenants = client.service.TenantService.getTenants();
            
            java.util.Map<String, Integer> roomTenantCount = new java.util.HashMap<>();
            if (tenants != null) {
                for (String[] t : tenants) {
                    String rId = t[7] != null ? t[7].trim() : "";
                    if (!rId.isEmpty() && !"Chưa có".equals(rId)) {
                        roomTenantCount.put(rId, roomTenantCount.getOrDefault(rId, 0) + 1);
                    }
                }
            }

            if (rooms != null && !rooms.isEmpty()) {
                return rooms.stream()
                        .filter(r -> {
                            if (r.getRoomId().trim().equals(ignoreRoomId)) return true;
                            if ("Đang bảo trì".equalsIgnoreCase(r.getStatus())) return false;
                            
                            int maxCap = r.getCapacity();
                            if (maxCap <= 0) maxCap = 2; // fallback
                            
                            int currentCount = roomTenantCount.getOrDefault(r.getRoomId().trim(), 0);
                            return currentCount < maxCap;
                        })
                        .map(r -> {
                            String code = r.getRoomId();
                            String area = "Khu nhà A";
                            if (r.getDescription() != null && r.getDescription().contains("::")) {
                                area = r.getDescription().split("::")[0];
                            }
                            if (code.contains("-")) {
                                code = code.substring(code.indexOf("-") + 1);
                            }
                            String display = "P." + code + " - " + area + " - " + r.getStatus();
                            displayToRealRoomId.put(display, r.getRoomId());
                            return display;
                        })
                        .toArray(String[]::new);
            }
        } catch (Exception e) {
            System.err.println("TenantsController: Không tải được danh sách phòng: " + e.getMessage());
        }
        return new String[0];
    }
}
