package com.bluemoon.app.controller.resident;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bluemoon.app.dao.resident.HoKhauDAO;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Resident;

public class HoKhauController {

    private HoKhauDAO hoKhauDAO;

    public HoKhauController() {
        this.hoKhauDAO = new HoKhauDAO();
    }

    public List<Household> getAll() {
        try {
            return hoKhauDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Household> search(String keyword) {
        try {
            return hoKhauDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addHoKhauWithChuHo(Household hk, Resident chuHo) {
        try {
            return hoKhauDAO.addHoKhauWithChuHo(hk, chuHo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Household getBySoCanHo(String soCanHo) {
        try {
            return hoKhauDAO.getBySoCanHo(soCanHo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Household getById(int maHo) {
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