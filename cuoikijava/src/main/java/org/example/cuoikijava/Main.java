package org.example.cuoikijava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/cuoiki/login.fxml"));
        Scene scene = new Scene(loader.load(), 850, 600);
        stage.setTitle("Login System");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText(null);
            alert.setContentText("Exit astpplication?");

            if(alert.showAndWait().get() != ButtonType.OK) {
                event.consume();
            }
        });

        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }
}