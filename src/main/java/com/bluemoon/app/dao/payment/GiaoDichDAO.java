package com.bluemoon.app.dao.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.Payment;
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
    public boolean insert(Payment gd) throws SQLException {
        String sql = "INSERT INTO GIAO_DICH_NOP_TIEN (MaHo, MaKhoanPhi, SoTien, NguoiNop, GhiChu, NgayNop) VALUES (?, ?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[GIAODICHDAO] Bat dau Insert giao dich");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gd.getHouseholdId());
            pstmt.setInt(2, gd.getFeeId());
            pstmt.setDouble(3, gd.getAmount());
            pstmt.setString(4, gd.getPayerName());
            pstmt.setString(5, gd.getNote());
            pstmt.setTimestamp(6, gd.getPaymentDate());

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
    public List<Payment> getAll() throws SQLException {
        return getAllBySoCanHo("");
    }

    /**
     * Lay lich su giao dich cua mot so ho Khau theo thoi gian
     * 
     * @param soCanHo Số căn hộ cần lọc
     * @return List<GiaoDich>
     * @throws SQLException lỗi truy vấn
     */
    public List<Payment> getAllBySoCanHo(String soCanHo) throws SQLException {
        List<Payment> list = new ArrayList<>();
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
                    Payment gd = new Payment();
                    gd.setId(rs.getInt("MaGiaoDich"));
                    gd.setHouseholdId(rs.getInt("MaHo"));
                    gd.setFeeId(rs.getInt("MaKhoanPhi"));
                    gd.setFeeName(rs.getString("TenKhoanPhi"));
                    gd.setRoomNumber(rs.getString("SoCanHo"));
                    gd.setPaymentDate(rs.getTimestamp("NgayNop"));
                    gd.setAmount(rs.getDouble("SoTien"));
                    gd.setPayerName(rs.getString("NguoiNop"));
                    gd.setNote(rs.getString("GhiChu"));

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