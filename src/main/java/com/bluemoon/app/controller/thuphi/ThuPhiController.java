package com.bluemoon.app.controller.thuphi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.dancu.HoKhauDAO;
import com.bluemoon.app.dao.thuphi.CongNoDAO;
import com.bluemoon.app.dao.thuphi.GiaoDichDAO;
import com.bluemoon.app.dao.thuphi.KhoanPhiDAO;
import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.model.GiaoDich;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.KhoanPhi;
import com.bluemoon.app.util.AppConstants;
import com.bluemoon.app.util.DatabaseConnector;

/**
 * Controller quản lý các chức năng liên quan đến Thu Phí, Công Nợ và Giao Dịch.
 */
public class ThuPhiController {

    private final CongNoDAO congNoDAO;
    private final KhoanPhiDAO khoanPhiDAO;
    private final GiaoDichDAO giaoDichDAO;
    private final HoKhauDAO hoKhauDAO;
    private final Logger logger;

    public ThuPhiController() {
        this.congNoDAO = new CongNoDAO();
        this.khoanPhiDAO = new KhoanPhiDAO();
        this.giaoDichDAO = new GiaoDichDAO();
        this.hoKhauDAO = new HoKhauDAO();
        this.logger = Logger.getLogger(ThuPhiController.class.getName());
    }

