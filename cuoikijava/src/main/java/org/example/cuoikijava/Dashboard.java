package org.example.cuoikijava;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Dashboard {

    public void openDashboard(Stage stage, String username, String role) {
        String fxmlFile = "";

        if (role.equals("OWNER")) {
            fxmlFile = "/cuoiki/dashboard_owner.fxml";
        }
        else if (role.equals("DRIVER")) {
            fxmlFile = "/cuoiki/dashboard_driver.fxml";
        }
        else if (role.equals("CUSTOMER")) {
            fxmlFile = "/cuoiki/dashboard_customer.fxml";
        }
        else {
            showAlert("Lỗi", "Vai trò không hợp lệ!");
            return;
        }

        try {
            // Load giao diện từ file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1300, 750); // Kích thước chuẩn

            stage.setScene(scene);
            stage.setTitle("Bus Management System - " + role);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi Hệ Thống", "Không thể tải giao diện: " + fxmlFile + "\n\n" + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}