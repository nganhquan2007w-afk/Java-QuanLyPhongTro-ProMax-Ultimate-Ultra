package server.controller;

import server.exception.ServiceException;
import common.model.User;
import server.protocol.Request;
import server.protocol.Response;
import server.service.AuthService;
import server.util.ServerLogger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AuthController — nhận request, gọi AuthService, trả response.
 */
public class AuthController {

    private final AuthService authService = new AuthService();

    /** LOGIN */
    public Response handleLogin(Request req) {
        try {
            User user = authService.authenticate(req.get("username"), req.get("password"));
            ServerLogger.info("Dang nhap thanh cong: " + user.getUsername());
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("role",     user.getRole());
            data.put("username", user.getUsername());
            data.put("fullName", user.getFullName() != null ? user.getFullName() : "");
            data.put("phone",    user.getPhone() != null ? user.getPhone() : "");
            return Response.success("Đăng nhập thành công!", data);
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** LOGOUT */
    public Response handleLogout(Request req) {
        authService.logout(req.get("username"));
        return Response.success("Đăng xuất thành công!");
    }

    /** CHANGE_PASSWORD */
    public Response handleChangePassword(Request req) {
        try {
            authService.changePassword(
                req.get("username"), req.get("oldPassword"), req.get("newPassword"));
            return Response.success("Đổi mật khẩu thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }

    /** UPDATE_PROFILE */
    public Response handleUpdateProfile(Request req) {
        try {
            authService.updateProfile(req.get("username"), req.get("fullName"), req.get("phone"));
            return Response.success("Cập nhật thông tin cá nhân thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
