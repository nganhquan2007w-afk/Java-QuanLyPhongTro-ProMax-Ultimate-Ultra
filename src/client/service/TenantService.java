package client.service;

import client.socket.SocketClient;
import server.protocol.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TenantService — Client-side service quản lý khách thuê.
 * Giao tiếp với server qua JSON protocol.
 */
public class TenantService {

    /**
     * Lấy danh sách khách thuê kèm thông tin hợp đồng.
     * Mỗi phần tử là String[12]: [tenantId, fullName, phone, cccd, gender, birthDate, address, roomId, startDate, endDate, deposit, roomStatus]
     */
    public static List<String[]> getTenants() {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_TENANTS");

        if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) return list;

        List<Map<String, Object>> items = resp.getList();
        if (items == null) return list;

        for (Map<String, Object> item : items) {
            list.add(new String[]{
                str(item, "tenantId"),
                str(item, "fullName"),
                str(item, "phone"),
                str(item, "cccd"),
                str(item, "gender"),
                str(item, "birthDate"),
                str(item, "address"),
                str(item, "roomId"),
                str(item, "startDate"),
                str(item, "endDate"),
                str(item, "deposit"),
                str(item, "roomStatus")
            });
        }
        return list;
    }

    /**
     * Thêm khách thuê mới kèm hợp đồng.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String addTenant(String fullName, String phone, String cccd, String gender,
                                    String birthDate, String address,
                                    String roomId, String startDate, String duration) {
        // JSON không cần escape ; | # nữa
        Response resp = SocketClient.send("ADD_TENANT",
            "fullName",  fullName  != null ? fullName  : "",
            "phone",     phone     != null ? phone     : "",
            "cccd",      cccd      != null ? cccd      : "",
            "gender",    gender    != null ? gender    : "Nam",
            "birthDate", birthDate != null ? birthDate : "",
            "address",   address   != null ? address   : "",
            "roomId",    roomId    != null ? roomId    : "",
            "startDate", startDate != null ? startDate : "",
            "duration",  duration  != null ? duration  : ""
        );
        if (!resp.isSuccess()) return resp.getMessage();
        // Nếu server trả kèm mật khẩu plain-text → trả về "SUCCESS:<password>"
        String pw = resp.getDataString("plainPassword");
        return (pw != null && !pw.isEmpty()) ? "SUCCESS:" + pw : "SUCCESS";
    }

    /**
     * Cập nhật thông tin khách thuê.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String updateTenant(int tenantId, String fullName, String phone, String cccd, 
                                      String gender, String birthDate, String address, String roomId) {
        Response resp = SocketClient.send("UPDATE_TENANT",
            "tenantId",  String.valueOf(tenantId),
            "fullName",  fullName  != null ? fullName  : "",
            "phone",     phone     != null ? phone     : "",
            "cccd",      cccd      != null ? cccd      : "",
            "gender",    gender    != null ? gender    : "Nam",
            "birthDate", birthDate != null ? birthDate : "",
            "address",   address   != null ? address   : "",
            "roomId",    roomId    != null ? roomId    : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Xóa khách thuê.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String deleteTenant(int tenantId) {
        Response resp = SocketClient.send("DELETE_TENANT",
            "tenantId", String.valueOf(tenantId)
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }
}
