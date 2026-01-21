package com.bluemoon.app.model;

public class Invoice {
    private int id;
    private int householdId;
    private int feeTypeId;
    private double amountDue;
    private double amountPaid;
    private int month;
    private int year;
    private int status; // 0: Unpaid, 1: Paid, 2: Partial

    // DTO Fields
    private String feeName;
    private String roomNumber;

    public Invoice() {
    }

    // Constructor đầy đủ (Cập nhật thêm roomNumber)
    public Invoice(int id, int householdId, String roomNumber, int feeTypeId, String feeName, int month, int year,
            double amountDue, double amountPaid, int status) {
        this.id = id;
        this.householdId = householdId;
        this.roomNumber = roomNumber;
        this.feeTypeId = feeTypeId;
        this.feeName = feeName;
        this.month = month;
        this.year = year;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.status = status;
    }

    public double getRemainingAmount() {
        return Math.max(0, amountDue - amountPaid);
    }

    // Getters & Setters
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFeeTypeId() {
        return feeTypeId;
    }

    public void setFeeTypeId(int feeTypeId) {
        this.feeTypeId = feeTypeId;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String tenKhoanPhi) {
        this.feeName = tenKhoanPhi;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int thang) {
        this.month = thang;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}