package com.bluemoon.app.controller.statistic;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.statistic.DashboardDAO;

/**
 * Controller cho màn hình Dashboard (Trang chủ).
 */
public class DashboardController {

    private final DashboardDAO dashboardDAO;
    private final Logger logger;

    public DashboardController() {
        this.dashboardDAO = new DashboardDAO();
        this.logger = Logger.getLogger(DashboardController.class.getName());
    }

    /**
     * Lấy số lượng hộ gia đình.
     * 
     * @return int
     */
    public int getSoLuongHo() {
        try {
            return dashboardDAO.getDemCuDan().getOrDefault("soHo", 0);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Loi getSoLuongHo", e);
            return 0;
        }
    }

    /**
     * Lấy số lượng nhân khẩu.
     * 
     * @return int
     */
    public int getSoLuongNguoi() {
        try {
            return dashboardDAO.getDemCuDan().getOrDefault("soNguoi", 0);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Loi getSoLuongNguoi", e);
            return 0;
        }
    }

    /**
     * Lấy tổng thu của tháng hiện tại.
     * 
     * @return String chuỗi định dạng (VD: "500 K", "1.2 Tr")
     */
    public String getTongThuThangNay() {
        Calendar cal = Calendar.getInstance();
        try {
            Map<String, Double> data = dashboardDAO.getTaiChinhThangNay(
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
            return formatMoney(data.getOrDefault("tongThu", 0.0));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Loi getTongThuThangNay", e);
            return "0";
        }
    }

    /**
     * Lấy tổng công nợ của tháng hiện tại.
     * 
     * @return String chuỗi định dạng
     */
    public String getCongNoThangNay() {
        Calendar cal = Calendar.getInstance();
        try {
            Map<String, Double> data = dashboardDAO.getTaiChinhThangNay(
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
            return formatMoney(data.getOrDefault("congNo", 0.0));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[DashboardController] Loi getCongNoThangNay", e);
            return "0";
        }
    }

    private String formatMoney(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1f Tỷ", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.1f Tr", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.0f K", amount / 1_000);
        } else {
            return String.format("%.0f", amount);
        }
    }
}