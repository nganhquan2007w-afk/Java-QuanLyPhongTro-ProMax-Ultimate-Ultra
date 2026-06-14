package server.service;

import server.dao.NotificationDAO;
import server.exception.ServiceException;
import common.model.Notification;
import java.util.List;

/**
 * NotificationService — Xử lý nghiệp vụ thông báo, giao tiếp với database qua DAO.
 */
public class NotificationService {
    
    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public List<Notification> getAllNotifications() {
        return notificationDAO.getAllNotifications();
    }

    public List<Notification> getNotificationsForRoom(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            return notificationDAO.getNotificationsForRoom(null);
        }
        return notificationDAO.getNotificationsForRoom(roomId);
    }

    public void addNotification(String title, String content, String roomId) throws ServiceException {
        if (title == null || title.trim().isEmpty()) {
            throw new ServiceException("Tiêu đề thông báo không được để trống!");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ServiceException("Nội dung thông báo không được để trống!");
        }

        if (!notificationDAO.addNotification(title.trim(), content.trim(), roomId)) {
            throw new ServiceException("Không thể lưu thông báo vào cơ sở dữ liệu!");
        }
    }
}
