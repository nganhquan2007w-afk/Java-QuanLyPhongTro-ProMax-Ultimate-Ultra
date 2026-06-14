package client.service;

import client.socket.SocketClient;
import server.protocol.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceSubscriptionService {
    
    public static String addSubscription(String roomId, String serviceName) {
        Response resp = SocketClient.send("ADD_SUBSCRIPTION", 
            "roomId", roomId, 
            "serviceName", serviceName);
        if (resp != null && resp.isSuccess()) {
            return "SUCCESS";
        }
        return resp != null ? resp.getMessage() : "Không kết nối được server";
    }

    public static List<String[]> getSubscriptions(String roomId) {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_SUBSCRIPTIONS", "roomId", roomId);
        
        if (resp != null && resp.isSuccess() && !"EMPTY".equals(resp.getMessage()) && resp.getList() != null) {
            for (Map<String, Object> map : resp.getList()) {
                String id = map.get("id") != null ? map.get("id").toString() : "";
                String name = map.get("serviceName") != null ? map.get("serviceName").toString() : "";
                String status = map.get("status") != null ? map.get("status").toString() : "";
                String regAt = map.get("registeredAt") != null ? map.get("registeredAt").toString() : "";
                list.add(new String[]{id, name, status, regAt});
            }
        }
        return list;
    }

    public static List<String[]> getAllSubscriptions() {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_ALL_SUBSCRIPTIONS");
        
        if (resp != null && resp.isSuccess() && !"EMPTY".equals(resp.getMessage()) && resp.getList() != null) {
            for (Map<String, Object> map : resp.getList()) {
                String id = map.get("id") != null ? map.get("id").toString() : "";
                String rId = map.get("roomId") != null ? map.get("roomId").toString() : "";
                String name = map.get("serviceName") != null ? map.get("serviceName").toString() : "";
                String status = map.get("status") != null ? map.get("status").toString() : "";
                String regAt = map.get("registeredAt") != null ? map.get("registeredAt").toString() : "";
                list.add(new String[]{id, rId, name, status, regAt});
            }
        }
        return list;
    }

    public static String updateSubscriptionStatus(int id, String newStatus) {
        Response resp = SocketClient.send("UPDATE_SUBSCRIPTION_STATUS", 
            "id", String.valueOf(id), 
            "status", newStatus);
        if (resp != null && resp.isSuccess()) {
            return "SUCCESS";
        }
        return resp != null ? resp.getMessage() : "Không kết nối được server";
    }
}
