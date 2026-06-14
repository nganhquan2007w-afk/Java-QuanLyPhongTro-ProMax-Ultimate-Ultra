package server.controller;

import server.exception.ServiceException;
import server.protocol.Request;
import server.protocol.Response;
import server.service.NotificationService;
import server.util.ServerLogger;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import common.model.Notification;

/**
 * NotificationController — nhận request, gọi NotificationService, trả response.
 */
public class NotificationController {

    private final NotificationService notificationService = new NotificationService();

    /** GET_NOTIFICATIONS */
    public Response handleGetNotifications(Request req) {
        String role = req.get("role");
        String roomId = req.get("roomId");
        
        List<Notification> notifications;
        if ("ADMIN".equalsIgnoreCase(role)) {
             notifications = notificationService.getAllNotifications();
        } else {
             notifications = notificationService.getNotificationsForRoom(roomId);
        }
        
        if (notifications.isEmpty()) return Response.success("EMPTY");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Map<String, Object>> list = new ArrayList<>();
        for (Notification n : notifications) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("title", n.getTitle());
            map.put("content", n.getContent());
            map.put("date", n.getDatePosted() != null ? sdf.format(n.getDatePosted()) : "");
            list.add(map);
        }
        return Response.successList("OK", list);
    }

    /** SEND_NOTIFICATION */
    public synchronized Response handleSendNotification(Request req) {
        try {
            String title   = req.get("title");
            String content = req.get("content");
            String roomId  = req.get("roomId"); // Can be null or empty for broadcast
            
            notificationService.addNotification(title, content, roomId);
            
            ServerLogger.info("Gui thong bao: " + title);
            return Response.success("Đã gửi thông báo thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
