package com.bluemoon.app.controller;

import com.bluemoon.app.dao.NhanKhauDAO;
import com.bluemoon.app.model.NhanKhau;
import java.util.List;

public class NhanKhauController {
    
    private NhanKhauDAO nhanKhauDAO;

    public NhanKhauController() {
        this.nhanKhauDAO = new NhanKhauDAO();
    }

    /**
     * Lấy danh sách nhân khẩu của một hộ.
     * @param maHo ID của hộ khẩu
     */
    public List<NhanKhau> getNhanKhauByHoKhau(int maHo) {
        return nhanKhauDAO.selectByHoKhau(maHo);
    }

    /**
     * Thêm nhân khẩu mới.
     * @param nk Đối tượng NhanKhau từ giao diện
     * @return true nếu thành công
     */
    public boolean addNhanKhau(NhanKhau nk) {
        // 1. Validate dữ liệu
        if (!validate(nk)) {
            return false;
        }

        // 2. Kiểm tra trùng CCCD (chỉ kiểm tra nếu có nhập CCCD)
        if (nk.getCccd() != null && !nk.getCccd().trim().isEmpty()) {
            if (nhanKhauDAO.checkCccdExist(nk.getCccd())) {
                System.err.println("Lỗi: Số CCCD " + nk.getCccd() + " đã tồn tại!");
                return false;
            }
        }

        // 3. Gọi DAO
        return nhanKhauDAO.insert(nk);
    }

    /**
     * Cập nhật thông tin nhân khẩu.
     */
    public boolean updateNhanKhau(NhanKhau nk) {
        if (!validate(nk) || nk.getMaNhanKhau() <= 0) {
            return false;
        }
        // Lưu ý: Logic kiểm tra trùng CCCD khi sửa phức tạp hơn (phải trừ chính mình ra)
        // Ở mức độ BTL này, ta có thể tạm bỏ qua hoặc giả định người dùng nhập đúng.
        return nhanKhauDAO.update(nk);
    }

    /**
     * Xóa nhân khẩu.
     */
    public boolean deleteNhanKhau(int maNhanKhau) {
        if (maNhanKhau <= 0) return false;
        return nhanKhauDAO.delete(maNhanKhau);
    }

    // Hàm kiểm tra dữ liệu đầu vào
    private boolean validate(NhanKhau nk) {
        if (nk.getHoTen() == null || nk.getHoTen().trim().isEmpty()) {
            System.err.println("Lỗi: Họ tên không được để trống.");
            return false;
        }
        if (nk.getNgaySinh() == null) {
            System.err.println("Lỗi: Ngày sinh không được để trống.");
            return false;
        }
        if (nk.getQuanHe() == null || nk.getQuanHe().trim().isEmpty()) {
            System.err.println("Lỗi: Quan hệ với chủ hộ không được để trống.");
            return false;
        }
        return true;
    }
}