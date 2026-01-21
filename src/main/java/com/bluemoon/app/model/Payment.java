package com.bluemoon.app.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Payment {
    private int id;
    private int householdId;
    private int feeTypeId;

    private Timestamp paymentDate;

    private double amount;
    private String payerName;
    private String note;

    // DTO Fields
    private String feeName;
    private String roomNumber;

    public Payment() {
    }

    // Constructor đầy đủ
    public Payment(int maGiaoDich, int maHo, int maKhoanPhi, Timestamp paymentDate, double amount, String payerName,
            String note) {
        this.id = maGiaoDich;
        this.householdId = maHo;
        this.feeTypeId = maKhoanPhi;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.payerName = payerName;
        this.note = note;
    }

    // Constructor ngắn (dùng khi tạo giao dịch mới)
    public Payment(int householdId, int feeId, double amount, String payerName, String note) {
        this.householdId = householdId;
        this.feeTypeId = feeId;
        this.amount = amount;
        this.payerName = payerName;
        this.note = note;
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }

    // --- Getter & Setter ---

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

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

    public int getFeeTypeId() {
        return feeTypeId;
    }

    public void setFeeTypeId(int feeId) {
        this.feeTypeId = feeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * Formats the payment date for display.
     * Example output: "20/10/2025 14:30:00"
     */
    public String getDisplayPaymentDate() {
        if (paymentDate == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(paymentDate);
    }
}