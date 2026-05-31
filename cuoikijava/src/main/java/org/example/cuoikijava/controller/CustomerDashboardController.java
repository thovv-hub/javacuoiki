package org.example.cuoikijava.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.cuoikijava.dao.*;
import org.example.cuoikijava.model.*;
import org.example.cuoikijava.security.SessionManager;
import org.example.cuoikijava.util.AESUtil;
import org.example.cuoikijava.util.ExportUtil;

public class CustomerDashboardController {
    @FXML private VBox pnlInfo, pnlTicket, pnlChat;
    @FXML private Button btnNavInfo, btnNavTicket, btnNavChat;
    @FXML private Label lblUserInfo;
    @FXML private Label lblProfileName, lblProfilePhone, lblProfileUsername;
    @FXML private ComboBox<String> cbRoute, cbTrip, cbSeat, cbPayment;
    @FXML private TableView<Ticket> tableTicket;
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
            lblUserInfo.setText("👤 " + user.getFull_name() + "\nRole: " + user.getRole());
            lblProfileName.setText("Họ và tên: " + user.getFull_name());
            lblProfilePhone.setText("Số điện thoại: " + user.getPhone());
            lblProfileUsername.setText("Tên đăng nhập (Email): " + user.getUsername());
        }

        setupTicketPanel();
        setupChatPanel();
    }

    @FXML
    void switchPanel(ActionEvent event) {
        pnlInfo.setVisible(false); pnlTicket.setVisible(false); pnlChat.setVisible(false);
        btnNavInfo.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnNavTicket.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnNavChat.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");

        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");

        if (btn == btnNavInfo) pnlInfo.setVisible(true);
        else if (btn == btnNavTicket) pnlTicket.setVisible(true);
        else if (btn == btnNavChat) pnlChat.setVisible(true);
    }

    private void setupTicketPanel() {

        cbPayment.getItems().addAll("QR", "Tiền mặt");
        cbPayment.setOnAction(e -> {
            if ("QR".equals(cbPayment.getValue())) {
                showQRPopup();
            }
        });

        cbSeat.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("(Đã đặt)")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        setDisable(true);
                    } else {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        setDisable(false);
                    }
                }
            }
        });

        TableColumn<Ticket, String> colTrip = new TableColumn<>("Chuyến xe");
        colTrip.setCellValueFactory(new PropertyValueFactory<>("trip_name"));

        TableColumn<Ticket, String> colSeatCol = new TableColumn<>("Ghế");
        colSeatCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSeatNumber())));

        TableColumn<Ticket, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("payment_status"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("PAID".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tableTicket.getColumns().addAll(colTrip, colSeatCol, colStatus);
        tableTicket.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadTicketTable();

        for(Trip trip : tripDAO.getAllTrip()) {
            String route = trip.getFromLocation() + " → " + trip.getToLocation();
            if(!cbRoute.getItems().contains(route)) cbRoute.getItems().add(route);
        }

        cbRoute.setOnAction(e -> {
            cbTrip.getItems().clear();
            String route = cbRoute.getValue();
            if(route == null) return;
            for(Trip trip : tripDAO.getAllTrip()) {
                if((trip.getFromLocation() + " → " + trip.getToLocation()).equals(route)) {
                    String displayTrip = route + " | " + trip.getDepartureTime() + " | " + String.format("%,.0fđ", trip.getTicket_price());
                    cbTrip.getItems().add(displayTrip);
                }
            }
        });

        cbTrip.setOnAction(e -> {
            cbSeat.getItems().clear();
            if(cbTrip.getValue() == null) return;
            for(int i = 1; i <= 16; i++) {
                boolean isBooked = false;
                for(Ticket t : ticketDAO.getAllTicket()) {
                    if(t.getTrip_name() != null && cbTrip.getValue().contains(t.getDeparture_time())
                            && t.getLicensePlate().equals(getPlateFromTrip(cbTrip.getValue()))
                            && t.getSeatNumber() == i) {
                        isBooked = true; break;
                    }
                }

                if(isBooked) {
                    cbSeat.getItems().add("Ghế " + i + " (Đã đặt)");
                } else {
                    cbSeat.getItems().add("Ghế " + i);
                }
            }
        });
    }

    private String getPlateFromTrip(String tripStr) {
        for(Trip trip : tripDAO.getAllTrip()) {
            if(tripStr.contains(trip.getDepartureTime()) && tripStr.contains(String.format("%,.0fđ", trip.getTicket_price()))) {
                return trip.getBus_license_plate();
            }
        }
        return "";
    }

    private void showQRPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanh Toán Bằng QR Code");
        alert.setHeaderText("Quét mã QR dưới đây để hoàn tất thanh toán");

        try {

            String path = "file:D:/truong/javatuong/cuoikijava/src/main/resources/qr.jpg";
            ImageView qrImage = new ImageView(new Image(path));
            qrImage.setFitWidth(250);
            qrImage.setFitHeight(250);
            alert.setGraphic(qrImage);
        } catch (Exception ex) {
            System.out.println("Lỗi không tải được ảnh QR: " + ex.getMessage());
        }

        alert.showAndWait();
    }

    private void loadTicketTable() {
        tableTicket.getItems().clear();
        User currentUser = SessionManager.getCurrentUser();
        for(Ticket ticket : ticketDAO.getAllTicket()) {
            if(ticket.getCustomerName() != null && ticket.getCustomerName().equals(currentUser.getFull_name())) {
                tableTicket.getItems().add(ticket);
            }
        }
    }

    @FXML
    void handlePayment() {
        if(cbTrip.getValue() == null || cbSeat.getValue() == null || cbPayment.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn đầy đủ thông tin!");
            return;
        }

        if (cbSeat.getValue().contains("(Đã đặt)")) {
            showAlert(Alert.AlertType.WARNING, "Lỗi Chọn Ghế", "Ghế này đã có người đặt, vui lòng chọn ghế màu xanh!");
            return;
        }

        try {
            User user = SessionManager.getCurrentUser();
            String seatStr = cbSeat.getValue().replace("Ghế ", "").trim();
            int seatNumber = Integer.parseInt(seatStr);

            Trip selectedTrip = null;
            for(Trip trip : tripDAO.getAllTrip()) {
                if(cbTrip.getValue().contains(trip.getDepartureTime()) && cbTrip.getValue().contains(String.format("%,.0fđ", trip.getTicket_price()))) {
                    selectedTrip = trip;
                    break;
                }
            }
            if(selectedTrip == null) return;

            Ticket ticket = new Ticket();
            ticket.setCustomerName(user.getFull_name());
            ticket.setCustomer_phone(user.getPhone());
            ticket.setCustomer_email(user.getUsername());
            ticket.setTicket_status("BOOKED");
            ticket.setTicket_price(selectedTrip.getTicket_price());
            ticket.setFrom_location(selectedTrip.getFromLocation());
            ticket.setTo_location(selectedTrip.getToLocation());
            ticket.setDeparture_time(selectedTrip.getDepartureTime());
            ticket.setLicensePlate(selectedTrip.getBus_license_plate());
            ticket.setSeatNumber(seatNumber);
            ticket.setTrip_name(cbTrip.getValue());

            if(cbPayment.getValue().equals("QR")) {
                ticket.setPayment_method("QR");
                ticket.setPayment_status("PAID");
            } else {
                ticket.setPayment_method("CASH");
                ticket.setPayment_status("UNPAID");
            }

            ticketDAO.bookTicket(ticket);
            ExportUtil.exportTicketToXML(ticket);

            try {
                String route = selectedTrip.getFromLocation() + " -> " + selectedTrip.getToLocation();
                String billLine = String.format("%s | %s | %s | %,.0fđ\n",
                        user.getFull_name(),
                        selectedTrip.getBus_license_plate(),
                        route,
                        selectedTrip.getTicket_price());

                java.io.File file = new java.io.File("src/main/resources/hoadon.txt");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                java.io.FileWriter fw = new java.io.FileWriter(file, true);
                java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
                bw.write(billLine);
                bw.close();
                System.out.println("Đã lưu hóa đơn TXT thành công!");

            } catch (Exception ex) {
                System.out.println("Lỗi ghi file txt: " + ex.getMessage());
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đặt vé thành công!");
            loadTicketTable();
            cbSeat.getSelectionModel().clearSelection();
            cbTrip.fireEvent(new ActionEvent());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupChatPanel() {
        cbReceiverRole.getItems().addAll("OWNER", "DRIVER");
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
                lbl.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-background-radius: 10;");
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}