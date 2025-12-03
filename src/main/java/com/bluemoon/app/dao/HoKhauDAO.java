package com.bluemoon.app.dao;

import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.util.AppConstants;
import com.bluemoon.app.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoKhauDAO {

    public List<HoKhau> getAll() {
        List<HoKhau> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_KHAU ORDER BY SoCanHo ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                HoKhau hk = new HoKhau();
                hk.setMaHo(rs.getInt("MaHo"));
                hk.setSoCanHo(rs.getString("SoCanHo"));
                hk.setTenChuHo(rs.getString("TenChuHo"));
                hk.setDienTich(rs.getDouble("DienTich"));
                hk.setSdt(rs.getString("SDT"));
                hk.setNgayTao(rs.getDate("NgayTao"));
                list.add(hk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * TRANSACTION: Thêm Hộ khẩu + Chủ hộ cùng lúc.
     */
    public boolean addHoKhauWithChuHo(HoKhau hk, NhanKhau chuHo) {
        Connection conn = null;
        PreparedStatement pstmtHk = null;
        PreparedStatement pstmtNk = null;
        boolean result = false;

        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) return false;

            // 1. Start Transaction
            conn.setAutoCommit(false);

            // 2. Insert HO_KHAU
            String sqlHk = "INSERT INTO HO_KHAU (SoCanHo, TenChuHo, DienTich, SDT) VALUES (?, ?, ?, ?)";
            pstmtHk = conn.prepareStatement(sqlHk, Statement.RETURN_GENERATED_KEYS);
            pstmtHk.setString(1, hk.getSoCanHo());
            pstmtHk.setString(2, hk.getTenChuHo());
            pstmtHk.setDouble(3, hk.getDienTich());
            pstmtHk.setString(4, hk.getSdt());

            int affectedRows = pstmtHk.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Lấy MaHo vừa sinh ra
            int newMaHo = 0;
            try (ResultSet generatedKeys = pstmtHk.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newMaHo = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Insert NHAN_KHAU (Chủ hộ)
            String sqlNk = "INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (?, ?, ?, ?, ?, ?)";
            pstmtNk = conn.prepareStatement(sqlNk);
            pstmtNk.setInt(1, newMaHo);
            pstmtNk.setString(2, chuHo.getHoTen());
            pstmtNk.setDate(3, new java.sql.Date(chuHo.getNgaySinh().getTime()));
            pstmtNk.setString(4, chuHo.getGioiTinh());

            if (chuHo.getCccd() == null || chuHo.getCccd().isEmpty()) {
                pstmtNk.setNull(5, Types.VARCHAR);
            } else {
                pstmtNk.setString(5, chuHo.getCccd());
            }
            // Gán cứng quan hệ là Chủ hộ từ Constants
            pstmtNk.setString(6, AppConstants.QH_CHU_HO);

            pstmtNk.executeUpdate();

            // 4. Commit
            conn.commit();
            result = true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            try {
                if (pstmtHk != null) pstmtHk.close();
                if (pstmtNk != null) pstmtNk.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    public boolean update(HoKhau hk) {
        String sql = "UPDATE HO_KHAU SET SoCanHo=?, TenChuHo=?, DienTich=?, SDT=? WHERE MaHo=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hk.getSoCanHo());
            pstmt.setString(2, hk.getTenChuHo());
            pstmt.setDouble(3, hk.getDienTich());
            pstmt.setString(4, hk.getSdt());
            pstmt.setInt(5, hk.getMaHo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int maHo) {
        String sql = "DELETE FROM HO_KHAU WHERE MaHo=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkExist(String soCanHo) {
        String sql = "SELECT COUNT(*) FROM HO_KHAU WHERE SoCanHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soCanHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<HoKhau> search(String keyword) {
        List<HoKhau> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_KHAU WHERE SoCanHo LIKE ? OR TenChuHo LIKE ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoKhau hk = new HoKhau();
                    hk.setMaHo(rs.getInt("MaHo"));
                    hk.setSoCanHo(rs.getString("SoCanHo"));
                    hk.setTenChuHo(rs.getString("TenChuHo"));
                    hk.setDienTich(rs.getDouble("DienTich"));
                    hk.setSdt(rs.getString("SDT"));
                    hk.setNgayTao(rs.getDate("NgayTao"));
                    list.add(hk);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}