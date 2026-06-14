package server.service;

import server.dao.ServiceDAO;
import server.exception.ServiceException;
import common.model.Service;

import java.util.List;

/**
 * ServicePricingService — Xử lý nghiệp vụ liên quan đến biểu giá dịch vụ (phòng, điện, nước...).
 */
public class ServicePricingService {
    private final ServiceDAO serviceDAO = new ServiceDAO();

    public List<Service> getAllServices() {
        return serviceDAO.getAllServices();
    }

    public void updateServices(double rent, double elec, double water, double internet) throws ServiceException {
        boolean ok1 = serviceDAO.updateServicePriceByName("phòng", rent);
        boolean ok2 = serviceDAO.updateServicePriceByName("điện", elec);
        boolean ok3 = serviceDAO.updateServicePriceByName("nước", water);
        boolean ok4 = serviceDAO.updateServicePriceByName("internet", internet);

        if (!ok1 && !ok2 && !ok3 && !ok4) {
            throw new ServiceException("Không thể cập nhật biểu giá dịch vụ do lỗi cơ sở dữ liệu!");
        }
    }

    public void addService(String name, double price, String unit) throws ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException("Tên dịch vụ không được để trống!");
        }
        if (price < 0) {
            throw new ServiceException("Đơn giá không hợp lệ!");
        }
        if (!serviceDAO.addService(name, price, unit)) {
            throw new ServiceException("Không thể thêm dịch vụ vào cơ sở dữ liệu!");
        }
    }

    public void updateSingleService(String oldName, String newName, double price, String unit) throws ServiceException {
        if (newName == null || newName.trim().isEmpty()) {
            throw new ServiceException("Tên dịch vụ mới không được để trống!");
        }
        if (price < 0) {
            throw new ServiceException("Đơn giá không hợp lệ!");
        }
        if (!serviceDAO.updateSingleService(oldName, newName, price, unit)) {
            throw new ServiceException("Không thể cập nhật dịch vụ này!");
        }
    }

    public void deleteService(String name) throws ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException("Tên dịch vụ không được để trống!");
        }
        if (!serviceDAO.deleteServiceByName(name)) {
            throw new ServiceException("Không thể xóa dịch vụ này!");
        }
    }
}
