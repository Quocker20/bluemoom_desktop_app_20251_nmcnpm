package com.bluemoon.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.GiaoDich;
import com.bluemoon.app.util.DatabaseConnector;

public class GiaoDichDAO {

    private static final Logger logger = Logger.getLogger(GiaoDichDAO.class.getName());

    /**
     * Them mot giao dich vao CSDL
     * 
     * @param gd Đối tượng giao dịch
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean insert(GiaoDich gd) throws SQLException {
        String sql = "INSERT INTO GIAO_DICH_NOP_TIEN (MaHo, MaKhoanPhi, SoTien, NguoiNop, GhiChu, NgayNop) VALUES (?, ?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[GIAODICHDAO] Bat dau Insert giao dich");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gd.getMaHo());
            pstmt.setInt(2, gd.getMaKhoanPhi());
            pstmt.setDouble(3, gd.getSoTien());
            pstmt.setString(4, gd.getNguoiNop());
            pstmt.setString(5, gd.getGhiChu());
            pstmt.setTimestamp(6, gd.getNgayNop());

            if (pstmt.executeUpdate() > 0) {
                logger.log(Level.INFO, "[GIAODICHDAO] Insert thanh cong");
                return true;
            } else {
                logger.log(Level.INFO, "[GIAODICHDAO] Insert that bai");
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[GIAODICHDAO] Loi SQL INSERT", e);
            throw e;
        }
    }

    /**
     * Lay lich su giao dich cua tat ca ho khau
     * 
     * @return List<GiaoDich>
     * @throws SQLException lỗi truy vấn
     */
    public List<GiaoDich> getAll() throws SQLException {
        return getAllBySoCanHo("");
    }

    /**
     * Lay lich su giao dich cua mot so ho Khau theo thoi gian
     * 
     * @param soCanHo Số căn hộ cần lọc
     * @return List<GiaoDich>
     * @throws SQLException lỗi truy vấn
     */
    public List<GiaoDich> getAllBySoCanHo(String soCanHo) throws SQLException {
        List<GiaoDich> list = new ArrayList<>();
        String sql = "SELECT gd.*, kp.TenKhoanPhi, hk.SoCanHo FROM GIAO_DICH_NOP_TIEN gd " +
                "JOIN KHOAN_PHI kp ON gd.MaKhoanPhi = kp.MaKhoanPhi " +
                "JOIN HO_KHAU hk ON gd.MaHo = hk.MaHo " +
                "WHERE hk.SoCanHo LIKE ? " +
                "ORDER BY hk.SoCanHo ASC, gd.NgayNop DESC";

        logger.log(Level.INFO, "[GIAODICHDAO] Bat dau truy van lich su giao dich cho tu khoa: {0}", soCanHo);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + soCanHo + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GiaoDich gd = new GiaoDich();
                    gd.setMaGiaoDich(rs.getInt("MaGiaoDich"));
                    gd.setMaHo(rs.getInt("MaHo"));
                    gd.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                    gd.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    gd.setSoCanHo(rs.getString("SoCanHo"));
                    gd.setNgayNop(rs.getTimestamp("NgayNop"));
                    gd.setSoTien(rs.getDouble("SoTien"));
                    gd.setNguoiNop(rs.getString("NguoiNop"));
                    gd.setGhiChu(rs.getString("GhiChu"));

                    list.add(gd);
                }
            }
            logger.log(Level.INFO, "[GIAODICHDAO] Truy van thanh cong. Tim thay {0} giao dich", list.size());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[GIAODICHDAO] Loi SQL getAll", e);
            throw e;
        }

        return list;
    }
}