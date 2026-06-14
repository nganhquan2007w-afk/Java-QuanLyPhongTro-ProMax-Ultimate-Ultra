package server.controller;

import server.exception.ServiceException;
import common.model.Room;
import server.protocol.Request;
import server.protocol.Response;
import server.service.RoomService;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RoomController — nhận request, gọi RoomService, trả response.
 * Không có validation, không có business logic, không có helpers.
 */
public class RoomController {

    private final RoomService roomService = new RoomService();

    /** GET_ROOMS */
    public Response handleGetRooms(Request req) {
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) return Response.success("EMPTY");

        List<Map<String, Object>> list = new ArrayList<>();
        for (Room r : rooms) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("roomId",      r.getRoomId());
            m.put("status",      r.getStatus());
            m.put("price",       r.getPrice());
            m.put("area",        r.getArea());
            m.put("zone",        r.getZone());
            m.put("type",        r.getType());
            m.put("capacity",    r.getCapacity());
            m.put("description", r.getDescription() != null ? r.getDescription() : "");
            list.add(m);
        }
        return Response.successList("OK", list);
    }

    public Response handleAddRoom(Request req) {
        try {
            roomService.addRoom(
                req.get("roomId"),
                req.get("status"),
                req.getDouble("price", 0),
                req.getDouble("area", 0),
                req.get("zone"),
                req.get("type"),
                req.getInt("capacity", 2),
                req.get("description")
            );
            return Response.success("Thêm phòng thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** UPDATE_ROOM */
    public Response handleUpdateRoom(Request req) {
        try {
            roomService.updateRoom(
                req.get("roomId"),
                req.get("status"),
                req.getDouble("price", 0),
                req.getDouble("area", 0),
                req.get("zone"),
                req.get("type"),
                req.getInt("capacity", 2),
                req.get("description")
            );
            return Response.success("Cập nhật phòng thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** DELETE_ROOM */
    public Response handleDeleteRoom(Request req) {
        try {
            roomService.deleteRoom(req.get("roomId"));
            return Response.success("Xóa phòng thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** IMPORT_ROOMS */
    public Response handleImportRooms(Request req) {
        try {
            String base64Data = req.get("csvData");
            if (base64Data == null || base64Data.trim().isEmpty()) throw new ServiceException("Dữ liệu CSV trống!");

            byte[] decoded = Base64.getDecoder().decode(base64Data);
            String csv     = new String(decoded, "UTF-8");
            String[] lines = csv.split("\n");

            List<Room> importList = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                // Flexible mapping: take whatever is provided. Minimum 1 column (Mã Phòng)
                if (cols.length < 1) continue;
                
                String roomId = cols[0].replaceAll("^\"|\"$", "").trim();
                if (roomId.isEmpty()) continue;
                
                String zone = "";
                if (cols.length >= 2 && !cols[1].trim().isEmpty()) zone = cols[1].replaceAll("^\"|\"$", "").trim();
                
                String type = "";
                if (cols.length >= 3 && !cols[2].trim().isEmpty()) type = cols[2].replaceAll("^\"|\"$", "").trim();
                
                double price = 0;
                if (cols.length >= 4) {
                    try { price = Double.parseDouble(cols[3].replaceAll("^\"|\"$", "").trim().replaceAll("[^\\d]", "")); } catch (Exception ignored) {}
                }
                
                int capacity = 0;
                if (cols.length >= 5) {
                    try { capacity = Integer.parseInt(cols[4].replaceAll("^\"|\"$", "").trim()); } catch (Exception ignored) {}
                }
                
                String status = "";
                if (cols.length >= 6 && !cols[5].trim().isEmpty()) status = cols[5].replaceAll("^\"|\"$", "").trim();
                
                String desc = "";
                if (cols.length >= 7) desc = cols[6].replaceAll("^\"|\"$", "").trim();
                
                // Fallback for older 6-column formats where capacity was not included:
                // Mã, Tên, Loại, Giá, Trạng thái, Mô tả
                if (cols.length == 6) {
                     status = cols[4].replaceAll("^\"|\"$", "").trim();
                     desc = cols[5].replaceAll("^\"|\"$", "").trim();
                     capacity = 0; // Empty instead of 2
                }

                importList.add(new Room(roomId, status, price, 20.0, zone, type, capacity, desc));
            }

            roomService.importRooms(importList);
            return Response.success("Đã import thành công " + importList.size() + " phòng");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        } catch (Exception e) {
            return Response.fail("Lỗi hệ thống khi import: " + e.getMessage());
        }
    }
}
