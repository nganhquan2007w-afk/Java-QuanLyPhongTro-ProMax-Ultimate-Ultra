package server.controller;

import server.exception.ServiceException;
import server.protocol.Request;
import server.protocol.Response;
import server.service.MaintenanceService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.Maintenance;

/**
 * MaintenanceController — nhận request, gọi MaintenanceService, trả response.
 */
public class MaintenanceController {

    private final MaintenanceService maintenanceService = new MaintenanceService();

    /** GET_MAINTENANCE */
    public Response handleGetMaintenance(Request req) {
        String role   = req.get("role");
        String roomId = req.get("roomId");

        List<Maintenance> items = "ADMIN".equalsIgnoreCase(role)
            ? maintenanceService.getAllRequests()
            : maintenanceService.getRequestsByRoom(roomId);

        if (items.isEmpty()) return Response.success("EMPTY");

        List<Map<String, Object>> list = new ArrayList<>();
        for (Maintenance r : items) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",          String.valueOf(r.getId()));
            m.put("roomId",      r.getRoomId());
            m.put("description", r.getDescription());
            m.put("priority",    r.getPriority());
            m.put("reportDate",  r.getReportDate() != null ? r.getReportDate().toString() : "");
            m.put("status",      r.getStatus());
            list.add(m);
        }
        return Response.successList("OK", list);
    }

    /** ADD_MAINTENANCE */
    public Response handleAddMaintenance(Request req) {
        try {
            maintenanceService.addRequest(
                req.get("roomId"),
                req.get("description"),
                req.get("priority"),
                req.get("reportDate")
            );
            return Response.success("Đã gửi yêu cầu sửa chữa thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** UPDATE_MAINTENANCE_STATUS */
    public Response handleUpdateMaintenanceStatus(Request req) {
        try {
            maintenanceService.updateStatus(req.getInt("id", 0), req.get("status"));
            return Response.success("Đã cập nhật trạng thái thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
