package server.controller;

import server.exception.ServiceException;
import common.model.Invoice;
import server.protocol.Request;
import server.protocol.Response;
import server.service.InvoiceService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * InvoiceController — nhận request, gọi InvoiceService, trả response.
 * Không có validation, date parsing, hay business logic.
 */
public class InvoiceController {

    private final InvoiceService invoiceService = new InvoiceService();

    /** GET_INVOICES */
    public Response handleGetInvoices(Request req) {
        String role   = req.get("role");
        String roomId = req.get("roomId");

        List<Invoice> invoices = "ADMIN".equalsIgnoreCase(role)
            ? invoiceService.getAllInvoices()
            : invoiceService.getInvoicesByRoom(roomId);

        if (invoices.isEmpty()) return Response.success("EMPTY");

        List<Map<String, Object>> list = new ArrayList<>();
        for (Invoice inv : invoices) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("invoiceId",   inv.getInvoiceId());
            m.put("roomId",      inv.getRoomId());
            m.put("tenantName",  inv.getTenantName() != null ? inv.getTenantName() : "");
            m.put("issueDate",   inv.getIssueDate()  != null ? inv.getIssueDate().toString()  : "");
            m.put("dueDate",     inv.getDueDate()    != null ? inv.getDueDate().toString()    : "");
            m.put("rent",        inv.getRent());
            m.put("elecUsage",   inv.getElecUsage());
            m.put("waterUsage",  inv.getWaterUsage());
            m.put("otherFee",    inv.getOtherFee());
            m.put("status",      inv.getStatus() != null ? inv.getStatus() : "");
            m.put("paymentDate", inv.getPaymentDate() != null ? inv.getPaymentDate().toString() : "-");
            list.add(m);
        }
        return Response.successList("OK", list);
    }

    /** ADD_INVOICE */
    public Response handleAddInvoice(Request req) {
        try {
            int id = invoiceService.createInvoice(
                req.get("roomId"),
                req.get("tenantName"),
                req.get("issueDate"),
                req.getDouble("rent",       0.0),
                req.getDouble("elecUsage",  0.0),
                req.getDouble("waterUsage", 0.0),
                req.getDouble("otherFee",   0.0)
            );
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("invoiceId", id);
            return Response.success("Đã tạo hóa đơn thành công!", data);
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** UPDATE_INVOICE_STATUS */
    public Response handleUpdateInvoiceStatus(Request req) {
        try {
            invoiceService.updatePaymentStatus(
                req.getInt("invoiceId", 0),
                req.get("status"),
                req.get("paymentDate")
            );
            return Response.success("Cập nhật trạng thái thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
