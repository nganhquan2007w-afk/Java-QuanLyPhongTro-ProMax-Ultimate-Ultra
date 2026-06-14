package server.dao;

import common.model.Notification;
import server.database.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO xử lý truy xuất và cập nhật dữ liệu bảng notifications
 */
public class NotificationDAO {

    public List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, title, content, created_at FROM notifications ORDER BY created_at DESC LIMIT 50";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Notification(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi load notifications: " + e.getMessage());
        }
        return list;
    }

    public List<Notification> getNotificationsForRoom(String roomId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, title, content, created_at FROM notifications WHERE room_id = ? OR room_id IS NULL ORDER BY created_at DESC LIMIT 50";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi load notifications for room: " + e.getMessage());
        }
        return list;
    }

    public boolean addNotification(String title, String content, String roomId) {
        String sql = "INSERT INTO notifications (title, content, room_id) VALUES (?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, title);
            ps.setString(2, content);
            if (roomId == null || roomId.trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, roomId);
            }
            
            int rows = ps.executeUpdate();
            if (!conn.getAutoCommit()) conn.commit();
            return rows > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi thêm notification: " + e.getMessage());
            return false;
        }
    }
}
