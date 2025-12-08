package com.bluemoon.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.bluemoon.app.util.DatabaseConnector;

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

        
        String sql = "SELECT " +
                     "(SELECT COALESCE(SUM(SoTien), 0) FROM GIAO_DICH_NOP_TIEN WHERE MONTH(NgayNop) = ? AND YEAR(NgayNop) = ?) as TongThu, " +
                     "(SELECT COALESCE(SUM(SoTienPhaiDong - SoTienDaDong), 0) FROM CONG_NO WHERE Thang = ? AND Nam = ?) as CongNo";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            // Set tham số (Lưu ý thứ tự: 1,2 cho Giao Dịch; 3,4 cho Công Nợ)
            pst.setInt(1, thang);
            pst.setInt(2, nam);
            pst.setInt(3, thang);
            pst.setInt(4, nam);
            
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