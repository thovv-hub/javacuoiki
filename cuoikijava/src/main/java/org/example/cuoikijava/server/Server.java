package org.example.cuoikijava.server;

import org.example.cuoikijava.dao.UserDAO;
import org.example.cuoikijava.model.User;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 8888;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 Server đang chạy trên cổng " + port + "...");
            System.out.println("Đang chờ Client kết nối...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ Có Client vừa kết nối: " + clientSocket.getInetAddress());
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String request = in.readLine();

                if (request != null && request.startsWith("LOGIN|")) {
                    String[] parts = request.split("\\|");
                    String username = parts[1];
                    String password = parts[2];
                    UserDAO dao = new UserDAO();
                    User user = dao.login(username, password);

                    if (user != null) {
                        out.println("SUCCESS|" + user.getRole() + "|" + user.getFull_name() + "|" + user.getPhone());
                        System.out.println("-> Đăng nhập thành công: " + username);
                    } else {
                        out.println("FAIL|Sai tài khoản hoặc mật khẩu");
                        System.out.println("-> Đăng nhập thất bại: " + username);
                    }
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Lỗi khởi động Server. Cổng " + port + " có thể đang được sử dụng.");
        }
    }
}