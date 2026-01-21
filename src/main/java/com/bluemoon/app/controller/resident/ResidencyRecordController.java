package com.bluemoon.app.controller.resident;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.resident.ResidencyRecordDAO;
import com.bluemoon.app.model.ResidencyRecord;

/**
 * Controller for managing Temporary Residency and Absence records.
 */
public class ResidencyRecordController {

    private final ResidencyRecordDAO residencyRecordDAO;
    private final Logger logger;

    public ResidencyRecordController() {
        this.residencyRecordDAO = new ResidencyRecordDAO();
        this.logger = Logger.getLogger(ResidencyRecordController.class.getName());
    }

    /**
     * Retrieves all residency records.
     */
    public List<ResidencyRecord> getAll() {
        try {
            return residencyRecordDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordController] Error getting all records", e);
            return Collections.emptyList();
        }
    }

    /**
     * Adds a new residency record.
     */
    public boolean add(ResidencyRecord record) {
        // 1. Validate Null/Empty
        if (record.getResidentId() <= 0 ||
                record.getType() == null || record.getType().trim().isEmpty() ||
                record.getStartDate() == null ||
                record.getReason() == null || record.getReason().trim().isEmpty()) {
            return false;
        }

        // 2. Validate Logic Date
        if (record.getEndDate() != null && record.getEndDate().before(record.getStartDate())) {
            logger.warning("Error: End date cannot be before start date!");
            return false;
        }

        try {
            return residencyRecordDAO.insert(record);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordController] Error adding record", e);
            return false;
        }
    }

    public List<ResidencyRecord> getByType(String type) {
        try {
            return residencyRecordDAO.getByType(type);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordController] Error getting records by type", e);
            return Collections.emptyList();
        }
    }

    public List<ResidencyRecord> getByResidentName(String name) {
        try {
            return residencyRecordDAO.getByResidentName(name);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordController] Error searching records by name", e);
            return Collections.emptyList();
        }
    }

    /**
     * Deletes records that have expired as of today.
     */
    public boolean deleteExpired() {
        LocalDate today = LocalDate.now();
        java.sql.Date sqlToday = java.sql.Date.valueOf(today);

        try {
            return residencyRecordDAO.deleteByExpirationDate(sqlToday);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordController] Error deleting expired records", e);
            return false;
        }
    }
}