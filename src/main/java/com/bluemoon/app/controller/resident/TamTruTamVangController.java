package com.bluemoon.app.controller.resident;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.resident.TamTruTamVangDAO;
import com.bluemoon.app.model.ResidencyRecord;

/**
 * Controller quản lý Tạm trú - Tạm vắng.
 */
public class TamTruTamVangController {

    private final TamTruTamVangDAO tamTruTamVangDAO;
    private final Logger logger;

    public TamTruTamVangController() {
        this.tamTruTamVangDAO = new TamTruTamVangDAO();
        this.logger = Logger.getLogger(TamTruTamVangController.class.getName());
    }

    /**
     * Lấy danh sách tất cả tạm trú tạm vắng.
     * 
     * @return List<TamTruTamVang>
     */
    public List<ResidencyRecord> getAllTamTruTamVang() {
        try {
            return tamTruTamVangDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TamTruTamVangController] Loi getAllTamTruTamVang", e);
            return Collections.emptyList();
        }
    }

    /**
     * Thêm mới đăng ký tạm trú/tạm vắng.
     * 
     * @param tttv Đối tượng TamTruTamVang
     * @return true nếu thành công
     */
    public boolean addTamTruTamVang(ResidencyRecord tttv) {
        // 1. Validate Null/Empty
        if (tttv.getResidentId() <= 0 ||
                tttv.getType() == null || tttv.getType().trim().isEmpty() ||
                tttv.getStartDate() == null ||
                tttv.getReason() == null || tttv.getReason().trim().isEmpty()) {
            return false;
        }

        // 2. Validate Logic Date
        if (tttv.getEndDate() != null && tttv.getEndDate().before(tttv.getStartDate())) {
            logger.warning("Lỗi: Ngày kết thúc không thể trước ngày bắt đầu!");
            return false;
        }

        try {
            return tamTruTamVangDAO.insert(tttv);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TamTruTamVangController] Loi addTamTruTamVang", e);
            return false;
        }
    }

    /**
     * Lấy danh sách theo loại hình (TamTru hoặc TamVang).
     * 
     * @param loaiHinh Loại hình
     * @return List<TamTruTamVang>
     */
    public List<ResidencyRecord> getByLoaiHinh(String loaiHinh) {
        try {
            return tamTruTamVangDAO.getByLoaiHinh(loaiHinh);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TamTruTamVangController] Loi getByLoaiHinh", e);
            return Collections.emptyList();
        }
    }

    /**
     * Tìm kiếm theo tên.
     * 
     * @param hoTen Tên nhân khẩu
     * @return List<TamTruTamVang>
     */
    public List<ResidencyRecord> getByHoTen(String hoTen) {
        try {
            return tamTruTamVangDAO.getByHoTen(hoTen);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TamTruTamVangController] Loi getByHoTen", e);
            return Collections.emptyList();
        }
    }

    /**
     * Xóa các bản ghi hết hạn tính đến ngày hôm nay.
     * 
     * @return true nếu thành công
     */
    public boolean deleteExpiredRecordsToday() {
        LocalDate today = LocalDate.now();
        java.sql.Date sqlToday = java.sql.Date.valueOf(today);

        try {
            return tamTruTamVangDAO.deleteByExpirationDate(sqlToday);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[TamTruTamVangController] Loi deleteExpiredRecordsToday", e);
            return false;
        }
    }
}