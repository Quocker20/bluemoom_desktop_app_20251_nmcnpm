package com.bluemoon.app.dao.guixe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bluemoon.app.model.PhuongTien;
import com.bluemoon.app.util.DatabaseConnector;

public class PhuongTienDAO {

    /**
     * Lấy danh sách tất cả xe đang gửi (TrangThai = 1)
     * Kèm theo thông tin Số phòng và Chủ hộ
     */
    public List<PhuongTien> getAll() throws SQLException {
        List<PhuongTien> list = new ArrayList<>();
        String sql = "SELECT pt.*, hk.SoCanHo, hk.TenChuHo " +
                     "FROM PHUONG_TIEN pt " +
                     "JOIN HO_KHAU hk ON pt.MaHo = hk.MaHo " +
                     "WHERE pt.TrangThai = 1 " + 
                     "ORDER BY hk.SoCanHo ASC";

        // Try-with-resources: Tự động đóng rs, pstmt, và connection
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhuongTien pt = new PhuongTien(
                    rs.getInt("MaPhuongTien"),
                    rs.getInt("MaHo"),
                    rs.getString("BienSo"),
                    rs.getInt("LoaiXe"),
                    rs.getInt("TrangThai"),
                    rs.getString("SoCanHo"),
                    rs.getString("TenChuHo")
                );
                list.add(pt);
            }
        }
        return list;
    }

    /**
     * Thêm phương tiện mới
     */
    public boolean add(PhuongTien pt) throws SQLException {
        String sql = "INSERT INTO PHUONG_TIEN (MaHo, BienSo, LoaiXe, TrangThai) VALUES (?, ?, ?, 1)";
        
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
             
            pstmt.setInt(1, pt.getMaHo());
            pstmt.setString(2, pt.getBienSo());
            pstmt.setInt(3, pt.getLoaiXe());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa mềm phương tiện (Chuyển trạng thái về 0)
     */
    public boolean softDelete(String bienSo) throws SQLException {
        String sql = "UPDATE PHUONG_TIEN SET TrangThai = 0 WHERE BienSo = ?";
        
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
             
            pstmt.setString(1, bienSo);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Kiểm tra biển số xe đã tồn tại hay chưa (Chỉ check xe đang gửi)
     */
    public boolean checkExist(String bienSo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PHUONG_TIEN WHERE BienSo = ? AND TrangThai = 1";
        
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
             
            pstmt.setString(1, bienSo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Tìm kiếm xe theo Biển số hoặc Số phòng
     */
    public List<PhuongTien> search(String keyword) throws SQLException {
        List<PhuongTien> list = new ArrayList<>();
        String sql = "SELECT pt.*, hk.SoCanHo, hk.TenChuHo " +
                     "FROM PHUONG_TIEN pt " +
                     "JOIN HO_KHAU hk ON pt.MaHo = hk.MaHo " +
                     "WHERE pt.TrangThai = 1 " +
                     "AND (pt.BienSo LIKE ? OR hk.SoCanHo LIKE ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
             
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PhuongTien pt = new PhuongTien(
                        rs.getInt("MaPhuongTien"),
                        rs.getInt("MaHo"),
                        rs.getString("BienSo"),
                        rs.getInt("LoaiXe"),
                        rs.getInt("TrangThai"),
                        rs.getString("SoCanHo"),
                        rs.getString("TenChuHo")
                    );
                    list.add(pt);
                }
            }
        }
        return list;
    }

    /**
     * Cập nhật biển số xe
     */
    public boolean update(PhuongTien pt) throws SQLException {
        String sql = "UPDATE PHUONG_TIEN SET BienSo = ? WHERE MaPhuongTien = ?";
        
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
             
            pstmt.setString(1, pt.getBienSo());
            pstmt.setInt(2, pt.getMaPhuongTien());
            
            return pstmt.executeUpdate() > 0;
        }
    }
}