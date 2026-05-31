package org.example.cuoikijava.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")

public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String bus_license_plate;
    private String seat_name;
    private String seat_status;

    public Seat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBus_license_plate() {
        return bus_license_plate;
    }

    public void setBus_license_plate(String bus_license_plate) {
        this.bus_license_plate = bus_license_plate;
    }

    public String getSeat_name() {
        return seat_name;
    }

    public void setSeat_name(String seat_name) {
        this.seat_name = seat_name;
    }

    public String getSeat_status() {
        return seat_status;
    }

    public void setSeat_status(String seat_status) {
        this.seat_status = seat_status;
    }
}