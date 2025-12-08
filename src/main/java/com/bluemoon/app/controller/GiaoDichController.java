package com.bluemoon.app.controller;

import com.bluemoon.app.dao.GiaoDichDAO;
import com.bluemoon.app.model.GiaoDich;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GiaoDichController {

    private static final Logger LOGGER = Logger.getLogger(GiaoDichController.class.getName());
    private final GiaoDichDAO giaoDichDAO;

    public GiaoDichController() {
        this.giaoDichDAO = new GiaoDichDAO();
    }

    /**
     * Lấy danh sách giao dịch theo số căn hộ
     * 
     * @param soCanHo
     * @return
     * @throws SQLException
     */
    public List<GiaoDich> getAllBySoCanHo(String soCanHo) throws SQLException {
        LOGGER.log(Level.INFO, "[CONTROLLER] Yeu cau lay danh sach giao dich cho can ho: {0}", soCanHo);

        try {
            List<GiaoDich> result = giaoDichDAO.getAllBySoCanHo(soCanHo);
            LOGGER.log(Level.INFO, "[CONTROLLER] Hoan tat. Tra ve {0} ket qua cho View.", result.size());

            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[CONTROLLER] That bai khi lay du lieu cho can ho {0}. Loi: {1}",
                    new Object[] { soCanHo, e.getMessage() });

            throw e;
        }
    }

    /**
     * Lấy toàn bộ danh sách
     * 
     * @return
     * @throws SQLException
     */
    public List<GiaoDich> getAll() throws SQLException {
        LOGGER.log(Level.INFO, "[CONTROLLER] Yeu cau lay TOAN BO danh sach giao dich");

        try {
            List<GiaoDich> result = giaoDichDAO.getAll();

            LOGGER.log(Level.INFO, "[CONTROLLER] Hoan tat lay toan bo. So luong: {0}", result.size());
            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[CONTROLLER] That bai khi lay toan bo du lieu. Loi: {0}", e.getMessage());
            throw e;
        }
    }
}