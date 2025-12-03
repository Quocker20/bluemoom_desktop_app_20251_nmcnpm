package com.bluemoon.app.dao;

import com.bluemoon.app.model.KhoanPhi;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhoanPhiDAO {

    // [MỚI] Hàm lấy tất cả không cần keyword (Dùng cho Controller tính phí tự động)
    public List<KhoanPhi> getAll() {
        return getAll("");
    }

    // Hàm tìm kiếm (Gộp logic để tránh lặp code)
    public List<KhoanPhi> getAll(String keyword) {
        List<KhoanPhi> list = new ArrayList<>();
        String sql = "SELECT * FROM KHOAN_PHI WHERE TenKhoanPhi LIKE ? ORDER BY MaKhoanPhi DESC";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    KhoanPhi kp = new KhoanPhi();
                    kp.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                    kp.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    kp.setDonGia(rs.getDouble("DonGia"));
                    kp.setDonViTinh(rs.getString("DonViTinh"));
                    kp.setLoaiPhi(rs.getInt("LoaiPhi"));
                    list.add(kp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(KhoanPhi kp) {
        String sql = "INSERT INTO KHOAN_PHI (TenKhoanPhi, DonGia, DonViTinh, LoaiPhi) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kp.getTenKhoanPhi());
            pstmt.setDouble(2, kp.getDonGia());
            pstmt.setString(3, kp.getDonViTinh());
            pstmt.setInt(4, kp.getLoaiPhi());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhoanPhi kp) {
        String sql = "UPDATE KHOAN_PHI SET TenKhoanPhi=?, DonGia=?, DonViTinh=?, LoaiPhi=? WHERE MaKhoanPhi=?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kp.getTenKhoanPhi());
            pstmt.setDouble(2, kp.getDonGia());
            pstmt.setString(3, kp.getDonViTinh());
            pstmt.setInt(4, kp.getLoaiPhi());
            pstmt.setInt(5, kp.getMaKhoanPhi());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM KHOAN_PHI WHERE MaKhoanPhi=?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}