package client.service;

import common.model.Invoice;
import common.model.UserSession;
import client.socket.SocketClient;
import server.protocol.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * InvoiceService (Client-side) — Giao tiếp với server qua JSON protocol.
 *
 * NOTE: Tính tiền hóa đơn (calcTotal) đã được chuyển về server/service/InvoiceService.java.
 * Client chỉ hiển thị, không tự tính business logic nữa.
 */
public class InvoiceService {

    /**
     * Lấy danh sách hóa đơn từ server.
     * Admin: tất cả. User: theo phòng.
     */
    public static List<Invoice> getInvoices(String roomId) {
        List<Invoice> list = new ArrayList<>();

        String role = "USER";
        if (UserSession.getInstance() != null) {
            role = UserSession.getInstance().getRole();
        }

        Response resp = SocketClient.send("GET_INVOICES",
            "role",   role,
            "roomId", roomId != null ? roomId : ""
        );

        if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) return list;

        List<Map<String, Object>> items = resp.getList();
        if (items == null) return list;

        for (Map<String, Object> item : items) {
            Invoice inv = parseInvoice(item);
            if (inv != null) list.add(inv);
        }
        return list;
    }

    /**
     * Tạo hóa đơn mới.
     * @return "SUCCESS" nếu thành công, thông báo lỗi nếu thất bại.
     */
    public static String addInvoice(String roomId, String tenantName, String issueDate,
                                     double rent, double elecUsage, double waterUsage, double otherFee) {
        Response resp = SocketClient.send("ADD_INVOICE",
            "roomId",     roomId     != null ? roomId     : "",
            "tenantName", tenantName != null ? tenantName : "",
            "issueDate",  issueDate  != null ? issueDate  : "",
            "rent",       String.valueOf(rent),
            "elecUsage",  String.valueOf(elecUsage),
            "waterUsage", String.valueOf(waterUsage),
            "otherFee",   String.valueOf(otherFee)
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Cập nhật trạng thái thanh toán hóa đơn.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String updateInvoiceStatus(int invoiceId, String status, String paymentDate) {
        Response resp = SocketClient.send("UPDATE_INVOICE_STATUS",
            "invoiceId",   String.valueOf(invoiceId),
            "status",      status      != null ? status      : "",
            "paymentDate", paymentDate != null ? paymentDate : "-"
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    // ---- Parser ----

    private static Invoice parseInvoice(Map<String, Object> item) {
        try {
            Invoice inv = new Invoice();
            inv.setInvoiceId(parseInt(str(item, "invoiceId")));
            inv.setRoomId(str(item, "roomId"));
            inv.setTenantName(str(item, "tenantName"));
            inv.setIssueDate(parseSqlDate(str(item, "issueDate")));
            inv.setDueDate(parseSqlDate(str(item, "dueDate")));
            inv.setRent(parseDouble(str(item, "rent")));
            inv.setElecUsage(parseDouble(str(item, "elecUsage")));
            inv.setWaterUsage(parseDouble(str(item, "waterUsage")));
            inv.setOtherFee(parseDouble(str(item, "otherFee")));
            inv.setStatus(str(item, "status"));
            inv.setPaymentDate(parseSqlDate(str(item, "paymentDate")));
            return inv;
        } catch (Exception e) {
            System.err.println("Lỗi parse Invoice: " + e.getMessage());
            return null;
        }
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }

    private static int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try { 
            if (s.contains(".")) return (int) Double.parseDouble(s.trim());
            return Integer.parseInt(s.trim()); 
        } catch (Exception e) { return 0; }
    }

    private static double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    private static java.sql.Date parseSqlDate(String s) {
        if (s == null || s.trim().isEmpty() || "-".equals(s.trim())) return null;
        try {
            return java.sql.Date.valueOf(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
