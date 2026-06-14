package client.service;

import common.model.Room;
import client.socket.SocketClient;
import server.protocol.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RoomService — Client-side service quản lý phòng trọ.
 * Giao tiếp với server qua JSON protocol.
 */
public class RoomService {

    /**
     * Lấy danh sách tất cả phòng từ server.
     */
    public static List<Room> getRooms() {
        List<Room> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_ROOMS");

        if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) return list;

        List<Map<String, Object>> items = resp.getList();
        if (items == null) return list;

        for (Map<String, Object> item : items) {
            try {
                String roomId      = str(item, "roomId");
                String status      = str(item, "status");
                double price       = dbl(item, "price");
                double area        = dbl(item, "area");
                String zone        = str(item, "zone");
                String type        = str(item, "type");
                int capacity       = intVal(item, "capacity");
                String description = str(item, "description");
                list.add(new Room(roomId, status, price, area, zone, type, capacity, description));
            } catch (Exception e) {
                System.err.println("Lỗi parse dữ liệu phòng: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Thêm phòng mới.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String addRoom(String roomId, String status, double price, double area, String zone, String type, int capacity, String description) {
        Response resp = SocketClient.send("ADD_ROOM",
            "roomId",      roomId      != null ? roomId.trim() : "",
            "status",      status      != null ? status : "",
            "price",       String.valueOf(price),
            "area",        String.valueOf(area),
            "zone",        zone        != null ? zone : "",
            "type",        type        != null ? type : "",
            "capacity",    String.valueOf(capacity),
            "description", description != null ? description : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Import hàng loạt phòng từ CSV (base64).
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String importRoomsBatch(String base64CsvData) {
        Response resp = SocketClient.send("IMPORT_ROOMS", "csvData", base64CsvData);
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Cập nhật thông tin phòng.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String updateRoom(String roomId, String status, double price, double area, String zone, String type, int capacity, String description) {
        Response resp = SocketClient.send("UPDATE_ROOM",
            "roomId",      roomId      != null ? roomId.trim() : "",
            "status",      status      != null ? status : "",
            "price",       String.valueOf(price),
            "area",        String.valueOf(area),
            "zone",        zone        != null ? zone : "",
            "type",        type        != null ? type : "",
            "capacity",    String.valueOf(capacity),
            "description", description != null ? description : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Xóa phòng.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String deleteRoom(String roomId) {
        Response resp = SocketClient.send("DELETE_ROOM", "roomId", roomId);
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    // ---- Helpers ----

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }

    private static double dbl(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return 0.0;
        try { return Double.parseDouble(v.toString()); }
        catch (NumberFormatException e) { return 0.0; }
    }
    
    private static int intVal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return 0;
        try { return Integer.parseInt(v.toString()); }
        catch (NumberFormatException e) {
            // Đôi khi Gson parse số nguyên thành double, ví dụ 2.0
            try { return (int) Double.parseDouble(v.toString()); }
            catch (Exception ex) { return 0; }
        }
    }
}
