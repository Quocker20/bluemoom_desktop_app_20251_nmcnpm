package com.bluemoon.app.dao;

import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaoCaoDAO {

    // Thống kê Tổng thu & Tổng nợ theo tháng
    public Map<String, Double> getThongKeTaiChinh(int thang, int nam) {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT " +
                "SUM(SoTienDaDong) as TongThu, " +
                "SUM(SoTienPhaiDong - SoTienDaDong) as TongNo " +
                "FROM CONG_NO WHERE Thang = ? AND Nam = ?";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.put("TongThu", rs.getDouble("TongThu"));
                    // Tổng nợ không được âm
                    double no = rs.getDouble("TongNo");
                    result.put("TongNo", no < 0 ? 0 : no);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Thống kê Cơ cấu dân cư (Nam/Nữ)
    public Map<String, Integer> getThongKeDanCu() {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT GioiTinh, COUNT(*) as SoLuong FROM NHAN_KHAU GROUP BY GioiTinh";
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
            e.printStackTrace();
        }
        return result;
    }

    // Lấy danh sách chi tiết cho bảng bên dưới
    public List<CongNo> getChiTietBaoCao(int thang, int nam) {
        List<CongNo> list = new ArrayList<>();
        String sql = "SELECT cn.*, hk.SoCanHo, hk.TenChuHo, kp.TenKhoanPhi FROM CONG_NO cn " +
                "JOIN HO_KHAU hk ON cn.MaHo = hk.MaHo " +
                "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                "WHERE cn.Thang = ? AND cn.Nam = ? " +
                "ORDER BY cn.MaHo ASC";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setSoCanHo(rs.getString("SoCanHo"));
                    // Tận dụng trường TenKhoanPhi để lưu Tên Chủ Hộ cho tiện hiển thị ở bảng báo
                    // cáo
                    // Hoặc bạn có thể thêm trường tenChuHo vào Model CongNo nếu muốn chuẩn hơn.
                    // Ở đây tôi gán tạm vào TenKhoanPhi để demo,
                    // nhưng đúng ra nên tạo DTO riêng cho Báo cáo.
                    cn.setTenKhoanPhi(rs.getString("TenChuHo"));

                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    cn.setTrangThai(rs.getInt("TrangThai"));
                    list.add(cn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}