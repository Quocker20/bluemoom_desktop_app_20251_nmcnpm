package com.bluemoon.app.controller.dancu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bluemoon.app.dao.dancu.HoKhauDAO;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;

public class HoKhauController {

    private HoKhauDAO hoKhauDAO;

    public HoKhauController() {
        this.hoKhauDAO = new HoKhauDAO();
    }

    public List<HoKhau> getAll() {
        try {
            return hoKhauDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<HoKhau> search(String keyword) {
        try {
            return hoKhauDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addHoKhauWithChuHo(HoKhau hk, NhanKhau chuHo) {
        try {
            return hoKhauDAO.addHoKhauWithChuHo(hk, chuHo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public HoKhau getBySoCanHo(String soCanHo) {
        try {
            return hoKhauDAO.getBySoCanHo(soCanHo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public HoKhau getById(int maHo) {
        try {
            return hoKhauDAO.getById(maHo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // [MỚI] Hàm xóa mềm hộ khẩu
    public boolean softDelete(int maHo) {
        try {
            return hoKhauDAO.softDelete(maHo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}