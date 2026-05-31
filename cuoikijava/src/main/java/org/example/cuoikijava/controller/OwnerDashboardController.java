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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.cuoikijava.dao.*;
import org.example.cuoikijava.model.*;
import org.example.cuoikijava.security.SessionManager;
import org.example.cuoikijava.util.AESUtil;

public class OwnerDashboardController {

    @FXML private VBox pnlInfo, pnlHome, pnlTicket, pnlTrip, pnlUser, pnlChat;
    @FXML private Button btnNavInfo, btnNavHome, btnNavTicket, btnNavTrip, btnNavUser, btnNavChat;
    @FXML private Label lblUserInfo;
    @FXML private Label lblProfileName, lblProfilePhone, lblProfileUsername;
    @FXML private Label lblTotalTicket, lblRevenue, lblTotalDriver, lblTotalCustomer;
    @FXML private TableView<Ticket> tableTicket;
    @FXML private TableView<Trip> tableTrip;
    @FXML private TextField txtFrom, txtTo, txtTime, txtPrice;
    @FXML private ComboBox<String> cbDriverForTrip;
    @FXML private TableView<User> tableUser;
    @FXML private TextField txtUsername, txtFullName, txtPhone;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbUserRole;
    @FXML private VBox messageBox;
    @FXML private ComboBox<String> cbReceiverRole, cbReceiver;
    @FXML private TextArea txtChatMessage;
    private final TicketDAO ticketDAO = new TicketDAO();
    private final TripDAO tripDAO = new TripDAO();
    private final UserDAO userDAO = new UserDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private Timeline chatTimeline;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if(user != null) {
            lblUserInfo.setText("👑 " + user.getFull_name() + "\nRole: " + user.getRole());
            lblProfileName.setText("Họ và tên: " + user.getFull_name());
            lblProfilePhone.setText("Số điện thoại: " + user.getPhone());
            lblProfileUsername.setText("Tài khoản hệ thống: " + user.getUsername());
        }

