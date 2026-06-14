package client.controller;


import client.view.dialog.EditPricesDialog;
import client.view.panel.ServicesPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;

/**
 * ServicesController - Coordinates catalog price updates and monthly electricity/water index entries.
 */
public class ServicesController {
    private final ServicesPanel view;

    // Price states (mock persistence)
    private long rentPrice = 2500000;
    private long elecPrice = 3500;
    private long waterPrice = 10000;
    private long internetPrice = 100000;
    private Runnable onDataChanged;

    public ServicesController(ServicesPanel view) {
        this.view = view;
        initController();
        loadCatalogData();
        loadIndexData();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    private void initController() {
        view.getBtnEditCat().addActionListener(e -> handleEditPrices());
        view.getBtnAddCat().addActionListener(e -> handleAddService());
        view.getBtnDeleteCat().addActionListener(e -> handleDeleteService());
        view.getBtnSaveIdx().addActionListener(e -> handleSaveIndexes());
    }

    public void loadCatalogData() {
        DefaultTableModel model = view.getCatModel();
        model.setRowCount(0);

        java.util.List<String[]> list = client.service.ServicesService.getServices();
        for (String[] s : list) {
            String name = s[1];
            double priceVal = 0.0;
            try {
                priceVal = Double.parseDouble(s[2]);
            } catch (Exception e) {}
            String unit = s[3];
            
            if (name.contains("phòng")) {
                rentPrice = (long) priceVal;
            } else if (name.contains("điện")) {
                elecPrice = (long) priceVal;
            } else if (name.contains("nước")) {
                waterPrice = (long) priceVal;
            } else if (name.contains("Internet")) {
                internetPrice = (long) priceVal;
            }

            model.addRow(new Object[]{name, formatCurrency((long) priceVal), unit});
        }
    }

    public void loadIndexData() {
        DefaultTableModel model = view.getIdxModel();
        model.setRowCount(0);

        new SwingWorker<java.util.List<common.model.Room>, Void>() {
            @Override
            protected java.util.List<common.model.Room> doInBackground() {
                return client.service.RoomService.getRooms();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<common.model.Room> rooms = get();
                    if (rooms != null) {
                        for (common.model.Room r : rooms) {
                            if (!"Trống".equalsIgnoreCase(r.getStatus()) && !"Đang sửa chữa".equalsIgnoreCase(r.getStatus())) {
                                model.addRow(new Object[]{r.getRoomId(), 0, 0});
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi tải danh sách phòng: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleEditPrices() {
        int selectedRow = view.getCatTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần sửa từ danh sách!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldName = (String) view.getCatModel().getValueAt(selectedRow, 0);
        String oldPriceStr = (String) view.getCatModel().getValueAt(selectedRow, 1);
        String oldUnit = (String) view.getCatModel().getValueAt(selectedRow, 2);

        // Bỏ định dạng tiền tệ để lấy số (VD: "3,500" -> "3500")
        String oldPrice = oldPriceStr.replaceAll("[^0-9]", "");

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        client.view.dialog.EditServiceDialog dialog = new client.view.dialog.EditServiceDialog(parent);
        dialog.setValues(oldName, oldPrice, oldUnit);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String newName = dialog.getName();
            String priceStr = dialog.getPrice();
            String unit = dialog.getUnit();

            if (newName.isEmpty() || priceStr.isEmpty() || unit.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin dịch vụ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                long price = parseLong(priceStr);

                String result = client.service.ServicesService.editSingleService(oldName, newName, price, unit);
                if ("SUCCESS".equals(result)) {
                    loadCatalogData();
                    if (onDataChanged != null) onDataChanged.run();
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Cập nhật dịch vụ thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, result, "Lỗi khi cập nhật", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chỉ nhập các chữ số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void handleAddService() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        client.view.dialog.AddServiceDialog dialog = new client.view.dialog.AddServiceDialog(parent);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String name = dialog.getName();
            String priceStr = dialog.getPrice();
            String unit = dialog.getUnit();

            if (name.isEmpty() || priceStr.isEmpty() || unit.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin dịch vụ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                long price = parseLong(priceStr);

                String result = client.service.ServicesService.addService(name, price, unit);
                if ("SUCCESS".equals(result)) {
                    loadCatalogData(); // Update data immediately
                    if (onDataChanged != null) onDataChanged.run();
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Thêm dịch vụ mới thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, result, "Lỗi khi thêm dịch vụ", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đơn giá hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void handleDeleteService() {
        int selectedRow = view.getCatTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String serviceName = (String) view.getCatModel().getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(view, 
            "Bạn có chắc chắn muốn xóa dịch vụ '" + serviceName + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String result = client.service.ServicesService.deleteService(serviceName);
            if ("SUCCESS".equals(result)) {
                loadCatalogData();
                if (onDataChanged != null) onDataChanged.run();
                JOptionPane.showMessageDialog(view, "Đã xóa dịch vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, result, "Lỗi khi xóa dịch vụ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSaveIndexes() {
        int rowCount = view.getIdxTable().getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(view,
                "Không có dữ liệu chỉ số phòng để lưu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate tất cả chỉ số trước khi lưu
        for (int i = 0; i < rowCount; i++) {
            String room  = (String) view.getIdxModel().getValueAt(i, 0);
            int elec = parseInteger(view.getIdxModel().getValueAt(i, 1));
            int water = parseInteger(view.getIdxModel().getValueAt(i, 2));

            if (elec < 0) {
                JOptionPane.showMessageDialog(view,
                    "Phòng " + room + ": Số điện tiêu thụ (" + elec + ") không hợp lệ!",
                    "Lỗi chỉ số điện", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (water < 0) {
                JOptionPane.showMessageDialog(view,
                    "Phòng " + room + ": Số nước tiêu thụ (" + water + ") không hợp lệ!",
                    "Lỗi chỉ số nước", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(view,
            "Tự động lập hóa đơn điện nước cho " + rowCount + " phòng?\n" +
            "Hóa đơn sẽ được lưu vào database ngay.",
            "Xác nhận lập hóa đơn", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        view.getBtnSaveIdx().setEnabled(false);
        view.getBtnSaveIdx().setText("Đang tạo...");

        // Lấy danh sách khách thuê để map Tên Khách Thuê
        java.util.Map<String, String> roomTenantMap = new java.util.HashMap<>();
        try {
            java.util.List<String[]> tenants = client.service.TenantService.getTenants();
            if (tenants != null) {
                for (String[] t : tenants) {
                    roomTenantMap.put(t[7], t[1]); // t[7] = roomId, t[1] = fullName
                }
            }
        } catch (Exception e) {}

        // Thu thập dữ liệu trước khi đưa vào SwingWorker
        String[][] indexData = new String[rowCount][3];
        for (int i = 0; i < rowCount; i++) {
            indexData[i][0] = String.valueOf(view.getIdxModel().getValueAt(i, 0)); // room
            indexData[i][1] = String.valueOf(parseInteger(view.getIdxModel().getValueAt(i, 1))); // elecKwh
            indexData[i][2] = String.valueOf(parseInteger(view.getIdxModel().getValueAt(i, 2))); // waterM3
        }

        final long finalElecPrice  = elecPrice;
        final long finalWaterPrice = waterPrice;
        final long finalRentPrice  = rentPrice;

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                int successCount = 0;
                String today = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

                for (String[] row : indexData) {
                    String room   = row[0];
                    double elecKwh  = Double.parseDouble(row[1]);
                    double waterM3  = Double.parseDouble(row[2]);
                    double elecCost  = elecKwh  * finalElecPrice;
                    double waterCost = waterM3  * finalWaterPrice;

                    // Controller → InvoiceService → Socket → ServerController
                    //           → InvoiceDAO.addInvoice() → INSERT INTO invoices → DB
                    String tenantName = roomTenantMap.getOrDefault(room, "Khách phòng " + room);
                    String result = client.service.InvoiceService.addInvoice(
                        room,
                        tenantName,
                        today,
                        finalRentPrice,  // Tiền thuê theo bảng giá hiện tại
                        elecKwh,         // Truyền kWh (server tính tiền từ đơn giá)
                        waterM3,         // Truyền m³
                        0.0              // Phí khác = 0
                    );
                    if ("SUCCESS".equals(result)) successCount++;
                }
                return successCount;
            }

            @Override
            protected void done() {
                view.getBtnSaveIdx().setEnabled(true);
                view.getBtnSaveIdx().setText("💾 Lưu nhanh chỉ số");
                try {
                    int count = get();
                    if (count > 0 && onDataChanged != null) onDataChanged.run();
                    JOptionPane.showMessageDialog(view,
                        "✅ Đã lập hóa đơn thành công cho " + count + "/" + rowCount + " phòng!\n" +
                        "Dữ liệu đã được lưu vào Database.",
                        "Lập hóa đơn hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view,
                        "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    private String formatCurrency(long val) {
        return String.format("%,d", val).replace(',', '.');
    }

    private long parseLong(String str) {
        return Long.parseLong(str.replaceAll("[^\\d]", ""));
    }

    private int parseInteger(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
