package com.bluemoon.app.controller;

import com.bluemoon.app.dao.TamTruTamVangDAO;
import com.bluemoon.app.model.TamTruTamVang;
import java.util.List;

public class TamTruTamVangController {
    private TamTruTamVangDAO tamTruTamVangDAO;

    public TamTruTamVangController() {
        this.tamTruTamVangDAO = new TamTruTamVangDAO();
    }

    /**
     * Lay dang sach tat ca tam tru/tam vang
     * 
     * @return List<TamTruTamVang>
     */
    public List<TamTruTamVang> getAllTamTruTamVang() {
        return tamTruTamVangDAO.getAll();
    }

    /**
     * Them moi tam tru/tam vang
     * 
     * @param tttv doi tuong TamTruTamVang
     * @return true neu thanh cong
     */
    public boolean addTamTruTamVang(TamTruTamVang tttv) {
        // 1. Validate du lieu co ban
        if (tttv.getMaNhanKhau() <= 0 ||
                tttv.getLoaiHinh() == null || tttv.getLoaiHinh().trim().isEmpty() ||
                tttv.getTuNgay() == null || tttv.getDenNgay() == null ||
                tttv.getLyDo() == null || tttv.getLyDo().trim().isEmpty()) {
            return false; // Du lieu khong hop le
        }

        // 2. Goi DAO de them
        return tamTruTamVangDAO.insert(tttv);
    }

    /**
     * Tìm kiếm tạm trú tạm vắng theo loai hinh
     * 
     * @param loaiHinh Loai hinh can tim (TamTru/TamVang/KhaiTu)
     * @return List<TamTruTamVang>
     */
    public List<TamTruTamVang> getByLoaiHinh(String loaiHinh) {
        return tamTruTamVangDAO.getByLoaiHinh(loaiHinh);
    }

    /**
     * Tìm kiếm tạm trú tạm vắng theo tên nhân khẩu
     * 
     * @param hoTen Ten nhan khau can tim
     * @return List<TamTruTamVang>
     */
    public List<TamTruTamVang> getByHoTen(String hoTen) {
        return tamTruTamVangDAO.getByHoTen(hoTen);
    }
}
