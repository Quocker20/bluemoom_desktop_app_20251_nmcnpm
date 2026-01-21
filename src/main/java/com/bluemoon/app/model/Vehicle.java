package com.bluemoon.app.model;

public class Vehicle {
    private int id;
    private int householdId;
    private String licensePlate;
    private int type; // 1: car, 2: motorbike/bicycle
    private int status; // 1: active, 0: inactive

    // DTO Fields
    private String roomNumber;
    private String ownerName;


    public Vehicle() {
    }

    public Vehicle(int householdId, String licensePlate, int type, int status) {
        this.householdId = householdId;
        this.licensePlate = licensePlate;
        this.type = type;
        this.status = status;
    }


    public Vehicle(int id, int householdId, String licensePlate, int type, int status, String roomNumber,
            String ownerName) {
        this.id = id;
        this.householdId = householdId;
        this.licensePlate = licensePlate;
        this.type = type;
        this.status = status;
        this.roomNumber = roomNumber;
        this.ownerName = ownerName;
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getVehicleTypeDisplay() {
        return (this.type == 1) ? "Ô tô" : "Xe máy/Xe đạp";
    }
}