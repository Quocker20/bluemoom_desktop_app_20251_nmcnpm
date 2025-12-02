package com.bluemoon.app.dao;

import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO xử lý thao tác CSDL cho bảng HO_KHAU.
 */
public class HoKhauDAO {

    /**
     * Lấy danh sách tất cả Hộ khẩu.
     * @return List<HoKhau>
     */
    public List<HoKhau> getAll() {
        List<HoKhau> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_KHAU ORDER BY SoCanHo ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                HoKhau hk = new HoKhau();
                hk.setMaHo(rs.getInt("MaHo"));
                hk.setSoCanHo(rs.getString("SoCanHo"));
                hk.setTenChuHo(rs.getString("TenChuHo"));
                hk.setDienTich(rs.getDouble("DienTich"));
                hk.setSdt(rs.getString("SDT"));
                hk.setNgayTao(rs.getDate("NgayTao"));
                list.add(hk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách Hộ khẩu: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy số mã hộ lớn nhất hiện tại (số căn hộ)
     * @return String mã hộ lớn nhất, -1 nếu không có
     *
     */
    public int getMaxMaHo() {
        String sql = "SELECT MAX(MaHo) AS MaxMaHo FROM HO_KHAU";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("MaxMaHo");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy Mã hộ lớn nhất: " + e.getMessage());
        }
        return -1; // Trả về -1 nếu không có hộ khẩu nào
    }

    /**
     * Thêm mới một Hộ khẩu.
     * @param hk Đối tượng HoKhau cần thêm
     * @return true nếu thành công
     */
    public boolean add(HoKhau hk) {
        String sql = "INSERT INTO HO_KHAU (SoCanHo, TenChuHo, DienTich, SDT) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hk.getSoCanHo());
            pstmt.setString(2, hk.getTenChuHo());
            pstmt.setDouble(3, hk.getDienTich());
            pstmt.setString(4, hk.getSdt());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm Hộ khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin Hộ khẩu.
     * @param hk Đối tượng HoKhau đã sửa (cần có MaHo)
     * @return true nếu thành công
     */
    public boolean update(HoKhau hk) {
        String sql = "UPDATE HO_KHAU SET SoCanHo=?, TenChuHo=?, DienTich=?, SDT=? WHERE MaHo=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hk.getSoCanHo());
            pstmt.setString(2, hk.getTenChuHo());
            pstmt.setDouble(3, hk.getDienTich());
            pstmt.setString(4, hk.getSdt());
            pstmt.setInt(5, hk.getMaHo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật Hộ khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa một Hộ khẩu theo ID.
     * @param maHo ID của hộ cần xóa
     * @return true nếu thành công
     */
    public boolean delete(int maHo) {
        String sql = "DELETE FROM HO_KHAU WHERE MaHo=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maHo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa Hộ khẩu (Có thể do ràng buộc khóa ngoại): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Kiểm tra xem Số căn hộ đã tồn tại chưa.
     * @param soCanHo Số căn hộ cần check (VD: A-101)
     * @return true nếu đã tồn tại
     */
    public boolean checkExist(String soCanHo) {
        String sql = "SELECT COUNT(*) FROM HO_KHAU WHERE SoCanHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soCanHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm kiếm Hộ khẩu theo từ khóa.
     * @param keyword Từ khóa (Tên chủ hộ hoặc Số phòng)
     * @return List<HoKhau> kết quả
     */
    public List<HoKhau> search(String keyword) {
        List<HoKhau> list = new ArrayList<>();
        // Tìm kiếm gần đúng (LIKE %...%) trên cả 2 cột
        String sql = "SELECT * FROM HO_KHAU WHERE SoCanHo LIKE ? OR TenChuHo LIKE ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoKhau hk = new HoKhau();
                    hk.setMaHo(rs.getInt("MaHo"));
                    hk.setSoCanHo(rs.getString("SoCanHo"));
                    hk.setTenChuHo(rs.getString("TenChuHo"));
                    hk.setDienTich(rs.getDouble("DienTich"));
                    hk.setSdt(rs.getString("SDT"));
                    hk.setNgayTao(rs.getDate("NgayTao"));
                    list.add(hk);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm Hộ khẩu: " + e.getMessage());
        }
        return list;
    }
}