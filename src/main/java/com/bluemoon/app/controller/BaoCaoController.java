package com.bluemoon.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bluemoon.app.dao.BaoCaoDAO;
import com.bluemoon.app.dao.TamTruTamVangDAO;
import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.model.TamTruTamVang;

/**
 * Controller xử lý các nghiệp vụ báo cáo thống kê và xuất file Excel.
 */
public class BaoCaoController {

    private final BaoCaoDAO baoCaoDAO;
    private final TamTruTamVangDAO tttvDAO;
    private final Logger logger;

    public BaoCaoController() {
        this.baoCaoDAO = new BaoCaoDAO();
        this.tttvDAO = new TamTruTamVangDAO();
        this.logger = Logger.getLogger(BaoCaoController.class.getName());
    }

    /**
     * Lấy thống kê tài chính theo tháng.
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return Map chứa tổng thu và tổng nợ
     */
    public Map<String, Double> getThongKeTaiChinh(int thang, int nam) {
        try {
            return baoCaoDAO.getThongKeTaiChinh(thang, nam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BaoCaoController] Loi getThongKeTaiChinh", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Lấy thống kê cơ cấu dân cư.
     * 
     * @return Map chứa số lượng nam/nữ
     */
    public Map<String, Integer> getThongKeDanCu() {
        try {
            return baoCaoDAO.getThongKeDanCu();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BaoCaoController] Loi getThongKeDanCu", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Lấy chi tiết báo cáo công nợ.
     * 
     * @param thang Tháng
     * @param nam   Năm
     * @return List<CongNo>
     */
    public List<CongNo> getChiTietBaoCao(int thang, int nam) {
        try {
            return baoCaoDAO.getChiTietBaoCao(thang, nam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BaoCaoController] Loi getChiTietBaoCao", e);
            return Collections.emptyList();
        }
    }

    /**
     * Xuất báo cáo Tạm trú - Tạm vắng ra file Excel.
     * 
     * @param file File đích để lưu
     * @return true nếu xuất thành công
     */
    public boolean xuatBaoCaoTamTruTamVang(File file) {
        logger.info("[BaoCaoController] Bat dau xuat Excel: " + file.getAbsolutePath());

        try (Workbook workbook = new XSSFWorkbook()) {

            // --- SHEET 1: TẠM TRÚ ---
            Sheet sheetTamTru = workbook.createSheet("Danh sách Tạm Trú");
            createHeader(sheetTamTru, new String[] { "ID", "Mã NK", "Họ Tên", "Từ Ngày", "Đến Ngày", "Lý Do" });

            List<TamTruTamVang> listTamTru = tttvDAO.getByLoaiHinh("TamTru");

            int rowIdx = 1;
            for (TamTruTamVang item : listTamTru) {
                Row row = sheetTamTru.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getMaTTTV());
                row.createCell(1).setCellValue(item.getMaNhanKhau());
                row.createCell(2).setCellValue(item.getHoTenNhanKhau());
                row.createCell(3).setCellValue(item.getTuNgay() != null ? item.getTuNgay().toString() : "");
                row.createCell(4)
                        .setCellValue(item.getDenNgay() != null ? item.getDenNgay().toString() : "Vô thời hạn");
                row.createCell(5).setCellValue(item.getLyDo());
            }

            // --- SHEET 2: TẠM VẮNG ---
            Sheet sheetTamVang = workbook.createSheet("Danh sách Tạm Vắng");
            createHeader(sheetTamVang, new String[] { "ID", "Mã NK", "Họ Tên", "Từ Ngày", "Đến Ngày", "Lý Do" });

            List<TamTruTamVang> listTamVang = tttvDAO.getByLoaiHinh("TamVang");

            rowIdx = 1;
            for (TamTruTamVang item : listTamVang) {
                Row row = sheetTamVang.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getMaTTTV());
                row.createCell(1).setCellValue(item.getMaNhanKhau());
                row.createCell(2).setCellValue(item.getHoTenNhanKhau());
                row.createCell(3).setCellValue(item.getTuNgay() != null ? item.getTuNgay().toString() : "");
                row.createCell(4).setCellValue(item.getDenNgay() != null ? item.getDenNgay().toString() : "");
                row.createCell(5).setCellValue(item.getLyDo());
            }

            for (int i = 0; i < 6; i++) {
                sheetTamTru.autoSizeColumn(i);
                sheetTamVang.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            logger.info("[BaoCaoController] Xuat Excel thanh cong.");
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "[BaoCaoController] Loi IO khi xuat Excel", e);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BaoCaoController] Loi SQL khi lay du lieu xuat Excel", e);
            return false;
        }
    }

    private void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }
}