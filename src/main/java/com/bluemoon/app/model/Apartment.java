package com.bluemoon.app.model;

public class Apartment {
    private int id;
    private String roomNumber;
    private double area;
    private int status; // 0: Vacant, 1: Occupied

    public Apartment() {
    }

    public Apartment(int id, String roomNumber, double area, int status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.area = area;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return roomNumber;
    }
}