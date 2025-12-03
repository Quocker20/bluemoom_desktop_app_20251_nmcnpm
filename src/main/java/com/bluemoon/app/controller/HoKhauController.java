package com.bluemoon.app.controller;

import com.bluemoon.app.dao.HoKhauDAO;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;
import java.util.List;

public class HoKhauController {
    private static final double MAX_AREA = 10000.0; // Giới hạn diện tích hợp lý
    private final HoKhauDAO hoKhauDAO;

    public HoKhauController() {
        this.hoKhauDAO = new HoKhauDAO();
    }

    public List<HoKhau> getAllHoKhau() {
        return hoKhauDAO.getAll();
    }

    /**
     * Phương thức thêm mới AN TOÀN (Dùng Transaction).
     * Bắt buộc phải có thông tin Chủ hộ đi kèm.
     */
    public boolean addHoKhauWithChuHo(HoKhau hk, NhanKhau chuHo) {
        if (!validateHoKhau(hk))
            return false;

        if (chuHo == null || chuHo.getHoTen() == null || chuHo.getHoTen().isEmpty()) {
            System.err.println("Lỗi: Thông tin chủ hộ không hợp lệ!");
            return false;
        }

        if (hoKhauDAO.checkExist(hk.getSoCanHo())) {
            System.err.println("Lỗi: Số căn hộ " + hk.getSoCanHo() + " đã tồn tại!");
            return false;
        }

        return hoKhauDAO.addHoKhauWithChuHo(hk, chuHo);
    }

    public boolean updateHoKhau(HoKhau hk) {
        if (!validateHoKhau(hk) || hk.getMaHo() <= 0) {
            return false;
        }
        return hoKhauDAO.update(hk);
    }

    public boolean deleteHoKhau(int maHo) {
        if (maHo <= 0)
            return false;
        return hoKhauDAO.delete(maHo);
    }

    public List<HoKhau> searchHoKhau(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHoKhau();
        }
        return hoKhauDAO.search(keyword.trim());
    }

    // Helper validation
    private boolean validateHoKhau(HoKhau hk) {
        return hk != null
                && hk.getSoCanHo() != null && !hk.getSoCanHo().trim().isEmpty()
                && hk.getTenChuHo() != null && !hk.getTenChuHo().trim().isEmpty()
                && hk.getDienTich() > 0 && hk.getDienTich() < MAX_AREA;
    }

    public HoKhau getById(int id) {
        return hoKhauDAO.getById(id);
    }
}