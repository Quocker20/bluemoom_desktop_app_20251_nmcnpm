package com.bluemoon.app.dao.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Fee;
import com.bluemoon.app.util.DatabaseConnector;

public class FeeDAO {

    private static final Logger logger = Logger.getLogger(FeeDAO.class.getName());

    /**
     * Retrieves all active fees from the database.
     *
     * @return A list of all {@link Fee} objects that are currently active.
     * @throws SQLException If a database access error occurs.
     */
    public List<Fee> getAll() throws SQLException {
        return getAllActiveFees("");
    }

    /**
     * Retrieves a list of active fees matching a specific keyword.
     * Searches by fee name.
     *
     * @param keyword The keyword to search for in the fee name.
     * @return A list of {@link Fee} objects matching the criteria.
     * @throws SQLException If a database access error occurs.
     */
    public List<Fee> getAllActiveFees(String keyword) throws SQLException {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT * FROM fee_types " +
                "WHERE name LIKE ? " +
                "AND status = 1 " +
                "ORDER BY id DESC";
        logger.log(Level.INFO, "[FeeDAO] Fetching active fees with keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Fee fee = new Fee();
                    fee.setId(rs.getInt("id"));
                    fee.setName(rs.getString("name"));
                    fee.setUnitPrice(rs.getDouble("unit_price"));
                    fee.setUnit(rs.getString("unit"));
                    fee.setType(rs.getInt("type"));
                    fee.setStatus(rs.getInt("status"));

                    list.add(fee);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[FeeDAO] Error in getAllActiveFees", e);
            throw e;
        }
        return list;
    }

    /**
     * Inserts a new fee configuration into the database.
     *
     * @param fee The {@link Fee} object containing the data to be inserted.
     * @return {@code true} if the insertion was successful, {@code false}
     *         otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insert(Fee fee) throws SQLException {
        String sql = "INSERT INTO fee_types (name, unit_price, unit, type, status) VALUES (?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[FeeDAO] Inserting new fee: {0}", fee.getName());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fee.getName());
            pstmt.setDouble(2, fee.getUnitPrice());
            pstmt.setString(3, fee.getUnit());
            pstmt.setInt(4, fee.getType());
            pstmt.setInt(5, 1); // Default status = 1 (Active)

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[FeeDAO] Error in insert", e);
            throw e;
        }
    }

    /**
     * Updates an existing fee configuration.
     *
     * @param fee The {@link Fee} object containing the updated data.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean update(Fee fee) throws SQLException {
        String sql = "UPDATE fee_types SET name=?, unit_price=?, unit=?, type=? WHERE id=?";
        logger.log(Level.INFO, "[FeeDAO] Updating fee ID: {0}", fee.getId());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fee.getName());
            pstmt.setDouble(2, fee.getUnitPrice());
            pstmt.setString(3, fee.getUnit());
            pstmt.setInt(4, fee.getType());
            pstmt.setInt(5, fee.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[FeeDAO] Error in update", e);
            throw e;
        }
    }

    /**
     * Soft deletes a fee by setting its status to inactive (0).
     *
     * @param id The ID of the fee to be deleted.
     * @return {@code true} if the operation was successful, {@code false}
     *         otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE fee_types SET status = 0 WHERE id = ?";
        logger.log(Level.INFO, "[FeeDAO] Disabling fee ID: {0}", id);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[FeeDAO] Error in delete", e);
            throw e;
        }
    }
}