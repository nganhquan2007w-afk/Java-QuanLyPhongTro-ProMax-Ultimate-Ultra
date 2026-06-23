package client.controller;


import client.view.panel.AdminHomePanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * AdminHomeController - Controller (MVC) cho màn hình Trang Chủ Admin.
 *
 * Vai trò: THIN DELEGATE — chỉ gắn listener cho 3 nút hành động nhanh
 * và ủy quyền xử lý cho các Controller chuyên dụng tương ứng.
 *
 * Nguyên tắc "Single Responsibility":
 *   - Thêm phòng   → RoomsController.openAddRoomDialog()
 *   - Thêm khách   → TenantsController.openAddTenantDialog()
 *   - Lập hóa đơn → InvoicesController.openAddInvoiceDialog()
 *
 * AdminHomeController KHÔNG chứa bất kỳ business logic nào.
 * Toàn bộ logic (load room list, validate, gọi Service...) nằm trong
 * controller chuyên dụng của từng chức năng.
 */
public class AdminHomeController {

    private final AdminHomePanel      view;
    private final RoomsController     roomsController;
    private final TenantsController   tenantsController;
    private final InvoicesController  invoicesController;

    /**
     * @param view              AdminHomePanel cần gắn listener
     * @param roomsController   Controller quản lý phòng (đã khởi tạo sẵn)
     * @param tenantsController Controller quản lý khách thuê (đã khởi tạo sẵn)
     * @param invoicesController Controller quản lý hóa đơn (đã khởi tạo sẵn)
     */
    public AdminHomeController(AdminHomePanel     view,
                               RoomsController    roomsController,
                               TenantsController  tenantsController,
                               InvoicesController invoicesController) {
        this.view               = view;
        this.roomsController    = roomsController;
        this.tenantsController  = tenantsController;
        this.invoicesController = invoicesController;
        initController();
        loadDashboardData();
    }

    /**
     * Gắn ActionListener cho 3 nút lối tắt trên AdminHomePanel.
     * Mỗi nút chỉ delegate tới controller chuyên dụng — không tự xử lý.
     */
    private void initController() {
        // Nút "Thêm Phòng Mới" → RoomsController xử lý (logic nằm ở đó)
        view.getBtnAddRoom().addActionListener(
            e -> roomsController.openAddRoomDialog()
        );

        // Nút "Thêm Khách Thuê" → TenantsController xử lý (logic nằm ở đó)
        view.getBtnAddTenant().addActionListener(
            e -> tenantsController.openAddTenantDialog()
        );

        // Nút "Lập Hóa Đơn Mới" → InvoicesController xử lý (logic nằm ở đó)
        view.getBtnCreateBill().addActionListener(
            e -> invoicesController.openAddInvoiceDialog()
        );

        // Nút "Nhập Dữ Liệu (Excel)" → RoomsController xử lý
        view.getBtnImportData().addActionListener(
            e -> roomsController.handleImportCSV()
        );

        // Nút "Xuất Báo Cáo (Excel)" → RoomsController xử lý
        view.getBtnExportData().addActionListener(
            e -> roomsController.handleExportCSV()
        );
    }

