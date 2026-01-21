package com.bluemoon.app.controller.system;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.system.UserDAO;
import com.bluemoon.app.model.User;
import com.bluemoon.app.util.SecurityUtil;

/**
 * Controller for managing system user accounts.
 */
public class UserController {

    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public UserController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Handles password change request.
     * * @param currentUser The currently logged-in user.
     * 
     * @param oldPassword     The old password input.
     * @param newPassword     The new password input.
     * @param confirmPassword The confirmation of the new password.
     * @return Error message string, or "SUCCESS" if successful.
     */
    public String changePassword(User currentUser, String oldPassword, String newPassword, String confirmPassword) {
        // 1. Basic Validation
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            return "Please enter all required fields!";
        }

        // 2. Verify Old Password
        String currentHashInput = SecurityUtil.hashPassword(oldPassword);
        if (!currentHashInput.equals(currentUser.getPassword())) {
            return "Incorrect old password!";
        }

        // 3. Validate New Password
        if (newPassword.length() < 6) {
            return "New password must be at least 6 characters long!";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Confirm password does not match!";
        }

        if (oldPassword.equals(newPassword)) {
            return "New password cannot be the same as the old one!";
        }

        // 4. Hash new password and update DB
        String newHash = SecurityUtil.hashPassword(newPassword);

        try {
            boolean success = userDAO.changePassword(currentUser.getId(), newHash);

            if (success) {
                // Update current session user
                currentUser.setPassword(newHash);
                return "SUCCESS";
            } else {
                return "System Error! Could not change password.";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[UserController] Error changing password", e);
            return "Database connection error!";
        }
    }
}