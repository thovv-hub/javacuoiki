package org.example.cuoikijava.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fromLocation;
    private String toLocation;
    private String departureTime;
    private int booked_seats;
    private double ticket_price;
    private String trip_status;
    private String bus_license_plate;

    @Column(name = "driver_name")
    private String driverName;

    public Trip() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getBooked_seats() {
        return booked_seats;
    }

    public void setBooked_seats(int booked_seats) {
        this.booked_seats = booked_seats;
    }

    public double getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(double ticket_price) {
        this.ticket_price = ticket_price;
    }

    public String getTrip_status() {
        return trip_status;
    }

    public void setTrip_status(String trip_status) {
        this.trip_status = trip_status;
    }

    public String getBus_license_plate() {
        return bus_license_plate;
    }

    public void setBus_license_plate(String bus_license_plate) {
        this.bus_license_plate = bus_license_plate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}