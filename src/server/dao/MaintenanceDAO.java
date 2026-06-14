package server.dao;

import server.database.ConnectDB;
import common.model.Maintenance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MaintenanceDAO - Lớp DAO xử lý tất cả thao tác CSDL liên quan đến bảng maintenance_requests.
 * Đây là tầng DUY NHẤT được phép truy cập Database cho module Sửa chữa.
 */
public class MaintenanceDAO {

    // ─── Đọc tất cả yêu cầu sửa chữa ────────────────────────────────────────
    /**
     * Lấy toàn bộ danh sách yêu cầu sửa chữa (dành cho Admin).
     * @return List của Maintenance
     */
    public List<Maintenance> getAllRequests() {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT id, room_id, description, priority, report_date, status FROM maintenance_requests ORDER BY report_date DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Maintenance(
                    rs.getInt("id"),
                    rs.getString("room_id"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getDate("report_date"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (MaintenanceDAO.getAllRequests): " + e.getMessage());
        }
        return list;
    }

    // ─── Đọc theo phòng (dành cho khách thuê) ────────────────────────────────
    /**
     * Lấy danh sách yêu cầu sửa chữa theo mã phòng.
     */
    public List<Maintenance> getRequestsByRoom(String roomId) {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT id, room_id, description, priority, report_date, status FROM maintenance_requests WHERE room_id = ? ORDER BY report_date DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Maintenance(
                        rs.getInt("id"),
                        rs.getString("room_id"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getDate("report_date"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (MaintenanceDAO.getRequestsByRoom): " + e.getMessage());
        }
        return list;
    }

    // ─── Thêm yêu cầu sửa chữa mới ───────────────────────────────────────────
    /**
     * INSERT một yêu cầu sửa chữa mới vào DB.
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addRequest(String roomId, String description, String priority, String reportDate) {
        String sql = "INSERT INTO maintenance_requests (room_id, description, priority, report_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ps.setString(2, description);
            ps.setString(3, priority);
            ps.setDate(4, parseDate(reportDate));
            ps.setString(5, "Chờ xử lý");
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (MaintenanceDAO.addRequest): " + e.getMessage());
            return false;
        }
    }

    // ─── Cập nhật trạng thái ─────────────────────────────────────────────────
    /**
     * Cập nhật trạng thái của một yêu cầu sửa chữa (UPDATE maintenance_requests).
     * @param id       ID của yêu cầu
     * @param status   Trạng thái mới (VD: "Đang sửa chữa", "Đã hoàn thành", "Từ chối")
     * @return true nếu thành công
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE maintenance_requests SET status = ? WHERE id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (MaintenanceDAO.updateStatus): " + e.getMessage());
            return false;
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────────
    private java.sql.Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return new java.sql.Date(System.currentTimeMillis());
        }
        String[] formats = {"dd/MM/yyyy", "yyyy-MM-dd"};
        for (String fmt : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(fmt);
                sdf.setLenient(false);
                return new java.sql.Date(sdf.parse(dateStr.trim()).getTime());
            } catch (Exception ignored) {}
        }
        return new java.sql.Date(System.currentTimeMillis());
    }
}
