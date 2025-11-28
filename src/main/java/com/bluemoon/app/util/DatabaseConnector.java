package com.bluemoon.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp tiện ích quản lý kết nối đến Cơ sở dữ liệu MySQL.
 * (Phục vụ toàn bộ hệ thống)
 */
public class DatabaseConnector {
    // Thông tin kết nối
    private static final String URL = "jdbc:mysql://localhost:3306/bluemoon_db";
    private static final String USER = "root"; 
    private static final String PASS = "";


    /**
     * Thiết lập và lấy một đối tượng kết nối (Connection) đến CSDL.
     * * @return Connection - Đối tượng kết nối nếu thành công, hoặc null nếu thất bại.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Kết nối tới MySQL
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Kết nối CSDL thành công!");
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
        }
        return conn;
    }
}
