package server.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * CryptoUtil — Lớp tiện ích mã hóa đối xứng (AES-128) để bảo vệ dữ liệu nhạy cảm
 * như Số điện thoại, CCCD, Địa chỉ trong Database.
 */
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    // Khóa bí mật cứng 16 byte (128-bit) dùng cho AES.
    private static final byte[] KEY = "QLPhongTroSecret".getBytes(StandardCharsets.UTF_8);

    /**
     * Mã hóa chuỗi văn bản (Plaintext) thành Base64.
     */
    public static String encrypt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi mã hóa dữ liệu: " + e.getMessage());
            return value; // Dự phòng: trả về bản gốc nếu lỗi
        }
    }

    /**
     * Giải mã chuỗi Base64 trở lại văn bản gốc (Plaintext).
     */
    public static String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.trim().isEmpty()) {
            return encryptedValue;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // Trường hợp không phải Base64 (có thể là dữ liệu cũ chưa mã hóa)
            return encryptedValue;
        } catch (Exception e) {
            // Lỗi giải mã hoặc dữ liệu không hợp lệ
            server.util.ServerLogger.error("Lỗi giải mã dữ liệu: " + e.getMessage());
            return encryptedValue; 
        }
    }
}
