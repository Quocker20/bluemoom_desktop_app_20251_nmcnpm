package com.bluemoon.app.controller;

import com.bluemoon.app.dao.BaoCaoDAO;
import com.bluemoon.app.dao.TamTruTamVangDAO;
import com.bluemoon.app.model.CongNo;
import com.bluemoon.app.model.TamTruTamVang;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BaoCaoController {
    
    private final BaoCaoDAO baoCaoDAO;
    private final TamTruTamVangDAO tttvDAO; // Thêm DAO mới

    public BaoCaoController() {
        this.baoCaoDAO = new BaoCaoDAO();
        this.tttvDAO = new TamTruTamVangDAO(); // Khởi tạo
    }

    public Map<String, Double> getThongKeTaiChinh(int thang, int nam) {
        return baoCaoDAO.getThongKeTaiChinh(thang, nam);
    }

    public Map<String, Integer> getThongKeDanCu() {
        return baoCaoDAO.getThongKeDanCu();
    }

    public List<CongNo> getChiTietBaoCao(int thang, int nam) {
        return baoCaoDAO.getChiTietBaoCao(thang, nam);
    }

    // --- [MỚI] LOGIC XUẤT EXCEL TẠM TRÚ - TẠM VẮNG ---
    public boolean xuatBaoCaoTamTruTamVang(File file) {
        // Sử dụng XSSFWorkbook cho định dạng .xlsx (Excel hiện đại)
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // --- SHEET 1: TẠM TRÚ ---
            Sheet sheetTamTru = workbook.createSheet("Danh sách Tạm Trú");
            createHeader(sheetTamTru, new String[]{"ID", "Mã NK", "Họ Tên", "Từ Ngày", "Đến Ngày", "Lý Do"});
            
            // Lấy dữ liệu TamTru từ DAO
            List<TamTruTamVang> listTamTru = tttvDAO.getByLoaiHinh("TamTru");
            
            int rowIdx = 1;
            for (TamTruTamVang item : listTamTru) {
                Row row = sheetTamTru.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getMaTTTV());
                row.createCell(1).setCellValue(item.getMaNhanKhau());
                row.createCell(2).setCellValue(item.getHoTenNhanKhau()); // Lấy tên đã map
                row.createCell(3).setCellValue(item.getTuNgay() != null ? item.getTuNgay().toString() : "");
                row.createCell(4).setCellValue(item.getDenNgay() != null ? item.getDenNgay().toString() : "Vô thời hạn");
                row.createCell(5).setCellValue(item.getLyDo());
            }

            // --- SHEET 2: TẠM VẮNG ---
            Sheet sheetTamVang = workbook.createSheet("Danh sách Tạm Vắng");
            createHeader(sheetTamVang, new String[]{"ID", "Mã NK", "Họ Tên", "Từ Ngày", "Đến Ngày", "Lý Do"});

            // Lấy dữ liệu TamVang từ DAO
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

            // Auto resize cột cho đẹp (Optional)
            for(int i=0; i<6; i++) {
                sheetTamTru.autoSizeColumn(i);
                sheetTamVang.autoSizeColumn(i);
            }

            // Ghi ra file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper tạo Header style
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