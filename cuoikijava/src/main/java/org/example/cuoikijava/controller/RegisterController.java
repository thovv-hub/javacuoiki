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
import org.example.cuoikijava.model.User;
import org.example.cuoikijava.dao.UserDAO;

public class RegisterController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtPhone;
    @FXML
    public void openLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cuoiki/login.fxml"));
            // Set size 850x600 để khớp với UI Login mới
            Scene scene = new Scene(loader.load(), 850, 600);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String fullName = txtFullName.getText();
        String phone = txtPhone.getText();

        if(username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng điền đầy đủ thông tin!");
            alert.showAndWait();
            return;
        }
        UserDAO userDAO = new UserDAO();

        if(userDAO.usernameExists(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng ký");
            alert.setHeaderText(null);
            alert.setContentText("Tên đăng nhập đã tồn tại!");
            alert.showAndWait();
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFull_name(fullName);
        user.setPhone(phone);
        user.setRole("CUSTOMER");
        userDAO.register(user);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đăng ký thành công!");
        alert.showAndWait();
        openLogin(event);
    }
}