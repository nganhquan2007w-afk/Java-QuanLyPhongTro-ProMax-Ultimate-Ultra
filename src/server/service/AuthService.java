package server.service;

import server.dao.UserDAO;
import server.exception.ServiceException;
import common.model.User;
import server.security.PasswordUtil;

/**
 * AuthService — Xác thực tài khoản, đổi mật khẩu.
 * Throw ServiceException khi lỗi — nhất quán với các service khác.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Xác thực đăng nhập.
     * @return User nếu thành công
     * @throws ServiceException nếu thông tin sai
     */
    public User authenticate(String username, String password) throws ServiceException {
        if (username == null || username.trim().isEmpty())
            throw new ServiceException("Tên tài khoản không được để trống!");
        if (password == null || password.trim().isEmpty())
            throw new ServiceException("Mật khẩu không được để trống!");

        User user = userDAO.getUserByUsername(username.trim());
        if (user == null || !PasswordUtil.verify(password, user.getPassword()))
            throw new ServiceException("Tên tài khoản hoặc mật khẩu không chính xác!");

        return user;
    }

    /**
     * Đăng xuất.
     */
    public void logout(String username) {
        // Không còn quản lý trạng thái ONLINE/OFFLINE trong DB
    }

    /**
     * Đổi mật khẩu sau khi xác thực mật khẩu cũ.
     * @throws ServiceException nếu validate lỗi hoặc mật khẩu sai
     */
    public void changePassword(String username, String oldPassword, String newPassword)
            throws ServiceException {
        if (username   == null || username.trim().isEmpty())   throw new ServiceException("Tên đăng nhập không được để trống!");
        if (oldPassword == null || oldPassword.trim().isEmpty()) throw new ServiceException("Mật khẩu cũ không được để trống!");
        if (newPassword == null || newPassword.trim().isEmpty()) throw new ServiceException("Mật khẩu mới không được để trống!");
        if (newPassword.length() < 6) throw new ServiceException("Mật khẩu mới phải có ít nhất 6 ký tự!");

        User user = userDAO.getUserByUsername(username.trim());
        if (user == null) throw new ServiceException("Tài khoản không tồn tại!");
        if (!PasswordUtil.verify(oldPassword, user.getPassword()))
            throw new ServiceException("Mật khẩu cũ không chính xác!");

        if (!userDAO.updatePassword(username.trim(), PasswordUtil.hashSHA256(newPassword)))
            throw new ServiceException("Lỗi cập nhật mật khẩu trong cơ sở dữ liệu!");
    }

    /**
     * Cập nhật thông tin cá nhân.
     * @throws ServiceException nếu thông tin không hợp lệ hoặc lỗi DB.
     */
    public void updateProfile(String username, String fullName, String phone) throws ServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("Tên đăng nhập không được để trống!");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ServiceException("Họ tên không được để trống!");
        }
        if (!userDAO.updateProfile(username.trim(), fullName.trim(), phone != null ? phone.trim() : "")) {
            throw new ServiceException("Lỗi hệ thống: Cập nhật thông tin thất bại!");
        }
    }
}
