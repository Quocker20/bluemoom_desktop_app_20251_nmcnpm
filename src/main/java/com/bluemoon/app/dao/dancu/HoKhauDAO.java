package com.bluemoon.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.util.AppConstants;
import com.bluemoon.app.util.DatabaseConnector;

public class HoKhauDAO {

    private static final Logger logger = Logger.getLogger(HoKhauDAO.class.getName());

    /**
     * Lay tat ca ban ghi HoKhau chua bi xoa mem
     * 
     * @return List<HoKhau>
     * @throws SQLException lỗi truy vấn
     */
    public List<HoKhau> getAll() throws SQLException {
        List<HoKhau> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_KHAU " +
                "WHERE IsDeleted = 0 " +
                "ORDER BY SoCanHo ASC";
        logger.log(Level.INFO, "[HOKHAUDAO] Bat dau truy van CSDL...");

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
            if (!list.isEmpty()) {
                logger.log(Level.INFO, "[HOKHAUDAO] Truy van thanh cong, ket qua: {0} ban ghi", list.size());
            } else {
                logger.log(Level.WARNING, "[HOKHAUDAO] Khong tim thay ban ghi Ho_Khau nao");
            }
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi he thong", e);
            throw e;
        }
    }

    /**
     * TRANSACTION: Thêm Hộ khẩu + Chủ hộ cùng lúc.
     * 
     * @param hk    Đối tượng Hộ khẩu
     * @param chuHo Đối tượng Nhân khẩu (Chủ hộ)
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean addHoKhauWithChuHo(HoKhau hk, NhanKhau chuHo) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtHk = null;
        PreparedStatement pstmtNk = null;
        boolean result = false;

        logger.info("[HOKHAUDAO] Bat dau Transaction them Ho Khau + Chu Ho");

        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null)
                throw new SQLException("Khong the ket noi CSDL");

            // 1. Start Transaction
            conn.setAutoCommit(false);

            // 2. Insert HO_KHAU
            String sqlHk = "INSERT INTO HO_KHAU (SoCanHo, TenChuHo, DienTich, SDT) VALUES (?, ?, ?, ?)";
            pstmtHk = conn.prepareStatement(sqlHk, Statement.RETURN_GENERATED_KEYS);
            pstmtHk.setString(1, hk.getSoCanHo());
            pstmtHk.setString(2, hk.getTenChuHo());
            pstmtHk.setDouble(3, hk.getDienTich());
            pstmtHk.setString(4, hk.getSdt());

            int affectedRows = pstmtHk.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Lấy MaHo vừa sinh ra
            int newMaHo = 0;
            try (ResultSet generatedKeys = pstmtHk.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newMaHo = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Insert NHAN_KHAU (Chủ hộ)
            String sqlNk = "INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (?, ?, ?, ?, ?, ?)";
            pstmtNk = conn.prepareStatement(sqlNk);
            pstmtNk.setInt(1, newMaHo);
            pstmtNk.setString(2, chuHo.getHoTen());
            pstmtNk.setDate(3, new java.sql.Date(chuHo.getNgaySinh().getTime()));
            pstmtNk.setString(4, chuHo.getGioiTinh());

            if (chuHo.getCccd() == null || chuHo.getCccd().isEmpty()) {
                pstmtNk.setNull(5, Types.VARCHAR);
            } else {
                pstmtNk.setString(5, chuHo.getCccd());
            }
            pstmtNk.setString(6, AppConstants.QH_CHU_HO);

            pstmtNk.executeUpdate();

            // 4. Commit
            conn.commit();
            result = true;
            logger.info("[HOKHAUDAO] Transaction thanh cong");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi Transaction", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Loi rollback", ex);
                }
            }
            throw e;
        } finally {
            if (pstmtHk != null)
                pstmtHk.close();
            if (pstmtNk != null)
                pstmtNk.close();
            if (conn != null)
                conn.close();
        }
        return result;
    }

    /**
     * Cập nhật thông tin hộ khẩu
     * 
     * @param hk Đối tượng Hộ khẩu
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean update(HoKhau hk) throws SQLException {
        String sql = "UPDATE HO_KHAU SET SoCanHo=?, TenChuHo=?, DienTich=?, SDT=? WHERE MaHo=?";
        logger.log(Level.INFO, "[HOKHAUDAO] Update ho khau ID: {0}", hk.getMaHo());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hk.getSoCanHo());
            pstmt.setString(2, hk.getTenChuHo());
            pstmt.setDouble(3, hk.getDienTich());
            pstmt.setString(4, hk.getSdt());
            pstmt.setInt(5, hk.getMaHo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi update", e);
            throw e;
        }
    }

    /**
     * Xoa mem HoKhau (Soft Delete)
     * 
     * @param maHo Mã hộ khẩu
     * @return true nếu xóa thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean softDelete(int maHo) throws SQLException {
        String sql = "UPDATE HO_KHAU SET IsDeleted = 1 " +
                "WHERE MaHo = ? " +
                "AND NOT EXISTS ( " +
                "SELECT 1 FROM CONG_NO WHERE CONG_NO.MaHo = HO_KHAU.MaHo )";
        logger.log(Level.INFO, "[HOKHAUDAO] Bat dau thuc hien xoa mem ID: {0}", maHo);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHo);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.log(Level.INFO, "[HOKHAUDAO] Xoa thanh cong ho khau ID: {0}", maHo);
                return true;
            } else {
                logger.log(Level.WARNING, "[HOKHAUDAO] Khong the xoa (ID sai hoac con no): {0}", maHo);
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi xoa mem", e);
            throw e;
        }
    }

    /**
     * Kiểm tra số căn hộ đã tồn tại chưa
     * 
     * @param soCanHo Số căn hộ
     * @return true nếu đã tồn tại
     * @throws SQLException lỗi truy vấn
     */
    public boolean checkExist(String soCanHo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM HO_KHAU WHERE SoCanHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soCanHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi checkExist", e);
            throw e;
        }
        return false;
    }

    /**
     * Tìm kiếm hộ khẩu theo từ khóa (Số căn hộ hoặc Tên chủ hộ)
     * 
     * @param keyword Từ khóa
     * @return List<HoKhau>
     * @throws SQLException lỗi truy vấn
     */
    public List<HoKhau> search(String keyword) throws SQLException {
        List<HoKhau> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_KHAU WHERE (SoCanHo LIKE ? OR TenChuHo LIKE ?) AND IsDeleted = 0";
        logger.log(Level.INFO, "[HOKHAUDAO] Search voi keyword: {0}", keyword);

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
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi search", e);
            throw e;
        }
        return list;
    }

    /**
     * Lấy hộ khẩu từ mã hộ
     * 
     * @param id Mã hộ
     * @return HoKhau
     * @throws SQLException lỗi truy vấn
     */
    public HoKhau getById(int id) throws SQLException {
        String sql = "SELECT * FROM HO_KHAU WHERE MaHo = ?";
        HoKhau hk = new HoKhau();

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    hk.setMaHo(rs.getInt("MaHo"));
                    hk.setSoCanHo(rs.getString("SoCanHo"));
                    hk.setTenChuHo(rs.getString("TenChuHo"));
                    hk.setDienTich(rs.getDouble("DienTich"));
                    hk.setSdt(rs.getString("SDT"));
                    hk.setNgayTao(rs.getDate("NgayTao"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi getById", e);
            throw e;
        }
        return hk;
    }

    /**
     * Lấy Hộ khẩu bằng Số căn hộ
     * 
     * @param soCanHo Số căn hộ
     * @return HoKhau hoặc null
     * @throws SQLException lỗi truy vấn
     */
    public HoKhau getBySoCanHo(String soCanHo) throws SQLException {
        String sql = "SELECT * FROM HO_KHAU WHERE SoCanHo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soCanHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HoKhau hk = new HoKhau();
                    hk.setMaHo(rs.getInt("MaHo"));
                    hk.setSoCanHo(rs.getString("SoCanHo"));
                    hk.setTenChuHo(rs.getString("TenChuHo"));
                    hk.setDienTich(rs.getDouble("DienTich"));
                    hk.setSdt(rs.getString("SDT"));
                    return hk;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUDAO] Loi getBySoCanHo", e);
            throw e;
        }
        return null;
    }
}