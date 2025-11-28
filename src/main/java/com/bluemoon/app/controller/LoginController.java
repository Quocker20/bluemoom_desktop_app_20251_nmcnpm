package com.bluemoon.app.controller;

import com.bluemoon.app.model.User;
import com.bluemoon.app.dao.UserDAO;

/**
 * Controller xử lý logic cho màn hình Đăng nhập.
 */

public class LoginController {
    private UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Xử lý đăng nhập.
     * @param username Tên đăng nhập
     * @param password Mật khẩu (chưa mã hóa)
     * @return User object nếu thành công, null nếu thất bại.
     */
    public User login(String username, String password) {
        // 1. Kiểm tra dữ liệu đầu vào cơ bản
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }

        // 2. Gọi DAO để kiểm tra trong CSDL
        // (DAO sẽ tự động băm mật khẩu để so sánh)
        return userDAO.checkLogin(username, password);
    }
}