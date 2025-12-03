package com.bluemoon.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý kết nối tới MySQL Database.
 */
public class DatabaseConnector {
    // Thêm useSSL=false và characterEncoding=UTF-8 để tránh lỗi font và cảnh báo SSL
    private static final String URL = "jdbc:mysql://localhost:3306/bluemoon_db?useSSL=false&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Cập nhật mật khẩu MySQL của bạn tại đây

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // Trong dự án thực tế, nên dùng Logger thay vì System.err
            System.err.println("[DB Error] Không thể kết nối CSDL: " + e.getMessage());
            return null;
        }
    }
}