        setupHomePanel();
        setupTicketPanel();
        setupTripPanel();
        setupUserPanel();
        setupChatPanel();
    }

    @FXML
    void switchPanel(ActionEvent event) {
        pnlInfo.setVisible(false); pnlHome.setVisible(false); pnlTicket.setVisible(false);
        pnlTrip.setVisible(false); pnlUser.setVisible(false); pnlChat.setVisible(false);
        resetButtons();
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10;");
        if (btn == btnNavInfo) { pnlInfo.setVisible(true); }
        else if (btn == btnNavHome) { pnlHome.setVisible(true); loadStatistics(); }
        else if (btn == btnNavTicket) { pnlTicket.setVisible(true); loadTicketTable(); }
        else if (btn == btnNavTrip) { pnlTrip.setVisible(true); loadTripTable(); }
        else if (btn == btnNavUser) { pnlUser.setVisible(true); loadUserTable(); }
        else if (btn == btnNavChat) { pnlChat.setVisible(true); }
    }

    private void resetButtons() {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;";
        btnNavInfo.setStyle(defaultStyle); btnNavHome.setStyle(defaultStyle);
        btnNavTicket.setStyle(defaultStyle); btnNavTrip.setStyle(defaultStyle);
        btnNavUser.setStyle(defaultStyle); btnNavChat.setStyle(defaultStyle);
    }

    @FXML
    private void setupHomePanel() { loadStatistics(); }
    @FXML
    private void loadStatistics() {
        int totalTicket = ticketDAO.getAllTicket().size();
        lblTotalTicket.setText(String.valueOf(totalTicket));
        double revenue = 0;
        for(Ticket t : ticketDAO.getAllTicket()) revenue += t.getTicket_price();
        lblRevenue.setText(String.format("%,.0fđ", revenue));
        int totalDriver = 0, totalCustomer = 0;
        for (User u : userDAO.getAllUser()) {
            if ("DRIVER".equals(u.getRole())) totalDriver++;
            else if ("CUSTOMER".equals(u.getRole())) totalCustomer++;
        }
        lblTotalDriver.setText(String.valueOf(totalDriver));
        lblTotalCustomer.setText(String.valueOf(totalCustomer));
    }

    private void setupTicketPanel() {
        TableColumn<Ticket, String> colCus = new TableColumn<>("Khách");
        colCus.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        TableColumn<Ticket, String> colTrip = new TableColumn<>("Chuyến");
        colTrip.setCellValueFactory(new PropertyValueFactory<>("trip_name"));
        TableColumn<Ticket, String> colSeat = new TableColumn<>("Ghế");
        colSeat.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSeatNumber())));

        TableColumn<Ticket, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("payment_status"));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
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

        tableTicket.getColumns().clear();
        tableTicket.getColumns().addAll(colCus, colTrip, colSeat, colStatus);
        tableTicket.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void loadTicketTable() { tableTicket.getItems().setAll(ticketDAO.getAllTicket()); }

    @FXML
    void handleDeleteTicket() {
        Ticket selected = tableTicket.getSelectionModel().getSelectedItem();
        if(selected != null) {
            ticketDAO.deleteTicket(selected.getId());
            loadTicketTable();
            loadStatistics();
        }
    }
    private void setupTripPanel() {
        TableColumn<Trip, String> colFrom = new TableColumn<>("Điểm đi");
        colFrom.setCellValueFactory(new PropertyValueFactory<>("fromLocation"));
        TableColumn<Trip, String> colTo = new TableColumn<>("Điểm đến");
        colTo.setCellValueFactory(new PropertyValueFactory<>("toLocation"));
        TableColumn<Trip, String> colTime = new TableColumn<>("Giờ chạy");
        colTime.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        TableColumn<Trip, String> colDriver = new TableColumn<>("Tài xế");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driverName"));
        TableColumn<Trip, String> colPrice = new TableColumn<>("Giá vé");
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0fđ", data.getValue().getTicket_price())));
        tableTrip.getColumns().addAll(colFrom, colTo, colTime, colDriver, colPrice);
        tableTrip.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        updateDriverComboBox();
        tableTrip.setOnMouseClicked(e -> {
            Trip selected = tableTrip.getSelectionModel().getSelectedItem();
            if(selected != null) {
                txtFrom.setText(selected.getFromLocation());
                txtTo.setText(selected.getToLocation());
                txtTime.setText(selected.getDepartureTime());
                txtPrice.setText(String.valueOf(selected.getTicket_price()));
                cbDriverForTrip.setValue(selected.getDriverName());
            }
        });
    }

    private void updateDriverComboBox() {
        cbDriverForTrip.getItems().clear();
        for(User u : userDAO.getAllUser()) {
            if("DRIVER".equals(u.getRole())) cbDriverForTrip.getItems().add(u.getFull_name());
        }
    }

    @FXML
    private void loadTripTable() { tableTrip.getItems().setAll(tripDAO.getAllTrip()); }

    @FXML
    void handleAddTrip() {
        try {
            Trip trip = new Trip();
            trip.setFromLocation(txtFrom.getText());
            trip.setToLocation(txtTo.getText());
            trip.setDepartureTime(txtTime.getText());
            trip.setTicket_price(Double.parseDouble(txtPrice.getText()));
            trip.setDriverName(cbDriverForTrip.getValue());
            tripDAO.addTrip(trip);
            loadTripTable();
            clearTripForm();

        } catch (Exception e) {
            showAlert("Lỗi", "Kiểm tra lại dữ liệu nhập!");
        }
    }

    @FXML
    void handleUpdateTrip() {
        Trip selected = tableTrip.getSelectionModel().getSelectedItem();
        if(selected != null) {
            selected.setFromLocation(txtFrom.getText());
            selected.setToLocation(txtTo.getText());
            selected.setDepartureTime(txtTime.getText());
            selected.setTicket_price(Double.parseDouble(txtPrice.getText()));
            selected.setDriverName(cbDriverForTrip.getValue());
            tripDAO.updateTrip(selected);
            loadTripTable();
            clearTripForm();
        }
    }

    @FXML
    void handleDeleteTrip() {
        Trip selected = tableTrip.getSelectionModel().getSelectedItem();
        if(selected != null) {
            tripDAO.deleteTrip(selected.getId());
            loadTripTable();
            clearTripForm();
        }
    }

    private void clearTripForm() {
        txtFrom.clear(); txtTo.clear(); txtTime.clear(); txtPrice.clear(); cbDriverForTrip.setValue(null);
    }

    private void setupUserPanel() {
        cbUserRole.getItems().addAll("OWNER", "DRIVER", "CUSTOMER");
        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> colName = new TableColumn<>("Họ tên");
        colName.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        TableColumn<User, String> colPhone = new TableColumn<>("SĐT");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    if ("OWNER".equals(item)) setStyle("-fx-text-fill: #8e44ad; -fx-font-weight: bold;"); // Tím
                    else if ("DRIVER".equals(item)) setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;"); // Xanh dương
                    else setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); // Cam
                }
            }
        });

        tableUser.getColumns().clear();
        tableUser.getColumns().addAll(colUser, colName, colPhone, colRole);
        tableUser.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableUser.setOnMouseClicked(e -> {
            User selected = tableUser.getSelectionModel().getSelectedItem();
            if(selected != null) {
                txtUsername.setText(selected.getUsername());
                txtFullName.setText(selected.getFull_name());
                txtPhone.setText(selected.getPhone());
                cbUserRole.setValue(selected.getRole());
            }
        });
    }

    @FXML
    private void loadUserTable() { tableUser.getItems().setAll(userDAO.getAllUser()); }

    @FXML
    void handleAddUser() {
        User u = new User();
        u.setUsername(txtUsername.getText());
        u.setPassword(txtPassword.getText());
        u.setFull_name(txtFullName.getText());
        u.setPhone(txtPhone.getText());
        u.setRole(cbUserRole.getValue());
        userDAO.register(u);
        loadUserTable();
        updateDriverComboBox();
    }

    @FXML
    void handleUpdateUser() {
        User selected = tableUser.getSelectionModel().getSelectedItem();
        if(selected != null) {
            selected.setUsername(txtUsername.getText());
            selected.setFull_name(txtFullName.getText());
            selected.setPhone(txtPhone.getText());
            selected.setRole(cbUserRole.getValue());
            if(!txtPassword.getText().isEmpty()) selected.setPassword(txtPassword.getText());
            userDAO.updateUser(selected);
            loadUserTable();
            updateDriverComboBox();
        }
    }

    @FXML
    void handleDeleteUser() {
        User selected = tableUser.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if("OWNER".equals(selected.getRole())) {
                showAlert("Lỗi", "Không thể xóa ADMIN!"); return;
            }
            userDAO.deleteUser(selected.getId());
            loadUserTable();
            updateDriverComboBox();
        }
    }

    private void setupChatPanel() {
        cbReceiverRole.getItems().addAll("DRIVER", "CUSTOMER");
        cbReceiverRole.setOnAction(e -> {
            cbReceiver.getItems().clear();
            for(User u : userDAO.getAllUser()) {
                if(u.getRole().equals(cbReceiverRole.getValue())) cbReceiver.getItems().add(u.getUsername());
            }
        });

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
        if(cbReceiver.getValue() == null || txtChatMessage.getText().isEmpty()) return;
        Message msg = new Message();
        msg.setSender(SessionManager.getCurrentUser().getUsername());
        msg.setSenderRole(SessionManager.getCurrentUser().getRole());
        msg.setReceiver(cbReceiver.getValue());
        msg.setReceiverRole(cbReceiverRole.getValue());
        msg.setContent(AESUtil.encrypt(txtChatMessage.getText()));
        msg.setTime(java.time.LocalDateTime.now().toString());
        messageDAO.sendMessage(msg);
        txtChatMessage.clear();
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

        }
        catch (Exception ex) {
            ex.printStackTrace();
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