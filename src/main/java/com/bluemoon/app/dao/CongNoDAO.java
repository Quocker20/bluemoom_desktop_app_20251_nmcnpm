package com.bluemoon.app.dao;

import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CongNoDAO {

    // Kiểm tra xem tháng này đã chốt công nợ chưa (để tránh tạo trùng)
    public boolean checkCalculated(int thang, int nam) {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE Thang = ? AND Nam = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Thêm mới 1 công nợ (Dùng trong vòng lặp tính phí)
    public void insert(CongNo cn) {
        String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cn.getMaHo());
            pstmt.setInt(2, cn.getMaKhoanPhi());
            pstmt.setInt(3, cn.getThang());
            pstmt.setInt(4, cn.getNam());
            pstmt.setDouble(5, cn.getSoTienPhaiDong());
            pstmt.setDouble(6, 0); // Mới tạo thì đã đóng = 0
            pstmt.setInt(7, 0);    // 0: Chưa xong
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Lấy danh sách công nợ (kết hợp tên khoản phí để hiển thị)
    public List<CongNo> getAll(int thang, int nam) {
        List<CongNo> list = new ArrayList<>();
        // JOIN bảng KHOAN_PHI để lấy tên
        String sql = "SELECT cn.*, kp.TenKhoanPhi FROM CONG_NO cn " +
                     "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                     "WHERE cn.Thang = ? AND cn.Nam = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setMaHo(rs.getInt("MaHo"));
                    cn.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                    cn.setTenKhoanPhi(rs.getString("TenKhoanPhi")); // Lấy từ JOIN
                    cn.setThang(rs.getInt("Thang"));
                    cn.setNam(rs.getInt("Nam"));
                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    // Convert int -> boolean (1 là Done, 0 là Not Done)
                    cn.setDone(rs.getInt("TrangThai") == 1);
                    list.add(cn);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Cập nhật số tiền đã đóng (Khi thanh toán)
    public boolean updatePayment(int maCongNo, double soTienMoiDaDong) {
        // Logic: Nếu Đã đóng >= Phải đóng thì TrangThai = 1 (Done)
        String sql = "UPDATE CONG_NO SET SoTienDaDong = ?, TrangThai = CASE WHEN SoTienDaDong >= SoTienPhaiDong THEN 1 ELSE 0 END WHERE MaCongNo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, soTienMoiDaDong);
            pstmt.setInt(2, maCongNo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public CongNo getById(int id) {
        // Hàm lấy chi tiết 1 công nợ (để hiển thị lên Dialog thanh toán)
        String sql = "SELECT cn.*, kp.TenKhoanPhi FROM CONG_NO cn JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi WHERE MaCongNo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setMaHo(rs.getInt("MaHo"));
                    cn.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                    cn.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    cn.setDone(rs.getInt("TrangThai") == 1);
                    return cn;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}