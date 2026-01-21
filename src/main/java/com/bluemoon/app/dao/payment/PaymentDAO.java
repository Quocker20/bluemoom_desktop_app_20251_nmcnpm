package com.bluemoon.app.dao.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Payment;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Payment Transactions.
 * Handles operations related to the 'payments' table history.
 */
public class PaymentDAO {

    private static final Logger logger = Logger.getLogger(PaymentDAO.class.getName());

    /**
     * Inserts a new payment transaction record into the database.
     * Records who paid, when, how much, and for which fee.
     *
     * @param payment The {@link Payment} object containing transaction details.
     * @return {@code true} if insertion was successful, {@code false} otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insert(Payment payment) throws SQLException {
        // Updated table: payments, columns: household_id, fee_type_id, ...
        String sql = "INSERT INTO payments (household_id, fee_type_id, amount, payer_name, note, payment_date) VALUES (?, ?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[PaymentDAO] Inserting new payment record for Household ID: {0}", payment.getHouseholdId());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, payment.getHouseholdId());
            pstmt.setInt(2, payment.getFeeTypeId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setString(4, payment.getPayerName());
            pstmt.setString(5, payment.getNote());
            pstmt.setTimestamp(6, payment.getPaymentDate());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.INFO, "[PaymentDAO] Insert successful");
                return true;
            } else {
                logger.log(Level.WARNING, "[PaymentDAO] Insert failed, no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[PaymentDAO] Error in insert", e);
            throw e; 
        }
    }

    /**
     * Retrieves the entire payment history.
     * Calls {@link #getAllByRoomNumber(String)} with an empty keyword.
     *
     * @return A list of all {@link Payment} records.
     * @throws SQLException If a database access error occurs.
     */
    public List<Payment> getAll() throws SQLException {
        return getAllByRoomNumber("");
    }

    /**
     * Retrieves payment history filtered by room number keyword.
     * Results are joined with 'fee_types' and 'households' tables for descriptive names.
     *
     * @param keyword The room number (or part of it) to filter by.
     * @return A list of {@link Payment} objects matching the criteria, sorted by room number (ASC) and date (DESC).
     * @throws SQLException If a database access error occurs.
     */
    public List<Payment> getAllByRoomNumber(String keyword) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, f.name AS fee_name, h.room_number " +
                     "FROM payments p " +
                     "JOIN fee_types f ON p.fee_type_id = f.id " +
                     "JOIN households h ON p.household_id = h.id " +
                     "WHERE h.room_number LIKE ? " +
                     "ORDER BY h.room_number ASC, p.payment_date DESC";

        logger.log(Level.INFO, "[PaymentDAO] Fetching payments for room keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("id"));
                    p.setHouseholdId(rs.getInt("household_id"));
                    p.setFeeTypeId(rs.getInt("fee_type_id"));
                    
                    // DTO fields mapping
                    p.setFeeName(rs.getString("fee_name"));
                    p.setRoomNumber(rs.getString("room_number"));
                    
                    p.setPaymentDate(rs.getTimestamp("payment_date"));
                    p.setAmount(rs.getDouble("amount"));
                    p.setPayerName(rs.getString("payer_name"));
                    p.setNote(rs.getString("note"));

                    list.add(p);
                }
            }
            logger.log(Level.INFO, "[PaymentDAO] Found {0} payment records", list.size());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[PaymentDAO] Error in getAllByRoomNumber", e);
            throw e; 
        }

        return list;
    }
}