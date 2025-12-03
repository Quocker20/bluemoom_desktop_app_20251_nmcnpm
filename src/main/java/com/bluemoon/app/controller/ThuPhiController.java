package com.bluemoon.app.controller;

import com.bluemoon.app.dao.CongNoDAO;
import com.bluemoon.app.dao.GiaoDichDAO;
import com.bluemoon.app.dao.HoKhauDAO;
import com.bluemoon.app.dao.KhoanPhiDAO;
import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.model.GiaoDich;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.KhoanPhi;

import java.util.List;

public class ThuPhiController {

    private CongNoDAO congNoDAO;
    private KhoanPhiDAO khoanPhiDAO;
    private GiaoDichDAO giaoDichDAO;
    private HoKhauDAO hoKhauDAO;

    public ThuPhiController() {
        this.congNoDAO = new CongNoDAO();
        this.khoanPhiDAO = new KhoanPhiDAO();
        this.giaoDichDAO = new GiaoDichDAO();
        this.hoKhauDAO = new HoKhauDAO();
    }

    // --- 1. QUẢN LÝ DANH MỤC PHÍ ---

    public List<KhoanPhi> getAllKhoanPhi() {
        return khoanPhiDAO.getAll();
    }

    public boolean updateKhoanPhi(KhoanPhi kp) {
        // Validate: Đơn giá không được âm
        if (kp.getDonGia() < 0) {
            System.err.println("Lỗi: Đơn giá không hợp lệ");
            return false;
        }
        return khoanPhiDAO.update(kp);
    }

    public boolean themKhoanThuTuNguyen(String tenKhoanThu, String ghiChu) {
        if (tenKhoanThu == null || tenKhoanThu.trim().isEmpty()) return false;
        
        KhoanPhi kp = new KhoanPhi();
        kp.setTenKhoanPhi(tenKhoanThu);
        kp.setDonGia(0); // Tự nguyện không có đơn giá cố định
        kp.setDonViTinh("Lần");
        kp.setMandatory(false); // Là tự nguyện
        
        return khoanPhiDAO.insert(kp);
    }

    // --- 2. NGHIỆP VỤ TÍNH PHÍ TỰ ĐỘNG (QUAN TRỌNG) ---

    /**
     * Tính toán và tạo công nợ cho tất cả hộ khẩu trong tháng chỉ định.
     * @param thang Tháng cần tính
     * @param nam Năm cần tính
     * @return Số lượng bản ghi công nợ đã tạo (-1 nếu lỗi hoặc đã tính rồi)
     */
    public int tinhPhiTuDong(int thang, int nam) {
        // 1. Kiểm tra xem tháng này đã tính chưa
        if (congNoDAO.checkCalculated(thang, nam)) {
            System.err.println("Tháng " + thang + "/" + nam + " đã được tính phí rồi.");
            return -1; 
        }

        // 2. Lấy danh sách tất cả hộ khẩu (để lấy Diện tích)
        List<HoKhau> listHoKhau = hoKhauDAO.getAll();
        
        // 3. Lấy danh sách các khoản phí BẮT BUỘC
        List<KhoanPhi> listPhi = khoanPhiDAO.getAll(); 
        
        int count = 0;

        // 4. Vòng lặp tính toán
        for (HoKhau hk : listHoKhau) {
            for (KhoanPhi kp : listPhi) {
                if (kp.isMandatory()) { // Chỉ tính phí bắt buộc
                    double soTienCanDong = 0;

                    // Logic tính tiền:
                    // Nếu đơn vị là 'm2' -> nhân với diện tích
                    // Nếu đơn vị là 'hộ' -> giá cố định
                    if ("m2".equalsIgnoreCase(kp.getDonViTinh())) {
                        soTienCanDong = kp.getDonGia() * hk.getDienTich();
                    } else {
                        soTienCanDong = kp.getDonGia();
                    }

                    // Tạo đối tượng Công nợ
                    CongNo cn = new CongNo();
                    cn.setMaHo(hk.getMaHo());
                    cn.setMaKhoanPhi(kp.getMaKhoanPhi());
                    cn.setThang(thang);
                    cn.setNam(nam);
                    cn.setSoTienPhaiDong(soTienCanDong);
                    cn.setSoTienDaDong(0);
                    cn.setDone(false);

                    congNoDAO.insert(cn);
                    count++;
                }
            }
        }
        return count;
    }

    // --- 3. QUẢN LÝ CÔNG NỢ & THANH TOÁN ---

    public List<CongNo> getDanhSachCongNo(int thang, int nam) {
        return congNoDAO.getAll(thang, nam);
    }

    public List<CongNo> getDanhSachCongNo() {
        return congNoDAO.getAll();
    }

    /**
     * Ghi nhận thanh toán cho 1 khoản công nợ.
     */
    public boolean thanhToan(int maCongNo, double soTienThu, String nguoiNop, String ghiChu) {
        if (soTienThu <= 0) return false;

        // 1. Lấy thông tin công nợ hiện tại
        CongNo cn = congNoDAO.getById(maCongNo);
        if (cn == null) return false;

        // 2. Cập nhật số tiền đã đóng trong bảng CONG_NO
        double tongDaDong = cn.getSoTienDaDong() + soTienThu;
        boolean updateSuccess = congNoDAO.updatePayment(maCongNo, tongDaDong);

        if (updateSuccess) {
            // 3. Ghi vào lịch sử GIAO_DICH
            GiaoDich gd = new GiaoDich();
            gd.setMaHo(cn.getMaHo());
            gd.setMaKhoanPhi(cn.getMaKhoanPhi());
            gd.setSoTien(soTienThu);
            gd.setNguoiNop(nguoiNop);
            gd.setGhiChu(ghiChu);
            
            giaoDichDAO.insert(gd);
            return true;
        }
        return false;
    }
}