package server.dao;

import server.database.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    public boolean addFeedback(String roomId, String title, String content, int rating) {
        String sql = "INSERT INTO feedbacks (room_id, title, content, rating, status) VALUES (?, ?, ?, ?, 'Chờ xử lý')";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setInt(4, rating);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (FeedbackDAO.addFeedback): " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getFeedbacksByRoom(String roomId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, title, content, rating, status, created_at FROM feedbacks WHERE room_id = ? ORDER BY created_at DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("title"),
                            rs.getString("content"),
                            String.valueOf(rs.getInt("rating")),
                            rs.getString("status"),
                            rs.getString("created_at")
                    });
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (FeedbackDAO.getFeedbacksByRoom): " + e.getMessage());
        }
        return list;
    }

    public List<String[]> getAllFeedbacks() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, room_id, title, content, rating, status, created_at FROM feedbacks ORDER BY created_at DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("room_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        String.valueOf(rs.getInt("rating")),
                        rs.getString("status"),
                        rs.getString("created_at")
                });
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (FeedbackDAO.getAllFeedbacks): " + e.getMessage());
        }
        return list;
    }

    public boolean updateFeedbackStatus(int id, String newStatus) {
        String sql = "UPDATE feedbacks SET status = ? WHERE id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (FeedbackDAO.updateFeedbackStatus): " + e.getMessage());
            return false;
        }
    }
}
