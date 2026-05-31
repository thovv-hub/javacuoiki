    package org.example.cuoikijava.controller;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Node;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.PasswordField;
    import javafx.scene.control.TextField;
    import javafx.stage.Stage;
    import org.example.cuoikijava.Dashboard;
    import org.example.cuoikijava.dao.UserDAO;
    import org.example.cuoikijava.model.User;
    import org.example.cuoikijava.security.SessionManager;
    import java.net.Socket;
    import java.io.*;

    public class LoginController {
        @FXML
        private TextField txtUsername;
        @FXML
        private PasswordField txtPassword;
        private final UserDAO dao = new UserDAO();
        @FXML
        void openRegister(ActionEvent event) {

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cuoiki/register.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }

            catch (Exception e) {

                e.printStackTrace();
            }
        }

        @FXML
        void handleLogin(ActionEvent event) {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            if (username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Vui lòng nhập đầy đủ thông tin!");
                alert.showAndWait();
                return;
            }

            try (java.net.Socket socket = new java.net.Socket("localhost", 8888)) {
                java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
                out.println("LOGIN|" + username + "|" + password);
                String response = in.readLine();

                if (response != null && response.startsWith("SUCCESS|")) {
                    String[] parts = response.split("\\|");
                    String role = parts[1];
                    String fullName = parts[2];
                    String phone = parts[3];
                    User user = new User();
                    user.setUsername(username);
                    user.setRole(role);
                    user.setFull_name(fullName);
                    user.setPhone(phone);
                    SessionManager.setCurrentUser(user);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Dashboard dashboard = new Dashboard();
                    dashboard.openDashboard(stage, user.getUsername(), user.getRole());
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Sai tài khoản hoặc mật khẩu!");
                    alert.showAndWait();
                }

            } catch (java.net.ConnectException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi kết nối Server");
                alert.setHeaderText(null);
                alert.setContentText("Không thể kết nối đến Server. Bạn đã bật Server chưa?");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Lỗi hệ thống: " + e.getMessage());
                alert.showAndWait();
            }
        }


    }