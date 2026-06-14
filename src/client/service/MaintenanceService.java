package client.service;

import client.socket.SocketClient;
import server.protocol.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MaintenanceService — Client-side service yêu cầu sửa chữa.
 * Giao tiếp với server qua JSON protocol.
 * Không cần escape ; | # nữa — JSON xử lý tự động.
 */
public class MaintenanceService {

    /**
     * Lấy danh sách yêu cầu sửa chữa.
     * @param role   "ADMIN" hoặc "USER"
     * @param roomId Mã phòng (chỉ dùng khi role = USER)
     * @return List của String[6]: [id, roomId, description, priority, reportDate, status]
     */
    public static List<String[]> getMaintenanceList(String role, String roomId) {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_MAINTENANCE",
            "role",   role   != null ? role   : "USER",
            "roomId", roomId != null ? roomId : ""
        );

        if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) return list;

        List<Map<String, Object>> items = resp.getList();
        if (items == null) return list;

        for (Map<String, Object> item : items) {
            list.add(new String[]{
                str(item, "id"),
                str(item, "roomId"),
                str(item, "description"),
                str(item, "priority"),
                str(item, "reportDate"),
                str(item, "status")
            });
        }
        return list;
    }

    /**
     * Gửi yêu cầu sửa chữa mới.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String addMaintenance(String roomId, String description, String priority, String reportDate) {
        Response resp = SocketClient.send("ADD_MAINTENANCE",
            "roomId",      roomId      != null ? roomId      : "",
            "description", description != null ? description : "",
            "priority",    priority    != null ? priority    : "Thấp",
            "reportDate",  reportDate  != null ? reportDate  : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Cập nhật trạng thái yêu cầu sửa chữa (Admin).
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String updateMaintenanceStatus(int id, String status) {
        Response resp = SocketClient.send("UPDATE_MAINTENANCE_STATUS",
            "id",     String.valueOf(id),
            "status", status != null ? status : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }
}
