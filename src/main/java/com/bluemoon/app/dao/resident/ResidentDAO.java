package com.bluemoon.app.dao.resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Resident;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Residents.
 * Handles CRUD operations for the 'residents' table.
 */
public class ResidentDAO {

    private static final Logger logger = Logger.getLogger(ResidentDAO.class.getName());

    /**
     * Retrieves all residents belonging to a specific household.
     *
     * @param householdId The ID of the household.
     * @return A list of {@link Resident} objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Resident> selectByHouseholdId(int householdId) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE household_id = ? AND is_deleted = 0 ORDER BY relationship ASC";

        logger.log(Level.INFO, "[ResidentDAO] Fetching residents for household ID: {0}", householdId);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, householdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resident r = new Resident();
                    r.setId(rs.getInt("id"));
                    r.setHouseholdId(rs.getInt("household_id"));
                    r.setFullName(rs.getString("full_name"));
                    r.setDob(rs.getDate("dob"));
                    r.setGender(rs.getString("gender"));
                    r.setIdentityCard(rs.getString("identity_card"));
                    r.setRelationship(rs.getString("relationship"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in selectByHouseholdId", e);
            throw e;
        }
        return list;
    }

    /**
     * Inserts a new resident into the database.
     *
     * @param resident The resident object.
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insert(Resident resident) throws SQLException {
        String sql = "INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (?, ?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[ResidentDAO] Inserting resident: {0}", resident.getFullName());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, resident.getHouseholdId());
            pstmt.setString(2, resident.getFullName());
            pstmt.setDate(3, new java.sql.Date(resident.getDob().getTime()));
            pstmt.setString(4, resident.getGender());

            if (resident.getIdentityCard() == null || resident.getIdentityCard().trim().isEmpty()) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, resident.getIdentityCard());
            }
            pstmt.setString(6, resident.getRelationship());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in insert", e);
            throw e;
        }
    }

    /**
     * Updates resident information.
     *
     * @param resident The resident object with updated data.
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean update(Resident resident) throws SQLException {
        String sql = "UPDATE residents SET full_name=?, dob=?, gender=?, identity_card=?, relationship=? WHERE id=?";
        logger.log(Level.INFO, "[ResidentDAO] Updating resident ID: {0}", resident.getId());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resident.getFullName());
            pstmt.setDate(2, new java.sql.Date(resident.getDob().getTime()));
            pstmt.setString(3, resident.getGender());

            if (resident.getIdentityCard() == null || resident.getIdentityCard().trim().isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, resident.getIdentityCard());
            }

            pstmt.setString(5, resident.getRelationship());
            pstmt.setInt(6, resident.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in update", e);
            throw e;
        }
    }

    /**
     * Soft deletes a resident.
     *
     * @param id The ID of the resident.
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE residents SET is_deleted = 1 WHERE id = ?";
        logger.log(Level.INFO, "[ResidentDAO] Soft deleting resident ID: {0}", id);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in delete", e);
            throw e;
        }
    }

    /**
     * Checks if an Identity Card (CCCD) already exists.
     * Used when creating a new resident.
     *
     * @param identityCard The identity card number.
     * @return {@code true} if exists.
     * @throws SQLException If a database access error occurs.
     */
    public boolean checkIdentityCardExist(String identityCard) throws SQLException {
        if (identityCard == null || identityCard.trim().isEmpty())
            return false;
        String sql = "SELECT COUNT(*) FROM residents WHERE identity_card = ? AND is_deleted = 0";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, identityCard);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in checkIdentityCardExist", e);
            throw e;
        }
        return false;
    }

    /**
     * Checks if an Identity Card exists for another resident (excluding the current one).
     * Used when updating a resident's info.
     *
     * @param identityCard The identity card number.
     * @param excludeId    The ID of the resident being updated (to ignore).
     * @return {@code true} if the identity card is used by another resident.
     * @throws SQLException If a database access error occurs.
     */
    public boolean checkIdentityCardExist(String identityCard, int excludeId) throws SQLException {
        if (identityCard == null || identityCard.trim().isEmpty())
            return false;
        String sql = "SELECT COUNT(*) FROM residents WHERE identity_card = ? AND id != ? AND is_deleted = 0";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, identityCard);
            pstmt.setInt(2, excludeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in checkIdentityCardExist (for update)", e);
            throw e;
        }
        return false;
    }

    /**
     * Searches for residents by name or identity card.
     *
     * @param keyword The keyword to search.
     * @return A list of matching residents.
     * @throws SQLException If a database access error occurs.
     */
    public List<Resident> search(String keyword) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE (full_name LIKE ? OR identity_card LIKE ?) AND is_deleted = 0 ORDER BY id ASC";
        logger.log(Level.INFO, "[ResidentDAO] Searching with keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resident r = new Resident();
                    r.setId(rs.getInt("id"));
                    r.setHouseholdId(rs.getInt("household_id"));
                    r.setFullName(rs.getString("full_name"));
                    r.setDob(rs.getDate("dob"));
                    r.setGender(rs.getString("gender"));
                    r.setIdentityCard(rs.getString("identity_card"));
                    r.setRelationship(rs.getString("relationship"));
                    
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in search", e);
            throw e;
        }
        return list;
    }

    /**
     * Retrieves all residents.
     *
     * @return A list of all residents.
     * @throws SQLException If a database access error occurs.
     */
    public List<Resident> getAll() throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE is_deleted = 0 ORDER BY id ASC";

        logger.log(Level.INFO, "[ResidentDAO] Fetching all residents");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Resident r = new Resident();
                r.setId(rs.getInt("id"));
                r.setHouseholdId(rs.getInt("household_id"));
                r.setFullName(rs.getString("full_name"));
                r.setDob(rs.getDate("dob"));
                r.setGender(rs.getString("gender"));
                r.setIdentityCard(rs.getString("identity_card"));
                r.setRelationship(rs.getString("relationship"));
                list.add(r);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidentDAO] Error in getAll", e);
            throw e;
        }
        return list;
    }
}