package com.bluemoon.app.dao.thongke;

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

import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.util.DatabaseConnector;

public class BaoCaoDAO {

    private static final Logger logger = Logger.getLogger(BaoCaoDAO.class.getName());

    /**
     * Thống kê Tổng thu & Tổng nợ theo tháng
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return Map chứa TongThu và TongNo
     * @throws SQLException lỗi truy vấn
     */
    public Map<String, Double> getThongKeTaiChinh(int thang, int nam) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT " +
                "(SELECT COALESCE(SUM(SoTien), 0) FROM GIAO_DICH_NOP_TIEN WHERE MONTH(NgayNop) = ? AND YEAR(NgayNop) = ?) as TongThu, "
                +
                "(SELECT COALESCE(SUM(SoTienPhaiDong - SoTienDaDong), 0) FROM CONG_NO WHERE Thang = ? AND Nam = ?) as TongNo";
        logger.log(Level.INFO, "[BAOCAODAO] Thong ke tai chinh thang {0}/{1}", new Object[] { thang, nam });

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            pstmt.setInt(3, thang);
            pstmt.setInt(4, nam);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.put("TongThu", rs.getDouble("TongThu"));
                    double no = rs.getDouble("TongNo");
                    result.put("TongNo", no < 0 ? 0 : no);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BAOCAODAO] Loi getThongKeTaiChinh", e);
            throw e;
        }
        return result;
    }

    /**
     * Thống kê Cơ cấu dân cư (Nam/Nữ)
     * 
     * @return Map<String, Integer>
     * @throws SQLException lỗi truy vấn
     */
    public Map<String, Integer> getThongKeDanCu() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT GioiTinh, COUNT(*) as SoLuong FROM NHAN_KHAU GROUP BY GioiTinh";
        logger.info("[BAOCAODAO] Thong ke dan cu");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String gt = rs.getString("GioiTinh");
                if (gt == null)
                    gt = "Khác";
                result.put(gt, rs.getInt("SoLuong"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BAOCAODAO] Loi getThongKeDanCu", e);
            throw e;
        }
        return result;
    }

    /**
     * Lấy danh sách chi tiết công nợ cho báo cáo
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return List<CongNo>
     * @throws SQLException lỗi truy vấn
     */
    public List<CongNo> getChiTietBaoCao(int thang, int nam) throws SQLException {
        List<CongNo> list = new ArrayList<>();
        String sql = "SELECT cn.*, hk.SoCanHo, hk.TenChuHo, kp.TenKhoanPhi FROM CONG_NO cn " +
                "JOIN HO_KHAU hk ON cn.MaHo = hk.MaHo " +
                "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                "WHERE cn.Thang = ? AND cn.Nam = ? " +
                "ORDER BY cn.MaHo ASC";
        logger.log(Level.INFO, "[BAOCAODAO] Get chi tiet bao cao");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setSoCanHo(rs.getString("SoCanHo"));
                    cn.setTenKhoanPhi(rs.getString("TenChuHo"));
                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    cn.setTrangThai(rs.getInt("TrangThai"));
                    list.add(cn);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BAOCAODAO] Loi getChiTietBaoCao", e);
            throw e;
        }
        return list;
    }
}