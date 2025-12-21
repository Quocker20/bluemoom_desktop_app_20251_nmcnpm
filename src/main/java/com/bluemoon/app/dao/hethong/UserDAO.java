package com.bluemoon.app.dao.hethong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;
import com.bluemoon.app.util.SecurityUtil;

public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * Kiểm tra đăng nhập
     * 
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User object hoặc null nếu sai
     * @throws SQLException lỗi truy vấn
     */
    public User checkLogin(String username, String password) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM TAI_KHOAN WHERE TenDangNhap = ? AND MatKhau = ?";
        logger.info("[USERDAO] Kiem tra dang nhap cho user: " + username);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null)
                throw new SQLException("Khong the ket noi Database");

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
            logger.log(Level.SEVERE, "[USERDAO] Loi checkLogin", e);
            throw e;
        }
        return user;
    }

    /**
     * Cập nhật mật khẩu
     * 
     * @param userId          ID tài khoản
     * @param newPasswordHash Mật khẩu mới đã mã hóa
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean changePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE TAI_KHOAN SET MatKhau = ? WHERE MaTK = ?";
        logger.info("[USERDAO] Doi mat khau cho User ID: " + userId);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[USERDAO] Loi changePassword", e);
            throw e;
        }
    }
}