    /**
     * Lấy danh sách công nợ theo tháng, năm và từ khóa tìm kiếm.
     * 
     * @param thang   Tháng cần xem
     * @param nam     Năm cần xem
     * @param keyword Từ khóa tìm kiếm (tên chủ hộ hoặc mã hộ)
     * @return List<CongNo> Danh sách công nợ
     */
    public List<CongNo> getDanhSachCongNo(int thang, int nam, String keyword) {
        try {
            return congNoDAO.getAll(thang, nam, keyword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi getDanhSachCongNo", e);
            return Collections.emptyList();
        }
    }

    /**
     * Lấy danh sách tất cả các khoản phí.
     * 
     * @return List<KhoanPhi>
     */
    public List<KhoanPhi> getAllKhoanPhi() {
        try {
            return khoanPhiDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi getAllKhoanPhi", e);
            return Collections.emptyList();
        }
    }

    /**
     * Lấy danh sách khoản phí đang hoạt động theo từ khóa.
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return List<KhoanPhi>
     */
    public List<KhoanPhi> getListKhoanPhi(String keyword) {
        try {
            return khoanPhiDAO.getAllActiveFee(keyword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi getListKhoanPhi", e);
            return Collections.emptyList();
        }
    }

    /**
     * Thêm mới một khoản phí.
     * 
     * @param kp Đối tượng KhoanPhi
     * @return true nếu thêm thành công, false nếu dữ liệu không hợp lệ hoặc lỗi
     */
    public boolean insertKhoanPhi(KhoanPhi kp) {
        if (kp.getTenKhoanPhi() == null || kp.getTenKhoanPhi().trim().isEmpty()) {
            logger.log(Level.WARNING, "[ThuPhiController] Ten khoan phi khong duoc de trong");
            return false;
        }
        if (kp.getDonGia() < 0) {
            logger.log(Level.WARNING, "[ThuPhiController] Don gia khong duoc am");
            return false;
        }
        if (kp.getLoaiPhi() == AppConstants.PHI_BAT_BUOC && kp.getDonGia() <= 0) {
            logger.log(Level.WARNING, "[ThuPhiController] Phi bat buoc phai co don gia > 0");
            return false;
        }
        try {
            return khoanPhiDAO.insert(kp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi insertKhoanPhi", e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin khoản phí.
     * 
     * @param kp Đối tượng KhoanPhi cần cập nhật
     * @return true nếu thành công
     */
    public boolean updateKhoanPhi(KhoanPhi kp) {
        if (kp.getDonGia() < 0) {
            return false;
        }
        try {
            return khoanPhiDAO.update(kp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi updateKhoanPhi", e);
            return false;
        }
    }

    /**
     * Vô hiệu hóa (xóa mềm) một khoản phí.
     * 
     * @param id Mã khoản phí
     * @return true nếu thành công
     */
    public boolean disableKhoanPhi(int id) {
        try {
            return khoanPhiDAO.disable(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi disableKhoanPhi", e);
            return false;
        }
    }

    /**
     * Tính phí tự động cho một khoản phí mới thêm vào.
     * Áp dụng cho tất cả các hộ khẩu hiện có.
     * 
     * @param thang Tháng áp dụng
     * @param nam   Năm áp dụng
     * @param kp    Khoản phí vừa tạo
     */
    public void tinhPhiTuDongChoKhoanPhi(int thang, int nam, KhoanPhi kp) {
        if (kp.getLoaiPhi() != AppConstants.PHI_BAT_BUOC) {
            return;
        }

        logger.log(Level.INFO, "[ThuPhiController] Bat dau tinh phi tu dong cho khoan phi: {0}", kp.getTenKhoanPhi());

        List<HoKhau> listHoKhau;
        try {
            listHoKhau = hoKhauDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi khi lay danh sach ho khau tu DAO", e);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) {
                return;
            }
            conn.setAutoCommit(false);

            String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (HoKhau hk : listHoKhau) {
                    double soTien = 0;
                    if ("m2".equalsIgnoreCase(kp.getDonViTinh())) {
                        soTien = kp.getDonGia() * hk.getDienTich();
                    } else {
                        soTien = kp.getDonGia();
                    }

                    pstmt.setInt(1, hk.getMaHo());
                    pstmt.setInt(2, kp.getMaKhoanPhi());
                    pstmt.setInt(3, thang);
                    pstmt.setInt(4, nam);
                    pstmt.setDouble(5, soTien);
                    pstmt.setDouble(6, 0);
                    pstmt.setInt(7, 0);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            logger.log(Level.INFO, "[ThuPhiController] Da tao cong no thanh cong cho khoan phi: {0}",
                    kp.getTenKhoanPhi());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi SQL khi tinh phi tu dong", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Loi rollback", ex);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Loi dong ket noi", e);
                }
            }
        }
    }

    /**
     * Tạo đợt thu phí định kỳ cho tháng (Batch Processing).
     * Duyệt qua tất cả hộ khẩu và tất cả khoản phí bắt buộc để tạo công nợ.
     * 
     * @param thang Tháng cần tính
     * @param nam   Năm cần tính
     * @return int Số lượng bản ghi công nợ được tạo, trả về -1 nếu lỗi hoặc đã tính
     *         rồi
     */
    public int tinhPhiTuDong(int thang, int nam) {
        try {
            if (congNoDAO.checkCalculated(thang, nam)) {
                logger.log(Level.WARNING, "[ThuPhiController] Thang {0}/{1} da duoc tinh phi truoc do.",
                        new Object[] { thang, nam });
                return -1;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi checkCalculated", e);
            return -1;
        }

        logger.log(Level.INFO, "[ThuPhiController] Bat dau batch job tinh phi cho thang {0}/{1}",
                new Object[] { thang, nam });

        List<HoKhau> listHoKhau;
        try {
            listHoKhau = hoKhauDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi khi lay danh sach ho khau", e);
            return -1;
        }

        List<KhoanPhi> listPhi;
        try {
            listPhi = khoanPhiDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi khi lay danh sach khoan phi", e);
            return -1;
        }

        int count = 0;
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) {
                return -1;
            }
            conn.setAutoCommit(false);

            String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (HoKhau hk : listHoKhau) {
                    for (KhoanPhi kp : listPhi) {
                        if (kp.getLoaiPhi() == AppConstants.PHI_BAT_BUOC) {
                            double soTien = 0;
                            if ("m2".equalsIgnoreCase(kp.getDonViTinh())) {
                                soTien = kp.getDonGia() * hk.getDienTich();
                            } else {
                                soTien = kp.getDonGia();
                            }
                            pstmt.setInt(1, hk.getMaHo());
                            pstmt.setInt(2, kp.getMaKhoanPhi());
                            pstmt.setInt(3, thang);
                            pstmt.setInt(4, nam);
                            pstmt.setDouble(5, soTien);
                            pstmt.setDouble(6, 0);
                            pstmt.setInt(7, 0);
                            pstmt.addBatch();
                            count++;
                        }
                    }
                }
                pstmt.executeBatch();
            }

            conn.commit();
            logger.log(Level.INFO, "[ThuPhiController] Batch job hoan tat. Tao duoc {0} ban ghi cong no.", count);
            return count;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi SQL khi chay batch tinh phi", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Loi rollback", ex);
                }
            }
            return 0;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Loi dong ket noi", e);
                }
            }
        }
    }

    /**
     * Thực hiện thanh toán cho một khoản công nợ.
     * 
     * @param maCongNo  Mã công nợ
     * @param soTienThu Số tiền khách đóng
     * @param nguoiNop  Tên người nộp
     * @param ghiChu    Ghi chú thêm
     * @return true nếu thanh toán thành công
     */
    public boolean thanhToan(int maCongNo, double soTienThu, String nguoiNop, String ghiChu) {
        if (soTienThu <= 0) {
            return false;
        }
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            CongNo cn = congNoDAO.getById(maCongNo);
            if (cn == null) {
                conn.rollback();
                return false;
            }

            double tongDaDong = cn.getSoTienDaDong() + soTienThu;
            boolean updateSuccess = congNoDAO.updatePayment(maCongNo, tongDaDong);
            if (!updateSuccess) {
                conn.rollback();
                return false;
            }

            GiaoDich gd = new GiaoDich(cn.getMaHo(), cn.getMaKhoanPhi(), soTienThu, nguoiNop, ghiChu);
            if (giaoDichDAO.insert(gd)) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Loi rollback", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Loi dong ket noi", e);
                }
            }
        }
    }

    /**
     * Kiểm tra khoản phí có đang được sử dụng trong bảng công nợ không.
     * 
     * @param maKhoanPhi Mã khoản phí
     * @return true nếu đang được sử dụng
     */
    public boolean checkKhoanPhiDangSuDung(int maKhoanPhi) {
        try {
            return congNoDAO.checkKhoanPhiInUse(maKhoanPhi);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi checkKhoanPhiDangSuDung", e);
            return false;
        }
    }

    /**
     * Lấy thông tin hộ khẩu dựa vào số phòng.
     * 
     * @param soPhong Số phòng (Số căn hộ)
     * @return HoKhau hoặc null nếu không tìm thấy
     */
    public HoKhau getHoKhauBySoPhong(String soPhong) {
        try {
            return hoKhauDAO.getBySoCanHo(soPhong);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi getHoKhauBySoPhong", e);
            return null;
        }
    }

    /**
     * Thêm công nợ thủ công cho một hộ gia đình cụ thể.
     * 
     * @param soCanHo Số căn hộ
     * @param kp      Khoản phí áp dụng
     * @param thang   Tháng
     * @param nam     Năm
     * @return String Thông báo kết quả ("SUCCESS" hoặc lỗi)
     */
    public String themCongNoDonLe(String soCanHo, KhoanPhi kp, int thang, int nam) {
        try {
            HoKhau hk = hoKhauDAO.getBySoCanHo(soCanHo);
            if (hk == null) {
                return "Không tìm thấy căn hộ số: " + soCanHo;
            }

            if (congNoDAO.checkExist(hk.getMaHo(), kp.getMaKhoanPhi(), thang, nam)) {
                return "Hộ này đã có công nợ '" + kp.getTenKhoanPhi() + "' trong tháng " + thang + "/" + nam;
            }

            double soTien = 0;
            if ("m2".equalsIgnoreCase(kp.getDonViTinh())) {
                soTien = kp.getDonGia() * hk.getDienTich();
            } else {
                soTien = kp.getDonGia();
            }

            CongNo cn = new CongNo();
            cn.setMaHo(hk.getMaHo());
            cn.setMaKhoanPhi(kp.getMaKhoanPhi());
            cn.setThang(thang);
            cn.setNam(nam);
            cn.setSoTienPhaiDong(soTien);

            congNoDAO.insert(cn);
            return "SUCCESS";
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi themCongNoDonLe", e);
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Xóa một bản ghi công nợ theo ID.
     * 
     * @param id Mã công nợ
     * @return int Số dòng bị ảnh hưởng
     */
    public int deleteCongNoById(int id) {
        if (id <= 0) {
            return 0;
        }
        try {
            return congNoDAO.deleteCongNoById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi deleteCongNoById", e);
            return 0;
        }
    }

    /**
     * Lấy thông tin chi tiết công nợ theo ID.
     * 
     * @param id Mã công nợ
     * @return CongNo hoặc null nếu không tìm thấy
     */
    public CongNo getCongNoById(int id) {
        if (id <= 0) {
            return null;
        }
        try {
            return congNoDAO.getById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ThuPhiController] Loi getCongNoById", e);
            return null;
        }
    }

    /**
     * Hàm Controller riêng biệt: CHỈ tính phí gửi xe (Ô tô & Xe máy).
     * Hàm này sẽ tìm tất cả các khoản phí có đơn vị là "Phương tiện..." và chạy tính toán.
     */
    public String chotSoPhiGuiXe(int thang, int nam) {
        try {
            List<KhoanPhi> listPhi = khoanPhiDAO.getAll(); // Lấy tất cả khoản phí đang active
            int countOto = 0;
            int countXeMay = 0;

            for (KhoanPhi kp : listPhi) {
                String donVi = kp.getDonViTinh().trim(); // Xóa khoảng trắng thừa cho chắc chắn

                // 1. Xử lý Ô tô
                if ("Phương tiện (Ô tô)".equalsIgnoreCase(donVi)) {
                    int rows = congNoDAO.tinhPhiPhuongTien(thang, nam, kp.getMaKhoanPhi(), kp.getDonGia(), 1);
                    countOto += rows;
                } 
                // 2. Xử lý Xe máy / Xe đạp
                else if ("Phương tiện (Xe đạp/Xe máy)".equalsIgnoreCase(donVi)) {
                    int rows = congNoDAO.tinhPhiPhuongTien(thang, nam, kp.getMaKhoanPhi(), kp.getDonGia(), 2);
                    countXeMay += rows;
                }
            }
            
            return "Hoàn tất! Đã tạo công nợ cho " + countOto + " hộ gửi Ô tô và " + countXeMay + " hộ gửi Xe máy.";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }
}