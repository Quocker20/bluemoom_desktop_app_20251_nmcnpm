package com.bluemoon.app.dao.dancu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.util.DatabaseConnector;

public class HoKhauDAO {

    private static final Logger logger = Logger.getLogger(HoKhauDAO.class.getName());

    /**
     * Lấy tất cả bản ghi Hộ khẩu (JOIN với bảng CAN_HO để lấy Diện tích)
     */
    public List<HoKhau> getAll() throws SQLException {
        List<HoKhau> list = new ArrayList<>();
        // [MỚI] JOIN bảng để lấy diện tích chính xác từ CAN_HO
        String sql = "SELECT hk.*, ch.DienTich FROM HO_KHAU hk " +
                     "JOIN CAN_HO ch ON hk.SoCanHo = ch.SoCanHo " +
                     "WHERE hk.IsDeleted = 0 " +
                     "ORDER BY hk.SoCanHo ASC";

        logger.log(Level.INFO, "[HOKHAUDAO] Bat dau truy van CSDL...");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                HoKhau hk = new HoKhau();
                hk.setMaHo(rs.getInt("MaHo"));
                hk.setSoCanHo(rs.getString("SoCanHo"));
                hk.setTenChuHo(rs.getString("TenChuHo"));
                hk.setDienTich(rs.getDouble("DienTich")); // Lấy từ bảng CAN_HO
                hk.setSdt(rs.getString("SDT"));
                hk.setNgayTao(rs.getDate("NgayTao"));
                list.add(hk);
            }
            logger.log(Level.INFO, "[HOKHAUDAO] Tim thay {0} ho khau", list.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi truy van getAll", e);
            throw e;
        }
        return list;
    }

    /**
     * Thêm mới hộ khẩu kèm chủ hộ (Transaction)
     * [MỚI] Không insert DienTich nữa
     */
    public boolean addHoKhauWithChuHo(HoKhau hk, NhanKhau chuHo) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtHk = null;
        PreparedStatement pstmtNk = null;

        // Bỏ cột DienTich trong câu lệnh Insert
        String sqlHk = "INSERT INTO HO_KHAU (SoCanHo, TenChuHo, SDT) VALUES (?, ?, ?)";
        String sqlNk = "INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Insert Hộ khẩu
            pstmtHk = conn.prepareStatement(sqlHk, Statement.RETURN_GENERATED_KEYS);
            pstmtHk.setString(1, hk.getSoCanHo());
            pstmtHk.setString(2, hk.getTenChuHo());
            pstmtHk.setString(3, hk.getSdt());
            
            int affectedRows = pstmtHk.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating HoKhau failed, no rows affected.");
            }

            // Lấy ID Hộ khẩu vừa sinh ra
            int maHo = 0;
            try (ResultSet generatedKeys = pstmtHk.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maHo = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating HoKhau failed, no ID obtained.");
                }
            }

            // 2. Insert Chủ hộ (Nhân khẩu)
            pstmtNk = conn.prepareStatement(sqlNk);
            pstmtNk.setInt(1, maHo);
            pstmtNk.setString(2, chuHo.getHoTen());
            pstmtNk.setDate(3, new java.sql.Date(chuHo.getNgaySinh().getTime()));
            pstmtNk.setString(4, chuHo.getGioiTinh());
            pstmtNk.setString(5, chuHo.getCccd());
            pstmtNk.setString(6, "Chủ hộ"); // Mặc định là chủ hộ
            pstmtNk.executeUpdate();

            conn.commit(); // Xác nhận Transaction
            
            // Lưu ý: Trigger trong DB sẽ tự động update TrangThai phòng thành 1
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warning("[HOKHAUDAO] Transaction Rollback due to error: " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi addHoKhauWithChuHo", e);
            throw e;
        } finally {
            if (pstmtNk != null) pstmtNk.close();
            if (pstmtHk != null) pstmtHk.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Kiểm tra số căn hộ đã tồn tại chưa (Chỉ tính những hộ chưa xóa)
     */
    public boolean checkExist(String soCanHo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM HO_KHAU WHERE SoCanHo = ? AND IsDeleted = 0";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soCanHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Cập nhật thông tin hộ khẩu
     */
    public boolean update(HoKhau hk) throws SQLException {
        // Chỉ cho phép sửa Tên chủ hộ và SĐT
        // Không cho sửa Số căn hộ (vì liên quan logic phòng trống phức tạp)
        String sql = "UPDATE HO_KHAU SET TenChuHo=?, SDT=? WHERE MaHo=?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hk.getTenChuHo());
            pstmt.setString(2, hk.getSdt());
            pstmt.setInt(3, hk.getMaHo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi update", e);
            throw e;
        }
    }

    /**
     * Xóa mềm hộ khẩu (Trigger DB sẽ tự trả phòng về trạng thái Trống)
     */
    public boolean softDelete(int maHo) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra công nợ (Nếu còn nợ chưa xong thì không cho xóa)
            // Logic này có thể để Controller kiểm tra hoặc DAO kiểm tra
            // Ở đây ta cứ xóa mềm
            
            String sqlDeleteHo = "UPDATE HO_KHAU SET IsDeleted = 1 WHERE MaHo = ?";
            String sqlDeleteNhanKhau = "UPDATE NHAN_KHAU SET IsDeleted = 1 WHERE MaHo = ?";

            try (PreparedStatement pst1 = conn.prepareStatement(sqlDeleteHo);
                 PreparedStatement pst2 = conn.prepareStatement(sqlDeleteNhanKhau)) {
                
                pst1.setInt(1, maHo);
                int row1 = pst1.executeUpdate();

                pst2.setInt(1, maHo);
                pst2.executeUpdate();

                if (row1 > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Tìm kiếm hộ khẩu (JOIN với CAN_HO)
     */
    public List<HoKhau> search(String keyword) throws SQLException {
        List<HoKhau> list = new ArrayList<>();
        String sql = "SELECT hk.*, ch.DienTich FROM HO_KHAU hk " +
                     "JOIN CAN_HO ch ON hk.SoCanHo = ch.SoCanHo " +
                     "WHERE hk.IsDeleted = 0 " +
                     "AND (hk.TenChuHo LIKE ? OR hk.SoCanHo LIKE ?) " +
                     "ORDER BY hk.SoCanHo ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

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
        }
        return list;
    }
    
    // Các hàm getById, getBySoCanHo cũng cần JOIN tương tự nếu bạn dùng đến
    public HoKhau getById(int id) throws SQLException {
        HoKhau hk = null;
        String sql = "SELECT hk.*, ch.DienTich FROM HO_KHAU hk JOIN CAN_HO ch ON hk.SoCanHo = ch.SoCanHo WHERE hk.MaHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    hk = new HoKhau();
                    hk.setMaHo(rs.getInt("MaHo"));
                    hk.setSoCanHo(rs.getString("SoCanHo"));
                    hk.setTenChuHo(rs.getString("TenChuHo"));
                    hk.setDienTich(rs.getDouble("DienTich"));
                    hk.setSdt(rs.getString("SDT"));
                }
            }
        }
        return hk;
    }
    
    public HoKhau getBySoCanHo(String soCanHo) throws SQLException {
        HoKhau hk = null;
        String sql = "SELECT hk.*, ch.DienTich FROM HO_KHAU hk JOIN CAN_HO ch ON hk.SoCanHo = ch.SoCanHo WHERE hk.SoCanHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soCanHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    hk = new HoKhau();
                    hk.setMaHo(rs.getInt("MaHo"));
                    hk.setSoCanHo(rs.getString("SoCanHo"));
                    hk.setTenChuHo(rs.getString("TenChuHo"));
                    hk.setDienTich(rs.getDouble("DienTich")); // Lấy từ DB
                    hk.setSdt(rs.getString("SDT"));
                }
            }
        }
        return hk;
    }
}