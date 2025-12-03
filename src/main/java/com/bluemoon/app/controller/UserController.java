package com.bluemoon.app.controller;

import com.bluemoon.app.dao.UserDAO;
import com.bluemoon.app.model.User;
import com.bluemoon.app.util.SecurityUtil;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Xử lý đổi mật khẩu.
     * 
     * @return Chuỗi thông báo lỗi (nếu có) hoặc "SUCCESS" nếu thành công.
     */
    public String doiMatKhau(User currentUser, String passCu, String passMoi, String xacNhanPass) {
        // 1. Validate cơ bản
        if (passCu.isEmpty() || passMoi.isEmpty() || xacNhanPass.isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }

        // 2. Kiểm tra mật khẩu cũ có đúng không
        // Hash mật khẩu cũ người dùng nhập vào để so sánh với Hash trong Object User
        // hiện tại
        String currentHashInput = SecurityUtil.hashPassword(passCu);
        if (!currentHashInput.equals(currentUser.getMatKhau())) {
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
        boolean success = userDAO.changePassword(currentUser.getMaTK(), newHash);

        if (success) {
            // Cập nhật lại thông tin user hiện tại trong phiên làm việc
            currentUser.setMatKhau(newHash);
            return "SUCCESS";
        } else {
            return "Lỗi hệ thống! Không thể đổi mật khẩu.";
        }
    }
}