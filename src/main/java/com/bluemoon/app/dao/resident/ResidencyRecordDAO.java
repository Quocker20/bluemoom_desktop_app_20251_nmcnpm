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

import com.bluemoon.app.model.ResidencyRecord;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Residency Records (Temporary Residence/Absence).
 * Handles operations for the 'residency_records' table.
 */
public class ResidencyRecordDAO {

    private static final Logger logger = Logger.getLogger(ResidencyRecordDAO.class.getName());

    /**
     * Inserts a new residency record.
     *
     * @param record The record to insert.
     * @return {@code true} if successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insert(ResidencyRecord record) throws SQLException {
        String sql = "INSERT INTO residency_records (resident_id, type, start_date, end_date, reason) VALUES (?, ?, ?, ?, ?)";
        logger.info("[ResidencyRecordDAO] Inserting new record");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getResidentId());
            pstmt.setString(2, record.getType());
            pstmt.setDate(3, new java.sql.Date(record.getStartDate().getTime()));

            if (record.getEndDate() != null) {
                pstmt.setDate(4, new java.sql.Date(record.getEndDate().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setString(5, record.getReason());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordDAO] Error in insert", e);
            throw e;
        }
    }

    /**
     * Retrieves all residency records.
     * Joins with 'residents' to get the full name.
     *
     * @return A list of records.
     * @throws SQLException If a database access error occurs.
     */
    public List<ResidencyRecord> getAll() throws SQLException {
        List<ResidencyRecord> list = new ArrayList<>();
        String sql = "SELECT r.*, res.full_name FROM residency_records r " +
                     "JOIN residents res ON r.resident_id = res.id " +
                     "ORDER BY r.id ASC";
        logger.info("[ResidencyRecordDAO] Fetching all records");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordDAO] Error in getAll", e);
            throw e;
        }
        return list;
    }

    /**
     * Retrieves records by type (e.g., 'Temporary', 'Absence').
     *
     * @param type The type to filter by.
     * @return A list of records.
     * @throws SQLException If a database access error occurs.
     */
    public List<ResidencyRecord> getByType(String type) throws SQLException {
        List<ResidencyRecord> list = new ArrayList<>();
        String sql = "SELECT r.*, res.full_name FROM residency_records r " +
                     "JOIN residents res ON r.resident_id = res.id " +
                     "WHERE r.type = ? ORDER BY r.id ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordDAO] Error in getByType", e);
            throw e;
        }
        return list;
    }

    /**
     * Searches records by resident name.
     *
     * @param residentName The name keyword.
     * @return A list of records.
     * @throws SQLException If a database access error occurs.
     */
    public List<ResidencyRecord> getByResidentName(String residentName) throws SQLException {
        List<ResidencyRecord> list = new ArrayList<>();
        String sql = "SELECT r.*, res.full_name FROM residency_records r " +
                     "JOIN residents res ON r.resident_id = res.id " +
                     "WHERE res.full_name LIKE ? ORDER BY r.id ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + residentName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordDAO] Error in getByResidentName", e);
            throw e;
        }
        return list;
    }

    // Helper method to map ResultSet to Object
    private ResidencyRecord mapRow(ResultSet rs) throws SQLException {
        ResidencyRecord r = new ResidencyRecord();
        r.setId(rs.getInt("id"));
        r.setResidentId(rs.getInt("resident_id"));
        r.setType(rs.getString("type"));
        r.setStartDate(rs.getDate("start_date"));
        r.setEndDate(rs.getDate("end_date"));
        r.setReason(rs.getString("reason"));
        r.setResidentName(rs.getString("full_name"));
        return r;
    }

    /**
     * Deletes records that expired before a specific date.
     *
     * @param date The expiration date threshold.
     * @return {@code true} if any rows were deleted.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteByExpirationDate(java.sql.Date date) throws SQLException {
        String sql = "DELETE FROM residency_records " +
                     "WHERE end_date IS NOT NULL " +
                     "AND end_date < ?";
        logger.info("[ResidencyRecordDAO] Cleaning up expired data");
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            int rowAffected = pstmt.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ResidencyRecordDAO] Error in deleteByExpirationDate", e);
            throw e;
        }
    }
}