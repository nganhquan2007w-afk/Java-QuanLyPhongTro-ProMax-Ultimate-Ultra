package server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tiện ích ghi nhật ký hoạt động (Log) của Server
 */
public class ServerLogger {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void info(String message) {
        System.out.println("[" + sdf.format(new Date()) + "] [INFO] " + message);
    }

    public static void error(String message) {
        System.err.println("[" + sdf.format(new Date()) + "] [ERROR] " + message);
    }
}
