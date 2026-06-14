package server.controller;

import server.exception.ServiceException;
import server.protocol.Request;
import server.protocol.Response;
import server.service.TenantService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import common.model.TenantWithContract;

/**
 * TenantController — nhận request, gọi TenantService, trả response.
 * Không có validation, date parsing, hay business logic.
 */
public class TenantController {

    private final TenantService tenantService = new TenantService();

    /** GET_TENANTS */
    public Response handleGetTenants(Request req) {
        List<TenantWithContract> tenants = tenantService.getTenantsWithContracts();
        if (tenants.isEmpty()) return Response.success("EMPTY");

        List<Map<String, Object>> list = new ArrayList<>();
        for (TenantWithContract twc : tenants) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tenantId",   String.valueOf(twc.getTenant().getTenantId()));
            m.put("fullName",   twc.getTenant().getFullName());
            m.put("phone",      twc.getTenant().getPhone());
            m.put("cccd",       twc.getTenant().getCccd());
            m.put("gender",     twc.getTenant().getGender());
            m.put("birthDate",  twc.getTenant().getBirthDate() != null ? twc.getTenant().getBirthDate().toString() : "");
            m.put("address",    twc.getTenant().getAddress());
            m.put("roomId",     twc.getRoomId());
            m.put("startDate",  twc.getStartDate());
            m.put("endDate",    twc.getEndDate());
            m.put("deposit",    String.valueOf(twc.getDeposit()));
            m.put("roomStatus", twc.getRoomStatus());
            list.add(m);
        }
        return Response.successList("OK", list);
    }

    /** ADD_TENANT */
    public Response handleAddTenant(Request req) {
        try {
            tenantService.addTenantWithContract(
                req.get("fullName"),
                req.get("phone"),
                req.get("cccd"),
                req.get("gender"),
                req.get("birthDate"),
                req.get("address"),
                req.get("roomId"),
                req.get("startDate"),
                req.get("duration")
            );
            // Trường hợp user đã tồn tại (không tạo mới) — không có mật khẩu để trả
            return Response.success("Đăng ký khách thuê thành công!");
        } catch (TenantService._SuccessWithPassword ok) {
            // Tạo user thành công — trả kèm mật khẩu plain-text cho client hiển thị
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("plainPassword", ok.plainPassword);
            return Response.success("Đăng ký khách thuê thành công!", data);
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** GET_ROOM_BY_USER */
    public Response handleGetRoomByUser(Request req) {
        try {
            String roomId = tenantService.getRoomIdByUsername(req.get("username"));
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("roomId", roomId);
            return Response.success("OK", data);
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** UPDATE_TENANT */
    public Response handleUpdateTenant(Request req) {
        try {
            tenantService.updateTenant(
                req.getInt("tenantId", -1),
                req.get("fullName"),
                req.get("phone"),
                req.get("cccd"),
                req.get("gender"),
                req.get("birthDate"),
                req.get("address"),
                req.get("roomId")
            );
            return Response.success("Cập nhật khách thuê thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** DELETE_TENANT */
    public Response handleDeleteTenant(Request req) {
        try {
            tenantService.deleteTenant(req.getInt("tenantId", -1));
            return Response.success("Xóa khách thuê thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
