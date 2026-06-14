package server.dao;

import server.database.ConnectDB;
import common.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT room_id, status, price, area, description FROM rooms ORDER BY room_id";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Loi SQL (RoomDAO.getAllRooms): " + e.getMessage());
        }
        return list;
    }

    /**
     * Tim phong theo ID — dung SQL WHERE, khong full scan.
     * @return Room neu tim thay, null neu khong co
     */
    public Room getRoomById(String roomId) {
        String sql = "SELECT room_id, status, price, area, description FROM rooms WHERE room_id = ? LIMIT 1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Loi SQL (RoomDAO.getRoomById): " + e.getMessage());
        }
        return null;
    }

    /**
     * Kiem tra phong co ton tai khong — SELECT 1, nhanh nhat cho existence check.
     */
    public boolean existsRoom(String roomId) {
        String sql = "SELECT 1 FROM rooms WHERE room_id = ? LIMIT 1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Loi SQL (RoomDAO.existsRoom): " + e.getMessage());
        }
        return false;
    }

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_id, status, price, area, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomId());
            ps.setString(2, room.getStatus());
            ps.setDouble(3, room.getPrice());
            ps.setDouble(4, room.getArea());
            ps.setString(5, packDescription(room));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (RoomDAO.addRoom): " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET status = ?, price = ?, area = ?, description = ? WHERE room_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getStatus());
            ps.setDouble(2, room.getPrice());
            ps.setDouble(3, room.getArea());
            ps.setString(4, packDescription(room));
            ps.setString(5, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (RoomDAO.updateRoom): " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoomStatus(String roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (RoomDAO.updateRoomStatus): " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(String roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (RoomDAO.deleteRoom): " + e.getMessage());
            return false;
        }
    }

    public String importRoomsBatch(List<Room> rooms) {
        String sqlCheck = "SELECT room_id FROM rooms WHERE room_id = ?";
        String sqlInsert = "INSERT INTO rooms (room_id, status, price, area, description) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE rooms SET status = ?, price = ?, area = ?, description = ? WHERE room_id = ?";

        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return "Lỗi kết nối cơ sở dữ liệu!";

            // Tắt auto-commit để bắt đầu Transaction (Rất quan trọng chống mất dữ liệu khi lỗi)
            conn.setAutoCommit(false);

            psCheck = conn.prepareStatement(sqlCheck);
            psInsert = conn.prepareStatement(sqlInsert);
            psUpdate = conn.prepareStatement(sqlUpdate);

            for (Room room : rooms) {
                psCheck.setString(1, room.getRoomId());
                boolean exists = false;
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) exists = true;
                }

                if (exists) {
                    psUpdate.setString(1, room.getStatus());
                    psUpdate.setDouble(2, room.getPrice());
                    psUpdate.setDouble(3, room.getArea());
                    psUpdate.setString(4, packDescription(room));
                    psUpdate.setString(5, room.getRoomId());
                    psUpdate.executeUpdate();
                } else {
                    psInsert.setString(1, room.getRoomId());
                    psInsert.setString(2, room.getStatus());
                    psInsert.setDouble(3, room.getPrice());
                    psInsert.setDouble(4, room.getArea());
                    psInsert.setString(5, packDescription(room));
                    psInsert.executeUpdate();
                }
            }

            // Mọi thứ trơn tru -> Lưu toàn bộ
            conn.commit();
            return "SUCCESS";

        } catch (Exception e) {
            // Có lỗi -> Hủy bỏ (Rollback) mọi thay đổi để an toàn dữ liệu
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    server.util.ServerLogger.error("Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            }
            return "Lỗi import (Đã rollback an toàn): " + e.getMessage();
        } finally {
            try {
                if (psCheck != null) psCheck.close();
                if (psInsert != null) psInsert.close();
                if (psUpdate != null) psUpdate.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                    conn.close();
                }
            } catch (Exception ex) {
                server.util.ServerLogger.error("Lỗi đóng kết nối: " + ex.getMessage());
            }
        }
    }
    /** Helper — map mot ResultSet row sang Room object */
    private Room mapRow(ResultSet rs) throws java.sql.SQLException {
        String rawDesc = rs.getString("description");
        String zone = "";
        String type = "";
        int capacity = 0;
        String desc = rawDesc;
        double price = rs.getDouble("price");
        
        // Cố gắng parse description nếu có chứa thông tin mới (định dạng zone::type::capacity::desc)
        if (rawDesc != null && rawDesc.contains("::")) {
            String[] parts = rawDesc.split("::", -1);
            if (parts.length >= 4) {
                zone = parts[0];
                type = parts[1];
                try { capacity = Integer.parseInt(parts[2]); } catch (Exception e) {}
                desc = parts[3];
                // Nối lại phần desc nếu trong desc cũng có ::
                for (int i = 4; i < parts.length; i++) {
                    desc += "::" + parts[i];
                }
            }
        } else {
            // Dữ liệu cũ, description chỉ là chuỗi mô tả thuần túy
            desc = rawDesc != null ? rawDesc : "";
            String rId = rs.getString("room_id");
            if (rId != null) {
                if (rId.contains("1")) zone = "Khu nhà A";
                else if (rId.contains("2")) zone = "Khu nhà B";
                else if (rId.contains("3")) zone = "Khu nhà C";
            }
            if (price > 3000000) { type = "Cao cấp"; capacity = 3; }
            else if (price < 2000000) { type = "Giá rẻ"; capacity = 1; }
        }

        return new Room(
            rs.getString("room_id"),
            rs.getString("status"),
            price,
            rs.getDouble("area"),
            zone,
            type,
            capacity,
            desc
        );
    }
    
    private String packDescription(Room room) {
        String zone = room.getZone() != null ? room.getZone() : "";
        String type = room.getType() != null ? room.getType() : "";
        String capacityStr = room.getCapacity() > 0 ? String.valueOf(room.getCapacity()) : "";
        String desc = room.getDescription() != null ? room.getDescription() : "";
        return zone + "::" + type + "::" + capacityStr + "::" + desc;
    }
}
