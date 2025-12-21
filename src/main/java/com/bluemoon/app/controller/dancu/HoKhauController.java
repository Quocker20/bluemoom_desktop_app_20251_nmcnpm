package com.bluemoon.app.controller.dancu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.dancu.HoKhauDAO;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;

/**
 * Controller quản lý Hộ khẩu.
 */
public class HoKhauController {
    private static final double MAX_AREA = 10000.0;
    private final HoKhauDAO hoKhauDAO;
    private final Logger logger;

    public HoKhauController() {
        this.hoKhauDAO = new HoKhauDAO();
        this.logger = Logger.getLogger(HoKhauController.class.getName());
    }

    /**
     * Lấy tất cả dữ liệu hộ khẩu chưa bị xóa mềm.
     * 
     * @return List<HoKhau>
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
     * Thêm mới hộ khẩu kèm chủ hộ (Transaction).
     * 
     * @param hk    Đối tượng Hộ khẩu
     * @param chuHo Đối tượng Chủ hộ
     * @return true nếu thành công
     */
    public boolean addHoKhauWithChuHo(HoKhau hk, NhanKhau chuHo) {
        if (!validateHoKhau(hk))
            return false;

        if (chuHo == null || chuHo.getHoTen() == null || chuHo.getHoTen().isEmpty()) {
            logger.warning("Lỗi: Thông tin chủ hộ không hợp lệ!");
            return false;
        }

        try {
            if (hoKhauDAO.checkExist(hk.getSoCanHo())) {
                logger.warning("Lỗi: Số căn hộ " + hk.getSoCanHo() + " đã tồn tại!");
                return false;
            }
            return hoKhauDAO.addHoKhauWithChuHo(hk, chuHo);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUCONTROLLER] Loi addHoKhauWithChuHo", e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin hộ khẩu.
     * 
     * @param hk Đối tượng Hộ khẩu
     * @return true nếu thành công
     */
    public boolean updateHoKhau(HoKhau hk) {
        if (!validateHoKhau(hk) || hk.getMaHo() <= 0) {
            return false;
        }
        try {
            return hoKhauDAO.update(hk);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUCONTROLLER] Loi updateHoKhau", e);
            return false;
        }
    }

    /**
     * Logic Xóa mềm hộ khẩu (Soft Delete).
     * 
     * @param maHo Mã hộ khẩu
     * @return true nếu thành công
     */
    public boolean xoaHoKhau(int maHo) {
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

    /**
     * Tìm kiếm hộ khẩu.
     * 
     * @param keyword Từ khóa
     * @return List<HoKhau>
     */
    public List<HoKhau> searchHoKhau(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHoKhau();
        }
        try {
            return hoKhauDAO.search(keyword.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUCONTROLLER] Loi searchHoKhau", e);
            return Collections.emptyList();
        }
    }

    private boolean validateHoKhau(HoKhau hk) {
        return hk != null
                && hk.getSoCanHo() != null && !hk.getSoCanHo().trim().isEmpty()
                && hk.getTenChuHo() != null && !hk.getTenChuHo().trim().isEmpty()
                && hk.getDienTich() > 0 && hk.getDienTich() < MAX_AREA;
    }

    /**
     * Lấy hộ khẩu theo ID.
     * 
     * @param id Mã hộ
     * @return HoKhau
     */
    public HoKhau getById(int id) {
        try {
            return hoKhauDAO.getById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[HOKHAUCONTROLLER] Loi getById", e);
            return null;
        }
    }
}