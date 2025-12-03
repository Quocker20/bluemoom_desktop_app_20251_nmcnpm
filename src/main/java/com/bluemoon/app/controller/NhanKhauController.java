package com.bluemoon.app.controller;

import com.bluemoon.app.dao.NhanKhauDAO;
import com.bluemoon.app.model.NhanKhau;
import java.util.List;

public class NhanKhauController {
    
    private final NhanKhauDAO nhanKhauDAO;

    public NhanKhauController() {
        this.nhanKhauDAO = new NhanKhauDAO();
    }

    public List<NhanKhau> getNhanKhauByHoKhau(int maHo) {
        return nhanKhauDAO.selectByHoKhau(maHo);
    }

    public boolean addNhanKhau(NhanKhau nk) {
        if (!validate(nk)) return false;

        // Check trùng CCCD (nếu có nhập)
        if (nk.getCccd() != null && !nk.getCccd().trim().isEmpty()) {
            if (nhanKhauDAO.checkCccdExist(nk.getCccd())) {
                System.err.println("Lỗi: Số CCCD " + nk.getCccd() + " đã tồn tại trong hệ thống!");
                return false;
            }
        }

        return nhanKhauDAO.insert(nk);
    }

    public boolean updateNhanKhau(NhanKhau nk) {
        if (!validate(nk) || nk.getMaNhanKhau() <= 0) {
            return false;
        }

        // Logic check trùng CCCD nâng cao: Trừ chính mình ra
        if (nk.getCccd() != null && !nk.getCccd().trim().isEmpty()) {
            boolean isDuplicate = nhanKhauDAO.checkCccdExistForUpdate(nk.getCccd(), nk.getMaNhanKhau());
            if (isDuplicate) {
                System.err.println("Lỗi: Số CCCD " + nk.getCccd() + " đang thuộc về một cư dân khác!");
                return false;
            }
        }

        return nhanKhauDAO.update(nk);
    }

    public boolean deleteNhanKhau(int maNhanKhau) {
        if (maNhanKhau <= 0) return false;
        return nhanKhauDAO.delete(maNhanKhau);
    }

    private boolean validate(NhanKhau nk) {
        if (nk == null) return false;
        if (nk.getHoTen() == null || nk.getHoTen().trim().isEmpty()) return false;
        if (nk.getNgaySinh() == null) return false;
        if (nk.getQuanHe() == null || nk.getQuanHe().trim().isEmpty()) return false;
        return true;
    }
}