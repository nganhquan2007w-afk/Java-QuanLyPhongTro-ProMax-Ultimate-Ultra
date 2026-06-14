package server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Quản lý kết nối cơ sở dữ liệu MySQL bằng HikariCP Connection Pool
 */
public class ConnectDB {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            File configFile = new File("server_config.properties");
            if (configFile.exists()) {
                try (FileInputStream in = new FileInputStream(configFile)) {
                    props.load(in);
                }
            } else {
                server.util.ServerLogger.error("Không tìm thấy server_config.properties, đang tạo file mặc định...");
                props.setProperty("db.url",
                        "jdbc:mysql://localhost:3306/QUANLYPHONGTRO?createDatabaseIfNotExist=true&allowMultiQueries=true&characterEncoding=UTF-8");
                props.setProperty("db.user", "root");
                props.setProperty("db.password", "yourpassword");
                props.setProperty("db.pool.maxSize", "10");
                props.setProperty("db.pool.minIdle", "2");
                props.setProperty("db.pool.timeout", "30000");
                try (FileOutputStream out = new FileOutputStream(configFile)) {
                    props.store(out, "Cấu hình Database Server");
                }
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));

            // Pool configuration
            int maxSize = Integer.parseInt(props.getProperty("db.pool.maxSize"));
            if (maxSize < 50)
                maxSize = 50; // Tối ưu hóa: Ép buộc tối thiểu 50 kết nối để tránh quá tải do reload liên tục
            config.setMaximumPoolSize(maxSize);
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.timeout")));

            // Optimizations for MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            // Tự động khởi tạo database nếu chưa có bảng
            // Toàn bộ cấu trúc bảng được định nghĩa trong sql/QUANLIPHONGTRO.sql
            try (Connection conn = dataSource.getConnection()) {
                initializeDatabase(conn);
            } catch (Exception e) {
                server.util.ServerLogger.error("Lỗi gọi initializeDatabase: " + e.getMessage());
            }

        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi khởi tạo Connection Pool: " + e.getMessage());
        }
    }

    private static void initializeDatabase(Connection conn) {
        try (java.sql.Statement checkStmt = conn.createStatement()) {
            try (java.sql.ResultSet rs = checkStmt.executeQuery("SHOW TABLES LIKE 'users'")) {
                if (rs.next()) {
                    server.util.ServerLogger.info("CSDL đã được khởi tạo trước đó — bỏ qua.");
                    return;
                }
            }
        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi kiểm tra CSDL: " + e.getMessage());
            return;
        }

        server.util.ServerLogger.info("Đang khởi tạo cấu trúc CSDL lần đầu tiên...");

        // Tìm file SQL
        java.io.File sqlFile = new java.io.File("sql/QUANLIPHONGTRO.sql");
        if (!sqlFile.exists()) {
            java.io.File currentDir = new java.io.File(".");
            java.io.File[] dirs = currentDir.listFiles(java.io.File::isDirectory);
            if (dirs != null) {
                for (java.io.File dir : dirs) {
                    java.io.File f2 = new java.io.File(dir, "sql/QUANLIPHONGTRO.sql");
                    if (f2.exists()) { sqlFile = f2; break; }
                }
            }
        }
        if (!sqlFile.exists()) {
            server.util.ServerLogger.error(
                "Không tìm thấy file sql/QUANLIPHONGTRO.sql! Vui lòng kiểm tra lại thư mục chạy.");
            return;
        }

        try {
            String content = new String(java.nio.file.Files.readAllBytes(sqlFile.toPath()), "UTF-8");

            // Loại bỏ các lệnh DROP/CREATE DATABASE và USE — URL JDBC đã lo việc này
            content = content.replaceAll("(?i)DROP\\s+DATABASE[^;]+;", "");
            content = content.replaceAll("(?i)CREATE\\s+DATABASE[^;]+;", "");
            content = content.replaceAll("(?i)USE\\s+\\w+\\s*;", "");

            // Xóa comment dạng -- ... (đến cuối dòng)
            content = content.replaceAll("--[^\n]*", "");

            // Tách từng câu lệnh SQL theo dấu ";" và thực thi lần lượt
            String[] statements = content.split(";");
            int successCount = 0;
            int errorCount   = 0;

            try (java.sql.Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (trimmed.isEmpty()) continue;
                    try {
                        stmt.execute(trimmed);
                        successCount++;
                    } catch (java.sql.SQLException ex) {
                        // Bỏ qua lỗi "table/index already exists" (có thể xảy ra khi chạy lại)
                        int errCode = ex.getErrorCode();
                        if (errCode == 1050 || errCode == 1061 || errCode == 1062) {
                            // 1050: Table already exists
                            // 1061: Duplicate key name (index)
                            // 1062: Duplicate entry
                            server.util.ServerLogger.info("Bỏ qua (đã tồn tại): " + trimmed.substring(0, Math.min(60, trimmed.length())));
                        } else {
                            errorCount++;
                            server.util.ServerLogger.error("Lỗi SQL [" + errCode + "]: " + ex.getMessage()
                                + " | Câu lệnh: " + trimmed.substring(0, Math.min(80, trimmed.length())));
                        }
                    }
                }
            }

            server.util.ServerLogger.info(
                "Khởi tạo CSDL hoàn tất: " + successCount + " lệnh thành công, " + errorCount + " lỗi.");

        } catch (Exception e) {
            server.util.ServerLogger.error("Lỗi khi đọc/thực thi file SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        throw new SQLException("DataSource chưa được khởi tạo!");
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
