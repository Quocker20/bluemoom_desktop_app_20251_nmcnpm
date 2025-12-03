package com.bluemoon.app.dao;

import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CongNoDAO {

    public List<CongNo> getAll(int thang, int nam) {
        return getAll(thang, nam, "");
    }

    public List<CongNo> getAll(int thang, int nam, String keyword) {
        List<CongNo> list = new ArrayList<>();
        // SQL đã JOIN sẵn HO_KHAU, chỉ cần lấy dữ liệu ra
        String sql = "SELECT cn.*, kp.TenKhoanPhi, hk.SoCanHo FROM CONG_NO cn " +
                "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                "JOIN HO_KHAU hk ON cn.MaHo = hk.MaHo " +
                "WHERE cn.Thang = ? AND cn.Nam = ? AND hk.SoCanHo LIKE ? " +
                "ORDER BY hk.SoCanHo ASC";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setMaHo(rs.getInt("MaHo"));
                    cn.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));

                    // [MỚI] Map dữ liệu hiển thị
                    cn.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    cn.setSoCanHo(rs.getString("SoCanHo"));

                    cn.setThang(rs.getInt("Thang"));
                    cn.setNam(rs.getInt("Nam"));
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

    public void insert(CongNo cn) {
        String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cn.getMaHo());
            pstmt.setInt(2, cn.getMaKhoanPhi());
            pstmt.setInt(3, cn.getThang());
            pstmt.setInt(4, cn.getNam());
            pstmt.setDouble(5, cn.getSoTienPhaiDong());
            pstmt.setDouble(6, 0);
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCalculated(int thang, int nam) {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE Thang = ? AND Nam = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkExist(int maHo, int maKhoanPhi, int thang, int nam) {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE MaHo = ? AND MaKhoanPhi = ? AND Thang = ? AND Nam = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHo);
            pstmt.setInt(2, maKhoanPhi);
            pstmt.setInt(3, thang);
            pstmt.setInt(4, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // [CẬP NHẬT] getById cũng cần JOIN HO_KHAU để lấy số căn hộ (hiển thị trên
    // Dialog thanh toán)
    public CongNo getById(int id) {
        String sql = "SELECT cn.*, kp.TenKhoanPhi, hk.SoCanHo FROM CONG_NO cn " +
                "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                "JOIN HO_KHAU hk ON cn.MaHo = hk.MaHo " +
                "WHERE cn.MaCongNo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setMaHo(rs.getInt("MaHo"));
                    cn.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));

                    // [MỚI] Map dữ liệu hiển thị
                    cn.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    cn.setSoCanHo(rs.getString("SoCanHo"));

                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    cn.setTrangThai(rs.getInt("TrangThai"));
                    return cn;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePayment(int maCongNo, double soTienMoiDaDong) {
        String sql = "UPDATE CONG_NO SET SoTienDaDong = ?, TrangThai = CASE WHEN SoTienDaDong >= SoTienPhaiDong THEN 1 ELSE 0 END WHERE MaCongNo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, soTienMoiDaDong);
            pstmt.setInt(2, maCongNo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkKhoanPhiInUse(int maKhoanPhi) {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE MaKhoanPhi = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maKhoanPhi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}