package com.bluemoon.app.controller;

import com.bluemoon.app.dao.UserDAO;
import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;
import javax.swing.JOptionPane;

public class LoginController {
    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        // 1. Validate Input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }

        // 2. Check Connection (UX Improvement)
        if (DatabaseConnector.getConnection() == null) {
            JOptionPane.showMessageDialog(null, 
                "Không thể kết nối đến Cơ sở dữ liệu!\nVui lòng kiểm tra lại mạng hoặc cấu hình MySQL.", 
                "Lỗi Kết Nối", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 3. Call DAO
        return userDAO.checkLogin(username, password);
    }
}