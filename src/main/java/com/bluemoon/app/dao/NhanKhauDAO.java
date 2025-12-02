package com.bluemoon.app.dao;

import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanKhauDAO {

    /**
     * Lấy danh sách tất cả nhân khẩu thuộc một Hộ khẩu cụ thể.
     * @param maHo ID của hộ khẩu
     * @return List<NhanKhau>
     */
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
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách nhân khẩu: " + e.getMessage());
        }
        return list;
    }

    /**
     * Thêm mới một nhân khẩu vào hộ.
     * @param nk Đối tượng NhanKhau
     * @return true nếu thành công
     */
    public boolean insert(NhanKhau nk) {
        String sql = "INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nk.getMaHo());
            pstmt.setString(2, nk.getHoTen());
            // Chuyển đổi java.util.Date sang java.sql.Date
            pstmt.setDate(3, new java.sql.Date(nk.getNgaySinh().getTime()));
            pstmt.setString(4, nk.getGioiTinh());
            
            // Xử lý CCCD null (cho trẻ em)
            if (nk.getCccd() == null || nk.getCccd().isEmpty()) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, nk.getCccd());
            }
            
            pstmt.setString(6, nk.getQuanHe());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm nhân khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin nhân khẩu.
     */
    public boolean update(NhanKhau nk) {
        String sql = "UPDATE NHAN_KHAU SET HoTen=?, NgaySinh=?, GioiTinh=?, CCCD=?, QuanHe=? WHERE MaNhanKhau=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nk.getHoTen());
            pstmt.setDate(2, new java.sql.Date(nk.getNgaySinh().getTime()));
            pstmt.setString(3, nk.getGioiTinh());
            
            if (nk.getCccd() == null || nk.getCccd().isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, nk.getCccd());
            }
            
            pstmt.setString(5, nk.getQuanHe());
            pstmt.setInt(6, nk.getMaNhanKhau());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật nhân khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa một nhân khẩu.
     */
    public boolean delete(int maNhanKhau) {
        String sql = "DELETE FROM NHAN_KHAU WHERE MaNhanKhau=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNhanKhau);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa nhân khẩu: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Kiểm tra trùng CCCD (Trừ trường hợp null).
     */
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
}