package com.bluemoon.app.controller.system;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.bluemoon.app.dao.system.UserDAO;
import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Controller handling user login authentication.
 */
public class LoginController {

    private final UserDAO userDAO;
    private final Logger logger;

    public LoginController() {
        this.userDAO = new UserDAO();
        this.logger = Logger.getLogger(LoginController.class.getName());
    }

    /**
     * Authenticates the user.
     * * @param username The input username.
     * @param password The input password.
     * @return User object if successful, null if failed.
     */
    public User login(String username, String password) {
        // 1. Validate Input
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return null;
        }

        // 2. Check Connection (UX Improvement)
        if (DatabaseConnector.getConnection() == null) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to Database!\nPlease check your network or MySQL configuration.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 3. Call DAO
        try {
            return userDAO.checkLogin(username, password);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[LoginController] Error during login check", e);
            return null;
        }
    }
}