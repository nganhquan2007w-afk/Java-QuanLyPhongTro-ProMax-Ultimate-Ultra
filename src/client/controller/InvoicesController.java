package client.controller;

import client.view.panel.InvoicesPanel;
import common.model.Invoice;
import common.model.UserSession;
import client.service.InvoiceService;
import client.util.DateFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;


/**
 * InvoicesController - Điều phối toàn bộ nghiệp vụ quản lý hóa đơn.
 * Dữ liệu được lấy từ Database qua Socket → InvoiceService.
 * Tuân thủ mô hình MVC: Controller không trực tiếp chạm vào DB.
 */
public class InvoicesController {

    private final InvoicesPanel view;

    // Lưu cache danh sách hóa đơn để phục vụ lọc và xem chi tiết
    private List<Invoice> cachedInvoices;
    private Runnable onDataChanged;

    public InvoicesController(InvoicesPanel view) {
        this.view = view;
        initController();
        loadInvoiceData();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    // ---------------------------------------------------------------- INIT

    private void initController() {
        view.getBtnAdd().addActionListener(e  -> openAddInvoiceDialog());
        view.getBtnExportPdf().addActionListener(e -> handleExportPdf());
        view.getBtnComplete().addActionListener(e -> handleCompleteInvoice());
        view.getBtnPay().addActionListener(e -> handlePayInvoiceDemo());

        // Nhấp vào cột "Hành Động" hoặc double-click bất kỳ dòng nào
        view.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = view.getTable().rowAtPoint(e.getPoint());
                int col = view.getTable().columnAtPoint(e.getPoint());
                if (row < 0 || row >= view.getTable().getRowCount()) return;
                if (col == 8 || e.getClickCount() == 2) {
                    view.getTable().setRowSelectionInterval(row, row);
                    handleViewDetail();
                }
            }
        });

        // Lọc theo tháng
        view.getCmbMonth().addActionListener(e -> filterData());
        // Lọc theo trạng thái
        view.getCmbStatus().addActionListener(e -> filterData());

        // Thanh tìm kiếm
        view.getTxtSearch().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterData();
            }
        });
    }

    // ---------------------------------------------------------------- LOAD

    public void loadInvoiceData() {
        String roomId = getUserRoomId();

        SwingWorker<List<Invoice>, Void> worker = new SwingWorker<List<Invoice>, Void>() {
            @Override
            protected List<Invoice> doInBackground() {
                return InvoiceService.getInvoices(roomId);
            }

            @Override
            protected void done() {
                try {
                    cachedInvoices = get();
                    renderTable(cachedInvoices);
                } catch (Exception ex) {
                    System.err.println("Lỗi tải hóa đơn: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ---------------------------------------------------------------- FILTER

    private void filterData() {
        if (cachedInvoices == null) return;

        String searchText  = view.getTxtSearch().getText().trim().toLowerCase();
        String filterMonth = (String) view.getCmbMonth().getSelectedItem();
        String filterStatus = (String) view.getCmbStatus().getSelectedItem();

        java.util.stream.Stream<Invoice> stream = cachedInvoices.stream();

        String roomId = getUserRoomId();
        if (roomId != null && !roomId.trim().isEmpty()) {
            stream = stream.filter(inv -> roomId.equals(inv.getRoomId()));
        }

        if (!searchText.isEmpty()) {
            stream = stream.filter(inv ->
                    inv.getRoomId().toLowerCase().contains(searchText) ||
                    inv.getTenantName().toLowerCase().contains(searchText));
        }

        if (filterMonth != null && !"Tất cả".equals(filterMonth)) {
            String[] parts = filterMonth.split(" ");
            if (parts.length >= 2) {
                String mm_yyyy = parts[1];
                stream = stream.filter(inv -> {
                    String d = DateFormatter.toDisplay(inv.getIssueDate());
                    return d.length() >= 7 && d.substring(3).equals(mm_yyyy);
                });
            }
        }

        if (filterStatus != null && !"Tất cả".equals(filterStatus)) {
            stream = stream.filter(inv -> filterStatus.equals(inv.getStatus()));
        }

        renderTable(stream.collect(java.util.stream.Collectors.toList()));
    }

    // ---------------------------------------------------------------- RENDER

    private void renderTable(List<Invoice> invoices) {
        long[] prices = getServicePrices();
        long elecPrice = prices[0];
        long waterPrice = prices[1];

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (Invoice inv : invoices) {
            long total    = (long)(inv.getRent() + inv.getElecUsage() * elecPrice + inv.getWaterUsage() * waterPrice + inv.getOtherFee());
            String totalFmt = formatCurrency(total) + " VND";
            String issueFmt = DateFormatter.toDisplay(inv.getIssueDate());
            String dueFmt   = DateFormatter.toDisplay(inv.getDueDate());
            String payFmt   = DateFormatter.toDisplay(inv.getPaymentDate());

            model.addRow(new Object[]{
                "HD" + String.format("%03d", inv.getInvoiceId()),
                inv.getRoomId(),
                inv.getTenantName(),
                issueFmt,
                dueFmt,
                totalFmt,
                inv.getStatus(),
                payFmt,
                "Xem chi tiết"
            });
        }
    }

    // ---------------------------------------------------------------- ACTIONS

    private void handleViewDetail() {
        client.controller.handler.InvoiceActionHandler.handleViewDetail(
            this, view, cachedInvoices, getServicePrices(), this::reloadAfterAction
        );
    }

    private void handleExportPdf() {
        client.controller.handler.InvoiceActionHandler.handleExportPdf(
            this, view, cachedInvoices, getServicePrices()
        );
    }

    public void openAddInvoiceDialog() {
        view.getBtnAdd().setEnabled(false);
        view.getBtnAdd().setText("Đang tải...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            java.util.Map<String, String> roomTenantMap;
            java.util.Map<String, Double> roomPriceMap;

            @Override
            protected Void doInBackground() {
                roomTenantMap = loadRoomTenantMap();
                roomPriceMap = loadRoomPriceMap();
                return null;
            }

            @Override
            protected void done() {
                view.getBtnAdd().setEnabled(true);
                view.getBtnAdd().setText("Thêm Hóa Đơn");
                client.controller.handler.InvoiceActionHandler.openAddInvoiceDialog(
                    InvoicesController.this, view, roomTenantMap, roomPriceMap, InvoicesController.this::reloadAfterAction
                );
            }
        };
        worker.execute();
    }

    private void reloadAfterAction() {
        loadInvoiceData();
        if (onDataChanged != null) onDataChanged.run();
    }

    private void handleCompleteInvoice() {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một hóa đơn để hoàn thành!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) view.getTableModel().getValueAt(selectedRow, 6);
        if ("Đã thanh toán".equals(status)) {
            JOptionPane.showMessageDialog(view, "Hóa đơn này đã được thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận hóa đơn này đã được thanh toán?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String invoiceIdStr = (String) view.getTableModel().getValueAt(selectedRow, 0);
        int invoiceId = Integer.parseInt(invoiceIdStr.replaceAll("[^0-9]", ""));
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        view.getBtnComplete().setEnabled(false);
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return InvoiceService.updateInvoiceStatus(invoiceId, "Đã thanh toán", today);
            }

            @Override
            protected void done() {
                view.getBtnComplete().setEnabled(true);
                try {
                    String result = get();
                    if ("SUCCESS".equals(result)) {
                        JOptionPane.showMessageDialog(view, "Cập nhật trạng thái thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        reloadAfterAction();
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi: " + result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void handlePayInvoiceDemo() {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một hóa đơn để thanh toán!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) view.getTableModel().getValueAt(selectedRow, 6);
        if ("Đã thanh toán".equals(status)) {
            JOptionPane.showMessageDialog(view, "Hóa đơn này đã được thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String roomId = (String) view.getTableModel().getValueAt(selectedRow, 1);
        String issueDate = (String) view.getTableModel().getValueAt(selectedRow, 3);
        String month = issueDate != null && issueDate.length() >= 7 ? issueDate.substring(3) : "";
        String qrMessage = String.format("Nội Dung: Phòng %s Thanh Toán Hóa Đơn Trọ Tháng %s", roomId, month);

        ImageIcon qrIcon = new ImageIcon("src/Image/QRThanhToan.jpg"); 
        
        // Tính toán kích thước giữ nguyên tỉ lệ (aspect ratio) để ảnh không bị méo
        int imgWidth = qrIcon.getIconWidth();
        int imgHeight = qrIcon.getIconHeight();
        
        if (imgWidth > 0 && imgHeight > 0) {
            int targetWidth, targetHeight;
            if (imgHeight > imgWidth) {
                // Ảnh dọc: giới hạn chiều cao max là 400
                targetHeight = 400;
                targetWidth = (imgWidth * targetHeight) / imgHeight;
            } else {
                // Ảnh ngang hoặc vuông: giới hạn chiều rộng max là 400
                targetWidth = 400;
                targetHeight = (imgHeight * targetWidth) / imgWidth;
            }
            java.awt.Image img = qrIcon.getImage().getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH);
            qrIcon = new ImageIcon(img);
        }

        JLabel lblMessage = new JLabel("Vui lòng quét mã QR dưới đây để thanh toán:");
        lblMessage.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblQr = new JLabel(qrIcon);
        lblQr.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblRemind = new JLabel(qrMessage);
        lblRemind.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblRemind.setForeground(new java.awt.Color(220, 53, 69)); // Màu đỏ để nổi bật
        lblRemind.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblNote = new JLabel("Sau khi thanh toán, chủ trọ sẽ kiểm tra và xác nhận hóa đơn của bạn.");
        lblNote.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
        lblNote.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel pnlBottom = new JPanel(new java.awt.GridLayout(2, 1, 0, 5));
        pnlBottom.add(lblRemind);
        pnlBottom.add(lblNote);
        
        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 15));
        panel.add(lblMessage, java.awt.BorderLayout.NORTH);
        panel.add(lblQr, java.awt.BorderLayout.CENTER);
        panel.add(pnlBottom, java.awt.BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(view, panel, "Thanh toán hóa đơn", JOptionPane.PLAIN_MESSAGE);
    }

    // ---------------------------------------------------------------- HELPERS

    private String getUserRoomId() {
        if (UserSession.getInstance() == null) return null;
        if ("ADMIN".equalsIgnoreCase(UserSession.getInstance().getRole())) return null;
        return UserSession.getInstance().getRoomId();
    }

    private String resolveTenantName(String roomId) {
        if (cachedInvoices != null) {
            for (Invoice inv : cachedInvoices) {
                if (roomId.equals(inv.getRoomId()) && !inv.getTenantName().isEmpty()) {
                    return inv.getTenantName();
                }
            }
        }
        return "Khách thuê " + roomId;
    }

    public String formatCurrency(long val) {
        return String.format("%,d", val).replace(',', '.');
    }

    public double parseAmount(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) return 0;
        str = str.trim();
        str = str.replaceAll("[^\\d\\.,-]", ""); 
        if (str.isEmpty()) throw new NumberFormatException("Empty after cleanup");

        int dotCount = str.length() - str.replace(".", "").length();
        int commaCount = str.length() - str.replace(",", "").length();
        
        if (dotCount > 1 || (dotCount == 1 && commaCount == 0 && str.lastIndexOf('.') < str.length() - 3)) {
            str = str.replace(".", "");
            str = str.replace(",", ".");
        } else if (commaCount > 1 || (commaCount == 1 && dotCount == 0 && str.lastIndexOf(',') < str.length() - 3)) {
            str = str.replace(",", "");
        } else if (commaCount == 1 && dotCount == 1) {
            if (str.indexOf(',') < str.indexOf('.')) {
                str = str.replace(",", "");
            } else {
                str = str.replace(".", "");
                str = str.replace(",", ".");
            }
        } else if (commaCount == 1 && dotCount == 0) {
            str = str.replace(",", ".");
        }

        return Double.parseDouble(str);
    }

    public java.util.Map<String, String> loadRoomTenantMap() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        try {
            java.util.List<common.model.Room> rooms = client.service.RoomService.getRooms();
            if (rooms != null) {
                for (common.model.Room r : rooms) {
                    map.put(r.getRoomId().trim(), ""); 
                }
            }
            
            java.util.List<String[]> tenants = client.service.TenantService.getTenants();
            if (tenants != null) {
                for (String[] t : tenants) {
                    String roomId = t[7] != null ? t[7].trim() : "";
                    String fullName = t[1];
                    if (!roomId.isEmpty() && !"null".equals(roomId) && !"Chưa có".equals(roomId)) {
                        String current = map.get(roomId);
                        if (current == null || current.isEmpty()) {
                            map.put(roomId, fullName);
                        } else {
                            map.put(roomId, current + ", " + fullName);
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return map;
    }

    public java.util.Map<String, Double> loadRoomPriceMap() {
        java.util.Map<String, Double> map = new java.util.HashMap<>();
        try {
            java.util.List<common.model.Room> rooms = client.service.RoomService.getRooms();
            if (rooms != null) {
                for (common.model.Room r : rooms) {
                    map.put(r.getRoomId(), r.getPrice());
                }
            }
        } catch (Exception e) {}
        return map;
    }

    public long[] getServicePrices() {
        long elecPrice = 3500;
        long waterPrice = 10000;
        try {
            java.util.List<String[]> svcs = client.service.ServicesService.getServices();
            if (svcs != null) {
                for (String[] s : svcs) {
                    try {
                        String priceStr = s[2].replace(",", "");
                        long val = (long) Double.parseDouble(priceStr);
                        if (s[1].toLowerCase().contains("điện")) elecPrice = val;
                        if (s[1].toLowerCase().contains("nước")) waterPrice = val;
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {}
        return new long[]{elecPrice, waterPrice};
    }
}
