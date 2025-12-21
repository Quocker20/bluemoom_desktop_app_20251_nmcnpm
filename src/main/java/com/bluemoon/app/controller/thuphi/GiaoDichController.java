package com.bluemoon.app.controller.thuphi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.thuphi.GiaoDichDAO;
import com.bluemoon.app.model.GiaoDich;

/**
 * Controller quản lý lịch sử giao dịch nộp tiền.
 */
public class GiaoDichController {

    private static final Logger LOGGER = Logger.getLogger(GiaoDichController.class.getName());
    private final GiaoDichDAO giaoDichDAO;

    public GiaoDichController() {
        this.giaoDichDAO = new GiaoDichDAO();
    }

    /**
     * Lấy danh sách giao dịch theo số căn hộ.
     * 
     * @param soCanHo Số căn hộ
     * @return List<GiaoDich>
     */
    public List<GiaoDich> getAllBySoCanHo(String soCanHo) {
        LOGGER.log(Level.INFO, "[GIAODICHCONTROLLER] Yeu cau lay danh sach giao dich cho can ho: {0}", soCanHo);
        List<GiaoDich> result = new ArrayList<>();

        try {
            result = giaoDichDAO.getAllBySoCanHo(soCanHo);
            LOGGER.log(Level.INFO, "[GIAODICHCONTROLLER] Hoan tat. Tra ve {0} ket qua cho View.", result.size());
            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[GIAODICHCONTROLLER] That bai khi lay du lieu cho can ho {0}. Loi: {1}",
                    new Object[] { soCanHo, e.getMessage() });
            return result;
        }
    }

    /**
     * Lấy toàn bộ lịch sử giao dịch của tất cả căn hộ.
     * 
     * @return List<GiaoDich>
     */
    public List<GiaoDich> getAll() {
        LOGGER.log(Level.INFO, "[GIAODICHCONTROLLER] Yeu cau lay TOAN BO danh sach giao dich");
        List<GiaoDich> result = new ArrayList<>();
        try {
            result = giaoDichDAO.getAll();
            LOGGER.log(Level.INFO, "[GIAODICHCONTROLLER] Hoan tat lay toan bo. So luong: {0}", result.size());
            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[GIAODICHCONTROLLER] That bai khi lay toan bo du lieu. Loi: {0}",
                    e.getMessage());
            return result;
        }
    }
}