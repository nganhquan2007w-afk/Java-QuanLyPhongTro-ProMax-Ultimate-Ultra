package client.service;

import common.model.UserSession;
import client.socket.SocketClient;
import server.protocol.Response;

/**
 * LoginService — Client-side service xử lý đăng nhập, đăng xuất, đổi mật khẩu.
 * Giao tiếp với server qua JSON protocol (SocketClient.send).
 */
public class LoginService {

    /**
     * Đăng nhập. Tạo UserSession nếu thành công.
     * @return "SUCCESS" hoặc chuỗi thông báo lỗi.
     */
    public static String login(String username, String password) {
        Response resp = SocketClient.send("LOGIN",
            "username", username != null ? username.trim() : "",
            "password", password != null ? password : ""
        );

        if (resp.isSuccess()) {
            String role         = resp.getDataString("role");
            String actualUser   = resp.getDataString("username");
            String fullName     = resp.getDataString("fullName");
            String phone        = resp.getDataString("phone");
            if (role.trim().isEmpty())       role       = "USER";
            if (actualUser.trim().isEmpty()) actualUser = username;

            UserSession.createSession(actualUser, role, fullName, phone);

            // Lấy roomId cho khách thuê (USER)
            if ("USER".equalsIgnoreCase(role)) {
                Response roomResp = SocketClient.send("GET_ROOM_BY_USER", "username", actualUser);
                if (roomResp.isSuccess()) {
                    String roomId = roomResp.getDataString("roomId");
                    if (!roomId.trim().isEmpty()) {
                        UserSession.getInstance().setRoomId(roomId);
                    }
                }
            }
            return "SUCCESS";
        }
        return resp.getMessage();
    }

    /**
     * Đăng xuất — báo server chuyển trạng thái OFFLINE.
     */
    public static void logout() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            SocketClient.send("LOGOUT", "username", session.getUsername());
            UserSession.clearSession();
        }
    }

    /**
     * Đổi mật khẩu.
     * @return "SUCCESS" hoặc chuỗi thông báo lỗi.
     */
    public static String changePassword(String oldPassword, String newPassword) {
        UserSession session = UserSession.getInstance();
        if (session == null) return "Phiên làm việc đã hết hạn hoặc không hợp lệ!";

        Response resp = SocketClient.send("CHANGE_PASSWORD",
            "username",    session.getUsername(),
            "oldPassword", oldPassword != null ? oldPassword : "",
            "newPassword", newPassword != null ? newPassword : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    /**
     * Cập nhật hồ sơ cá nhân.
     */
    public static String updateProfile(String fullName, String phone) {
        UserSession session = UserSession.getInstance();
        if (session == null) return "Phiên làm việc đã hết hạn hoặc không hợp lệ!";

        Response resp = SocketClient.send("UPDATE_PROFILE",
            "username", session.getUsername(),
            "fullName", fullName != null ? fullName : "",
            "phone",    phone != null ? phone : ""
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }
}
