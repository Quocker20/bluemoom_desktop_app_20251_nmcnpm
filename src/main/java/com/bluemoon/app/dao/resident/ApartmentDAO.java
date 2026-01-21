package com.bluemoon.app.dao.resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Apartment;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Apartments.
 * Handles read-only operations for 'apartments' table mostly.
 */
public class ApartmentDAO {

    private static final Logger logger = Logger.getLogger(ApartmentDAO.class.getName());

    /**
     * Retrieves a list of vacant apartments (status = 0).
     * Used for populating ComboBoxes when creating new households.
     *
     * @return A list of vacant {@link Apartment} objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Apartment> getVacantApartments() throws SQLException {
        List<Apartment> list = new ArrayList<>();
        String sql = "SELECT * FROM apartments WHERE status = 0 ORDER BY room_number ASC";
        
        logger.info("[ApartmentDAO] Fetching vacant apartments...");
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Apartment apt = new Apartment();
                apt.setId(rs.getInt("id"));
                apt.setRoomNumber(rs.getString("room_number"));
                apt.setArea(rs.getDouble("area"));
                apt.setStatus(rs.getInt("status"));
                list.add(apt);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ApartmentDAO] Error in getVacantApartments", e);
            throw e;
        }
        return list;
    }
    
    /**
     * Retrieves apartment information by room number.
     *
     * @param roomNumber The room number to search for.
     * @return An {@link Apartment} object if found, otherwise {@code null}.
     * @throws SQLException If a database access error occurs.
     */
    public Apartment getByRoomNumber(String roomNumber) throws SQLException {
        String sql = "SELECT * FROM apartments WHERE room_number = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, roomNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Apartment(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getDouble("area"),
                        rs.getInt("status")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ApartmentDAO] Error in getByRoomNumber", e);
            throw e;
        }
        return null;
    }
}