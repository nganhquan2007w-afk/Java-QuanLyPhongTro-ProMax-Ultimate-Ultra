package client.service;

import client.socket.SocketClient;
import server.protocol.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeedbackService {
    
    public static String addFeedback(String roomId, String title, String content, int rating) {
        Response resp = SocketClient.send("ADD_FEEDBACK", 
            "roomId", roomId, 
            "title", title,
            "content", content,
            "rating", String.valueOf(rating));
        if (resp != null && resp.isSuccess()) {
            return "SUCCESS";
        }
        return resp != null ? resp.getMessage() : "Không kết nối được server";
    }

    public static List<String[]> getMyFeedbacks(String roomId) {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_MY_FEEDBACKS", "roomId", roomId);
        
        if (resp != null && resp.isSuccess() && !"EMPTY".equals(resp.getMessage()) && resp.getList() != null) {
            for (Map<String, Object> map : resp.getList()) {
                String id = map.get("id") != null ? map.get("id").toString() : "";
                String title = map.get("title") != null ? map.get("title").toString() : "";
                String content = map.get("content") != null ? map.get("content").toString() : "";
                String rating = map.get("rating") != null ? map.get("rating").toString() : "";
                String status = map.get("status") != null ? map.get("status").toString() : "";
                String createdAt = map.get("createdAt") != null ? map.get("createdAt").toString() : "";
                list.add(new String[]{id, title, content, rating, status, createdAt});
            }
        }
        return list;
    }

    public static List<String[]> getAllFeedbacks() {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_ALL_FEEDBACKS");
        
        if (resp != null && resp.isSuccess() && !"EMPTY".equals(resp.getMessage()) && resp.getList() != null) {
            for (Map<String, Object> map : resp.getList()) {
                String id = map.get("id") != null ? map.get("id").toString() : "";
                String rId = map.get("roomId") != null ? map.get("roomId").toString() : "";
                String title = map.get("title") != null ? map.get("title").toString() : "";
                String content = map.get("content") != null ? map.get("content").toString() : "";
                String rating = map.get("rating") != null ? map.get("rating").toString() : "";
                String status = map.get("status") != null ? map.get("status").toString() : "";
                String createdAt = map.get("createdAt") != null ? map.get("createdAt").toString() : "";
                list.add(new String[]{id, rId, title, content, rating, status, createdAt});
            }
        }
        return list;
    }

    public static String updateFeedbackStatus(int id, String newStatus) {
        Response resp = SocketClient.send("UPDATE_FEEDBACK_STATUS", 
            "id", String.valueOf(id), 
            "status", newStatus);
        if (resp != null && resp.isSuccess()) {
            return "SUCCESS";
        }
        return resp != null ? resp.getMessage() : "Không kết nối được server";
    }
}
