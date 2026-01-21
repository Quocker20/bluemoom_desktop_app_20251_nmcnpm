package com.bluemoon.app.dao.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;
import com.bluemoon.app.util.SecurityUtil;

/**
 * Data Access Object for managing System Users.
 * Handles authentication, registration, and password updates for the 'users' table.
 */
public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * Authenticates a user based on username and password.
     *
     * @param username The username to check.
     * @param password The raw password to check.
     * @return A {@link User} object if credentials are valid, otherwise {@code null}.
     * @throws SQLException If a database access error occurs.
     */
    public User checkLogin(String username, String password) throws SQLException {
        User user = null;
        // Updated table: users, columns: username, password
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        logger.log(Level.INFO, "[UserDAO] Checking login for user: {0}", username);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                throw new SQLException("Cannot connect to Database");
            }

            pstmt.setString(1, username);
            // Hash the input password to match the stored hash
            String hashedPassword = SecurityUtil.hashPassword(password);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[UserDAO] Error in checkLogin", e);
            throw e;
        }
        return user;
    }

    /**
     * Updates the password for a specific user.
     *
     * @param userId          The ID of the user.
     * @param newPasswordHash The new password (already hashed).
     * @return {@code true} if the update was successful, {@code false} otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean changePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        logger.log(Level.INFO, "[UserDAO] Changing password for User ID: {0}", userId);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[UserDAO] Error in changePassword", e);
            throw e;
        }
    }
}