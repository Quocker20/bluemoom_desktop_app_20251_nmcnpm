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

public class DashboardDAO {

    private static final Logger logger = Logger.getLogger(DashboardDAO.class.getName());

    /**
     * Lấy thống kê cơ bản: Số hộ, Số nhân khẩu
     * 
     * @return Map chứa key "soHo", "soNguoi"
     * @throws SQLException lỗi truy vấn
     */
    public Map<String, Integer> getDemCuDan() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sqlHo = "SELECT COUNT(*) FROM HO_KHAU WHERE IsDeleted = 0";
        String sqlNguoi = "SELECT COUNT(*) FROM NHAN_KHAU";

        logger.info("[DASHBOARD] Lay thong ke dan cu");

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
            logger.log(Level.SEVERE, "[DASHBOARD] Loi lay thong ke dan cu", e);
            throw e;
        }
        return map;
    }

    /**
     * Lấy thống kê tài chính theo tháng chỉ định
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return Map chứa key "tongThu", "congNo"
     * @throws SQLException lỗi truy vấn
     */
    public Map<String, Double> getTaiChinhThangNay(int thang, int nam) throws SQLException {
        Map<String, Double> map = new HashMap<>();
        String sql = "SELECT " +
                "(SELECT COALESCE(SUM(SoTien), 0) FROM GIAO_DICH_NOP_TIEN WHERE MONTH(NgayNop) = ? AND YEAR(NgayNop) = ?) as TongThu, "
                +
                "(SELECT COALESCE(SUM(SoTienPhaiDong - SoTienDaDong), 0) FROM CONG_NO WHERE Thang = ? AND Nam = ?) as CongNo";

        logger.log(Level.INFO, "[DASHBOARD] Lay thong ke tai chinh thang {0}/{1}", new Object[] { thang, nam });

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

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
            logger.log(Level.SEVERE, "[DASHBOARD] Loi lay thong ke tai chinh", e);
            throw e;
        }
        return map;
    }
}