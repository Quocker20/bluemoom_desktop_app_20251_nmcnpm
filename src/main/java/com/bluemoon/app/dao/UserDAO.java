package com.bluemoon.app.dao;

import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;
import com.bluemoon.app.util.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Kiểm tra đăng nhập (Giữ nguyên code cũ)
     */
    public User checkLogin(String username, String password) {
        User user = null;
        String sql = "SELECT * FROM TAI_KHOAN WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return null;

            pstmt.setString(1, username);
            String hashedPassword = SecurityUtil.hashPassword(password);
            pstmt.setString(2, hashedPassword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setMaTK(rs.getInt("MaTK"));
                    user.setTenDangNhap(rs.getString("TenDangNhap"));
                    user.setMatKhau(rs.getString("MatKhau"));
                    user.setVaiTro(rs.getString("VaiTro"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // [MỚI] Cập nhật mật khẩu
    public boolean changePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE TAI_KHOAN SET MatKhau = ? WHERE MaTK = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}