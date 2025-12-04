package com.bluemoon.app;

import com.bluemoon.app.view.LoginFrame;
import com.bluemoon.app.util.DatabaseConnector;

import javax.swing.*;
import java.sql.Connection;

public class App {

    public static void main(String[] args) {
        // 1. Thiết lập giao diện (Look & Feel)
        // Cố gắng dùng giao diện đẹp nhất của hệ điều hành đang chạy (Windows/Mac/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(); // Nếu lỗi thì dùng giao diện Java mặc định, không sao cả
        }

        // 2. Chạy ứng dụng trên luồng sự kiện (Event Dispatch Thread)
        // Đây là quy tắc bắt buộc của Java Swing để tránh lỗi treo giao diện
        SwingUtilities.invokeLater(() -> {
            checkDatabaseAndLaunch();
        });
    }

    private static void checkDatabaseAndLaunch() {
        // Kiểm tra kết nối CSDL trước khi mở màn hình
        System.out.println("Đang khởi động BlueMoon System...");
        
        try {
            Connection conn = DatabaseConnector.getConnection();
            if (conn == null) {
                // Nếu không kết nối được, hiện thông báo lỗi nghiêm trọng
                JOptionPane.showMessageDialog(null, 
                    "LỖI KHỞI ĐỘNG:\nKhông thể kết nối đến Cơ sở dữ liệu MySQL.\nVui lòng kiểm tra lại XAMPP/MySQL Server.", 
                    "Lỗi hệ thống", 
                    JOptionPane.ERROR_MESSAGE);
                // Có thể chọn System.exit(1) nếu muốn tắt luôn, hoặc vẫn hiện Login để config
            } else {
                System.out.println("Kết nối CSDL thành công. Đang mở giao diện...");
                // Đóng kết nối kiểm tra (để tiết kiệm tài nguyên, các DAO sẽ tự mở lại khi cần)
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Mở màn hình Đăng nhập
        LoginFrame loginScreen = new LoginFrame();
        loginScreen.setVisible(true);
    }
}