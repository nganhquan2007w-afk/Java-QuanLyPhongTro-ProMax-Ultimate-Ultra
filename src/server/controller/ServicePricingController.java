package server.controller;

import server.exception.ServiceException;
import common.model.Service;
import server.protocol.Request;
import server.protocol.Response;
import server.service.ServicePricingService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ServicePricingController — nhận request, gọi ServicePricingService, trả response.
 */
public class ServicePricingController {

    private final ServicePricingService servicePricingService = new ServicePricingService();

    /** GET_SERVICES */
    public Response handleGetServices(Request req) {
        List<Service> services = servicePricingService.getAllServices();
        if (services.isEmpty()) return Response.success("EMPTY");

        List<Map<String, Object>> list = new ArrayList<>();
        for (Service s : services) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("serviceId",   s.getServiceId());
            m.put("serviceName", s.getServiceName());
            m.put("unitPrice",   s.getUnitPrice());
            m.put("unit",        s.getUnit());
            list.add(m);
        }
        return Response.successList("OK", list);
    }

    /** UPDATE_SERVICE */
    public Response handleUpdateService(Request req) {
        try {
            double rent = req.getDouble("rent", 0.0);
            double elec = req.getDouble("elec", 0.0);
            double water = req.getDouble("water", 0.0);
            double internet = req.getDouble("internet", 0.0);

            servicePricingService.updateServices(rent, elec, water, internet);
            return Response.success("Cập nhật đơn giá dịch vụ thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** ADD_SERVICE */
    public Response handleAddService(Request req) {
        try {
            String name = req.get("name");
            double price = req.getDouble("price", 0.0);
            String unit = req.get("unit");

            servicePricingService.addService(name, price, unit);
            return Response.success("Thêm dịch vụ mới thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** EDIT_SINGLE_SERVICE */
    public Response handleEditSingleService(Request req) {
        try {
            String oldName = req.get("oldName");
            String newName = req.get("newName");
            double price = req.getDouble("price", 0.0);
            String unit = req.get("unit");

            servicePricingService.updateSingleService(oldName, newName, price, unit);
            return Response.success("Cập nhật dịch vụ thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** DELETE_SERVICE */
    public Response handleDeleteService(Request req) {
        try {
            String name = req.get("name");
            servicePricingService.deleteService(name);
            return Response.success("Xóa dịch vụ thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
