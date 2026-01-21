package com.bluemoon.app.controller.statistic;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.statistic.DashboardDAO;

/**
 * Controller for the Main Dashboard.
 * Handles summary data display for the home screen.
 */
public class DashboardController {

    private final DashboardDAO dashboardDAO;
    private final Logger logger;

    public DashboardController() {
        this.dashboardDAO = new DashboardDAO();
        this.logger = Logger.getLogger(DashboardController.class.getName());
    }

    /**
     * Get total number of households.
     * * @return Total active households count.
     */
    public int getHouseholdCount() {
        try {
            // DAO returns Map<String, Integer> with key "householdCount"
            return dashboardDAO.getDemographics().getOrDefault("householdCount", 0);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Error in getHouseholdCount", e);
            return 0;
        }
    }

    /**
     * Get total number of residents.
     * * @return Total active residents count.
     */
    public int getResidentCount() {
        try {
            // DAO returns Map<String, Integer> with key "residentCount"
            return dashboardDAO.getDemographics().getOrDefault("residentCount", 0);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Error in getResidentCount", e);
            return 0;
        }
    }

    /**
     * Get total revenue for the current month.
     * * @return Formatted string (e.g., "500 K", "1.2 M").
     */
    public String getCurrentMonthRevenue() {
        Calendar cal = Calendar.getInstance();
        try {
            // Month in Calendar is 0-indexed, so add 1
            Map<String, Double> data = dashboardDAO.getMonthlyFinance(
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
            
            return formatMoney(data.getOrDefault("revenue", 0.0));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Error in getCurrentMonthRevenue", e);
            return "0";
        }
    }

    /**
     * Get total outstanding debt for the current month.
     * * @return Formatted string.
     */
    public String getCurrentMonthDebt() {
        Calendar cal = Calendar.getInstance();
        try {
            Map<String, Double> data = dashboardDAO.getMonthlyFinance(
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
            
            return formatMoney(data.getOrDefault("debt", 0.0));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Error in getCurrentMonthDebt", e);
            return "0";
        }
    }

    /**
     * Helper to format large numbers with suffixes (B, M, K).
     */
    private String formatMoney(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1f B", amount / 1_000_000_000); // Billion (Tỷ)
        } else if (amount >= 1_000_000) {
            return String.format("%.1f M", amount / 1_000_000); // Million (Triệu)
        } else if (amount >= 1_000) {
            return String.format("%.0f K", amount / 1_000); // Thousand (Nghìn)
        } else {
            return String.format("%.0f", amount);
        }
    }
}