package com.bluemoon.app.dao;

import com.bluemoon.app.model.KhoanPhi;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhoanPhiDAO {

    public List<KhoanPhi> getAll() {
        List<KhoanPhi> list = new ArrayList<>();
        String sql = "SELECT * FROM KHOAN_PHI";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                KhoanPhi kp = new KhoanPhi();
                kp.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                kp.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                kp.setDonGia(rs.getDouble("DonGia"));
                kp.setDonViTinh(rs.getString("DonViTinh"));
                
                // Convert int (DB) -> boolean (Java)
                // DB: 0 là Bắt buộc (True), 1 là Tự nguyện (False)
                // Logic: Nếu LoaiPhi == 0 thì isMandatory = true
                kp.setMandatory(rs.getInt("LoaiPhi") == 0);
                
                list.add(kp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean update(KhoanPhi kp) {
        String sql = "UPDATE KHOAN_PHI SET TenKhoanPhi=?, DonGia=?, DonViTinh=?, LoaiPhi=? WHERE MaKhoanPhi=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kp.getTenKhoanPhi());
            pstmt.setDouble(2, kp.getDonGia());
            pstmt.setString(3, kp.getDonViTinh());
            
            // Convert boolean (Java) -> int (DB)
            // True (Bắt buộc) -> 0, False (Tự nguyện) -> 1
            pstmt.setInt(4, kp.isMandatory() ? 0 : 1);
            
            pstmt.setInt(5, kp.getMaKhoanPhi());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Thêm khoản thu tự nguyện mới
    public boolean insert(KhoanPhi kp) {
        String sql = "INSERT INTO KHOAN_PHI (TenKhoanPhi, DonGia, DonViTinh, LoaiPhi) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kp.getTenKhoanPhi());
            pstmt.setDouble(2, kp.getDonGia());
            pstmt.setString(3, kp.getDonViTinh());
            pstmt.setInt(4, kp.isMandatory() ? 0 : 1);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}