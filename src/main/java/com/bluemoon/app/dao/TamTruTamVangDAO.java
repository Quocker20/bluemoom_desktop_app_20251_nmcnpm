package com.bluemoon.app.dao;

import com.bluemoon.app.model.TamTruTamVang;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TamTruTamVangDAO {

    /**
     * Thêm mới một bản ghi Tạm trú hoặc Tạm vắng.
     * 
     * @param tttv Đối tượng TamTruTamVang
     * @return true nếu thành công
     */
    public boolean insert(TamTruTamVang tttv) {
        String sql = "INSERT INTO TAM_TRU_TAM_VANG (MaNhanKhau, LoaiHinh, TuNgay, DenNgay, LyDo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tttv.getMaNhanKhau());
            pstmt.setString(2, tttv.getLoaiHinh());
            pstmt.setDate(3, new java.sql.Date(tttv.getTuNgay().getTime()));

            // Xử lý ngày kết thúc (có thể null)
            if (tttv.getDenNgay() != null) {
                pstmt.setDate(4, new java.sql.Date(tttv.getDenNgay().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setString(5, tttv.getLyDo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm Tạm trú/Tạm vắng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy lịch sử biến động của một nhân khẩu (Để hiển thị nếu cần).
     */
    public List<TamTruTamVang> getHistoryByNhanKhau(int maNhanKhau) {
        List<TamTruTamVang> list = new ArrayList<>();
        String sql = "SELECT * FROM TAM_TRU_TAM_VANG WHERE MaNhanKhau = ? ORDER BY TuNgay DESC";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNhanKhau);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TamTruTamVang t = new TamTruTamVang();
                    t.setMaTTTV(rs.getInt("MaTTTV"));
                    t.setMaNhanKhau(rs.getInt("MaNhanKhau"));
                    t.setLoaiHinh(rs.getString("LoaiHinh"));
                    t.setTuNgay(rs.getDate("TuNgay"));
                    t.setDenNgay(rs.getDate("DenNgay"));
                    t.setLyDo(rs.getString("LyDo"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lay danh sách tất cả bản ghi Tạm trú/Tạm vắng.
     * @return List<TamTruTamVang>
     */
    public List<TamTruTamVang> getAll() {
        List<TamTruTamVang> list = new ArrayList<>();
        String sql = "SELECT * FROM TAM_TRU_TAM_VANG ORDER BY MaTTTV ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TamTruTamVang t = new TamTruTamVang();
                t.setMaTTTV(rs.getInt("MaTTTV"));
                t.setMaNhanKhau(rs.getInt("MaNhanKhau"));
                t.setLoaiHinh(rs.getString("LoaiHinh"));
                t.setTuNgay(rs.getDate("TuNgay"));
                t.setDenNgay(rs.getDate("DenNgay"));
                t.setLyDo(rs.getString("LyDo"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách Tạm trú/Tạm vắng: " + e.getMessage());
        }
        return list;
    }
}