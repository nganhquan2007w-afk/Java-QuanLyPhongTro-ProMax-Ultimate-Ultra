package common.model;

/**
 * Quản lý phiên làm việc hiện tại của người dùng sau khi đăng nhập thành công ở phía Client.
 * Lưu thông tin: username, role, roomId (dùng cho khách thuê).
 */
public class UserSession {
    private static UserSession instance;

    private final String username;
    private final String role;
    private String roomId; // Phòng của khách thuê (null nếu là Admin)
    private String fullName;
    private String phone;

    private UserSession(String username, String role, String fullName, String phone) {
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.roomId = null;
    }

    /**
     * Tạo một phiên làm việc mới (không có roomId — dùng cho Admin hoặc khi chưa biết phòng)
     */
    public static void createSession(String username, String role, String fullName, String phone) {
        instance = new UserSession(username, role, fullName, phone);
    }

    /**
     * Tạo phiên làm việc kèm roomId (dùng cho khách thuê biết phòng của mình)
     */
    public static void createSession(String username, String role, String fullName, String phone, String roomId) {
        instance = new UserSession(username, role, fullName, phone);
        instance.roomId = roomId;
    }

    /**
     * Lấy thông tin phiên làm việc hiện tại
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Hủy phiên làm việc (Đăng xuất)
     */
    public static void clearSession() {
        instance = null;
    }

    public String getUsername() { return username; }

    public String getRole() { return role; }

    /**
     * Trả về mã phòng của khách thuê.
     * Null hoặc rỗng nếu là Admin (không có phòng riêng).
     */
    public String getRoomId() { return roomId; }

    /**
     * Gán mã phòng sau khi tra cứu hợp đồng (dành cho khách thuê).
     */
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
