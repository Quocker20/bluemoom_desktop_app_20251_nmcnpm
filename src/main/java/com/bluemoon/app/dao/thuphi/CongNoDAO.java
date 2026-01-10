package com.bluemoon.app.dao.thuphi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.util.DatabaseConnector;

public class CongNoDAO {

    private static final Logger logger = Logger.getLogger(CongNoDAO.class.getName());

    /**
     * Lấy danh sách công nợ theo tháng năm (không có keyword)
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return List<CongNo>
     * @throws SQLException lỗi kết nối CSDL
     */
    public List<CongNo> getAll(int thang, int nam) throws SQLException {
        return getAll(thang, nam, "");
    }

    /**
     * Lấy danh sách công nợ có lọc theo từ khóa, tháng, năm
     * 
     * @param thang   Tháng
     * @param nam     Năm
     * @param keyword Từ khóa tìm kiếm (số căn hộ)
     * @return List<CongNo>
     * @throws SQLException lỗi kết nối CSDL
     */
    public List<CongNo> getAll(int thang, int nam, String keyword) throws SQLException {
        List<CongNo> list = new ArrayList<>();
        String sql = "SELECT cn.*, kp.TenKhoanPhi, hk.SoCanHo FROM CONG_NO cn " +
                "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                "JOIN HO_KHAU hk ON cn.MaHo = hk.MaHo " +
                "WHERE cn.Thang = ? AND cn.Nam = ? AND hk.SoCanHo LIKE ? " +
                "ORDER BY hk.SoCanHo ASC";

        logger.log(Level.INFO, "[CONGNODAO] Lay danh sach cong no Thang {0}/{1}", new Object[] { thang, nam });

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setMaHo(rs.getInt("MaHo"));
                    cn.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                    cn.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    cn.setSoCanHo(rs.getString("SoCanHo"));
                    cn.setThang(rs.getInt("Thang"));
                    cn.setNam(rs.getInt("Nam"));
                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    cn.setTrangThai(rs.getInt("TrangThai"));
                    list.add(cn);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi lay danh sach cong no", e);
            throw e;
        }
        return list;
    }

    /**
     * Thêm mới một bản ghi công nợ
     * 
     * @param cn Đối tượng CongNo
     * @throws SQLException lỗi kết nối CSDL
     */
    public void insert(CongNo cn) throws SQLException {
        String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        logger.log(Level.INFO, "[CONGNODAO] Bat dau them cong no cho Ho: {0}, Phi: {1}",
                new Object[] { cn.getMaHo(), cn.getMaKhoanPhi() });

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cn.getMaHo());
            pstmt.setInt(2, cn.getMaKhoanPhi());
            pstmt.setInt(3, cn.getThang());
            pstmt.setInt(4, cn.getNam());
            pstmt.setDouble(5, cn.getSoTienPhaiDong());
            pstmt.setDouble(6, 0);
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();

            logger.log(Level.INFO, "[CONGNODAO] Them cong no thanh cong");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi them cong no", e);
            throw e;
        }
    }

    /**
     * Kiểm tra xem tháng/năm này đã được tính phí (batch job) chưa
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return true nếu đã có dữ liệu
     * @throws SQLException lỗi kết nối CSDL
     */
    public boolean checkCalculated(int thang, int nam) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE Thang = ? AND Nam = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi checkCalculated", e);
            throw e;
        }
        return false;
    }

    /**
     * Kiểm tra trùng lặp công nợ cho một hộ cụ thể
     * 
     * @param maHo       Mã hộ
     * @param maKhoanPhi Mã khoản phí
     * @param thang      Tháng
     * @param nam        Năm
     * @return true nếu đã tồn tại
     * @throws SQLException lỗi kết nối CSDL
     */
    public boolean checkExist(int maHo, int maKhoanPhi, int thang, int nam) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE MaHo = ? AND MaKhoanPhi = ? AND Thang = ? AND Nam = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHo);
            pstmt.setInt(2, maKhoanPhi);
            pstmt.setInt(3, thang);
            pstmt.setInt(4, nam);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi checkExist", e);
            throw e;
        }
        return false;
    }

    /**
     * Lấy thông tin chi tiết công nợ theo ID
     * 
     * @param id Mã công nợ
     * @return CongNo hoặc null
     * @throws SQLException lỗi kết nối CSDL
     */
    public CongNo getById(int id) throws SQLException {
        String sql = "SELECT cn.*, kp.TenKhoanPhi, hk.SoCanHo FROM CONG_NO cn " +
                "JOIN KHOAN_PHI kp ON cn.MaKhoanPhi = kp.MaKhoanPhi " +
                "JOIN HO_KHAU hk ON cn.MaHo = hk.MaHo " +
                "WHERE cn.MaCongNo = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CongNo cn = new CongNo();
                    cn.setMaCongNo(rs.getInt("MaCongNo"));
                    cn.setMaHo(rs.getInt("MaHo"));
                    cn.setMaKhoanPhi(rs.getInt("MaKhoanPhi"));
                    cn.setTenKhoanPhi(rs.getString("TenKhoanPhi"));
                    cn.setSoCanHo(rs.getString("SoCanHo"));
                    cn.setSoTienPhaiDong(rs.getDouble("SoTienPhaiDong"));
                    cn.setSoTienDaDong(rs.getDouble("SoTienDaDong"));
                    cn.setTrangThai(rs.getInt("TrangThai"));
                    return cn;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi getById: " + id, e);
            throw e;
        }
        return null;
    }

    /**
     * Cập nhật số tiền đã đóng cho công nợ
     * 
     * @param maCongNo        Mã công nợ
     * @param soTienMoiDaDong Tổng số tiền đã đóng mới
     * @return true nếu update thành công
     * @throws SQLException lỗi kết nối CSDL
     */
    public boolean updatePayment(int maCongNo, double soTienMoiDaDong) throws SQLException {
        String sql = "UPDATE CONG_NO SET SoTienDaDong = ?, TrangThai = CASE WHEN SoTienDaDong >= SoTienPhaiDong THEN 1 ELSE 0 END WHERE MaCongNo = ?";
        logger.log(Level.INFO, "[CONGNODAO] Update thanh toan ID: {0}, So tien: {1}",
                new Object[] { maCongNo, soTienMoiDaDong });

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, soTienMoiDaDong);
            pstmt.setInt(2, maCongNo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi updatePayment", e);
            throw e;
        }
    }

    /**
     * Kiểm tra khoản phí có đang được sử dụng trong bảng công nợ không
     * 
     * @param maKhoanPhi Mã khoản phí
     * @return true nếu đang sử dụng
     * @throws SQLException lỗi kết nối CSDL
     */
    public boolean checkKhoanPhiInUse(int maKhoanPhi) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CONG_NO WHERE MaKhoanPhi = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maKhoanPhi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi checkKhoanPhiInUse", e);
            throw e;
        }
        return false;
    }

    /**
     * Xóa một công nợ bằng id
     * 
     * @param id Mã công nợ
     * @return số dòng ảnh hưởng
     * @throws SQLException lỗi kết nối CSDL
     */
    public int deleteCongNoById(int id) throws SQLException {
        String sql = "DELETE FROM CONG_NO WHERE MaCongNo = ?";
        int affectedRows;

        logger.log(Level.INFO, "[CONGNODAO] Bat dau xoa cong no ID: {0}", id);
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("[CONGNODAO] Xoa cong no thanh cong");
            } else {
                logger.warning("[CONGNODAO] Khong tim thay cong no de xoa");
            }
            return affectedRows;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONGNODAO] Loi khi xoa cong no ID: " + id, e);
            throw e;
        }
    }

    /**
     * Hàm DAO riêng biệt để tính phí gửi xe.
     * Logic: Tìm các hộ có xe đang gửi (TrangThai=1) thuộc loại xe chỉ định -> Đếm số lượng -> Nhân đơn giá -> Tạo nợ.
     * * @param thang Tháng thu phí
     * @param nam Năm thu phí
     * @param maKhoanPhi ID của khoản phí trong bảng KHOAN_PHI
     * @param donGia Giá tiền mỗi xe
     * @param loaiXe 1 = Ô tô, 2 = Xe máy/Xe đạp
     * @return Số bản ghi công nợ được tạo ra
     */
    public int tinhPhiPhuongTien(int thang, int nam, int maKhoanPhi, double donGia, int loaiXe) throws SQLException {
        // Query: Insert vào CONG_NO lấy dữ liệu từ PHUONG_TIEN
        String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, SoTienPhaiDong, SoTienDaDong, Thang, Nam, TrangThai) " +
                     "SELECT pt.MaHo, ?, (COUNT(pt.MaPhuongTien) * ?), 0, ?, ?, 0 " +
                     "FROM PHUONG_TIEN pt " +
                     "WHERE pt.LoaiXe = ? AND pt.TrangThai = 1 " + // Chỉ tính xe đang hoạt động
                     "GROUP BY pt.MaHo " + 
                     // Dòng dưới để tránh tạo trùng nếu đã có công nợ khoản này trong tháng rồi
                     "HAVING NOT EXISTS (SELECT 1 FROM CONG_NO cn WHERE cn.MaHo = pt.MaHo AND cn.MaKhoanPhi = ? AND cn.Thang = ? AND cn.Nam = ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Set params cho phần SELECT & INSERT
            pstmt.setInt(1, maKhoanPhi);
            pstmt.setDouble(2, donGia);
            pstmt.setInt(3, thang);
            pstmt.setInt(4, nam);
            pstmt.setInt(5, loaiXe); 
            
            // Set params cho phần CHECK EXISTS
            pstmt.setInt(6, maKhoanPhi);
            pstmt.setInt(7, thang);
            pstmt.setInt(8, nam);
            
            return pstmt.executeUpdate();
        }
    }
}