package com.bluemoon.app.controller.vehicle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bluemoon.app.dao.vehicle.PhuongTienDAO;
import com.bluemoon.app.model.Vehicle;

public class PhuongTienController {

    private final PhuongTienDAO dao;

    public PhuongTienController() {
        this.dao = new PhuongTienDAO();
    }

    /**
     * Lấy danh sách tất cả phương tiện đang gửi
     */
    public List<Vehicle> getAllPhuongTien() {
        try {
            return dao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về list rỗng nếu lỗi để không crash UI
        }
    }

    /**
     * Tìm kiếm phương tiện
     */
    public List<Vehicle> searchPhuongTien(String keyword) {
        try {
            return dao.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Thêm phương tiện mới
     * Trả về: 
     * 1: Thành công
     * -1: Lỗi Database
     * -2: Biển số đã tồn tại
     */
    public int addPhuongTien(Vehicle pt) {
        try {
            // Validate: Kiểm tra trùng biển số trước
            if (dao.checkExist(pt.getLicensePlate())) {
                return -2; // Mã lỗi: Đã tồn tại
            }
            
            boolean success = dao.add(pt);
            return success ? 1 : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Lỗi hệ thống
        }
    }

    /**
     * Xóa phương tiện (Xóa mềm)
     */
    public boolean deletePhuongTien(String bienSo) {
        try {
            return dao.softDelete(bienSo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra nhanh xem biển số có tồn tại không (Dùng cho Validate UI)
     */
    public boolean isBienSoExist(String bienSo) {
        try {
            return dao.checkExist(bienSo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin phương tiện
     */
    public boolean updatePhuongTien(Vehicle pt) {
        try {
            return dao.update(pt);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}