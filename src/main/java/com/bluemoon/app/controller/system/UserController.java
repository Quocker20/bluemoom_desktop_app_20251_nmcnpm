package com.bluemoon.app.controller.system;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.system.UserDAO;
import com.bluemoon.app.model.User;
import com.bluemoon.app.util.SecurityUtil;

/**
 * Controller quản lý tài khoản người dùng hệ thống.
 */
public class UserController {

    private final UserDAO userDAO;
    private final Logger logger;

    public UserController() {
        this.userDAO = new UserDAO();
        this.logger = Logger.getLogger(UserController.class.getName());
    }

    /**
     * Xử lý đổi mật khẩu.
     * 
     * @param currentUser User hiện tại
     * @param passCu      Mật khẩu cũ
     * @param passMoi     Mật khẩu mới
     * @param xacNhanPass Xác nhận mật khẩu mới
     * @return Chuỗi thông báo lỗi (nếu có) hoặc "SUCCESS" nếu thành công.
     */
    public String doiMatKhau(User currentUser, String passCu, String passMoi, String xacNhanPass) {
        // 1. Validate cơ bản
        if (passCu.isEmpty() || passMoi.isEmpty() || xacNhanPass.isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }

        // 2. Kiểm tra mật khẩu cũ có đúng không
        String currentHashInput = SecurityUtil.hashPassword(passCu);
        if (!currentHashInput.equals(currentUser.getPassword())) {
            return "Mật khẩu cũ không chính xác!";
        }

        // 3. Kiểm tra mật khẩu mới
        if (passMoi.length() < 6) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự!";
        }

        if (!passMoi.equals(xacNhanPass)) {
            return "Mật khẩu xác nhận không khớp!";
        }

        if (passCu.equals(passMoi)) {
            return "Mật khẩu mới không được trùng mật khẩu cũ!";
        }

        // 4. Hash mật khẩu mới và lưu xuống DB
        String newHash = SecurityUtil.hashPassword(passMoi);

        try {
            boolean success = userDAO.changePassword(currentUser.getId(), newHash);

            if (success) {
                // Cập nhật lại thông tin user hiện tại trong phiên làm việc
                currentUser.setPassword(newHash);
                return "SUCCESS";
            } else {
                return "Lỗi hệ thống! Không thể đổi mật khẩu.";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[UserController] Loi doi mat khau", e);
            return "Lỗi kết nối cơ sở dữ liệu!";
        }
    }

    /**
     * Controller Dang ky
     * 
     * @param username
     * @param password
     * @param userRole
     * @return
     */
    public String register(String username, String password, String userRole) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                userRole == null || userRole.trim().isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }

        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }

        try {
            boolean success = userDAO.registerUser(username, password, userRole);

            if (success) {
                return "SUCCESS";
            } else {
                return "Lỗi hệ thống! Không thể đăng ký.";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[UserController] Loi dang ky", e);

            String msg = e.getMessage();
            if (msg != null && (msg.contains("Duplicate entry") || e.getMessage().contains("UNIQUE constraint"))) {
                return "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.";
            }

            return "Lỗi Truy vấn Cơ sở dữ liệu";
        }
    }
}