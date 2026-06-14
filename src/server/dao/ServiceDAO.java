package server.dao;

import server.database.ConnectDB;
import common.model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM services";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Service(
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getDouble("unit_price"),
                        rs.getString("unit")
                ));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ServiceDAO.getAllServices): " + e.getMessage());
        }
        return list;
    }

    public boolean updateServicePriceByName(String name, double newPrice) {
        String sql = "UPDATE services SET unit_price = ? WHERE service_name LIKE ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, "%" + name + "%");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ServiceDAO.updateServicePriceByName): " + e.getMessage());
            return false;
        }
    }

    public boolean updateService(Service service) {
        String sql = "UPDATE services SET unit_price = ?, unit = ? WHERE service_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, service.getUnitPrice());
            ps.setString(2, service.getUnit());
            ps.setInt(3, service.getServiceId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ServiceDAO.updateService): " + e.getMessage());
            return false;
        }
    }

    public boolean updateSingleService(String oldName, String newName, double price, String unit) {
        String sql = "UPDATE services SET service_name = ?, unit_price = ?, unit = ? WHERE service_name = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setDouble(2, price);
            ps.setString(3, unit);
            ps.setString(4, oldName);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ServiceDAO.updateSingleService): " + e.getMessage());
            return false;
        }
    }

    public boolean addService(String name, double price, String unit) {
        String sql = "INSERT INTO services (service_name, unit_price, unit) VALUES (?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, unit);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ServiceDAO.addService): " + e.getMessage());
            return false;
        }
    }

    public boolean deleteServiceByName(String name) {
        String sql = "DELETE FROM services WHERE service_name = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (ServiceDAO.deleteServiceByName): " + e.getMessage());
            return false;
        }
    }
}
