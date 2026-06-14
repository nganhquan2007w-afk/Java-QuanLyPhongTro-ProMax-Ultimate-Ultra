package server.controller;

import server.protocol.Request;
import server.protocol.Response;
import server.service.FeedbackService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackController {
    private final FeedbackService service;

    public FeedbackController() {
        this.service = new FeedbackService();
    }

    public Response handleAddFeedback(Request req) {
        String roomId = req.get("roomId");
        String title = req.get("title");
        String content = req.get("content");
        Integer rating = req.getInt("rating", 5);

        if (roomId == null || title == null || content == null || rating == null || roomId.trim().isEmpty() || title.trim().isEmpty() || content.trim().isEmpty()) {
            return Response.fail("Thiếu thông tin");
        }

        boolean success = service.addFeedback(roomId, title, content, rating);
        if (success) {
            return Response.success("Gửi phản hồi thành công");
        } else {
            return Response.fail("Lỗi gửi phản hồi");
        }
    }

    public Response handleGetMyFeedbacks(Request req) {
        String roomId = req.get("roomId");
        if (roomId == null || roomId.trim().isEmpty()) {
            return Response.fail("Thiếu mã phòng");
        }

        List<String[]> list = service.getFeedbacksByRoom(roomId);
        if (list == null || list.isEmpty()) {
            return Response.success("EMPTY");
        }

        List<Map<String, Object>> resList = new ArrayList<>();
        for (String[] r : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r[0]);
            map.put("title", r[1]);
            map.put("content", r[2]);
            map.put("rating", r[3]);
            map.put("status", r[4]);
            map.put("createdAt", r[5]);
            resList.add(map);
        }

        return Response.successList("SUCCESS", resList);
    }

    public Response handleGetAllFeedbacks(Request req) {
        List<String[]> list = service.getAllFeedbacks();
        if (list == null || list.isEmpty()) {
            return Response.success("EMPTY");
        }

        List<Map<String, Object>> resList = new ArrayList<>();
        for (String[] r : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r[0]);
            map.put("roomId", r[1]);
            map.put("title", r[2]);
            map.put("content", r[3]);
            map.put("rating", r[4]);
            map.put("status", r[5]);
            map.put("createdAt", r[6]);
            resList.add(map);
        }

        return Response.successList("SUCCESS", resList);
    }

    public Response handleUpdateFeedbackStatus(Request req) {
        int id = req.getInt("id", -1);
        String status = req.get("status");

        if (id == -1 || status == null || status.trim().isEmpty()) {
            return Response.fail("Thiếu id hoặc trạng thái");
        }

        boolean success = service.updateFeedbackStatus(id, status);
        if (success) {
            return Response.success("Cập nhật trạng thái thành công");
        } else {
            return Response.fail("Lỗi cập nhật trạng thái");
        }
    }
}
