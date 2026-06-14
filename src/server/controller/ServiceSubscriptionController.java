package server.controller;

import server.protocol.Request;
import server.protocol.Response;
import server.service.ServiceSubscriptionService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ServiceSubscriptionController {
    private final ServiceSubscriptionService service;

    public ServiceSubscriptionController() {
        this.service = new ServiceSubscriptionService();
    }

    public Response handleAddSubscription(Request req) {
        String roomId = req.get("roomId");
        String serviceName = req.get("serviceName");

        if (roomId == null || serviceName == null || roomId.trim().isEmpty() || serviceName.trim().isEmpty()) {
            return Response.fail("Thiếu thông tin phòng hoặc dịch vụ");
        }

        boolean success = service.addSubscription(roomId, serviceName);
        if (success) {
            return Response.success("Đăng ký dịch vụ thành công");
        } else {
            return Response.fail("Lỗi khi đăng ký dịch vụ");
        }
    }

    public Response handleGetSubscriptions(Request req) {
        String roomId = req.get("roomId");
        
        if (roomId == null || roomId.trim().isEmpty()) {
            return Response.fail("Thiếu mã phòng");
        }

        List<String[]> subs = service.getSubscriptionsByRoom(roomId);
        if (subs == null || subs.isEmpty()) {
            return Response.success("EMPTY");
        }

        List<Map<String, Object>> resList = new ArrayList<>();
        for (String[] row : subs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("serviceName", row[1]);
            map.put("status", row[2]);
            map.put("registeredAt", row[3]);
            resList.add(map);
        }

        return Response.successList("SUCCESS", resList);
    }

    public Response handleGetAllSubscriptions(Request req) {
        List<String[]> subs = service.getAllSubscriptions();
        if (subs == null || subs.isEmpty()) {
            return Response.success("EMPTY");
        }

        List<Map<String, Object>> resList = new ArrayList<>();
        for (String[] row : subs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("roomId", row[1]);
            map.put("serviceName", row[2]);
            map.put("status", row[3]);
            map.put("registeredAt", row[4]);
            resList.add(map);
        }

        return Response.successList("SUCCESS", resList);
    }

    public Response handleUpdateSubscriptionStatus(Request req) {
        int id = req.getInt("id", -1);
        String status = req.get("status");

        if (id == -1 || status == null || status.trim().isEmpty()) {
            return Response.fail("Thiếu id hoặc trạng thái");
        }

        boolean success = service.updateSubscriptionStatus(id, status);
        if (success) {
            return Response.success("Cập nhật trạng thái thành công");
        } else {
            return Response.fail("Lỗi cập nhật trạng thái");
        }
    }
}
