package com.bluemoon.app.dao.statistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.util.DatabaseConnector;

/**
 * DAO for Dashboard widgets and summary data.
 */
public class DashboardDAO {

    private static final Logger logger = Logger.getLogger(DashboardDAO.class.getName());

    /**
     * Get basic demographics: Count of Households and Residents.
     *
     * @return Map containing keys "householdCount" and "residentCount".
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Integer> getDemographics() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sqlHouseholds = "SELECT COUNT(*) FROM households WHERE is_deleted = 0";
        String sqlResidents = "SELECT COUNT(*) FROM residents WHERE is_deleted = 0";

        logger.info("[DashboardDAO] Fetching demographic counts");

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Count Households
            try (PreparedStatement pst = conn.prepareStatement(sqlHouseholds);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    map.put("householdCount", rs.getInt(1));
                }
            }
            // Count Residents
            try (PreparedStatement pst = conn.prepareStatement(sqlResidents);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    map.put("residentCount", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardDAO] Error in getDemographics", e);
            throw e;
        }
        return map;
    }

    /**
     * Get financial summary for the current (or specific) month.
     *
     * @param month The month.
     * @param year  The year.
     * @return Map containing keys "revenue" and "debt".
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Double> getMonthlyFinance(int month, int year) throws SQLException {
        Map<String, Double> map = new HashMap<>();
        // Revenue: Sum of payments made in this month
        // Debt: Sum of (Due - Paid) for invoices of this month
        String sql = "SELECT " +
                "(SELECT COALESCE(SUM(amount), 0) FROM payments WHERE MONTH(payment_date) = ? AND YEAR(payment_date) = ?) as revenue, " +
                "(SELECT COALESCE(SUM(amount_due - amount_paid), 0) FROM invoices WHERE month = ? AND year = ?) as debt";

        logger.log(Level.INFO, "[DashboardDAO] Fetching monthly finance for {0}/{1}", new Object[]{month, year});

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, month);
            pst.setInt(2, year);
            pst.setInt(3, month);
            pst.setInt(4, year);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    map.put("revenue", rs.getDouble("revenue"));
                    double debt = rs.getDouble("debt");
                    map.put("debt", debt < 0 ? 0 : debt);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardDAO] Error in getMonthlyFinance", e);
            throw e;
        }
        return map;
    }
}