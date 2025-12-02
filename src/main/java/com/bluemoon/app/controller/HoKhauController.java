package com.bluemoon.app.controller;

import com.bluemoon.app.dao.HoKhauDAO;
import com.bluemoon.app.model.HoKhau;
import java.util.List;

public class HoKhauController {
    static double maxArea = 1000.0; // Diện tích tối đa hợp lệ
    private HoKhauDAO hoKhauDAO;

    public HoKhauController() {
        this.hoKhauDAO = new HoKhauDAO();
    }

    // Lấy danh sách tất cả hộ khẩu
    public List<HoKhau> getAllHoKhau() {
        return hoKhauDAO.getAll();
    }

    // Thêm mới hộ khẩu (có kiểm tra trùng lặp)
    public boolean addHoKhau(HoKhau hk) {
        // 1. Validate dữ liệu cơ bản
        if (hk.getSoCanHo() == null || hk.getSoCanHo().trim().isEmpty() ||
                hk.getTenChuHo() == null || hk.getTenChuHo().trim().isEmpty() ||
                hk.getDienTich() <= 0 || hk.getDienTich() > maxArea) {
            return false; // Dữ liệu không hợp lệ
        }

        // 2. Kiểm tra trùng số căn hộ
        if (hoKhauDAO.checkExist(hk.getSoCanHo())) {
            System.err.println("Lỗi: Số căn hộ " + hk.getSoCanHo() + " đã tồn tại!");
            return false;
        }

        // 3. Gọi DAO để thêm
        return hoKhauDAO.add(hk);
    }

    // Cập nhật hộ khẩu
    public boolean updateHoKhau(HoKhau hk) {
        // Validate cơ bản
        if (hk.getMaHo() <= 0 || hk.getDienTich() <= 0 || hk.getDienTich() > maxArea) {
            return false;
        }
        return hoKhauDAO.update(hk);
    }

    // Xóa hộ khẩu
    public boolean deleteHoKhau(int maHo) {
        if (maHo <= 0)
            return false;
        return hoKhauDAO.delete(maHo);
    }

    // Tìm kiếm hộ khẩu
    public List<HoKhau> searchHoKhau(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHoKhau(); // Nếu từ khóa rỗng, trả về tất cả
        }
        return hoKhauDAO.search(keyword.trim());
    }

    //Lấy số mã hộ lớn nhất
    public int getMaxMaHo() {
        return hoKhauDAO.getMaxMaHo();
    }
}