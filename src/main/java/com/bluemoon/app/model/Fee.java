package com.bluemoon.app.model;

public class Fee {
    private int id;
    private String name;
    private double unitPrice;
    private String unit;
    private int type; // 0: Mandatory, 1: Voluntary
    private int status; // 1: Active, 0: Inactive

    public Fee() {
    }

    public Fee(String name, double unitPrice, String unit, int type) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.type = type;
    }

    // Getters and Setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    @Override
    public String toString() {
        return name;
    }


}