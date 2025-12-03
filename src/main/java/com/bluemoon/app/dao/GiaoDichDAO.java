package com.bluemoon.app.dao;

import com.bluemoon.app.model.GiaoDich;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;

public class GiaoDichDAO {

    public boolean insert(GiaoDich gd) {
        String sql = "INSERT INTO GIAO_DICH (MaHo, MaKhoanPhi, SoTien, NguoiNop, GhiChu, NgayNop) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, gd.getMaHo());
            pstmt.setInt(2, gd.getMaKhoanPhi());
            pstmt.setDouble(3, gd.getSoTien());
            pstmt.setString(4, gd.getNguoiNop());
            pstmt.setString(5, gd.getGhiChu());
            // Sử dụng thời gian hiện tại
            pstmt.setTimestamp(6, new java.sql.Timestamp(new java.util.Date().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}