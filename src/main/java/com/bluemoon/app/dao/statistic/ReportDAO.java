package com.bluemoon.app.dao.statistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Invoice;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * DAO for generating Reports (Statistics & Excel data).
 */
public class ReportDAO {

    private static final Logger logger = Logger.getLogger(ReportDAO.class.getName());

    /**
     * Get Total Revenue and Total Debt for a specific month.
     *
     * @param month The month.
     * @param year  The year.
     * @return Map containing "totalRevenue" and "totalDebt".
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Double> getFinancialStats(int month, int year) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        // Query to get sum of payments (Revenue) and sum of unpaid amounts (Debt)
        String sql = "SELECT " +
                "(SELECT COALESCE(SUM(amount), 0) FROM payments WHERE MONTH(payment_date) = ? AND YEAR(payment_date) = ?) as total_revenue, " +
                "(SELECT COALESCE(SUM(amount_due - amount_paid), 0) FROM invoices WHERE month = ? AND year = ?) as total_debt";
        
        logger.log(Level.INFO, "[ReportDAO] Fetching financial stats for {0}/{1}", new Object[]{month, year});

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            pstmt.setInt(4, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.put("totalRevenue", rs.getDouble("total_revenue"));
                    double debt = rs.getDouble("total_debt");
                    result.put("totalDebt", debt < 0 ? 0 : debt);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportDAO] Error in getFinancialStats", e);
            throw e;
        }
        return result;
    }

    /**
     * Get Demographic Statistics (Gender distribution).
     *
     * @return Map with Gender as Key and Count as Value.
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Integer> getDemographicStats() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT gender, COUNT(*) as count FROM residents WHERE is_deleted = 0 GROUP BY gender";
        logger.info("[ReportDAO] Fetching demographic stats");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String gender = rs.getString("gender");
                if (gender == null || gender.trim().isEmpty()) {
                    gender = "Unknown";
                }
                result.put(gender, rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportDAO] Error in getDemographicStats", e);
            throw e;
        }
        return result;
    }

    /**
     * Get detailed list of invoices for reporting.
     * Joins with Households and FeeTypes for names.
     *
     * @param month The month filter.
     * @param year  The year filter.
     * @return List of Invoices.
     * @throws SQLException If a database access error occurs.
     */
    public List<Invoice> getReportDetails(int month, int year) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, h.room_number, h.owner_name, f.name as fee_name FROM invoices i " +
                     "JOIN households h ON i.household_id = h.id " +
                     "JOIN fee_types f ON i.fee_type_id = f.id " +
                     "WHERE i.month = ? AND i.year = ? " +
                     "ORDER BY i.household_id ASC";
        
        logger.log(Level.INFO, "[ReportDAO] Fetching detailed report data");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    // Use Room Number for display instead of ID if model supports, or set both
                    inv.setRoomNumber(rs.getString("room_number"));
                    // Mapping Owner Name to feeName field temporarily for display in report if needed, 
                    // OR better: ensure Invoice model has ownerName field. 
                    // Based on previous Invoice model, we used feeName. 
                    inv.setFeeName(rs.getString("fee_name")); 
                    
                    // Note: If you need owner name in the report, make sure Invoice model has it.
                    // For now, mapping standard fields:
                    
                    inv.setAmountDue(rs.getDouble("amount_due"));
                    inv.setAmountPaid(rs.getDouble("amount_paid"));
                    inv.setStatus(rs.getInt("status"));
                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportDAO] Error in getReportDetails", e);
            throw e;
        }
        return list;
    }
}