package org.example.cuoikijava.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.cuoikijava.dao.BusDAO;
import org.example.cuoikijava.model.Bus;
import java.util.List;

public class OwnerController {
    @FXML
    private TextField txtPlate;
    @FXML
    private TextField txtSeats;
    @FXML
    private TextField txtType;
    @FXML
    private ListView<String> listBus;
    @FXML
    public void handleAddBus() {

        String plate = txtPlate.getText();
        int seats = Integer.parseInt(txtSeats.getText());
        String type = txtType.getText();
        Bus bus = new Bus();
        bus.setLicensePlate(plate);
        bus.setTotalSeats(seats);
        bus.setBusType(type);
        BusDAO busDAO = new BusDAO();
        busDAO.addBus(bus);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bus");
        alert.setHeaderText(null);
        alert.setContentText("Add bus success!");
        alert.showAndWait();
    }

    @FXML
    public void handleLoadBus() {
        BusDAO busDAO = new BusDAO();
        List<Bus> buses = busDAO.getAllBus();
        ObservableList<String> items = FXCollections.observableArrayList();
        for(Bus bus : buses) {
            items.add("Plate: " + bus.getLicensePlate() + " | Seats: " + bus.getTotalSeats() + " | Type: " + bus.getBusType());
        }
        listBus.setItems(items);
    }
}