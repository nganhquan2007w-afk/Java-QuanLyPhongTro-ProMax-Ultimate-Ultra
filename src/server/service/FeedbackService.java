package server.service;

import server.dao.FeedbackDAO;
import java.util.List;

public class FeedbackService {
    private final FeedbackDAO dao;

    public FeedbackService() {
        this.dao = new FeedbackDAO();
    }

    public boolean addFeedback(String roomId, String title, String content, int rating) {
        return dao.addFeedback(roomId, title, content, rating);
    }

    public List<String[]> getFeedbacksByRoom(String roomId) {
        return dao.getFeedbacksByRoom(roomId);
    }

    public List<String[]> getAllFeedbacks() {
        return dao.getAllFeedbacks();
    }

    public boolean updateFeedbackStatus(int id, String newStatus) {
        return dao.updateFeedbackStatus(id, newStatus);
    }
}
