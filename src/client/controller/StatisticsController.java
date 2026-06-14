package client.controller;

import client.view.panel.StatisticsPanel;
import common.model.Room;
import common.model.Invoice;
import client.service.RoomService;
import client.service.InvoiceService;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.Calendar;

public class StatisticsController {
    private final StatisticsPanel view;

    public StatisticsController(StatisticsPanel view) {
        this.view = view;
        loadStatistics();
    }

    public void loadStatistics() {
        new SwingWorker<Void, Void>() {
            List<Room> rooms;
            List<Invoice> invoices;

            @Override
            protected Void doInBackground() throws Exception {
                rooms = RoomService.getRooms();
                invoices = InvoiceService.getInvoices(null);
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (rooms != null) {
                        int total = rooms.size();
                        long rented = rooms.stream().filter(r -> {
                            String s = r.getStatus();
                            return "Đã thuê".equalsIgnoreCase(s) || "Đang thuê".equalsIgnoreCase(s) || "Đã đầy".equalsIgnoreCase(s);
                        }).count();
                        long empty = rooms.stream().filter(r -> {
                            String s = r.getStatus();
                            return "Còn trống".equalsIgnoreCase(s) || "Trống".equalsIgnoreCase(s) || "Đang trống".equalsIgnoreCase(s);
                        }).count();
                        long fixing = rooms.stream().filter(r -> "Đang sửa chữa".equalsIgnoreCase(r.getStatus())).count();

                        int percentRented = total == 0 ? 0 : (int) ((rented * 100) / total);
                        int percentEmpty = total == 0 ? 0 : (int) ((empty * 100) / total);
                        int percentFixing = total == 0 ? 0 : (int) ((fixing * 100) / total);

                        if (view.getRing() != null) {
                            view.getRing().setProgress(percentRented);
                        }
                        if (view.getLblStat1() != null) {
                            view.getLblStat1().setText(String.format("🟢   Phòng đang hoạt động: %d phòng (%d%%)", rented, percentRented));
                        }
                        if (view.getLblStat2() != null) {
                            view.getLblStat2().setText(String.format("⚪   Phòng trống sẵn sàng: %d phòng (%d%%)", empty, percentEmpty));
                        }
                        if (view.getLblStat3() != null) {
                            view.getLblStat3().setText(String.format("🛠️   Phòng đang sửa chữa: %d phòng (%d%%)", fixing, percentFixing));
                        }
                    }

                    if (invoices != null) {
                        int[] rev6Months = new int[6];
                        String[] labels = new String[6];
                        
                        for (int i = 0; i < 6; i++) {
                            Calendar tempCal = Calendar.getInstance();
                            tempCal.add(Calendar.MONTH, -(5 - i));
                            labels[i] = "T" + (tempCal.get(Calendar.MONTH) + 1);
                        }

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

                        long totalRevenue = 0;
                        long thisMonthRev = 0;
                        long lastMonthRev = 0;

                        Calendar cal = Calendar.getInstance();

                        for (Invoice inv : invoices) {
                            if (!"Đã thanh toán".equalsIgnoreCase(inv.getStatus())) continue;
                            
                            long totalVal = (long)(inv.getRent() + inv.getElecUsage() * elecPrice + inv.getWaterUsage() * waterPrice + inv.getOtherFee());
                            
                            try {
                                java.util.Date issueDate = inv.getIssueDate();
                                cal.setTime(issueDate);
                            } catch (Exception e) {
                                continue;
                            }
                            
                            int invMonth = cal.get(Calendar.MONTH);
                            int invYear = cal.get(Calendar.YEAR);
                            
                            for (int i = 0; i < 6; i++) {
                                Calendar tempCal = Calendar.getInstance();
                                tempCal.add(Calendar.MONTH, -(5 - i));
                                if (tempCal.get(Calendar.MONTH) == invMonth && tempCal.get(Calendar.YEAR) == invYear) {
                                    rev6Months[i] += (int)(totalVal / 1000000); // in millions
                                    totalRevenue += totalVal;
                                    
                                    if (i == 5) thisMonthRev += totalVal;
                                    if (i == 4) lastMonthRev += totalVal;
                                    break;
                                }
                            }
                        }

                        if (view.getBarChart() != null) {
                            view.getBarChart().setData(rev6Months, labels);
                        }
                        if (view.getLblS1Val() != null) {
                            view.getLblS1Val().setText(String.format("%,d", totalRevenue).replace(',', '.') + " đ");
                        }

                        if (view.getLblGrowth() != null) {
                            if (lastMonthRev > 0) {
                                double growth = ((double)(thisMonthRev - lastMonthRev) / lastMonthRev) * 100;
                                if (growth >= 0) {
                                    view.getLblGrowth().setText(String.format("So với tháng trước: Tăng %.1f%%", growth));
                                    view.getLblGrowth().setForeground(new java.awt.Color(16, 185, 129));
                                } else {
                                    view.getLblGrowth().setText(String.format("So với tháng trước: Giảm %.1f%%", -growth));
                                    view.getLblGrowth().setForeground(new java.awt.Color(239, 68, 68));
                                }
                            } else {
                                if (thisMonthRev > 0) {
                                    view.getLblGrowth().setText("So với tháng trước: Tăng 100%");
                                    view.getLblGrowth().setForeground(new java.awt.Color(16, 185, 129));
                                } else {
                                    view.getLblGrowth().setText("So với tháng trước: Không biến động");
                                    view.getLblGrowth().setForeground(new java.awt.Color(148, 163, 184));
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi load statistics: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
