package com.bluemoon.app.dao.resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Resident;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Households.
 * Handles operations for the 'households' table.
 */
public class HouseholdDAO {

    private static final Logger logger = Logger.getLogger(HouseholdDAO.class.getName());

    /**
     * Retrieves all active households.
     * Joins with 'apartments' table to get the area size.
     *
     * @return A list of {@link Household} objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Household> getAll() throws SQLException {
        List<Household> list = new ArrayList<>();
        // Join with apartments to get 'area'
        String sql = "SELECT h.*, a.area FROM households h " +
                     "JOIN apartments a ON h.room_number = a.room_number " +
                     "WHERE h.is_deleted = 0 " +
                     "ORDER BY h.room_number ASC";

        logger.info("[HouseholdDAO] Fetching all households...");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Household hk = new Household();
                hk.setId(rs.getInt("id"));
                hk.setRoomNumber(rs.getString("room_number"));
                hk.setOwnerName(rs.getString("owner_name"));
                hk.setArea(rs.getDouble("area")); // From apartments table
                hk.setPhoneNumber(rs.getString("phone_number"));
                hk.setCreatedAt(rs.getDate("created_at"));
                list.add(hk);
            }
            logger.log(Level.INFO, "[HouseholdDAO] Found {0} households", list.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in getAll", e);
            throw e;
        }
        return list;
    }

    /**
     * Creates a new household and its owner (resident) in a single transaction.
     *
     * @param household The household information.
     * @param owner     The resident information (owner).
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean addHouseholdWithOwner(Household household, Resident owner) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtHk = null;
        PreparedStatement pstmtNk = null;

        String sqlHousehold = "INSERT INTO households (room_number, owner_name, phone_number) VALUES (?, ?, ?)";
        String sqlResident = "INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (?, ?, ?, ?, ?, ?)";

        logger.info("[HouseholdDAO] Starting transaction to add household and owner");

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Insert Household
            pstmtHk = conn.prepareStatement(sqlHousehold, Statement.RETURN_GENERATED_KEYS);
            pstmtHk.setString(1, household.getRoomNumber());
            pstmtHk.setString(2, household.getOwnerName());
            pstmtHk.setString(3, household.getPhoneNumber());
            
            int affectedRows = pstmtHk.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating household failed, no rows affected.");
            }

            // Get generated ID
            int householdId = 0;
            try (ResultSet generatedKeys = pstmtHk.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    householdId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating household failed, no ID obtained.");
                }
            }

            // 2. Insert Owner (Resident)
            pstmtNk = conn.prepareStatement(sqlResident);
            pstmtNk.setInt(1, householdId);
            pstmtNk.setString(2, owner.getFullName());
            pstmtNk.setDate(3, new java.sql.Date(owner.getDob().getTime()));
            pstmtNk.setString(4, owner.getGender());
            pstmtNk.setString(5, owner.getIdentityCard());
            pstmtNk.setString(6, "Owner"); // Default relationship
            pstmtNk.executeUpdate();

            conn.commit(); // Commit Transaction
            logger.info("[HouseholdDAO] Transaction committed successfully");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warning("[HouseholdDAO] Transaction rolled back due to error");
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "[HouseholdDAO] Error during rollback", ex);
                }
            }
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in addHouseholdWithOwner", e);
            throw e;
        } finally {
            if (pstmtNk != null) pstmtNk.close();
            if (pstmtHk != null) pstmtHk.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Checks if a household exists for a given room number.
     *
     * @param roomNumber The room number to check.
     * @return {@code true} if exists and not deleted.
     * @throws SQLException If a database access error occurs.
     */
    public boolean exists(String roomNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM households WHERE room_number = ? AND is_deleted = 0";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in exists", e);
            throw e;
        }
        return false;
    }

    /**
     * Updates household information (Owner Name and Phone Number).
     * Room number is usually fixed for a household record.
     *
     * @param household The household object with updated info.
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean update(Household household) throws SQLException {
        String sql = "UPDATE households SET owner_name=?, phone_number=? WHERE id=?";
        logger.log(Level.INFO, "[HouseholdDAO] Updating household ID: {0}", household.getId());
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, household.getOwnerName());
            pstmt.setString(2, household.getPhoneNumber());
            pstmt.setInt(3, household.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in update", e);
            throw e;
        }
    }

    /**
     * Deletes a household and its residents.
     * <p>
     * <b>Note:</b> This is a <b>soft delete</b> operation. The record is not removed from the database,
     * but its 'is_deleted' flag is set to 1. Trigger in DB will handle Apartment status update.
     * </p>
     *
     * @param id The ID of the household.
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean delete(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            
            logger.log(Level.INFO, "[HouseholdDAO] Soft deleting household ID: {0}", id);

            String sqlDeleteHousehold = "UPDATE households SET is_deleted = 1 WHERE id = ?";
            String sqlDeleteResidents = "UPDATE residents SET is_deleted = 1 WHERE household_id = ?";

            try (PreparedStatement pst1 = conn.prepareStatement(sqlDeleteHousehold);
                 PreparedStatement pst2 = conn.prepareStatement(sqlDeleteResidents)) {
                
                pst1.setInt(1, id);
                int row1 = pst1.executeUpdate();

                pst2.setInt(1, id);
                pst2.executeUpdate();

                if (row1 > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in delete", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Searches for households by owner name or room number.
     *
     * @param keyword The keyword to search.
     * @return A list of matching households.
     * @throws SQLException If a database access error occurs.
     */
    public List<Household> search(String keyword) throws SQLException {
        List<Household> list = new ArrayList<>();
        String sql = "SELECT h.*, a.area FROM households h " +
                     "JOIN apartments a ON h.room_number = a.room_number " +
                     "WHERE h.is_deleted = 0 " +
                     "AND (h.owner_name LIKE ? OR h.room_number LIKE ?) " +
                     "ORDER BY h.room_number ASC";

        logger.log(Level.INFO, "[HouseholdDAO] Searching with keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Household hk = new Household();
                    hk.setId(rs.getInt("id"));
                    hk.setRoomNumber(rs.getString("room_number"));
                    hk.setOwnerName(rs.getString("owner_name"));
                    hk.setArea(rs.getDouble("area"));
                    hk.setPhoneNumber(rs.getString("phone_number"));
                    hk.setCreatedAt(rs.getDate("created_at"));
                    list.add(hk);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in search", e);
            throw e;
        }
        return list;
    }
    
    public Household getById(int id) throws SQLException {
        String sql = "SELECT h.*, a.area FROM households h JOIN apartments a ON h.room_number = a.room_number WHERE h.id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Household hk = new Household();
                    hk.setId(rs.getInt("id"));
                    hk.setRoomNumber(rs.getString("room_number"));
                    hk.setOwnerName(rs.getString("owner_name"));
                    hk.setArea(rs.getDouble("area"));
                    hk.setPhoneNumber(rs.getString("phone_number"));
                    return hk;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in getById", e);
            throw e;
        }
        return null;
    }
    
    public Household getByRoomNumber(String roomNumber) throws SQLException {
        String sql = "SELECT h.*, a.area FROM households h JOIN apartments a ON h.room_number = a.room_number WHERE h.room_number = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Household hk = new Household();
                    hk.setId(rs.getInt("id"));
                    hk.setRoomNumber(rs.getString("room_number"));
                    hk.setOwnerName(rs.getString("owner_name"));
                    hk.setArea(rs.getDouble("area"));
                    hk.setPhoneNumber(rs.getString("phone_number"));
                    return hk;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HouseholdDAO] Error in getByRoomNumber", e);
            throw e;
        }
        return null;
    }
}