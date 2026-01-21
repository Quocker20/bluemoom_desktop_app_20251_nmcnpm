package com.bluemoon.app.model;

import java.util.Date;

public class ResidencyRecord {
    private int id;
    private int residentId;
    private String type; // 'Temporary', 'Absence'
    private Date startDate;
    private Date endDate;
    private String reason;

    // DTO Field
    private String residentName;

    public ResidencyRecord() {
    }

    public ResidencyRecord(int residentId, String type, Date startDate, Date endDate, String reason) {
        this.residentId = residentId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String hoTenNhanKhau) {
        this.residentName = hoTenNhanKhau;
    }
}