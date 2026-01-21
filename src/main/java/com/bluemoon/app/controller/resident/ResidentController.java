package com.bluemoon.app.controller.resident;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.resident.ResidentDAO;
import com.bluemoon.app.model.Resident;

/**
 * Controller for managing Residents.
 */
public class ResidentController {

    private final ResidentDAO residentDAO;
    private final Logger logger;

    public ResidentController() {
        this.residentDAO = new ResidentDAO();
        this.logger = Logger.getLogger(ResidentController.class.getName());
    }

    /**
     * Get list of residents by household ID.
     */
    public List<Resident> getByHouseholdId(int householdId) {
        try {
            return residentDAO.selectByHouseholdId(householdId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentController] Error in getByHouseholdId", e);
            return Collections.emptyList();
        }
    }

    /**
     * Add a new resident.
     */
    public boolean add(Resident resident) {
        if (!validate(resident)) {
            return false;
        }

        try {
            // Check for duplicate Identity Card (CCCD)
            if (resident.getIdentityCard() != null && !resident.getIdentityCard().trim().isEmpty()) {
                if (residentDAO.checkIdentityCardExist(resident.getIdentityCard())) {
                    logger.warning("Error: Identity Card " + resident.getIdentityCard() + " already exists!");
                    return false;
                }
            }
            return residentDAO.insert(resident);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentController] Error in add", e);
            return false;
        }
    }

    /**
     * Update resident information.
     */
    public boolean update(Resident resident) {
        if (!validate(resident) || resident.getId() <= 0) {
            return false;
        }

        try {
            // Check for duplicate Identity Card, excluding the current resident ID
            if (resident.getIdentityCard() != null && !resident.getIdentityCard().trim().isEmpty()) {
                boolean isDuplicate = residentDAO.checkIdentityCardExist(resident.getIdentityCard(), resident.getId());
                if (isDuplicate) {
                    logger.warning("Error: Identity Card " + resident.getIdentityCard() + " belongs to another resident!");
                    return false;
                }
            }
            return residentDAO.update(resident);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentController] Error in update", e);
            return false;
        }
    }

    /**
     * Soft delete a resident.
     */
    public boolean delete(int residentId) {
        if (residentId <= 0) return false;
        try {
            return residentDAO.delete(residentId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentController] Error in delete", e);
            return false;
        }
    }

    /**
     * Validate resident data before adding/updating.
     */
    private boolean validate(Resident resident) {
        if (resident == null) return false;
        if (resident.getFullName() == null || resident.getFullName().trim().isEmpty()) return false;
        if (resident.getDob() == null) return false;
        if (resident.getRelationship() == null || resident.getRelationship().trim().isEmpty()) return false;
        return true;
    }

    /**
     * Get all residents.
     */
    public List<Resident> getAll() {
        List<Resident> list = Collections.emptyList();
        try {
            logger.info("[ResidentController] Fetching all residents");
            list = residentDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "[ResidentController] Failed to fetch all residents", e);
        }
        return list;
    }

    /**
     * Search residents by keyword.
     */
    public List<Resident> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        
        try {
            return residentDAO.search(keyword.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentController] Error in search", e);
            return Collections.emptyList();
        }
    }
}