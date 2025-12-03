package com.bluemoon.app.dao;

import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanKhauDAO {

    public List<NhanKhau> selectByHoKhau(int maHo) {
        List<NhanKhau> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAN_KHAU WHERE MaHo = ? ORDER BY QuanHe ASC";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NhanKhau nk = new NhanKhau();
                    nk.setMaNhanKhau(rs.getInt("MaNhanKhau"));
                    nk.setMaHo(rs.getInt("MaHo"));
                    nk.setHoTen(rs.getString("HoTen"));
                    nk.setNgaySinh(rs.getDate("NgaySinh"));
                    nk.setGioiTinh(rs.getString("GioiTinh"));
                    nk.setCccd(rs.getString("CCCD"));
                    nk.setQuanHe(rs.getString("QuanHe"));
                    list.add(nk);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(NhanKhau nk) {
        String sql = "INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nk.getMaHo());
            pstmt.setString(2, nk.getHoTen());
            pstmt.setDate(3, new java.sql.Date(nk.getNgaySinh().getTime()));
            pstmt.setString(4, nk.getGioiTinh());
            
            if (nk.getCccd() == null || nk.getCccd().trim().isEmpty()) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, nk.getCccd());
            }
            pstmt.setString(6, nk.getQuanHe());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhanKhau nk) {
        String sql = "UPDATE NHAN_KHAU SET HoTen=?, NgaySinh=?, GioiTinh=?, CCCD=?, QuanHe=? WHERE MaNhanKhau=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nk.getHoTen());
            pstmt.setDate(2, new java.sql.Date(nk.getNgaySinh().getTime()));
            pstmt.setString(3, nk.getGioiTinh());
            
            if (nk.getCccd() == null || nk.getCccd().trim().isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, nk.getCccd());
            }
            
            pstmt.setString(5, nk.getQuanHe());
            pstmt.setInt(6, nk.getMaNhanKhau());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int maNhanKhau) {
        String sql = "DELETE FROM NHAN_KHAU WHERE MaNhanKhau=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNhanKhau);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check trùng khi thêm mới: Tìm xem có CCCD nào y hệt không
    public boolean checkCccdExist(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM NHAN_KHAU WHERE CCCD = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Check trùng khi update: Tìm CCCD y hệt NHƯNG loại trừ chính mình ra
    public boolean checkCccdExistForUpdate(String cccd, int maNhanKhauDangSua) {
        if (cccd == null || cccd.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM NHAN_KHAU WHERE CCCD = ? AND MaNhanKhau != ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, maNhanKhauDangSua);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}