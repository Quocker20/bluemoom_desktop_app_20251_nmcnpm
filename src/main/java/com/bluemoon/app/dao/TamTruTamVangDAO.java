package com.bluemoon.app.dao;

import com.bluemoon.app.model.TamTruTamVang;
import com.bluemoon.app.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TamTruTamVangDAO {

    public boolean insert(TamTruTamVang tttv) {
        String sql = "INSERT INTO TAM_TRU_TAM_VANG (MaNhanKhau, LoaiHinh, TuNgay, DenNgay, LyDo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tttv.getMaNhanKhau());
            pstmt.setString(2, tttv.getLoaiHinh());
            pstmt.setDate(3, new java.sql.Date(tttv.getTuNgay().getTime()));

            if (tttv.getDenNgay() != null) {
                pstmt.setDate(4, new java.sql.Date(tttv.getDenNgay().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setString(5, tttv.getLyDo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TamTruTamVang> getAll() {
        List<TamTruTamVang> list = new ArrayList<>();
        String sql = "SELECT t.*, n.HoTen FROM TAM_TRU_TAM_VANG t " +
                "JOIN NHAN_KHAU n ON t.MaNhanKhau = n.MaNhanKhau " +
                "ORDER BY t.MaTTTV ASC";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TamTruTamVang> getByLoaiHinh(String loaiHinh) {
        List<TamTruTamVang> list = new ArrayList<>();
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
            e.printStackTrace();
        }
        return list;
    }

    public List<TamTruTamVang> getByHoTen(String tenNhanKhau) {
        List<TamTruTamVang> list = new ArrayList<>();
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
            e.printStackTrace();
        }
        return list;
    }

    // Helper method để map dữ liệu, tránh lặp code
    private TamTruTamVang mapRow(ResultSet rs) throws SQLException {
        TamTruTamVang t = new TamTruTamVang();
        t.setMaTTTV(rs.getInt("MaTTTV"));
        t.setMaNhanKhau(rs.getInt("MaNhanKhau"));
        t.setLoaiHinh(rs.getString("LoaiHinh"));
        t.setTuNgay(rs.getDate("TuNgay"));
        t.setDenNgay(rs.getDate("DenNgay"));
        t.setLyDo(rs.getString("LyDo"));
        t.setHoTenNhanKhau(rs.getString("HoTen")); // Map vào biến mới hoTenNhanKhau
        return t;
    }

    /**
     * Xoa cac tam tru tam vang het han truoc ngay ??-??-????
     * 
     * @param date
     *             return true: Xoa thanh cong/ false: Xoa that bai
     */
    public boolean deleteByExpirationDate(java.sql.Date date) {
        String sql = "DELETE FROM TAM_TRU_TAM_VANG " +
                "WHERE DenNgay IS NOT NULL " +
                "AND DenNgay < ?";
        int rowAffected;
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            rowAffected = pstmt.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa Tạm trú Tạm Vắng hết hạn: " + e.getMessage());

            return false;
        }
    }
}