package server.service;

import server.dao.RoomDAO;
import server.exception.ServiceException;
import common.model.Room;
import server.util.ServerLogger;

import java.util.List;

/**
 * RoomService — Business logic phòng trọ.
 * Validate + business rule + gọi DAO.
 * Throw ServiceException khi có lỗi — Controller chỉ bắt và trả Response.
 */
public class RoomService {

    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    /** Lấy tất cả phòng */
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    /**
     * Tìm phòng theo ID — dùng SQL WHERE qua DAO, không filter Java.
     * @return Room, hoặc null nếu không tồn tại
     */
    public Room getRoomById(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) return null;
        return roomDAO.getRoomById(roomId.trim());
    }

    /**
     * Tính tiền đặt cọc = 2 tháng tiền phòng.
     * @throws ServiceException nếu phòng không tồn tại
     */
    public double calcDeposit(String roomId) throws ServiceException {
        Room room = roomDAO.getRoomById(roomId.trim());
        if (room == null) throw new ServiceException("Không tìm thấy phòng: " + roomId);
        return room.getPrice() * 2;
    }

    /**
     * Thêm phòng mới — validate + dùng existsRoom() (SELECT 1) để check trùng.
     * @throws ServiceException nếu validation lỗi hoặc phòng đã tồn tại
     */
    public void addRoom(String roomId, String status, double price, double area, String zone, String type, int capacity, String description)
            throws ServiceException {
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không được để trống!");
        if (status == null || status.trim().isEmpty())
            throw new ServiceException("Trạng thái phòng không được để trống!");
        if (price < 0)
            throw new ServiceException("Giá phòng không được âm!");
        if (area <= 0)
            throw new ServiceException("Diện tích phòng phải lớn hơn 0!");

        // Dùng existsRoom() — SELECT 1, nhanh hơn full scan
        if (roomDAO.existsRoom(roomId.trim()))
            throw new ServiceException("Phòng " + roomId.trim() + " đã tồn tại!");

        Room room = new Room(roomId.trim(), status, price, area, zone, type, capacity, description != null ? description : "");
        if (!roomDAO.addRoom(room))
            throw new ServiceException("Lỗi hệ thống: không thể thêm phòng vào cơ sở dữ liệu!");

        ServerLogger.info("Thêm phòng thành công: " + roomId);
    }

    public void updateRoomStatus(String roomId, String newStatus) throws ServiceException {
        if (!roomDAO.updateRoomStatus(roomId, newStatus))
            throw new ServiceException("Không thể cập nhật trạng thái phòng " + roomId);
        ServerLogger.info("Trạng thái phòng " + roomId + " → " + newStatus);
    }

    /**
     * Cập nhật thông tin phòng.
     * @throws ServiceException nếu cập nhật thất bại
     */
    public void updateRoom(String roomId, String status, double price, double area, String zone, String type, int capacity, String description) throws ServiceException {
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không hợp lệ!");
        if (price < 0)
            throw new ServiceException("Giá phòng không được âm!");
        
        Room room = new Room(roomId.trim(), status, price, area, zone, type, capacity, description != null ? description : "");
        if (!roomDAO.updateRoom(room))
            throw new ServiceException("Không thể cập nhật thông tin phòng " + roomId);
        ServerLogger.info("Cập nhật phòng thành công: " + roomId);
    }

    /**
     * Xóa phòng.
     * @throws ServiceException nếu xóa thất bại (VD: đang có hợp đồng)
     */
    public void deleteRoom(String roomId) throws ServiceException {
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không hợp lệ!");
            
        Room room = roomDAO.getRoomById(roomId.trim());
        if (room != null && ("Đang thuê".equalsIgnoreCase(room.getStatus()) || "Đã đầy".equalsIgnoreCase(room.getStatus()))) {
            throw new ServiceException("Không thể xóa phòng đang có khách thuê!");
        }
        
        if (!roomDAO.deleteRoom(roomId.trim()))
            throw new ServiceException("Không thể xóa phòng! (Phòng có thể đang chứa hợp đồng/khách thuê)");
        ServerLogger.info("Xóa phòng thành công: " + roomId);
    }

    /**
     * Import hàng loạt phòng từ danh sách đã parse.
     * @throws ServiceException nếu import thất bại
     */
    public void importRooms(List<Room> rooms) throws ServiceException {
        if (rooms == null || rooms.isEmpty())
            throw new ServiceException("Danh sách phòng import trống!");
        String result = roomDAO.importRoomsBatch(rooms);
        if (!result.startsWith("SUCCESS"))
            throw new ServiceException(result);
        ServerLogger.info("Import thành công " + rooms.size() + " phòng");
    }
}
