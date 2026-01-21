package com.bluemoon.app.dao.resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Apartment;
import com.bluemoon.app.util.DatabaseConnector;

public class CanHoDAO {
    private static final Logger logger = Logger.getLogger(CanHoDAO.class.getName());

    /**
     * Lấy danh sách các căn hộ còn trống (TrangThai = 0)
     * Để đổ vào ComboBox khi thêm hộ khẩu mới.
     */
    public List<Apartment> getDanhSachPhongTrong() throws SQLException {
        List<Apartment> list = new ArrayList<>();
        String sql = "SELECT * FROM CAN_HO WHERE TrangThai = 0 ORDER BY SoCanHo ASC";
        
        logger.info("[CanHoDAO] Lay danh sach phong trong...");
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Apartment ch = new Apartment();
                ch.setId(rs.getInt("MaCanHo"));
                ch.setRoomNumber(rs.getString("SoCanHo"));
                ch.setArea(rs.getDouble("DienTich"));
                ch.setStatus(rs.getInt("TrangThai"));
                list.add(ch);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CanHoDAO] Loi lay danh sach phong trong", e);
            throw e;
        }
        return list;
    }
    
    /**
     * Lấy thông tin căn hộ theo số phòng (Dùng khi cần check lại)
     */
    public Apartment getBySoPhong(String soPhong) throws SQLException {
        String sql = "SELECT * FROM CAN_HO WHERE SoCanHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soPhong);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Apartment(
                        rs.getInt("MaCanHo"),
                        rs.getString("SoCanHo"),
                        rs.getDouble("DienTich"),
                        rs.getInt("TrangThai")
                    );
                }
            }
        }
        return null;
    }
}