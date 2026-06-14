package client.service;

import client.socket.SocketClient;
import server.protocol.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ServicesService — Client-side service quản lý dịch vụ và biểu giá.
 * Giao tiếp với server qua JSON protocol.
 */
public class ServicesService {

    /**
     * Lấy danh sách dịch vụ và đơn giá.
     * Mỗi phần tử là String[4]: [serviceId, serviceName, unitPrice, unit]
     */
    public static List<String[]> getServices() {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_SERVICES");

        if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) return list;

        List<Map<String, Object>> items = resp.getList();
        if (items == null) return list;

        for (Map<String, Object> item : items) {
            list.add(new String[]{
                str(item, "serviceId"),
                str(item, "serviceName"),
                str(item, "unitPrice"),
                str(item, "unit")
            });
        }
        return list;
    }

    /**
     * Cập nhật đơn giá 4 dịch vụ chính.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String updateServices(double rent, double elec, double water, double internet) {
        Response resp = SocketClient.send("UPDATE_SERVICE",
            "rent",     String.valueOf(rent),
            "elec",     String.valueOf(elec),
            "water",    String.valueOf(water),
            "internet", String.valueOf(internet)
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    public static String addService(String name, double price, String unit) {
        Response resp = SocketClient.send("ADD_SERVICE",
            "name", name,
            "price", String.valueOf(price),
            "unit", unit
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    public static String editSingleService(String oldName, String newName, double price, String unit) {
        Response resp = SocketClient.send("EDIT_SINGLE_SERVICE",
            "oldName", oldName,
            "newName", newName,
            "price", String.valueOf(price),
            "unit", unit
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    public static String deleteService(String name) {
        Response resp = SocketClient.send("DELETE_SERVICE",
            "name", name
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }
}
