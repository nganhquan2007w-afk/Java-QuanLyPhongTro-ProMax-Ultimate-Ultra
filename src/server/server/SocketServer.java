package server.server;

import java.net.ServerSocket;
import java.net.Socket;

import server.util.ServerLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SocketServer — Điểm khởi chạy TCP Socket Server.
 * Mỗi kết nối Client được xử lý bởi một ClientHandler thông qua Thread Pool.
 */
public class SocketServer {
    private static final int PORT = 5000;
    // Sử dụng Thread Pool động, giới hạn tối đa luồng để tiết kiệm RAM
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        ServerLogger.info("Bắt đầu khởi động Socket Server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ServerLogger.info("Socket Server đang chạy trên cổng " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // ClientHandler tự khởi tạo tất cả Domain Controllers
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                threadPool.execute(clientHandler);
            }
        } catch (Exception e) {
            ServerLogger.error("Gặp lỗi khi chạy Server: " + e.getMessage());
        }
    }
}
