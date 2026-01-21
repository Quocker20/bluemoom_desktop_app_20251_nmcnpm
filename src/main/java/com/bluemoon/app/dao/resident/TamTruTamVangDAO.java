package com.bluemoon.app.dao.resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.ResidencyRecord;
import com.bluemoon.app.util.DatabaseConnector;

public class TamTruTamVangDAO {

    private static final Logger logger = Logger.getLogger(TamTruTamVangDAO.class.getName());

    /**
     * Thêm mới bản ghi tạm trú/tạm vắng
     * 
     * @param tttv Đối tượng TamTruTamVang
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean insert(ResidencyRecord tttv) throws SQLException {
        String sql = "INSERT INTO TAM_TRU_TAM_VANG (MaNhanKhau, LoaiHinh, TuNgay, DenNgay, LyDo) VALUES (?, ?, ?, ?, ?)";
        logger.info("[TAMTRUTAMVANGDAO] Insert ban ghi moi");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tttv.getResidentId());
            pstmt.setString(2, tttv.getType());
            pstmt.setDate(3, new java.sql.Date(tttv.getStartDate().getTime()));

            if (tttv.getEndDate() != null) {
                pstmt.setDate(4, new java.sql.Date(tttv.getEndDate().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setString(5, tttv.getReason());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TAMTRUTAMVANGDAO] Loi insert", e);
            throw e;
        }
    }

    /**
     * Lấy danh sách tất cả tạm trú tạm vắng
     * 
     * @return List<TamTruTamVang>
     * @throws SQLException lỗi truy vấn
     */
    public List<ResidencyRecord> getAll() throws SQLException {
        List<ResidencyRecord> list = new ArrayList<>();
        String sql = "SELECT t.*, n.HoTen FROM TAM_TRU_TAM_VANG t " +
                "JOIN NHAN_KHAU n ON t.MaNhanKhau = n.MaNhanKhau " +
                "ORDER BY t.MaTTTV ASC";
        logger.info("[TAMTRUTAMVANGDAO] Get All");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TAMTRUTAMVANGDAO] Loi getAll", e);
            throw e;
        }
        return list;
    }

    /**
     * Lấy danh sách theo loại hình
     * 
     * @param loaiHinh Loại hình
     * @return List<TamTruTamVang>
     * @throws SQLException lỗi truy vấn
     */
    public List<ResidencyRecord> getByLoaiHinh(String loaiHinh) throws SQLException {
        List<ResidencyRecord> list = new ArrayList<>();
        String sql = "SELECT t.*, n.HoTen FROM TAM_TRU_TAM_VANG t " +
                "JOIN NHAN_KHAU n ON t.MaNhanKhau = n.MaNhanKhau " +
                "WHERE t.LoaiHinh = ? ORDER BY t.MaTTTV ASC";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loaiHinh);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TAMTRUTAMVANGDAO] Loi getByLoaiHinh", e);
            throw e;
        }
        return list;
    }

    /**
     * Tìm kiếm theo tên nhân khẩu
     * 
     * @param tenNhanKhau Tên nhân khẩu
     * @return List<TamTruTamVang>
     * @throws SQLException lỗi truy vấn
     */
    public List<ResidencyRecord> getByHoTen(String tenNhanKhau) throws SQLException {
        List<ResidencyRecord> list = new ArrayList<>();
        String sql = "SELECT t.*, n.HoTen FROM TAM_TRU_TAM_VANG t " +
                "JOIN NHAN_KHAU n ON t.MaNhanKhau = n.MaNhanKhau " +
                "WHERE n.HoTen LIKE ? ORDER BY t.MaTTTV ASC";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + tenNhanKhau + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TAMTRUTAMVANGDAO] Loi getByHoTen", e);
            throw e;
        }
        return list;
    }

    // Helper method để map dữ liệu
    private ResidencyRecord mapRow(ResultSet rs) throws SQLException {
        ResidencyRecord t = new ResidencyRecord();
        t.setId(rs.getInt("MaTTTV"));
        t.setResidentId(rs.getInt("MaNhanKhau"));
        t.setType(rs.getString("LoaiHinh"));
        t.setStartDate(rs.getDate("TuNgay"));
        t.setEndDate(rs.getDate("DenNgay"));
        t.setReason(rs.getString("LyDo"));
        t.setResidentName(rs.getString("HoTen"));
        return t;
    }

    /**
     * Xóa các tạm trú tạm vắng hết hạn trước ngày chỉ định
     * 
     * @param date Ngày hết hạn
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean deleteByExpirationDate(java.sql.Date date) throws SQLException {
        String sql = "DELETE FROM TAM_TRU_TAM_VANG " +
                "WHERE DenNgay IS NOT NULL " +
                "AND DenNgay < ?";
        logger.info("[TAMTRUTAMVANGDAO] Clean up expired data");
        int rowAffected;

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            rowAffected = pstmt.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TAMTRUTAMVANGDAO] Loi deleteByExpirationDate", e);
            throw e;
        }
    }
}