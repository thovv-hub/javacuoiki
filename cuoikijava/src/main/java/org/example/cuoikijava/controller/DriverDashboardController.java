package org.example.cuoikijava.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.cuoikijava.dao.*;
import org.example.cuoikijava.model.*;
import org.example.cuoikijava.security.SessionManager;
import org.example.cuoikijava.util.AESUtil;

public class DriverDashboardController {

    @FXML private VBox pnlInfo, pnlTrip, pnlChat;
    @FXML private Button btnNavInfo, btnNavTrip, btnNavChat;
    @FXML private Label lblUserInfo, lblInfo;
    @FXML private Label lblProfileName, lblProfilePhone, lblProfileUsername;
    @FXML private ComboBox<String> cbTrip;
    @FXML private VBox seatBox;
    @FXML private VBox messageBox;
    @FXML private ComboBox<String> cbReceiverRole, cbReceiver;
    @FXML private TextArea txtMessage;

    private final TripDAO tripDAO = new TripDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private final UserDAO userDAO = new UserDAO();
    private Timeline chatTimeline;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if(user != null) {

            lblUserInfo.setText("🧑‍✈️ " + user.getFull_name() + "\nRole: " + user.getRole());
            lblProfileName.setText("Họ và tên tài xế: " + user.getFull_name());
            lblProfilePhone.setText("Số điện thoại: " + user.getPhone());
            lblProfileUsername.setText("Tài khoản hệ thống: " + user.getUsername());
        }

        setupTripPanel();
        setupChatPanel();
    }

    @FXML
    void switchPanel(ActionEvent event) {
        pnlInfo.setVisible(false); pnlTrip.setVisible(false); pnlChat.setVisible(false);
        String btnStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;";
        btnNavInfo.setStyle(btnStyle);
        btnNavTrip.setStyle(btnStyle);
        btnNavChat.setStyle(btnStyle);
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");

        if (btn == btnNavInfo) pnlInfo.setVisible(true);
        else if (btn == btnNavTrip) pnlTrip.setVisible(true);
        else if (btn == btnNavChat) pnlChat.setVisible(true);
    }

    private void setupTripPanel() {
        User currentUser = SessionManager.getCurrentUser();

        for(Trip trip : tripDAO.getAllTrip()) {
            if(trip.getDriverName() != null && trip.getDriverName().equals(currentUser.getFull_name())) {
                String tripText = trip.getFromLocation() + " → " + trip.getToLocation() + " | " + trip.getDepartureTime() + " | " + String.format("%,.0fđ", trip.getTicket_price());
                cbTrip.getItems().add(tripText);
            }
        }

        cbTrip.setOnAction(e -> loadSeats());
    }

    private void loadSeats() {
        seatBox.getChildren().clear();
        String selected = cbTrip.getValue();
        if(selected == null) return;
        int booked = 0;
        int empty = 16;
        for(int i = 1; i <= 16; i++) {
            boolean found = false;

            for(Ticket ticket : ticketDAO.getAllTicket()) {
                String routeText = ticket.getFrom_location() + " → " + ticket.getTo_location() + " | " + ticket.getDeparture_time();
                if(selected.contains(routeText) && ticket.getSeatNumber() == i) {
                    Label lblSeat = new Label("💺 Ghế " + i + " | Khách: " + ticket.getCustomerName() + " | SĐT: " + ticket.getCustomer_phone() + " | TT: " + ticket.getPayment_status());
                    lblSeat.setPrefHeight(45);
                    lblSeat.setMaxWidth(Double.MAX_VALUE);

                    if("PAID".equals(ticket.getPayment_status())) {
                        lblSeat.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #d5f5e3; -fx-border-color: #27ae60; -fx-border-radius: 5; -fx-background-radius: 5;");
                    } else {
                        lblSeat.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #fadbd8; -fx-border-color: #e74c3c; -fx-border-radius: 5; -fx-background-radius: 5;");
                    }

                    seatBox.getChildren().add(lblSeat);
                    found = true;
                    booked++;
                    empty--;
                    break;
                }
            }

            if(!found) {
                Label lblSeat = new Label("💺 Ghế " + i + " (Trống)");
                lblSeat.setPrefHeight(45);
                lblSeat.setMaxWidth(Double.MAX_VALUE);
                lblSeat.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
                seatBox.getChildren().add(lblSeat);
            }
        }

        lblInfo.setText("Đã đặt: " + booked + " | Còn trống: " + empty);
    }

    private void setupChatPanel() {
        cbReceiverRole.getItems().addAll("OWNER", "CUSTOMER");
        cbReceiverRole.setOnAction(e -> {
            cbReceiver.getItems().clear();
            String role = cbReceiverRole.getValue();
            if(role == null) return;
            for(User u : userDAO.getAllUser()) {
                if(u.getRole().equals(role)) cbReceiver.getItems().add(u.getUsername());
            }
        });

        loadMessages();
        chatTimeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> loadMessages()));
        chatTimeline.setCycleCount(Timeline.INDEFINITE);
        chatTimeline.play();
    }

    private void loadMessages() {
        messageBox.getChildren().clear();
        String currentUser = SessionManager.getCurrentUser().getUsername();
        for(Message m : messageDAO.getAllMessage()) {
            if(m.getSender().equals(currentUser) || m.getReceiver().equals(currentUser)) {
                Label lbl = new Label("[" + m.getSenderRole() + "] " + m.getSender() + ": \n" + AESUtil.decrypt(m.getContent()));
                lbl.setStyle("-fx-background-color: #f1f2f6; -fx-padding: 10; -fx-background-radius: 10;");
                messageBox.getChildren().add(lbl);
            }
        }
    }

    @FXML
    void handleSendMessage() {
        if(cbReceiver.getValue() == null || txtMessage.getText().isEmpty()) return;

        Message msg = new Message();
        msg.setSender(SessionManager.getCurrentUser().getUsername());
        msg.setSenderRole(SessionManager.getCurrentUser().getRole());
        msg.setReceiver(cbReceiver.getValue());
        msg.setReceiverRole(cbReceiverRole.getValue());
        msg.setContent(AESUtil.encrypt(txtMessage.getText()));
        msg.setTime(java.time.LocalDateTime.now().toString());

        messageDAO.sendMessage(msg);
        txtMessage.clear();
        loadMessages();
    }

    @FXML
    void handleLogout(ActionEvent event) {
        if(chatTimeline != null) chatTimeline.stop();
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cuoiki/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 850, 600));
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}