    /**
     * Tải dữ liệu tổng quan cho Dashboard (Chạy ngầm tránh block UI)
     */
    public void loadDashboardData() {
        new SwingWorker<Void, Void>() {
            java.util.List<common.model.Room> rooms;
            java.util.List<common.model.Invoice> invoices;
            java.util.List<String[]> tenants;

            @Override
            protected Void doInBackground() throws Exception {
                rooms   = client.service.RoomService.getRooms();
                invoices = client.service.InvoiceService.getInvoices(null);
                tenants  = client.service.TenantService.getTenants();
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (rooms != null) {
                        int total = rooms.size();

                        // --- FIX: Đếm phòng đã thuê dựa trên khách thuê thực tế, không phụ thuộc vào status phòng ---
                        // Tập hợp room_id của các khách đang có phòng
                        java.util.Set<String> rentedRoomIds = new java.util.HashSet<>();
                        if (tenants != null) {
                            for (String[] t : tenants) {
                                if (t[7] != null && !t[7].trim().isEmpty()) {
                                    rentedRoomIds.add(t[7].trim());
                                }
                            }
                        }
                        long rented = rentedRoomIds.size(); // số phòng thực sự có khách

                        // Phòng bảo trì: dựa vào status (không phụ thuộc tenant)
                        long maintenance = rooms.stream().filter(r -> {
                            String s = r.getStatus();
                            return "Đang sửa chữa".equalsIgnoreCase(s)
                                || "Đang bảo trì".equalsIgnoreCase(s)
                                || "Bảo trì".equalsIgnoreCase(s);
                        }).count();

                        long empty = total - rented - maintenance;
                        if (empty < 0) empty = 0;

                        if (view.getLblTotalRoomsVal()  != null) view.getLblTotalRoomsVal() .setText(total   + " Phòng");
                        if (view.getLblRentedRoomsVal() != null) view.getLblRentedRoomsVal().setText(rented  + " Phòng");
                        if (view.getLblEmptyRoomsVal()  != null) view.getLblEmptyRoomsVal() .setText(empty   + " Phòng");

                        if (view.getLblTotalRoomsDesc() != null) {
                            if (maintenance > 0) {
                                view.getLblTotalRoomsDesc().setText(String.format
                                    ("(%d ph\u00f2ng kh\u1ea3 d\u1ee5ng \u2014 %d ph\u00f2ng b\u1ea3o tr\u00ec)", empty, maintenance));
                            } else {
                                view.getLblTotalRoomsDesc().setText(String.format
                                    ("(%d ph\u00f2ng \u0111ang kh\u1ea3 d\u1ee5ng)", empty));
                            }
                        }
                        if (view.getLblRentedRoomsDesc() != null) view.getLblRentedRoomsDesc().setText("(Phòng đang có khách)");
                        if (view.getLblEmptyRoomsDesc()  != null) view.getLblEmptyRoomsDesc() .setText("(Sẵn sàng cho thuê)");
                    }

                    if (invoices != null) {
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

                        long revenue = 0;
                        for (common.model.Invoice inv : invoices) {
                            if ("Đã thanh toán".equalsIgnoreCase(inv.getStatus())) {
                                revenue += (long)(inv.getRent() + inv.getElecUsage() * elecPrice + inv.getWaterUsage() * waterPrice + inv.getOtherFee());
                            }
                        }
                        String revStr = String.format("%,d", revenue).replace(',', '.') + "đ";
                        if (view.getLblRevenueVal() != null) view.getLblRevenueVal().setText(revStr);
                        if (view.getLblRevenueDesc() != null) view.getLblRevenueDesc().setText("(Từ các HĐ đã thanh toán)");

                        // Populate recent invoices table (last 10)
                        javax.swing.table.DefaultTableModel model = view.getTableModel();
                        if (model != null) {
                            model.setRowCount(0);
                            
                            java.util.List<common.model.Invoice> recent = new java.util.ArrayList<>(invoices);
                            // Sắp xếp ID giảm dần (mới nhất lên đầu)
                            recent.sort((a, b) -> Integer.compare(b.getInvoiceId(), a.getInvoiceId()));
                            
                            int count = 0;
                            for (common.model.Invoice inv : recent) {
                                if (count >= 10) break;
                                long totalVal = (long)(inv.getRent() + inv.getElecUsage() * 3500 + inv.getWaterUsage() * 10000 + inv.getOtherFee());
                                String totalFmt = String.format("%,d", totalVal).replace(',', '.') + " VND";
                                String dateFmt = client.util.DateFormatter.toDisplay(inv.getIssueDate());
                                
                                model.addRow(new Object[]{
                                    "HD" + String.format("%03d", inv.getInvoiceId()),
                                    inv.getTenantName(),
                                    dateFmt,
                                    totalFmt,
                                    inv.getStatus()
                                });
                                count++;
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi load dashboard: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
