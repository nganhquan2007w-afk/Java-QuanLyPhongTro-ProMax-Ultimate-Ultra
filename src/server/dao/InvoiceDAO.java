package server.dao;

import server.database.ConnectDB;
import common.model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý toàn bộ thao tác CRUD với bảng invoices trong cơ sở dữ liệu.
 */
public class InvoiceDAO {

    // ------------------------------------------------------------------ READ

    /**
     * Lấy toàn bộ hóa đơn, sắp xếp theo ngày lập mới nhất trước.
     */
    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY issue_date DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (InvoiceDAO.getAllInvoices): " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy hóa đơn của một phòng cụ thể.
     */
    public List<Invoice> getInvoicesByRoom(String roomId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE room_id = ? ORDER BY issue_date DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (InvoiceDAO.getInvoicesByRoom): " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy hóa đơn theo ID.
     */
    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (InvoiceDAO.getInvoiceById): " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------- WRITE

    /**
     * Thêm hóa đơn mới vào database, trả về invoice_id vừa tạo hoặc -1 nếu thất bại.
     */
    public int addInvoice(Invoice inv) {
        String sql = "INSERT INTO invoices (room_id, tenant_name, issue_date, due_date, rent, " +
                     "elec_usage, water_usage, other_fee, status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, inv.getRoomId());
            ps.setString(2, inv.getTenantName());
            ps.setDate(3, inv.getIssueDate());
            ps.setDate(4, inv.getDueDate());
            ps.setDouble(5, inv.getRent());
            ps.setDouble(6, inv.getElecUsage());
            ps.setDouble(7, inv.getWaterUsage());
            ps.setDouble(8, inv.getOtherFee());
            ps.setString(9, inv.getStatus() != null ? inv.getStatus() : "Chưa thanh toán");

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (InvoiceDAO.addInvoice): " + e.getMessage());
        }
        return -1;
    }

    /**
     * Cập nhật trạng thái thanh toán và ngày thu tiền của một hóa đơn.
     */
    public boolean updatePaymentStatus(int invoiceId, String status, Date paymentDate) {
        String sql = "UPDATE invoices SET status = ?, payment_date = ? WHERE invoice_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setDate(2, paymentDate);
            ps.setInt(3, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (InvoiceDAO.updatePaymentStatus): " + e.getMessage());
            return false;
        }
    }

    /**
     * Tính tổng doanh thu trong tháng hiện tại (chỉ tính hóa đơn đã thanh toán).
     */
    public double getMonthlyRevenue() {
        double elecPrice = 3500;
        double waterPrice = 10000;
        try {
            ServiceDAO svcDao = new ServiceDAO();
            List<common.model.Service> svcs = svcDao.getAllServices();
            if (svcs != null) {
                for (common.model.Service s : svcs) {
                    if (s.getServiceName().toLowerCase().contains("điện")) elecPrice = s.getUnitPrice();
                    if (s.getServiceName().toLowerCase().contains("nước")) waterPrice = s.getUnitPrice();
                }
            }
        } catch (Exception e) {}

        String sql = "SELECT COALESCE(SUM(rent + elec_usage * ? + water_usage * ? + other_fee), 0) " +
                     "FROM invoices " +
                     "WHERE status = 'Đã thanh toán' " +
                     "AND MONTH(issue_date) = MONTH(CURDATE()) AND YEAR(issue_date) = YEAR(CURDATE())";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, elecPrice);
            ps.setDouble(2, waterPrice);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi SQL (InvoiceDAO.getMonthlyRevenue): " + e.getMessage());
        }
        return 0;
    }

    // --------------------------------------------------------------- HELPER

    /** Map một ResultSet row sang đối tượng Invoice */
    private Invoice mapRow(ResultSet rs) throws SQLException {
        return new Invoice(
                rs.getInt("invoice_id"),
                rs.getString("room_id"),
                rs.getString("tenant_name"),
                rs.getDate("issue_date"),
                rs.getDate("due_date"),
                rs.getDouble("rent"),
                rs.getDouble("elec_usage"),
                rs.getDouble("water_usage"),
                rs.getDouble("other_fee"),
                rs.getString("status"),
                rs.getDate("payment_date")
        );
    }
}
