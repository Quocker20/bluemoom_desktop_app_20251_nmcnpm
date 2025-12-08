package com.bluemoon.app.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.HoKhauDAO;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;

public class HoKhauController {
    private static final double MAX_AREA = 10000.0; // Giới hạn diện tích hợp lý
    private final HoKhauDAO hoKhauDAO;
    private static final Logger logger = Logger.getLogger(HoKhauDAO.class.getName());

    public HoKhauController() {
        this.hoKhauDAO = new HoKhauDAO();
    }

    /**
     * Lay tat ca du lieu ho khau chua bi xoa mem
     * 
     * @return
     */
    public List<HoKhau> getAllHoKhau() {
        List<HoKhau> list = new ArrayList<>();

        try {
            logger.log(Level.INFO, "[HOKHAUCONTROLLER] Yeu cau lay du lieu tu CSDL");
            list = hoKhauDAO.getAll();
            logger.log(Level.INFO, "[HOKHAUCONTROLLER] Yeu cau hoan tat, tra ve {0} ban ghi Ho_Khau", list.size());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "[HOKHAUCONTROLLER] Yeu cau that bai, loi {0}", e);
        }

        return list;
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

    /**
     * Logic Xoa mem ho khau (Soft Delete)
     * 
     * @param maHo
     * @return
     * @throws SQLException
     */
    public boolean xoaHoKhau(int maHo) {
        // 1. Validate dữ liệu đầu vào
        if (maHo <= 0) {
            logger.log(Level.WARNING, "[CONTROLLER] Ma ho khong hop le: {0}", maHo);
            return false;
        }

        try {
            boolean isDeleted = hoKhauDAO.softDelete(maHo);

            if (isDeleted) {
                logger.log(Level.INFO, "[CONTROLLER] Yeu cau xoa ho khau {0} thanh cong.", maHo);
            } else {
                logger.log(Level.WARNING,
                        "[CONTROLLER] Yeu cau xoa ho khau {0} that bai (Co the do con no hoac ID khong ton tai).",
                        maHo);
            }

            return isDeleted;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[CONTROLLER] Loi he thong khi goi DAO xoa ho khau: ", e);

            return false;
        }
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