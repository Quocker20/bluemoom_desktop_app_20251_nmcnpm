package com.bluemoon.app.controller.resident;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.resident.HouseholdDAO;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Resident;

/**
 * Controller for managing Households.
 */
public class HouseholdController {

    private final HouseholdDAO householdDAO;
    private final Logger logger;

    public HouseholdController() {
        this.householdDAO = new HouseholdDAO();
        this.logger = Logger.getLogger(HouseholdController.class.getName());
    }

    public List<Household> getAll() {
        try {
            return householdDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdController] Error getting all households", e);
            return new ArrayList<>();
        }
    }

    public List<Household> search(String keyword) {
        try {
            return householdDAO.search(keyword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdController] Error searching households", e);
            return new ArrayList<>();
        }
    }

    public boolean addHouseholdWithOwner(Household household, Resident owner) {
        try {
            return householdDAO.addHouseholdWithOwner(household, owner);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdController] Error adding household with owner", e);
            return false;
        }
    }

    public Household getByRoomNumber(String roomNumber) {
        try {
            return householdDAO.getByRoomNumber(roomNumber);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdController] Error getting household by room number", e);
            return null;
        }
    }
    
    public Household getById(int id) {
        try {
            return householdDAO.getById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdController] Error getting household by ID", e);
            return null;
        }
    }

    /**
     * Deletes a household by ID.
     * Note: This operation performs a soft delete in the database (sets is_deleted = 1).
     */
    public boolean delete(int id) {
        try {
            return householdDAO.delete(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdController] Error deleting household", e);
            return false;
        }
    }
}