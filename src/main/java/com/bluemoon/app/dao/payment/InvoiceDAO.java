package com.bluemoon.app.dao.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Invoice;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Data Access Object for managing Invoices (Monthly Debts).
 * Handles operations related to the 'invoices' table.
 */
public class InvoiceDAO {

    private static final Logger logger = Logger.getLogger(InvoiceDAO.class.getName());

    /**
     * Retrieves all invoices for a specific month and year.
     *
     * @param month The month to filter.
     * @param year  The year to filter.
     * @return A list of {@link Invoice} objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Invoice> getAll(int month, int year) throws SQLException {
        return getAll(month, year, "");
    }

    /**
     * Retrieves invoices for a specific month and year, filtered by room number keyword.
     * Joins with 'fee_types' and 'households' to get detailed information.
     *
     * @param month   The month to filter.
     * @param year    The year to filter.
     * @param keyword The room number keyword to search for.
     * @return A list of {@link Invoice} objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Invoice> getAll(int month, int year, String keyword) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, f.name AS fee_name, h.room_number " +
                     "FROM invoices i " +
                     "JOIN fee_types f ON i.fee_type_id = f.id " +
                     "JOIN households h ON i.household_id = h.id " +
                     "WHERE i.month = ? AND i.year = ? AND h.room_number LIKE ? " +
                     "ORDER BY h.room_number ASC";

        logger.log(Level.INFO, "[InvoiceDAO] Fetching invoices for {0}/{1}", new Object[]{month, year});

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    inv.setHouseholdId(rs.getInt("household_id"));
                    inv.setFeeTypeId(rs.getInt("fee_type_id"));
                    
                    // DTO fields (Display only)
                    inv.setFeeName(rs.getString("fee_name"));
                    inv.setRoomNumber(rs.getString("room_number"));
                    
                    inv.setMonth(rs.getInt("month"));
                    inv.setYear(rs.getInt("year"));
                    inv.setAmountDue(rs.getDouble("amount_due"));
                    inv.setAmountPaid(rs.getDouble("amount_paid"));
                    inv.setStatus(rs.getInt("status"));
                    
                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in getAll", e);
            throw e; // Rethrow exception after logging
        }
        return list;
    }

    /**
     * Inserts a new invoice record into the database.
     *
     * @param inv The {@link Invoice} object to be inserted.
     * @throws SQLException If a database access error occurs.
     */
    public void insert(Invoice inv) throws SQLException {
        String sql = "INSERT INTO invoices (household_id, fee_type_id, month, year, amount_due, amount_paid, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        logger.log(Level.INFO, "[InvoiceDAO] Inserting invoice for Household ID: {0}", inv.getHouseholdId());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, inv.getHouseholdId());
            pstmt.setInt(2, inv.getFeeTypeId());
            pstmt.setInt(3, inv.getMonth());
            pstmt.setInt(4, inv.getYear());
            pstmt.setDouble(5, inv.getAmountDue());
            pstmt.setDouble(6, 0); // Default amount_paid is 0
            pstmt.setInt(7, 0);    // Default status is Unpaid (0)
            
            pstmt.executeUpdate();
            logger.log(Level.INFO, "[InvoiceDAO] Insert successful");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in insert", e);
            throw e;
        }
    }

    /**
     * Checks if invoices for a specific month and year have already been calculated.
     *
     * @param month The month to check.
     * @param year  The year to check.
     * @return {@code true} if invoices exist for this period, {@code false} otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isMonthCalculated(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM invoices WHERE month = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in isMonthCalculated", e);
            throw e;
        }
        return false;
    }

    /**
     * Checks if a specific invoice already exists to avoid duplicates.
     *
     * @param householdId The household ID.
     * @param feeTypeId   The fee type ID.
     * @param month       The month.
     * @param year        The year.
     * @return {@code true} if the invoice exists, {@code false} otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean exists(int householdId, int feeTypeId, int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM invoices WHERE household_id = ? AND fee_type_id = ? AND month = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, householdId);
            pstmt.setInt(2, feeTypeId);
            pstmt.setInt(3, month);
            pstmt.setInt(4, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in exists", e);
            throw e;
        }
        return false;
    }

    /**
     * Retrieves an invoice by its ID.
     *
     * @param id The ID of the invoice.
     * @return The {@link Invoice} object if found, otherwise {@code null}.
     * @throws SQLException If a database access error occurs.
     */
    public Invoice getById(int id) throws SQLException {
        String sql = "SELECT i.*, f.name AS fee_name, h.room_number " +
                     "FROM invoices i " +
                     "JOIN fee_types f ON i.fee_type_id = f.id " +
                     "JOIN households h ON i.household_id = h.id " +
                     "WHERE i.id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    inv.setHouseholdId(rs.getInt("household_id"));
                    inv.setFeeTypeId(rs.getInt("fee_type_id"));
                    inv.setFeeName(rs.getString("fee_name"));
                    inv.setRoomNumber(rs.getString("room_number"));
                    inv.setAmountDue(rs.getDouble("amount_due"));
                    inv.setAmountPaid(rs.getDouble("amount_paid"));
                    inv.setStatus(rs.getInt("status"));
                    return inv;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in getById: " + id, e);
            throw e;
        }
        return null;
    }

    /**
     * Updates the payment status and amount paid for an invoice.
     * Automatically sets status to 1 (Paid) if amount_paid >= amount_due.
     *
     * @param invoiceId     The ID of the invoice to update.
     * @param newAmountPaid The total amount paid so far.
     * @return {@code true} if the update was successful.
     * @throws SQLException If a database access error occurs.
     */
    public boolean updatePaymentStatus(int invoiceId, double newAmountPaid) throws SQLException {
        String sql = "UPDATE invoices SET amount_paid = ?, status = CASE WHEN amount_paid >= amount_due THEN 1 ELSE 0 END WHERE id = ?";
        logger.log(Level.INFO, "[InvoiceDAO] Updating payment for ID: {0}, New Paid: {1}", new Object[]{invoiceId, newAmountPaid});

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newAmountPaid);
            pstmt.setInt(2, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in updatePaymentStatus", e);
            throw e;
        }
    }

    /**
     * Checks if a specific fee type is currently referenced by any invoice.
     * Prevents deletion of fee types that are in use.
     *
     * @param feeTypeId The ID of the fee type to check.
     * @return {@code true} if the fee type is in use, {@code false} otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isFeeTypeInUse(int feeTypeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM invoices WHERE fee_type_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feeTypeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in isFeeTypeInUse", e);
            throw e;
        }
        return false;
    }

    /**
     * Deletes an invoice by its ID.
     *
     * @param id The ID of the invoice to delete.
     * @return The number of rows affected.
     * @throws SQLException If a database access error occurs.
     */
    public int deleteById(int id) throws SQLException {
        String sql = "DELETE FROM invoices WHERE id = ?";
        logger.log(Level.INFO, "[InvoiceDAO] Deleting invoice ID: {0}", id);
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in deleteById", e);
            throw e;
        }
    }

    /**
     * Automatically calculates and inserts vehicle fees for a specific month.
     *
     * @param month       The month to generate fees for.
     * @param year        The year to generate fees for.
     * @param feeTypeId   The ID of the fee type (e.g., Parking Fee).
     * @param unitPrice   The price per vehicle.
     * @param vehicleType The type of vehicle (1: Car, 2: Motorbike).
     * @return The number of invoices created.
     * @throws SQLException If a database access error occurs.
     */
    public int calculateVehicleFees(int month, int year, int feeTypeId, double unitPrice, int vehicleType) throws SQLException {
        String sql = "INSERT INTO invoices (household_id, fee_type_id, amount_due, amount_paid, month, year, status) " +
                     "SELECT v.household_id, ?, (COUNT(v.id) * ?), 0, ?, ?, 0 " +
                     "FROM vehicles v " +
                     "WHERE v.vehicle_type = ? AND v.status = 1 " + // Only active vehicles
                     "GROUP BY v.household_id " +
                     // Avoid duplicates
                     "HAVING NOT EXISTS (SELECT 1 FROM invoices i WHERE i.household_id = v.household_id AND i.fee_type_id = ? AND i.month = ? AND i.year = ?)";

        logger.log(Level.INFO, "[InvoiceDAO] Calculating vehicle fees (Type: {0}) for {1}/{2}", new Object[]{vehicleType, month, year});

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Params for SELECT & INSERT part
            pstmt.setInt(1, feeTypeId);
            pstmt.setDouble(2, unitPrice);
            pstmt.setInt(3, month);
            pstmt.setInt(4, year);
            pstmt.setInt(5, vehicleType); 
            
            // Params for HAVING NOT EXISTS check
            pstmt.setInt(6, feeTypeId);
            pstmt.setInt(7, month);
            pstmt.setInt(8, year);
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[InvoiceDAO] Error in calculateVehicleFees", e);
            throw e;
        }
    }
}