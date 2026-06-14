package client.controller;

import client.view.panel.UserHomePanel;
import common.model.UserSession;
import client.service.InvoiceService;
import client.service.ServicesService;
import client.socket.SocketClient;
import server.protocol.Response;

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UserHomeController {
    private final UserHomePanel view;

    public UserHomeController(UserHomePanel view, Runnable refreshAll) {
        this.view = view;
        loadData(refreshAll);
    }

    public void loadData() {
        loadData(null);
    }

    private void loadData(Runnable refreshAll) {
        UserSession session = UserSession.getInstance();
        if (session == null) return;
        
        // Run async tasks to fetch data
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    Response roomResp = SocketClient.send("GET_ROOM_BY_USER", "username", session.getUsername());
                    if (roomResp.isSuccess()) {
                        String fetchedRoomId = roomResp.getDataString("roomId");
                        if (fetchedRoomId != null && !fetchedRoomId.trim().isEmpty()) {
                            session.setRoomId(fetchedRoomId);
                            if (refreshAll != null) {
                                javax.swing.SwingUtilities.invokeLater(() -> refreshAll.run());
                            }
                        }
                    }

                    String currentRoomId = session.getRoomId();
                    
                    // Cập nhật lên UI ngay sau khi lấy mã phòng
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        if (currentRoomId == null || currentRoomId.trim().isEmpty() || "Chưa có".equals(currentRoomId) || "null".equals(currentRoomId)) {
                            view.getLblWelcomeRoom().setText("Chưa nhận phòng");
                            view.getLblRoomValue().setText("-");
                            view.getLblRoomPrice().setText("0đ");
                            view.getLblDueDate().setText("-");
                            view.getLblBillStatus().setText("-");
                        } else {
                            view.getLblWelcomeRoom().setText("Phòng " + currentRoomId);
                            view.getLblRoomValue().setText(currentRoomId);
                        }
                    });

                    if (currentRoomId == null || currentRoomId.trim().isEmpty() || "Chưa có".equals(currentRoomId) || "null".equals(currentRoomId)) {
                        return null; // Không cần tải hóa đơn nếu chưa có phòng
                    }

                    // Fetch Room price
                    double roomPrice = 0;
                    java.util.List<common.model.Room> rooms = client.service.RoomService.getRooms();
                    if (rooms != null) {
                        for (common.model.Room r : rooms) {
                            if (currentRoomId.equals(r.getRoomId())) {
                                roomPrice = r.getPrice();
                                break;
                            }
                        }
                    }

                    // Fetch invoices for due date
                    List<common.model.Invoice> invoices = InvoiceService.getInvoices(currentRoomId);
                    String dueDateStr = "-";
                    String status = "Chưa có";
                    
                    if (invoices != null) {
                        for (int i = invoices.size() - 1; i >= 0; i--) {
                            common.model.Invoice inv = invoices.get(i);
                            if (currentRoomId.equals(inv.getRoomId())) {
                                status = inv.getStatus();
                                dueDateStr = client.util.DateFormatter.toDisplay(inv.getDueDate());
                                break; // Just take the latest invoice
                            }
                        }
                    }
                    
                    final double finalRoomPrice = roomPrice;
                    final String finalDueDate = dueDateStr;
                    final String finalStatus = status;

                    // Services
                    List<String[]> services = ServicesService.getServices();

                    // Fetch notifications
                    Response notifResp = SocketClient.send("GET_NOTIFICATIONS");
                    final String feedHtml;
                    if (notifResp.isSuccess() && !"EMPTY".equals(notifResp.getMessage())) {
                        java.util.List<java.util.Map<String, Object>> items = notifResp.getList();
                        if (items != null && !items.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("<html><body style='font-family:Segoe UI, sans-serif; font-size:12px; color:#475569; padding: 5px;'>");
                            for (int i = 0; i < items.size(); i++) {
                                java.util.Map<String, Object> item = items.get(i);
                                String title = (String) item.get("title");
                                String content = (String) item.get("content");
                                String date = (String) item.get("date");
                                sb.append("<div style='margin-bottom: 12px; padding: 10px; background-color: #F8FAFC; border-radius: 8px; border-left: 4px solid #3B82F6;'>");
                                sb.append("<div style='font-weight: bold; color: #0F172A; margin-bottom: 4px;'>").append(title).append("</div>");
                                sb.append("<div style='font-size: 10px; color: #94A3B8; margin-bottom: 6px;'>").append(date).append("</div>");
                                sb.append("<div style='color: #475569; line-height: 1.4;'>").append(content.replace("\n", "<br>")).append("</div>");
                                sb.append("</div>");
                            }
                            sb.append("</body></html>");
                            feedHtml = sb.toString();
                        } else {
                            feedHtml = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#475569;'><div style='text-align:center; padding-top:40px; color:#94A6B8;'>Không có thông báo mới từ chủ nhà.</div></body></html>";
                        }
                    } else {
                        feedHtml = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#475569;'><div style='text-align:center; padding-top:40px; color:#94A6B8;'>Không có thông báo mới từ chủ nhà.</div></body></html>";
                    }

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        view.getTxtFeed().setText(feedHtml);
                        view.getLblRoomPrice().setText(String.format("%,.0f đ", finalRoomPrice));
                        view.getLblDueDate().setText(finalDueDate);
                        view.getLblBillStatus().setText(finalStatus);

                        DefaultTableModel model = view.getSvcModel();
                        model.setRowCount(0);
                        if (services != null) {
                            for (String[] s : services) {
                                String name = s[1];
                                String price = s[2];
                                String unit = s[3];
                                try {
                                    double p = Double.parseDouble(price);
                                    price = String.format("%,.0f", p);
                                } catch (Exception ignored) {}
                                model.addRow(new Object[]{name, unit, price, "Chưa có dữ liệu"});
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
