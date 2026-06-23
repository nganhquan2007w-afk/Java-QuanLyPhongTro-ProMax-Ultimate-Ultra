package client.util;

import common.model.Invoice;
import common.model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Tiện ích hỗ trợ Xuất và Nhập file CSV, dùng chung cho tất cả các Controller.
 * Giúp giảm thiểu code trùng lặp và "giảm cân" cho Controller.
 */
public class CSVHelper {

    /**
     * Mở hộp thoại lưu file và xuất dữ liệu từ JTable ra file CSV.
     * @param parent Component cha (để hiển thị hộp thoại)
     * @param table  JTable chứa dữ liệu cần xuất
     * @param defaultFileName Tên file mặc định (VD: "DanhSachPhong.csv")
     */
    public static void exportTableToCSV(JComponent parent, JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file CSV");
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }
            
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
                // Ghi BOM để Excel đọc tiếng Việt UTF-8 chuẩn
                pw.write('\ufeff');
                
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int colCount = model.getColumnCount();
                
                // Ghi header
                for (int i = 0; i < colCount; i++) {
                    pw.print("\"" + model.getColumnName(i).replace("\"", "\"\"") + "\"");
                    if (i < colCount - 1) pw.print(",");
                }
                pw.println();
                
                // Ghi data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < colCount; j++) {
                        Object val = model.getValueAt(i, j);
                        String strVal = (val == null) ? "" : val.toString();
                        pw.print("\"" + strVal.replace("\"", "\"\"") + "\"");
                        if (j < colCount - 1) pw.print(",");
                    }
                    pw.println();
                }
                
                JOptionPane.showMessageDialog(parent, "Xuất CSV thành công:\n" + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xuất báo cáo tổng hợp gồm nhiều section:
     *   1. Tổng Quan Thống Kê
     *   2. Danh Sách Phòng
     *   3. Danh Sách Khách Thuê
     *   4. Danh Sách Hóa Đơn
     *   5. Tổng Kết Doanh Thu
     */
    public static void exportFullReport(
            JComponent parent,
            List<Room>    rooms,
            List<String[]> tenants,
            List<Invoice>  invoices,
            long elecPrice,
            long waterPrice) {

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu Báo Cáo Tổng Hợp");
        String stamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmm")
                           .format(new java.util.Date());
        fc.setSelectedFile(new File("BaoCaoTongHop_" + stamp + ".csv"));

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv"))
            file = new File(file.getParentFile(), file.getName() + ".csv");

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {

            pw.write('\ufeff'); // BOM — Excel đọc tiếng Việt chuẩn

            // ─── 1. Tổng Quan ───────────────────────────────────────────────
            pw.println("\"=== TỔNG QUAN THỐNG KÊ ===");
            pw.println("\"Ngày xuất báo cáo\",\"" +
                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "\"");
            pw.println();

            int totalRooms = (rooms == null) ? 0 : rooms.size();

            // Đếm phòng theo tenant thực tế (nhất quán với dashboard)
            Set<String> rentedIds = new HashSet<>();
            if (tenants != null) {
                for (String[] t : tenants)
                    if (t[7] != null && !t[7].trim().isEmpty()) rentedIds.add(t[7].trim());
            }
            long rentedRooms = rentedIds.size();

            long maintenanceRooms = (rooms == null) ? 0 : rooms.stream().filter(r -> {
                String s = r.getStatus();
                return "Đang sửa chữa".equalsIgnoreCase(s)
                    || "Đang bảo trì".equalsIgnoreCase(s)
                    || "Bảo trì".equalsIgnoreCase(s);
            }).count();

            long emptyRooms = totalRooms - rentedRooms - maintenanceRooms;
            if (emptyRooms < 0) emptyRooms = 0;

            long totalTenants = (tenants == null) ? 0 :
                tenants.stream().filter(t -> t[7] != null && !t[7].trim().isEmpty()).count();

            // Doanh thu: chỉ hóa đơn Đã thanh toán
            long totalRevenue = 0, paidCount = 0, unpaidCount = 0;
            if (invoices != null) {
                for (Invoice inv : invoices) {
                    long val = (long)(inv.getRent()
                        + inv.getElecUsage()  * elecPrice
                        + inv.getWaterUsage() * waterPrice
                        + inv.getOtherFee());
                    if ("Đã thanh toán".equalsIgnoreCase(inv.getStatus())) {
                        totalRevenue += val;
                        paidCount++;
                    } else {
                        unpaidCount++;
                    }
                }
            }

            int pctRented = totalRooms == 0 ? 0 : (int)((rentedRooms * 100) / totalRooms);

            pw.println("\"Chỉ Số\",\"Giá Trị\"");
            pw.println("\"Tổng số phòng\",\"" + totalRooms + " phòng\"");
            pw.println("\"Phòng đang có khách\",\"" + rentedRooms + " phòng\"");
            pw.println("\"Phòng còn trống\",\"" + emptyRooms + " phòng\"");
            pw.println("\"Phòng bảo trì/sửa chữa\",\"" + maintenanceRooms + " phòng\"");
            pw.println("\"Tỉ lệ lấp đầy\",\"" + pctRented + "%\"");
            pw.println("\"Tổng khách đang thuê\",\"" + totalTenants + " người\"");
            pw.println("\"Tổng doanh thu (đã thanh toán)\",\"" +
                String.format("%,d", totalRevenue).replace(',', '.') + " VNĐ\"");
            pw.println("\"Số hóa đơn đã thanh toán\",\"" + paidCount + " hóa đơn\"");
            pw.println("\"Số hóa đơn chưa thanh toán\",\"" + unpaidCount + " hóa đơn\"");
            pw.println();

            // ─── 2. Danh Sách Phòng ──────────────────────────────────────────
            pw.println("\"=== DANH SÁCH PHÒNG ===");
            pw.println("\"Mã Phòng\",\"Khu Vực\",\"Loại Phòng\",\"Giá Thuê (VNĐ)\",\"Sức Chứa\",\"Khách Hiện Tại\",\"Trạng Thái\",\"Mô Tả\"");
            if (rooms != null) {
                for (Room r : rooms) {
                    int cur = (int) tenants.stream()
                        .filter(t -> r.getRoomId().equals(t[7] != null ? t[7].trim() : "")).count();
                    pw.println(String.format("\"%s\",\"%s\",\"%s\",\"%,.0f\",\"%d\",\"%d/%d\",\"%s\",\"%s\"",
                        esc(r.getRoomId()), esc(r.getZone()), esc(r.getType()),
                        r.getPrice(), r.getCapacity(), cur, r.getCapacity(),
                        esc(r.getStatus()), esc(r.getDescription())));
                }
            }
            pw.println();

            // ─── 3. Danh Sách Khách Thuê ──────────────────────────────────────
            pw.println("\"=== DANH SÁCH KHÁCH THUÊ ===");
            pw.println("\"STT\",\"Họ Tên\",\"Số Điện Thoại\",\"CCCD\",\"Giới Tính\",\"Ngày Sinh\",\"Địa Chỉ\",\"Phòng\",\"Ngày Vào\",\"Ngày Kết Thúc HĐ\"");
            if (tenants != null) {
                int row = 1;
                for (String[] t : tenants) {
                    // t: [0]id [1]name [2]phone [3]cccd [4]gender [5]birth [6]addr [7]roomId [8]start [9]end
                    pw.println(String.format("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        row++,
                        esc(t[1]), esc(t[2]), esc(t[3]), esc(t[4]),
                        esc(t[5]), esc(t[6]), esc(t[7]), esc(t[8]), esc(t[9])));
                }
            }
            pw.println();

            // ─── 4. Danh Sách Hóa Đơn ───────────────────────────────────────
            pw.println("\"=== DANH SÁCH HÓA ĐƠN ===");
            pw.println("\"Mã HĐ\",\"Phòng\",\"Khách Thuê\",\"Ngày Lập\",\"Hạn Thanh Toán\",\"Tiền Phòng\",\"Điện\",\"Nước\",\"Phí Khác\",\"Tổng Tiền\",\"Trạng Thái\",\"Ngày Thanh Toán\"");
            if (invoices != null) {
                for (Invoice inv : invoices) {
                    long total = (long)(inv.getRent()
                        + inv.getElecUsage()  * elecPrice
                        + inv.getWaterUsage() * waterPrice
                        + inv.getOtherFee());
                    pw.println(String.format(
                        "\"HD%03d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%,.0f\",\"%,.0f\",\"%,.0f\",\"%,.0f\",\"%s\",\"%s\",\"%s\"",
                        inv.getInvoiceId(),
                        esc(inv.getRoomId()), esc(inv.getTenantName()),
                        DateFormatter.toDisplay(inv.getIssueDate()),
                        DateFormatter.toDisplay(inv.getDueDate()),
                        inv.getRent(),
                        inv.getElecUsage()  * elecPrice,
                        inv.getWaterUsage() * waterPrice,
                        inv.getOtherFee(),
                        String.format("%,d", total).replace(',', '.') + " VNĐ",
                        esc(inv.getStatus()),
                        DateFormatter.toDisplay(inv.getPaymentDate())));
                }
            }
            pw.println();

            // ─── 5. Tổng Kết Doanh Thu ───────────────────────────────────────
            pw.println("\"=== TỔNG KẾT DOANH THU ===");
            pw.println("\"Loại\",\"Số Tiền (VNĐ)\"");
            // Doanh thu theo tháng (6 tháng gần nhất)
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Map<String, Long> monthlyRev = new java.util.LinkedHashMap<>();
            for (int i = 5; i >= 0; i--) {
                java.util.Calendar tmp = java.util.Calendar.getInstance();
                tmp.add(java.util.Calendar.MONTH, -i);
                String key = String.format("Tháng %02d/%d",
                    tmp.get(java.util.Calendar.MONTH) + 1, tmp.get(java.util.Calendar.YEAR));
                monthlyRev.put(key, 0L);
            }
            if (invoices != null) {
                for (Invoice inv : invoices) {
                    if (!"Đã thanh toán".equalsIgnoreCase(inv.getStatus())) continue;
                    if (inv.getIssueDate() == null) continue;
                    cal.setTime(inv.getIssueDate());
                    String key = String.format("Tháng %02d/%d",
                        cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.YEAR));
                    if (monthlyRev.containsKey(key)) {
                        long val = (long)(inv.getRent()
                            + inv.getElecUsage()  * elecPrice
                            + inv.getWaterUsage() * waterPrice
                            + inv.getOtherFee());
                        monthlyRev.put(key, monthlyRev.get(key) + val);
                    }
                }
            }
            for (java.util.Map.Entry<String, Long> e : monthlyRev.entrySet()) {
                pw.println("\"" + e.getKey() + "\",\"" +
                    String.format("%,d", e.getValue()).replace(',', '.') + " VNĐ\"");
            }
            pw.println("\"TỔNG DOANH THU\",\"" +
                String.format("%,d", totalRevenue).replace(',', '.') + " VNĐ\"");

            JOptionPane.showMessageDialog(parent,
                "Xuất báo cáo thành công!\n" + file.getAbsolutePath(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                "Lỗi khi lưu báo cáo: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Escape dấu nháy kép trong CSV */
    private static String esc(Object val) {
        if (val == null) return "";
        return val.toString().replace("\"", "\"\"");
    }

    /**
     * Mở hộp thoại chọn file CSV, đọc nội dung (bỏ qua header) và mã hóa sang chuỗi Base64.
     * Cần thiết để truyền dữ liệu văn bản thuần tùy ý qua Socket an toàn.
     * @param parent Component cha
     * @return Chuỗi mã hóa Base64 của file CSV (không chứa header), hoặc null nếu hủy bỏ hoặc file trống.
     */
    public static String readCSVToBase64(JComponent parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file CSV để nhập");
        
        int userSelection = fileChooser.showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToOpen), "UTF-8"))) {
                String line;
                boolean isFirstLine = true;
                StringBuilder csvContent = new StringBuilder();
                
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        // Bỏ qua BOM nếu có
                        if (line.startsWith("\uFEFF")) {
                            line = line.substring(1);
                        }
                        // Bỏ qua dòng Header
                        continue;
                    }
                    if (line.trim().isEmpty()) continue;
                    csvContent.append(line).append("\n");
                }
                
                if (csvContent.length() == 0) {
                    JOptionPane.showMessageDialog(parent, "File CSV trống hoặc sai định dạng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return null;
                }

                return Base64.getEncoder().encodeToString(csvContent.toString().getBytes("UTF-8"));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Lỗi khi đọc file CSV: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null; // Bị hủy bỏ
    }
}
