package com.bluemoon.app.dao.vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Vehicle;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Vehicles.
 * Handles CRUD operations for the 'vehicles' table.
 */
public class VehicleDAO {

    private static final Logger logger = Logger.getLogger(VehicleDAO.class.getName());

    /**
     * Retrieves all active vehicles (status = 1).
     * Joins with 'households' to get room number and owner name.
     *
     * @return A list of {@link Vehicle} objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Vehicle> getAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        // Correct column names: vehicle_type, license_plate
        String sql = "SELECT v.*, h.room_number, h.owner_name FROM vehicles v " +
                     "JOIN households h ON v.household_id = h.id " +
                     "WHERE v.status = 1 " +
                     "ORDER BY h.room_number ASC";

        logger.log(Level.INFO, "[VehicleDAO] Fetching all active vehicles");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setHouseholdId(rs.getInt("household_id"));
                v.setLicensePlate(rs.getString("license_plate"));
                v.setType(rs.getInt("vehicle_type")); // DB: vehicle_type
                v.setStatus(rs.getInt("status"));
                
                // DTO fields (Display info)
                v.setRoomNumber(rs.getString("room_number"));
                v.setOwnerName(rs.getString("owner_name"));
                
                list.add(v);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in getAll", e);
            throw e;
        }
        return list;
    }

    /**
     * Inserts a new vehicle into the database.
     *
     * @param vehicle The {@link Vehicle} object to insert.
     * @return {@code true} if insertion was successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insert(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (household_id, license_plate, vehicle_type, status) VALUES (?, ?, ?, ?)";
        logger.log(Level.INFO, "[VehicleDAO] Inserting vehicle: {0}", vehicle.getLicensePlate());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vehicle.getHouseholdId());
            pstmt.setString(2, vehicle.getLicensePlate());
            pstmt.setInt(3, vehicle.getType());
            pstmt.setInt(4, 1); // Default status: 1 (Active)
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in insert", e);
            throw e;
        }
    }

    /**
     * Updates an existing vehicle's information.
     *
     * @param vehicle The {@link Vehicle} object with updated data.
     * @return {@code true} if update was successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET license_plate = ?, vehicle_type = ? WHERE id = ?";
        logger.log(Level.INFO, "[VehicleDAO] Updating vehicle ID: {0}", vehicle.getId());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setInt(2, vehicle.getType());
            pstmt.setInt(3, vehicle.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in update", e);
            throw e;
        }
    }

    /**
     * Soft deletes a vehicle by setting status to 0 (Inactive) using ID.
     * This is the standard method.
     *
     * @param id The ID of the vehicle to delete.
     * @return {@code true} if deletion was successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE vehicles SET status = 0 WHERE id = ?";
        logger.log(Level.INFO, "[VehicleDAO] Deleting (soft) vehicle ID: {0}", id);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in delete", e);
            throw e;
        }
    }

    /**
     * Soft deletes a vehicle by License Plate (Legacy support/Alternative).
     *
     * @param licensePlate The license plate to delete.
     * @return {@code true} if deletion was successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteByLicensePlate(String licensePlate) throws SQLException {
        String sql = "UPDATE vehicles SET status = 0 WHERE license_plate = ?";
        logger.log(Level.INFO, "[VehicleDAO] Deleting (soft) vehicle Plate: {0}", licensePlate);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in deleteByLicensePlate", e);
            throw e;
        }
    }

    /**
     * Checks if a license plate already exists among active vehicles.
     *
     * @param licensePlate The license plate to check.
     * @return {@code true} if the license plate exists.
     * @throws SQLException If a database access error occurs.
     */
    public boolean exists(String licensePlate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE license_plate = ? AND status = 1";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licensePlate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in exists", e);
            throw e;
        }
        return false;
    }

    /**
     * Search vehicles by License Plate OR Room Number.
     *
     * @param keyword Keyword to search.
     * @return List of matching vehicles.
     * @throws SQLException If a database access error occurs.
     */
    public List<Vehicle> search(String keyword) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.*, h.room_number, h.owner_name FROM vehicles v " +
                     "JOIN households h ON v.household_id = h.id " +
                     "WHERE v.status = 1 " +
                     "AND (v.license_plate LIKE ? OR h.room_number LIKE ?) " +
                     "ORDER BY h.room_number ASC";

        logger.log(Level.INFO, "[VehicleDAO] Searching vehicles with keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = new Vehicle();
                    v.setId(rs.getInt("id"));
                    v.setHouseholdId(rs.getInt("household_id"));
                    v.setLicensePlate(rs.getString("license_plate"));
                    v.setType(rs.getInt("vehicle_type"));
                    v.setStatus(rs.getInt("status"));
                    
                    v.setRoomNumber(rs.getString("room_number"));
                    v.setOwnerName(rs.getString("owner_name"));
                    
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[VehicleDAO] Error in search", e);
            throw e;
        }
        return list;
    }
}