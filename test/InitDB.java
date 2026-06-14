
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitDB {
    public static void main(String[] args) {
        System.out.println("=== KHỞI TẠO CƠ SỞ DỮ LIỆU TỪ SQL SCRIPT ===");

        String url = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
        String user = "root";
        String pass = "yourpassword";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                    Statement stmt = conn.createStatement()) {

                System.out.println("Đang tìm file QUANLIPHONGTRO.sql...");
                java.io.File sqlFile = new java.io.File("sql/QUANLIPHONGTRO.sql");
                if (!sqlFile.exists()) {
                    java.io.File currentDir = new java.io.File(".");
                    java.io.File[] dirs = currentDir.listFiles(java.io.File::isDirectory);
                    if (dirs != null) {
                        for (java.io.File dir : dirs) {
                            java.io.File f2 = new java.io.File(dir, "sql/QUANLIPHONGTRO.sql");
                            if (f2.exists()) {
                                sqlFile = f2;
                                break;
                            }
                        }
                    }
                }

                if (!sqlFile.exists()) {
                    throw new java.io.FileNotFoundException("Không tìm thấy file sql/QUANLIPHONGTRO.sql. Vui lòng kiểm tra lại thư mục chạy project.");
                }

                System.out.println("Đang đọc file: " + sqlFile.getAbsolutePath());
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Bỏ qua các dòng chú thích hoặc trống
                        if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                            continue;
                        }
                        sb.append(line).append("\n");
                    }
                }

                // Tách các câu lệnh SQL bằng dấu chấm phẩy
                String[] sqlStatements = sb.toString().split(";");
                System.out.println("Tìm thấy " + sqlStatements.length + " câu lệnh SQL.");

                for (String sql : sqlStatements) {
                    String cleanSql = sql.trim();
                    if (!cleanSql.isEmpty()) {
                        System.out.println(
                                "Đang chạy: " + cleanSql.substring(0, Math.min(cleanSql.length(), 60)) + "...");
                        stmt.execute(cleanSql);
                    }
                }

                System.out.println("\n=== KHỞI TẠO CSDL THÀNH CÔNG! ===");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
