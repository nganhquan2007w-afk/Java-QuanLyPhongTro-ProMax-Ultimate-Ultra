package server.service;

import server.dao.ServiceSubscriptionDAO;
import java.util.List;

public class ServiceSubscriptionService {
    private final ServiceSubscriptionDAO dao;

    public ServiceSubscriptionService() {
        this.dao = new ServiceSubscriptionDAO();
    }

    public boolean addSubscription(String roomId, String serviceName) {
        return dao.addSubscription(roomId, serviceName);
    }

    public List<String[]> getSubscriptionsByRoom(String roomId) {
        return dao.getSubscriptionsByRoom(roomId);
    }

    public List<String[]> getAllSubscriptions() {
        return dao.getAllSubscriptions();
    }

    public boolean updateSubscriptionStatus(int id, String newStatus) {
        return dao.updateSubscriptionStatus(id, newStatus);
    }
}
