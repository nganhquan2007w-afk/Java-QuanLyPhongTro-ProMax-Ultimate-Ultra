package server.dao;

import server.database.ConnectDB;
import common.model.Tenant;
import common.model.TenantWithContract;
import server.security.CryptoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TenantDAO {

    public List<Tenant> getAllTenants() {
        List<Tenant> list = new ArrayList<>();
        String sql = "SELECT * FROM tenants";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Tenant(
                        rs.getInt("tenant_id"),
                        rs.getString("full_name"),
                        CryptoUtil.decrypt(rs.getString("phone")),
                        CryptoUtil.decrypt(rs.getString("cccd")),
                        rs.getString("gender"),
                        rs.getDate("birth_date"),
                        CryptoUtil.decrypt(rs.getString("address"))
                ));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (TenantDAO.getAllTenants): " + e.getMessage());
        }
        return list;
    }

    public List<TenantWithContract> getTenantsWithContracts() {
        List<TenantWithContract> list = new ArrayList<>();
        String sql = "SELECT t.tenant_id, t.full_name, t.phone, t.cccd, t.gender, t.birth_date, t.address, " +
                      "c.room_id, c.start_date, c.end_date, c.deposit, r.status AS room_status " +
                      "FROM tenants t " +
                      "LEFT JOIN contracts c ON t.tenant_id = c.tenant_id " +
                      "LEFT JOIN rooms r ON c.room_id = r.room_id";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tenant tenant = new Tenant(
                    rs.getInt("tenant_id"),
                    rs.getString("full_name"),
                    CryptoUtil.decrypt(rs.getString("phone")),
                    CryptoUtil.decrypt(rs.getString("cccd")),
                    rs.getString("gender"),
                    rs.getDate("birth_date"),
                    CryptoUtil.decrypt(rs.getString("address"))
                );
                
                TenantWithContract twc = new TenantWithContract(
                    tenant,
                    rs.getString("room_id"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getDouble("deposit"),
                    rs.getString("room_status")
                );
                list.add(twc);
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (TenantDAO.getTenantsWithContracts): " + e.getMessage());
        }
        return list;
    }

    public int addTenant(Tenant tenant) {
        String sql = "INSERT INTO tenants (full_name, phone, cccd, gender, birth_date, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tenant.getFullName());
            ps.setString(2, CryptoUtil.encrypt(tenant.getPhone()));
            ps.setString(3, CryptoUtil.encrypt(tenant.getCccd()));
            ps.setString(4, tenant.getGender());
            ps.setDate(5, tenant.getBirthDate());
            ps.setString(6, CryptoUtil.encrypt(tenant.getAddress()));
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (TenantDAO.addTenant): " + e.getMessage());
        }
        return -1;
    }

    public boolean updateTenant(Tenant tenant) {
        String sql = "UPDATE tenants SET full_name = ?, phone = ?, cccd = ?, gender = ?, birth_date = ?, address = ? WHERE tenant_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenant.getFullName());
            ps.setString(2, CryptoUtil.encrypt(tenant.getPhone()));
            ps.setString(3, CryptoUtil.encrypt(tenant.getCccd()));
            ps.setString(4, tenant.getGender());
            ps.setDate(5, tenant.getBirthDate());
            ps.setString(6, CryptoUtil.encrypt(tenant.getAddress()));
            ps.setInt(7, tenant.getTenantId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (TenantDAO.updateTenant): " + e.getMessage());
        }
        return false;
    }

    public boolean deleteTenant(int tenantId) {
        String sql = "DELETE FROM tenants WHERE tenant_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            boolean result = ps.executeUpdate() > 0;
            if (result) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE tenants AUTO_INCREMENT = 1");
                    stmt.execute("ALTER TABLE contracts AUTO_INCREMENT = 1");
                } catch (Exception ex) {
                    server.util.ServerLogger.error("Lỗi reset auto_increment: " + ex.getMessage());
                }
            }
            return result;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (TenantDAO.deleteTenant): " + e.getMessage());
        }
        return false;
    }

    public Tenant getTenantByName(String fullName) {
        String sql = "SELECT * FROM tenants WHERE LOWER(full_name) = LOWER(?) LIMIT 1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tenant(
                            rs.getInt("tenant_id"),
                            rs.getString("full_name"),
                            CryptoUtil.decrypt(rs.getString("phone")),
                            CryptoUtil.decrypt(rs.getString("cccd")),
                            rs.getString("gender"),
                            rs.getDate("birth_date"),
                            CryptoUtil.decrypt(rs.getString("address"))
                    );
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (TenantDAO.getTenantByName): " + e.getMessage());
        }
        return null;
    }
}
