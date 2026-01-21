package com.bluemoon.app.controller.vehicle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.vehicle.VehicleDAO;
import com.bluemoon.app.model.Vehicle;

/**
 * Controller for managing Vehicles (Parking).
 */
public class VehicleController {

    private final VehicleDAO vehicleDAO;
    private final Logger logger;

    public VehicleController() {
        this.vehicleDAO = new VehicleDAO();
        this.logger = Logger.getLogger(VehicleController.class.getName());
    }

    /**
     * Get all active vehicles.
     */
    public List<Vehicle> getAll() {
        try {
            return vehicleDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleController] Error in getAll", e);
            return new ArrayList<>();
        }
    }

    /**
     * Search vehicles by keyword (License Plate or Room Number).
     */
    public List<Vehicle> search(String keyword) {
        try {
            return vehicleDAO.search(keyword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleController] Error in search", e);
            return new ArrayList<>();
        }
    }

    /**
     * Add a new vehicle.
     * @return 
     * 1: Success
     * -1: Database/System Error
     * -2: License Plate already exists
     */
    public int add(Vehicle vehicle) {
        try {
            // Validate: Check duplicate license plate
            if (vehicleDAO.exists(vehicle.getLicensePlate())) {
                return -2; // Error Code: Duplicate
            }
            
            boolean success = vehicleDAO.insert(vehicle);
            return success ? 1 : -1;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleController] Error in add", e);
            return -1; // System Error
        }
    }

    /**
     * Delete a vehicle (Soft Delete).
     * Recommended: Delete by ID.
     */
    public boolean delete(int id) {
        try {
            return vehicleDAO.delete(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleController] Error in delete", e);
            return false;
        }
    }

    /**
     * Check if license plate exists (For UI Validation).
     */
    public boolean exists(String licensePlate) {
        try {
            return vehicleDAO.exists(licensePlate);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleController] Error in exists", e);
            return false;
        }
    }

    /**
     * Update vehicle information.
     */
    public boolean update(Vehicle vehicle) {
        try {
            return vehicleDAO.update(vehicle);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleController] Error in update", e);
            return false;
        }
    }
}