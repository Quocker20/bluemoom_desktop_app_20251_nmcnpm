package com.bluemoon.app;

import com.bluemoon.app.view.LoginFrame;
import com.bluemoon.app.util.DatabaseConnector;
import com.bluemoon.app.controller.TamTruTamVangController;

import javax.swing.*;
import java.sql.Connection;

public class App {

    public static void main(String[] args) {
        // 1. Thiết lập giao diện (Look & Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); 
        }

        // 2. Chạy ứng dụng trên luồng sự kiện (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            checkDatabaseAndLaunch();
            
            // 3. Mở màn hình Đăng nhập (Phải mở trên EDT)
            LoginFrame loginScreen = new LoginFrame();
            loginScreen.setVisible(true);
        });
    }

    private static void checkDatabaseAndLaunch() {
        System.out.println("Đang khởi động BlueMoon System...");
        
        try (Connection conn = DatabaseConnector.getConnection()) { // Sử dụng try-with-resources
            if (conn == null) {
                JOptionPane.showMessageDialog(null, 
                    "LỖI KHỞI ĐỘNG:\nKhông thể kết nối đến Cơ sở dữ liệu MySQL.\nVui lòng kiểm tra lại XAMPP/MySQL Server.", 
                    "Lỗi hệ thống", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("Kết nối CSDL thành công.");

                runCleanupInBackground();

                System.out.println("Đang mở giao diện...");
                
                // Lưu ý: conn.close() không cần thiết vì đã dùng try-with-resources.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đã chuyển LoginFrame xuống cuối main() trong invokeLater() để chạy sau khi kiểm tra CSDL
    }
    
    /**
     * Khởi tạo luồng nền để thực hiện thao tác xóa CSDL.
     * Điều này đảm bảo giao diện không bị treo.
     */
    private static void runCleanupInBackground() {
        // Tạo và khởi chạy một luồng mới
        new Thread(() -> {
            System.out.println("Bắt đầu dọn dẹp Tạm trú/Tạm vắng hết hạn trong luồng nền...");
            
            try {
                TamTruTamVangController controller = new TamTruTamVangController();
                boolean success = controller.deleteExpiredRecordsToday();
                
                if (success) {
                    System.out.println("Dọn dẹp CSDL thành công: Đã xóa các hồ sơ hết hạn.");
                } else {
                    // Cảnh báo nếu không xóa được (có thể do không có records hoặc lỗi CSDL)
                    System.out.println("Dọn dẹp CSDL hoàn tất: Không có hồ sơ hết hạn nào được xóa (hoặc xảy ra lỗi nhỏ).");
                }
            } catch (Exception e) {
                System.err.println("LỖI NGHIÊM TRỌNG: Lỗi khi chạy dọn dẹp CSDL nền.");
                e.printStackTrace();
            }
        }).start(); // Bắt đầu luồng nền
    }
}