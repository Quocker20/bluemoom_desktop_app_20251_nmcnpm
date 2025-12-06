package com.bluemoon.app.controller;

import com.bluemoon.app.dao.TamTruTamVangDAO;
import com.bluemoon.app.model.TamTruTamVang;
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;

public class TamTruTamVangController {
    private final TamTruTamVangDAO tamTruTamVangDAO;

    public TamTruTamVangController() {
        this.tamTruTamVangDAO = new TamTruTamVangDAO();
    }

    public List<TamTruTamVang> getAllTamTruTamVang() {
        return tamTruTamVangDAO.getAll();
    }

    public boolean addTamTruTamVang(TamTruTamVang tttv) {
        // 1. Validate Null/Empty
        if (tttv.getMaNhanKhau() <= 0 ||
                tttv.getLoaiHinh() == null || tttv.getLoaiHinh().trim().isEmpty() ||
                tttv.getTuNgay() == null ||
                tttv.getLyDo() == null || tttv.getLyDo().trim().isEmpty()) {
            return false;
        }

        // 2. Validate Logic Date
        if (tttv.getDenNgay() != null && tttv.getDenNgay().before(tttv.getTuNgay())) {
            System.err.println("Lỗi: Ngày kết thúc không thể trước ngày bắt đầu!");
            return false;
        }

        return tamTruTamVangDAO.insert(tttv);
    }

    public List<TamTruTamVang> getByLoaiHinh(String loaiHinh) {
        return tamTruTamVangDAO.getByLoaiHinh(loaiHinh);
    }

    public List<TamTruTamVang> getByHoTen(String hoTen) {
        return tamTruTamVangDAO.getByHoTen(hoTen);
    }

    public boolean deleteExpiredRecordsToday() {
        LocalDate today = LocalDate.now();
        java.sql.Date sqlToday = java.sql.Date.valueOf(today);

        return tamTruTamVangDAO.deleteByExpirationDate(sqlToday);
    }
}