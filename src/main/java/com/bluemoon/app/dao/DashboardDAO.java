package com.bluemoon.app.dao;

import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardDAO {

    // Lấy thống kê cơ bản: Số hộ, Số nhân khẩu
    public Map<String, Integer> getDemCuDan() {
        Map<String, Integer> map = new HashMap<>();
        String sqlHo = "SELECT COUNT(*) FROM HO_KHAU";
        String sqlNguoi = "SELECT COUNT(*) FROM NHAN_KHAU";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Đếm hộ
            try (PreparedStatement pst = conn.prepareStatement(sqlHo);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next())
                    map.put("soHo", rs.getInt(1));
            }
            // Đếm người
            try (PreparedStatement pst = conn.prepareStatement(sqlNguoi);
                    ResultSet rs = pst.executeQuery()) {
                if (rs.next())
                    map.put("soNguoi", rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Lấy thống kê tài chính TOÀN THỜI GIAN (hoặc theo tháng hiện tại tùy nhu cầu)
    // Ở đây tôi làm thống kê theo THÁNG HIỆN TẠI để Dashboard có tính thời điểm
    public Map<String, Double> getTaiChinhThangNay(int thang, int nam) {
        Map<String, Double> map = new HashMap<>();
        // Tổng thu: Tổng tiền đã đóng trong tháng
        // Công nợ: Tổng tiền còn thiếu (Phải đóng - Đã đóng) trong tháng
        String sql = "SELECT " +
                "SUM(SoTienDaDong) as TongThu, " +
                "SUM(SoTienPhaiDong - SoTienDaDong) as CongNo " +
                "FROM CONG_NO WHERE Thang = ? AND Nam = ?";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, thang);
            pst.setInt(2, nam);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    map.put("tongThu", rs.getDouble("TongThu"));
                    double no = rs.getDouble("CongNo");
                    map.put("congNo", no < 0 ? 0 : no);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}