package com.bluemoon.app.controller;

import com.bluemoon.app.dao.*;
import com.bluemoon.app.model.*;
import com.bluemoon.app.util.AppConstants;
import com.bluemoon.app.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ThuPhiController {

    private final CongNoDAO congNoDAO;
    private final KhoanPhiDAO khoanPhiDAO;
    private final GiaoDichDAO giaoDichDAO;
    private final HoKhauDAO hoKhauDAO;

    public ThuPhiController() {
        this.congNoDAO = new CongNoDAO();
        this.khoanPhiDAO = new KhoanPhiDAO();
        this.giaoDichDAO = new GiaoDichDAO();
        this.hoKhauDAO = new HoKhauDAO();
    }

    public List<CongNo> getDanhSachCongNo(int thang, int nam, String keyword) {
        return congNoDAO.getAll(thang, nam, keyword);
    }

    public List<KhoanPhi> getAllKhoanPhi() {
        return khoanPhiDAO.getAll();
    }

    public List<KhoanPhi> getListKhoanPhi(String keyword) {
        return khoanPhiDAO.getAll(keyword);
    }

    public boolean insertKhoanPhi(KhoanPhi kp) {
        if (kp.getTenKhoanPhi() == null || kp.getTenKhoanPhi().trim().isEmpty())
            return false;
        if (kp.getDonGia() < 0)
            return false;
        if (kp.getLoaiPhi() == AppConstants.PHI_BAT_BUOC && kp.getDonGia() <= 0)
            return false;
        return khoanPhiDAO.insert(kp);
    }

    public boolean updateKhoanPhi(KhoanPhi kp) {
        if (kp.getDonGia() < 0)
            return false;
        return khoanPhiDAO.update(kp);
    }

    public boolean deleteKhoanPhi(int id) {
        return khoanPhiDAO.delete(id);
    }

    // --- LOGIC TÍNH PHÍ TỰ ĐỘNG CHO 1 KHOẢN PHÍ MỚI (New Feature) ---
    public void tinhPhiTuDongChoKhoanPhi(int thang, int nam, KhoanPhi kp) {
        // Chỉ áp dụng cho phí bắt buộc
        if (kp.getLoaiPhi() != AppConstants.PHI_BAT_BUOC)
            return;

        List<HoKhau> listHoKhau = hoKhauDAO.getAll();
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null)
                return;
            conn.setAutoCommit(false); // Transaction

            // Logic: Insert công nợ cho từng hộ với khoản phí này
            // Sử dụng INSERT IGNORE hoặc kiểm tra tồn tại để tránh lỗi nếu chạy lại
            String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

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
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
    }

    // --- LOGIC TẠO ĐỢT THU ĐỊNH KỲ (Batch Processing) ---
    public int tinhPhiTuDong(int thang, int nam) {
        if (congNoDAO.checkCalculated(thang, nam))
            return -1;

        List<HoKhau> listHoKhau = hoKhauDAO.getAll();
        List<KhoanPhi> listPhi = khoanPhiDAO.getAll();
        int count = 0;
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null)
                return -1;
            conn.setAutoCommit(false);

            String sql = "INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

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
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
            }
            return 0;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
            }
        }
        return count;
    }

    public boolean thanhToan(int maCongNo, double soTienThu, String nguoiNop, String ghiChu) {
        if (soTienThu <= 0)
            return false;
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null)
                return false;
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
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception ex) {
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
            }
        }
    }

    // Check ràng buộc dữ liệu trước khi Xóa/Sửa
    public boolean checkKhoanPhiDangSuDung(int maKhoanPhi) {
        return congNoDAO.checkKhoanPhiInUse(maKhoanPhi);
    }

    public HoKhau getHoKhauBySoPhong(String soPhong) {
        return hoKhauDAO.getBySoCanHo(soPhong);
    }

    // Thêm công nợ đơn lẻ cho 1 hộ
    public String themCongNoDonLe(String soCanHo, KhoanPhi kp, int thang, int nam) {
        // 1. Tìm hộ khẩu
        HoKhau hk = hoKhauDAO.getBySoCanHo(soCanHo);
        if (hk == null) {
            return "Không tìm thấy căn hộ số: " + soCanHo;
        }

        // 2. Check đã tồn tại chưa
        if (congNoDAO.checkExist(hk.getMaHo(), kp.getMaKhoanPhi(), thang, nam)) {
            return "Hộ này đã có công nợ '" + kp.getTenKhoanPhi() + "' trong tháng " + thang + "/" + nam;
        }

        // 3. Tính tiền
        double soTien = 0;
        if ("m2".equalsIgnoreCase(kp.getDonViTinh())) {
            soTien = kp.getDonGia() * hk.getDienTich();
        } else {
            soTien = kp.getDonGia();
        }

        // 4. Tạo đối tượng CongNo
        CongNo cn = new CongNo();
        cn.setMaHo(hk.getMaHo());
        cn.setMaKhoanPhi(kp.getMaKhoanPhi());
        cn.setThang(thang);
        cn.setNam(nam);
        cn.setSoTienPhaiDong(soTien);

        // 5. Insert
        congNoDAO.insert(cn);
        return "SUCCESS";
    }
}