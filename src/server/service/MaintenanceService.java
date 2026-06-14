package server.service;

import server.dao.MaintenanceDAO;
import server.exception.ServiceException;
import server.util.ServerLogger;
import common.model.Maintenance;

import java.util.List;

/**
 * MaintenanceService — Validate, gọi DAO. Throw ServiceException khi lỗi.
 */
public class MaintenanceService {

    private final MaintenanceDAO maintenanceDAO;

    public MaintenanceService() {
        this.maintenanceDAO = new MaintenanceDAO();
    }

    /** Lấy tất cả yêu cầu sửa chữa (Admin) */
    public List<Maintenance> getAllRequests() {
        return maintenanceDAO.getAllRequests();
    }

    /** Lấy yêu cầu theo phòng (Tenant) */
    public List<Maintenance> getRequestsByRoom(String roomId) {
        return maintenanceDAO.getRequestsByRoom(roomId);
    }

    /**
     * Gửi yêu cầu sửa chữa mới.
     * @throws ServiceException nếu validate lỗi hoặc DB thất bại
     */
    public void addRequest(String roomId, String description, String priority, String reportDate)
            throws ServiceException {
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không được để trống!");
        if (description == null || description.trim().isEmpty())
            throw new ServiceException("Nội dung sự cố không được để trống!");

        String safePriority = (priority != null && !priority.trim().isEmpty()) ? priority : "Thấp";
        String safeDate     = (reportDate != null) ? reportDate : "";

        if (!maintenanceDAO.addRequest(roomId.trim(), description.trim(), safePriority, safeDate))
            throw new ServiceException("Không thể gửi yêu cầu sửa chữa, vui lòng thử lại!");

        ServerLogger.info("Them yeu cau sua chua phong " + roomId);
    }

    /**
     * Cập nhật trạng thái yêu cầu (Admin).
     * @throws ServiceException nếu validate lỗi hoặc DB thất bại
     */
    public void updateStatus(int id, String status) throws ServiceException {
        if (id <= 0)
            throw new ServiceException("ID yêu cầu không hợp lệ!");
        if (status == null || status.trim().isEmpty())
            throw new ServiceException("Trạng thái không được để trống!");

        if (!maintenanceDAO.updateStatus(id, status.trim()))
            throw new ServiceException("Không tìm thấy yêu cầu sửa chữa #" + id);

        ServerLogger.info("Cap nhat yeu cau sua chua #" + id + " -> " + status);
    }
}
