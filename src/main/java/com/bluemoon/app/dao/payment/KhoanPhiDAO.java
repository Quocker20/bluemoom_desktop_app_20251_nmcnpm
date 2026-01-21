package com.bluemoon.app.dao.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Fee;
import com.bluemoon.app.util.DatabaseConnector;

public class KhoanPhiDAO {

    private static final Logger logger = Logger.getLogger(KhoanPhiDAO.class.getName());

    /**
     * Lấy tất cả khoản phí đang hoạt động
     * 
     * @return List<KhoanPhi>
     * @throws SQLException lỗi truy vấn
     */
    public List<Fee> getAll() throws SQLException {
        return getAllActiveFee("");
    }

    /**
     * Lấy danh sách khoản phí theo từ khóa
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return List<KhoanPhi>
     * @throws SQLException lỗi truy vấn
     */
    public List<Fee> getAllActiveFee(String keyword) throws SQLException {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT * FROM KHOAN_PHI " +
                "WHERE TenKhoanPhi LIKE ? " +
                "AND TrangThai = 1 " +
                "ORDER BY MaKhoanPhi DESC";
        logger.log(Level.INFO, "[KHOANPHIDAO] Lay danh sach khoan phi voi keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Fee kp = new Fee();
                    kp.setId(rs.getInt("MaKhoanPhi"));
                    kp.setName(rs.getString("TenKhoanPhi"));
                    kp.setUnitPrice(rs.getDouble("DonGia"));
                    kp.setUnit(rs.getString("DonViTinh"));
                    kp.setType(rs.getInt("LoaiPhi"));
                    kp.setStatus(rs.getInt("TrangThai"));
                    list.add(kp);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[KHOANPHIDAO] Loi getAllActiveFee", e);
            throw e;
        }
        return list;
    }

    /**
     * Thêm mới khoản phí
     * 
     * @param kp Đối tượng KhoanPhi
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean insert(Fee kp) throws SQLException {
        String sql = "INSERT INTO KHOAN_PHI (TenKhoanPhi, DonGia, DonViTinh, LoaiPhi) VALUES (?, ?, ?, ?)";
        logger.log(Level.INFO, "[KHOANPHIDAO] Insert khoan phi: {0}", kp.getName());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kp.getName());
            pstmt.setDouble(2, kp.getUnitPrice());
            pstmt.setString(3, kp.getUnit());
            pstmt.setInt(4, kp.getType());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[KHOANPHIDAO] Loi insert", e);
            throw e;
        }
    }

    /**
     * Cập nhật khoản phí
     * 
     * @param kp Đối tượng KhoanPhi
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean update(Fee kp) throws SQLException {
        String sql = "UPDATE KHOAN_PHI SET TenKhoanPhi=?, DonGia=?, DonViTinh=?, LoaiPhi=? WHERE MaKhoanPhi=?";
        logger.log(Level.INFO, "[KHOANPHIDAO] Update khoan phi ID: {0}", kp.getId());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kp.getName());
            pstmt.setDouble(2, kp.getUnitPrice());
            pstmt.setString(3, kp.getUnit());
            pstmt.setInt(4, kp.getType());
            pstmt.setInt(5, kp.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[KHOANPHIDAO] Loi update", e);
            throw e;
        }
    }

    /**
     * Vô hiệu hóa khoản phí (Xóa mềm)
     * 
     * @param id Mã khoản phí
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean disable(int id) throws SQLException {
        String sql = "UPDATE KHOAN_PHI SET TrangThai = 0 WHERE MaKhoanPhi = ? ";
        logger.log(Level.INFO, "[KHOANPHIDAO] Disable khoan phi ID: {0}", id);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[KHOANPHIDAO] Loi disable", e);
            throw e;
        }
    }
}