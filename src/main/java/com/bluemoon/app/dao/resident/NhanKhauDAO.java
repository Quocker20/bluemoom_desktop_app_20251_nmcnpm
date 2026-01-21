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

import com.bluemoon.app.model.Resident;
import com.bluemoon.app.util.DatabaseConnector;

public class NhanKhauDAO {

    private static final Logger logger = Logger.getLogger(NhanKhauDAO.class.getName());

    /**
     * Lấy danh sách nhân khẩu thuộc một hộ
     * 
     * @param maHo Mã hộ khẩu
     * @return List<NhanKhau>
     * @throws SQLException lỗi truy vấn
     */
    public List<Resident> selectByHoKhau(int maHo) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAN_KHAU WHERE MaHo = ? AND IsDeleted = 0 ORDER BY QuanHe ASC";

        logger.log(Level.INFO, "[NHANKHAUDAO] Lay danh sach nhan khau cua ho ID: {0}", maHo);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resident nk = new Resident();
                    nk.setId(rs.getInt("MaNhanKhau"));
                    nk.setHouseholdId(rs.getInt("MaHo"));
                    nk.setFullName(rs.getString("HoTen"));
                    nk.setDob(rs.getDate("NgaySinh"));
                    nk.setGender(rs.getString("GioiTinh"));
                    nk.setIdentityCard(rs.getString("CCCD"));
                    nk.setRelationship(rs.getString("QuanHe"));
                    list.add(nk);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi selectByHoKhau", e);
            throw e;
        }
        return list;
    }

    /**
     * Thêm mới nhân khẩu
     * 
     * @param nk Đối tượng nhân khẩu
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean insert(Resident nk) throws SQLException {
        String sql = "INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (?, ?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[NHANKHAUDAO] Insert nhan khau: {0}", nk.getFullName());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nk.getHouseholdId());
            pstmt.setString(2, nk.getFullName());
            pstmt.setDate(3, new java.sql.Date(nk.getDob().getTime()));
            pstmt.setString(4, nk.getGender());

            if (nk.getIdentityCard() == null || nk.getIdentityCard().trim().isEmpty()) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, nk.getIdentityCard());
            }
            pstmt.setString(6, nk.getRelationship());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi insert", e);
            throw e;
        }
    }

    /**
     * Cập nhật thông tin nhân khẩu
     * 
     * @param nk Đối tượng nhân khẩu
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean update(Resident nk) throws SQLException {
        String sql = "UPDATE NHAN_KHAU SET HoTen=?, NgaySinh=?, GioiTinh=?, CCCD=?, QuanHe=? WHERE MaNhanKhau=?";
        logger.log(Level.INFO, "[NHANKHAUDAO] Update nhan khau ID: {0}", nk.getId());

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nk.getFullName());
            pstmt.setDate(2, new java.sql.Date(nk.getDob().getTime()));
            pstmt.setString(3, nk.getGender());

            if (nk.getIdentityCard() == null || nk.getIdentityCard().trim().isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, nk.getIdentityCard());
            }

            pstmt.setString(5, nk.getRelationship());
            pstmt.setInt(6, nk.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi update", e);
            throw e;
        }
    }

    /**
     * Xóa nhân khẩu (SOFT DELETE)
     * 
     * @param maNhanKhau Mã nhân khẩu
     * @return true nếu thành công
     * @throws SQLException lỗi truy vấn
     */
    public boolean delete(int maNhanKhau) throws SQLException {
        String sql = "UPDATE NHAN_KHAU SET IsDeleted = 1 WHERE MaNhanKhau = ?";
        logger.log(Level.INFO, "[NHANKHAUDAO] Delete nhan khau ID: {0}", maNhanKhau);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNhanKhau);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi delete", e);
            throw e;
        }
    }

    /**
     * Kiểm tra CCCD đã tồn tại chưa (cho chức năng thêm mới)
     * 
     * @param cccd Số CCCD
     * @return true nếu đã tồn tại
     * @throws SQLException lỗi truy vấn
     */
    public boolean checkCccdExist(String cccd) throws SQLException {
        if (cccd == null || cccd.trim().isEmpty())
            return false;
        String sql = "SELECT COUNT(*) FROM NHAN_KHAU WHERE CCCD = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi checkCccdExist", e);
            throw e;
        }
        return false;
    }

    /**
     * Kiểm tra CCCD đã tồn tại chưa (cho chức năng cập nhật)
     * Loại trừ chính bản ghi đang sửa
     * 
     * @param cccd              Số CCCD
     * @param maNhanKhauDangSua ID nhân khẩu đang sửa
     * @return true nếu đã tồn tại ở bản ghi khác
     * @throws SQLException lỗi truy vấn
     */
    public boolean checkCccdExistForUpdate(String cccd, int maNhanKhauDangSua) throws SQLException {
        if (cccd == null || cccd.trim().isEmpty())
            return false;
        String sql = "SELECT COUNT(*) FROM NHAN_KHAU WHERE CCCD = ? AND MaNhanKhau != ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, maNhanKhauDangSua);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi checkCccdExistForUpdate", e);
            throw e;
        }
        return false;
    }


    /**
     * 
     * @param keyword
     * @return
     * @throws SQLException
     */
    public List<Resident> search(String keyword) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAN_KHAU WHERE (HoTen LIKE ? OR CCCD LIKE ?) AND IsDeleted = 0 ORDER BY MaNhanKhau ASC";
        logger.log(Level.INFO, "NHANKHAUDAO Search voi keyword: {0}", keyword);

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resident nk = new Resident();
                    nk.setId(rs.getInt("MaNhanKhau"));
                    nk.setHouseholdId(rs.getInt("MaHo"));
                    nk.setFullName(rs.getString("HoTen"));
                    nk.setDob(rs.getDate("NgaySinh"));
                    nk.setGender(rs.getString("GioiTinh"));
                    nk.setIdentityCard(rs.getString("CCCD"));
                    nk.setRelationship(rs.getString("QuanHe"));
                    
                    list.add(nk);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi search", e);
            throw e;
        }
        
        return list;
    }

    /**
     * Lấy tất cả nhân khẩu
     * 
     * @return List<NhanKhau>
     * @throws SQLException lỗi truy vấn
     */
    public List<Resident> getAll() throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAN_KHAU WHERE IsDeleted = 0 ORDER BY MaNhanKhau ASC";

        logger.log(Level.INFO, "[NHANKHAUDAO] Lay tat ca nhan khau");

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Resident nk = new Resident();
                nk.setId(rs.getInt("MaNhanKhau"));
                nk.setHouseholdId(rs.getInt("MaHo"));
                nk.setFullName(rs.getString("HoTen"));
                nk.setDob(rs.getDate("NgaySinh"));
                nk.setGender(rs.getString("GioiTinh"));
                nk.setIdentityCard(rs.getString("CCCD"));
                nk.setRelationship(rs.getString("QuanHe"));
                list.add(nk);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUDAO] Loi getAll", e);
            throw e;
        }
        return list;
    }
}