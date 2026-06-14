package client.socket;

import server.protocol.Request;
import server.protocol.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;


/**
 * SocketClient — Gửi JSON Request lên Server, nhận JSON Response về.
 *
 * Short-lived connection: mỗi request mở/đóng socket riêng.
 * Hai overload tiện lợi:
 *   send(Request req)                        — gửi Request object trực tiếp
 *   send(String action, String... kvPairs)   — builder nhanh từ key-value varargs
 */
public class SocketClient {

    private static String HOST = "localhost";
    private static int    PORT = 5000;
    
    static {
        loadConfig();
    }

    private static void loadConfig() {
        File configFile = new File("client_config.properties");
        Properties props = new Properties();
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
                HOST = props.getProperty("SERVER_IP", "localhost").trim();
                PORT = Integer.parseInt(props.getProperty("SERVER_PORT", "5000").trim());
            } catch (Exception e) {
                System.err.println("Loi khi doc client_config.properties: " + e.getMessage());
            }
        } else {
            try (FileOutputStream out = new FileOutputStream(configFile)) {
                props.setProperty("SERVER_IP", "localhost");
                props.setProperty("SERVER_PORT", "5000");
                props.store(out, "Cau hinh ket noi Server - Thay doi SERVER_IP neu chay tren may khac");
            } catch (Exception e) {
                System.err.println("Loi khi tao client_config.properties: " + e.getMessage());
            }
        }
    }

    /**
     * Gửi Request và nhận Response từ Server.
     */
    public static Response send(Request req) {
        try (
            Socket         socket = new Socket(HOST, PORT);
            PrintWriter    out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
        ) {
            out.println(req.toJson());
            return Response.fromJson(in.readLine());
        } catch (Exception e) {
            System.err.println("Loi ket noi Server: " + e.getMessage());
            return Response.fail("Khong the ket noi may chu! Kiem tra lai Server.");
        }
    }

    /**
     * Builder tiện lợi — truyền params dạng varargs key-value.
     * Ví dụ: SocketClient.send("LOGIN", "username", "admin", "password", "123")
     */
    public static Response send(String action, String... keyValues) {
        Request req = new Request(action);
        Map<String, String> params = req.getParams();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            params.put(keyValues[i], keyValues[i + 1]);
        }
        return send(req);
    }

    /**
     * Shortcut khi không có params.
     * Ví dụ: SocketClient.send("GET_NOTIFICATIONS")
     */
    public static Response send(String action) {
        return send(new Request(action));
    }
}
