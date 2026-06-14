package server.security;

import java.security.MessageDigest;

/**
 * Tiện ích băm mật khẩu bằng SHA-256 để bảo vệ thông tin đăng nhập
 */
public class PasswordUtil {

    /**
     * Băm chuỗi mật khẩu thông thường bằng SHA-256 thành chuỗi Hex
     */
    public static String hashSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi băm mật khẩu SHA-256: ", e);
        }
    }

    /**
     * Kiểm tra mật khẩu nhập vào khớp với mật khẩu đã băm trong database hay không
     */
    public static boolean verify(String inputPassword, String hashedPassword) {
        if (inputPassword == null || hashedPassword == null) {
            return false;
        }
        return hashSHA256(inputPassword).equals(hashedPassword);
    }
}
