package server.dao;

import server.database.ConnectDB;
import common.model.Contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

    public static class ContractWithTenantName {
        public Contract contract;
        public String tenantName;

        public ContractWithTenantName(Contract contract, String tenantName) {
            this.contract = contract;
            this.tenantName = tenantName;
        }
    }

    public List<ContractWithTenantName> getAllContracts() {
        List<ContractWithTenantName> list = new ArrayList<>();
        String sql = "SELECT c.*, t.full_name FROM contracts c JOIN tenants t ON c.tenant_id = t.tenant_id";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Contract c = new Contract(
                        rs.getInt("contract_id"),
                        rs.getString("room_id"),
                        rs.getInt("tenant_id"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getDouble("deposit")
                );
                String tenantName = rs.getString("full_name");
                list.add(new ContractWithTenantName(c, tenantName));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.getAllContracts): " + e.getMessage());
        }
        return list;
    }

    public List<ContractWithTenantName> getContractsByRoom(String roomId) {
        List<ContractWithTenantName> list = new ArrayList<>();
        String sql = "SELECT c.*, t.full_name FROM contracts c JOIN tenants t ON c.tenant_id = t.tenant_id WHERE c.room_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract c = new Contract(
                            rs.getInt("contract_id"),
                            rs.getString("room_id"),
                            rs.getInt("tenant_id"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getDouble("deposit")
                    );
                    String tenantName = rs.getString("full_name");
                    list.add(new ContractWithTenantName(c, tenantName));
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.getContractsByRoom): " + e.getMessage());
        }
        return list;
    }

    public int addContract(Contract contract) {
        String sql = "INSERT INTO contracts (room_id, tenant_id, start_date, end_date, deposit) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, contract.getRoomId());
            ps.setInt(2, contract.getTenantId());
            ps.setDate(3, contract.getStartDate());
            ps.setDate(4, contract.getEndDate());
            ps.setDouble(5, contract.getDeposit());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                // NOTE: updateRoomStatus() được gọi từ ContractService sau khi return,
                // không gọi ở đây vì DAO không được gọi DAO khác.
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.addContract): " + e.getMessage());
        }
        return -1;
    }
    /**
     * Lấy mã phòng đang thuê của một user dựa theo tên đăng nhập hoặc số điện thoại.
     * Di chuyển từ ServerController.handleGetRoomByUsername() — đã từng gọi SQL thô.
     * @return room_id nếu tìm thấy, null nếu không
     */
    public String getRoomIdByUsername(String username) {
        String sql = "SELECT c.room_id FROM contracts c " +
                     "JOIN tenants t ON c.tenant_id = t.tenant_id " +
                     "WHERE LOWER(t.full_name) = LOWER(?) OR t.phone = ? OR t.phone = ? " +
                     "ORDER BY c.start_date DESC LIMIT 1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, server.security.CryptoUtil.encrypt(username.trim()));
            ps.setString(3, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("room_id");
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.getRoomIdByUsername): " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy mã phòng của khách thuê hiện tại.
     * @return room_id nếu tìm thấy, null nếu không
     */
    public String getRoomIdByTenantId(int tenantId) {
        String sql = "SELECT room_id FROM contracts WHERE tenant_id = ? ORDER BY start_date DESC LIMIT 1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("room_id");
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.getRoomIdByTenantId): " + e.getMessage());
        }
        return null;
    }

    /**
     * Cập nhật mã phòng của hợp đồng hiện tại của khách thuê.
     */
    public boolean updateContractRoom(int tenantId, String newRoomId) {
        String sql = "UPDATE contracts SET room_id = ? WHERE tenant_id = ? ORDER BY start_date DESC LIMIT 1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRoomId);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.updateContractRoom): " + e.getMessage());
        }
        return false;
    }

    /**
     * Đếm số lượng khách thuê hiện tại đang ở trong một phòng.
     */
    public int countTenantsInRoom(String roomId) {
        String sql = "SELECT COUNT(DISTINCT tenant_id) FROM contracts WHERE room_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ContractDAO.countTenantsInRoom): " + e.getMessage());
        }
        return 0;
    }
}
