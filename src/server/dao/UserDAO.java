package server.dao;

import server.database.ConnectDB;
import common.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Lớp truy cập cơ sở dữ liệu (DAO) cho bảng USERS
 */
public class UserDAO {

    /**
     * Tìm kiếm người dùng dựa trên tên đăng nhập (username)
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM USERS WHERE username = ?";
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) return null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int tenantIdRaw = rs.getInt("tenant_id");
                        Integer tenantId = rs.wasNull() ? null : tenantIdRaw;
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("role"),
                                rs.getString("full_name"),
                                rs.getString("phone"),
                                tenantId
                        );
                    }
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi truy vấn SQL (getUserByUsername): " + e.getMessage());
        }
        return null;
    }



    /**
     * Cập nhật mật khẩu mới (đã băm) của người dùng
     */
    public boolean updatePassword(String username, String hashedNewPassword) {
        String sql = "UPDATE USERS SET password = ? WHERE username = ?";
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, hashedNewPassword);
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi truy vấn SQL (updatePassword): " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin profile của người dùng
     */
    public boolean updateProfile(String username, String fullName, String phone) {
        String sql = "UPDATE USERS SET full_name = ?, phone = ? WHERE username = ?";
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, phone);
                ps.setString(3, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi truy vấn SQL (updateProfile): " + e.getMessage());
            return false;
        }
    }

    /**
     * Thêm mới người dùng vào hệ thống (thường dùng cho tự động cấp tài khoản)
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO USERS (username, password, role, full_name, phone, tenant_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRole());
                ps.setString(4, user.getFullName());
                ps.setString(5, user.getPhone());
                if (user.getTenantId() != null) {
                    ps.setInt(6, user.getTenantId());
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                }
                int rows = ps.executeUpdate();
                if (!conn.getAutoCommit()) conn.commit();
                return rows > 0;
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi truy vấn SQL (addUser): " + e.getMessage());
            return false;
        }
    }

    /**
     * Liên kết tài khoản người dùng với một khách thuê (tenant_id).
     * Dùng khi admin tạo tài khoản cho khách thuê.
     * @param username tên đăng nhập
     * @param tenantId ID khách thuê trong bảng tenants (null để hủy liên kết)
     */
    public boolean linkTenantToUser(String username, Integer tenantId) {
        String sql = "UPDATE USERS SET tenant_id = ? WHERE username = ?";
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (tenantId != null) {
                    ps.setInt(1, tenantId);
                } else {
                    ps.setNull(1, java.sql.Types.INTEGER);
                }
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi truy vấn SQL (linkTenantToUser): " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa người dùng dựa trên tên đăng nhập (username)
     */
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM USERS WHERE username = ?";
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                int rows = ps.executeUpdate();
                if (!conn.getAutoCommit()) conn.commit();
                return rows > 0;
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi truy vấn SQL (deleteUser): " + e.getMessage());
            return false;
        }
    }
}
