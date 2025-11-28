package com.bluemoon.app.dao;

import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;
import com.bluemoon.app.util.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp truy cập dữ liệu (DAO) cho thực thể User (Tài khoản).
 * Chứa các phương thức thao tác với bảng TAI_KHOAN.
 */
public class UserDAO {

    /**
     * Kiểm tra thông tin đăng nhập của người dùng.
     * (Phục vụ chức năng UC-3.1.1: Đăng nhập)
     * @param username Tên đăng nhập do người dùng nhập.
     * @param password Mật khẩu do người dùng nhập.
     * @return Đối tượng User nếu thông tin đúng, hoặc null nếu sai.
     */
    public User checkLogin(String username, String password) {
        User user = null;
        // Câu lệnh SQL sử dụng tham số (?) để tránh SQL Injection
        String sql = "SELECT * FROM TAI_KHOAN WHERE TenDangNhap = ? AND MatKhau = ?";
        
        // Sử dụng try-with-resources để tự động đóng Connection, PreparedStatement, ResultSet
        try (Connection conn = DatabaseConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Gán giá trị cho các tham số (?)
            pstmt.setString(1, username);
            String hashedPassword = SecurityUtil.hashPassword(password);
            pstmt.setString(2, hashedPassword);
            
            // Thực thi truy vấn
            try (ResultSet rs = pstmt.executeQuery()) {
                // Nếu có kết quả trả về (tức là đăng nhập đúng)
                if (rs.next()) {
                    user = new User();
                    // Ánh xạ dữ liệu từ ResultSet sang đối tượng User
                    user.setMaTK(rs.getInt("MaTK"));
                    user.setTenDangNhap(rs.getString("TenDangNhap"));
                    user.setMatKhau(rs.getString("MatKhau"));
                    user.setVaiTro(rs.getString("VaiTro"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Lỗi truy vấn đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }
}