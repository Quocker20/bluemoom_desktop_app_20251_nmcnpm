package com.bluemoon.app.model;

import java.util.Date;

public class Household {
    private int id;
    private String roomNumber;
    private String ownerName;
    private double area;
    private String phoneNumber;
    private Date createdAt;
    private int isDeleted;

    public Household() {
    }


    public Household(String roomNumber, String ownerName, String phoneNumber) {
        this.roomNumber = roomNumber;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
    }



    public Household(int maHo, String roomNumber, String ownerName, double area, String phoneNumber, Date createdAt, int isDeleted) {
        this.id = maHo;
        this.roomNumber = roomNumber;
        this.ownerName = ownerName;
        this.area = area;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    //Getters and Setters...
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date date) {
        this.createdAt = date;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}