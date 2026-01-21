package com.bluemoon.app.model;

import java.util.Date;

public class Resident {
    private int id;
    private int householdId;
    private String fullName;
    private Date dob;
    private String gender;
    private String identityCard;
    private String relationship;
    private int isDeleted;

    public Resident() {
    }

    public Resident(int householdId, String fullName, Date dob, String gender, String identityCard,
            String relationship) {
        this.householdId = householdId;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.identityCard = identityCard;
        this.relationship = relationship;
    }

    //Getters and Setters...

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}