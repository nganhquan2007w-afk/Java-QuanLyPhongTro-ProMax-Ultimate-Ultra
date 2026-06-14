package server.dao;

import server.database.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceSubscriptionDAO {

    public boolean addSubscription(String roomId, String serviceName) {
        // Tra cứu service_id theo tên (nullable — vẫn insert được dù không tìm thấy)
        Integer serviceId = null;
        String lookupSql = "SELECT service_id FROM services WHERE service_name = ? LIMIT 1";
        try (Connection c = ConnectDB.getConnection();
             PreparedStatement lps = c.prepareStatement(lookupSql)) {
            lps.setString(1, serviceName);
            try (ResultSet lrs = lps.executeQuery()) {
                if (lrs.next()) serviceId = lrs.getInt("service_id");
            }
        } catch (Exception ignore) {}

        String sql = "INSERT INTO service_subscriptions (room_id, service_id, service_name, status) VALUES (?, ?, ?, 'Chờ duyệt')";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            if (serviceId != null) ps.setInt(2, serviceId);
            else                   ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, serviceName);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (ServiceSubscriptionDAO.addSubscription): " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getSubscriptionsByRoom(String roomId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, service_name, status, registered_at FROM service_subscriptions WHERE room_id = ? ORDER BY registered_at DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("service_name"),
                            rs.getString("status"),
                            rs.getString("registered_at")
                    });
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (ServiceSubscriptionDAO.getSubscriptionsByRoom): " + e.getMessage());
        }
        return list;
    }

    public List<String[]> getAllSubscriptions() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, room_id, service_name, status, registered_at FROM service_subscriptions ORDER BY registered_at DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("room_id"),
                        rs.getString("service_name"),
                        rs.getString("status"),
                        rs.getString("registered_at")
                });
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (ServiceSubscriptionDAO.getAllSubscriptions): " + e.getMessage());
        }
        return list;
    }

    public boolean updateSubscriptionStatus(int id, String newStatus) {
        String sql = "UPDATE service_subscriptions SET status = ? WHERE id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi (ServiceSubscriptionDAO.updateSubscriptionStatus): " + e.getMessage());
            return false;
        }
    }
}
