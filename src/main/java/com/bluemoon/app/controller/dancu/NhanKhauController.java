package com.bluemoon.app.controller.dancu;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.dancu.NhanKhauDAO;
import com.bluemoon.app.model.NhanKhau;


/**
 * Controller quản lý nhân khẩu.
 */
public class NhanKhauController {

    private final NhanKhauDAO nhanKhauDAO;
    private final Logger logger;

    public NhanKhauController() {
        this.nhanKhauDAO = new NhanKhauDAO();
        this.logger = Logger.getLogger(NhanKhauController.class.getName());
    }

    /**
     * Lấy danh sách nhân khẩu theo mã hộ.
     * 
     * @param maHo Mã hộ khẩu
     * @return List<NhanKhau>
     */
    public List<NhanKhau> getNhanKhauByHoKhau(int maHo) {
        try {
            return nhanKhauDAO.selectByHoKhau(maHo);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NhanKhauController] Loi getNhanKhauByHoKhau", e);
            return Collections.emptyList();
        }
    }

    /**
     * Thêm mới nhân khẩu.
     * 
     * @param nk Đối tượng nhân khẩu
     * @return true nếu thành công
     */
    public boolean addNhanKhau(NhanKhau nk) {
        if (!validate(nk))
            return false;

        try {
            // Check trùng CCCD (nếu có nhập)
            if (nk.getCccd() != null && !nk.getCccd().trim().isEmpty()) {
                if (nhanKhauDAO.checkCccdExist(nk.getCccd())) {
                    logger.warning("Lỗi: Số CCCD " + nk.getCccd() + " đã tồn tại trong hệ thống!");
                    return false;
                }
            }
            return nhanKhauDAO.insert(nk);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NhanKhauController] Loi addNhanKhau", e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin nhân khẩu.
     * 
     * @param nk Đối tượng nhân khẩu
     * @return true nếu thành công
     */
    public boolean updateNhanKhau(NhanKhau nk) {
        if (!validate(nk) || nk.getMaNhanKhau() <= 0) {
            return false;
        }

        try {
            // Logic check trùng CCCD nâng cao: Trừ chính mình ra
            if (nk.getCccd() != null && !nk.getCccd().trim().isEmpty()) {
                boolean isDuplicate = nhanKhauDAO.checkCccdExistForUpdate(nk.getCccd(), nk.getMaNhanKhau());
                if (isDuplicate) {
                    logger.warning("Lỗi: Số CCCD " + nk.getCccd() + " đang thuộc về một cư dân khác!");
                    return false;
                }
            }
            return nhanKhauDAO.update(nk);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NhanKhauController] Loi updateNhanKhau", e);
            return false;
        }
    }

    /**
     * Xóa nhân khẩu. (SOFT DELETE)
     * 
     * @param maNhanKhau Mã nhân khẩu
     * @return true nếu thành công
     */
    public boolean deleteNhanKhau(int maNhanKhau) {
        if (maNhanKhau <= 0)
            return false;
        try {
            return nhanKhauDAO.delete(maNhanKhau);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NhanKhauController] Loi deleteNhanKhau", e);
            return false;
        }
    }

    /**
     * 
     * @param nk
     * @return
     */
    private boolean validate(NhanKhau nk) {
        if (nk == null)
            return false;
        if (nk.getHoTen() == null || nk.getHoTen().trim().isEmpty())
            return false;
        if (nk.getNgaySinh() == null)
            return false;
        if (nk.getQuanHe() == null || nk.getQuanHe().trim().isEmpty())
            return false;
        return true;
    }


/**
     * Lấy tất cả nhân khẩu.
     * @return
     */
    public List<NhanKhau> getAllNhanKhau() {
        List<NhanKhau> list = Collections.emptyList();
        try {
            logger.info("[NHANKHAUCONTROLLER] Bat dau yeu cau lay tat ca nhan khau");
            list = nhanKhauDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "[NHANKHAUCONTROLLER] Yeu cau that bai", e);
        }
        return list;
    }

    /**
     * Tìm kiếm nhân khẩu theo từ khóa.
     * @param keyword
     * @return
     */
    public List<NhanKhau> searchNhanKhau(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNhanKhau();
        }
        
        try {
            return nhanKhauDAO.search(keyword.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[NHANKHAUCONTROLLER] Loi searchNhanKhau", e);
            return Collections.emptyList();
        }
    }
}