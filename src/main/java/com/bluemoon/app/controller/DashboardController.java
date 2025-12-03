package com.bluemoon.app.controller;

import com.bluemoon.app.dao.DashboardDAO;
import java.util.Calendar;
import java.util.Map;

public class DashboardController {

    private final DashboardDAO dashboardDAO;

    public DashboardController() {
        this.dashboardDAO = new DashboardDAO();
    }

    public int getSoLuongHo() {
        return dashboardDAO.getDemCuDan().getOrDefault("soHo", 0);
    }

    public int getSoLuongNguoi() {
        return dashboardDAO.getDemCuDan().getOrDefault("soNguoi", 0);
    }

    // Trả về chuỗi định dạng (VD: "500 K", "1.2 Tr")
    public String getTongThuThangNay() {
        Calendar cal = Calendar.getInstance();
        Map<String, Double> data = dashboardDAO.getTaiChinhThangNay(
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
        return formatMoney(data.getOrDefault("tongThu", 0.0));
    }

    public String getCongNoThangNay() {
        Calendar cal = Calendar.getInstance();
        Map<String, Double> data = dashboardDAO.getTaiChinhThangNay(
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
        return formatMoney(data.getOrDefault("congNo", 0.0));
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