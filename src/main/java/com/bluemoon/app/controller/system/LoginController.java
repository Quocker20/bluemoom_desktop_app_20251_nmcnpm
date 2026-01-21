package com.bluemoon.app.controller.system;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.bluemoon.app.dao.system.UserDAO;
import com.bluemoon.app.model.User;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Controller xử lý đăng nhập.
 */
public class LoginController {

    private final UserDAO userDAO;
    private final Logger logger;

    public LoginController() {
        this.userDAO = new UserDAO();
        this.logger = Logger.getLogger(LoginController.class.getName());
    }

    /**
     * Kiểm tra đăng nhập.
     * 
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User object nếu đúng, null nếu sai
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
                    "Không thể kết nối đến Cơ sở dữ liệu!\nVui lòng kiểm tra lại mạng hoặc cấu hình MySQL.",
                    "Lỗi Kết Nối",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 3. Call DAO
        try {
            return userDAO.checkLogin(username, password);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[LoginController] Loi khi checkLogin", e);
            return null;
        }
    }
}