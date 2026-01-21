package com.bluemoon.app.controller.statistic;

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

import com.bluemoon.app.dao.resident.ResidencyRecordDAO;
import com.bluemoon.app.dao.statistic.ReportDAO;
import com.bluemoon.app.model.Invoice;
import com.bluemoon.app.model.ResidencyRecord;

/**
 * Controller for handling Statistics and Reporting (Excel Export).
 */
public class ReportController {

    private final ReportDAO reportDAO;
    private final ResidencyRecordDAO residencyRecordDAO;
    private final Logger logger;

    public ReportController() {
        this.reportDAO = new ReportDAO();
        this.residencyRecordDAO = new ResidencyRecordDAO();
        this.logger = Logger.getLogger(ReportController.class.getName());
    }

    /**
     * Get financial statistics for a specific month.
     * @return Map containing 'totalRevenue' and 'totalDebt'.
     */
    public Map<String, Double> getFinancialStats(int month, int year) {
        try {
            return reportDAO.getFinancialStats(month, year);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportController] Error in getFinancialStats", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get demographic statistics (Gender distribution).
     * @return Map containing counts for 'Male', 'Female', etc.
     */
    public Map<String, Integer> getDemographicStats() {
        try {
            return reportDAO.getDemographicStats();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportController] Error in getDemographicStats", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get detailed report of invoices/debts.
     */
    public List<Invoice> getReportDetails(int month, int year) {
        try {
            return reportDAO.getReportDetails(month, year);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportController] Error in getReportDetails", e);
            return Collections.emptyList();
        }
    }

    /**
     * Export Residency Records (Temporary Residence/Absence) to Excel.
     */
    public boolean exportResidencyReport(File file) {
        logger.info("[ReportController] Starting Excel export: " + file.getAbsolutePath());

        try (Workbook workbook = new XSSFWorkbook()) {

            // --- SHEET 1: TEMPORARY RESIDENCE ---
            Sheet sheetTemp = workbook.createSheet("Temporary Residence List");
            createHeader(sheetTemp, new String[] { "ID", "Resident ID", "Full Name", "Start Date", "End Date", "Reason" });

            // Note: Ensure your DB data uses "Temporary" or update this string to match your DB value (e.g. "TamTru")
            List<ResidencyRecord> listTemp = residencyRecordDAO.getByType("Temporary");

            int rowIdx = 1;
            for (ResidencyRecord item : listTemp) {
                Row row = sheetTemp.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getResidentId());
                row.createCell(2).setCellValue(item.getResidentName());
                row.createCell(3).setCellValue(item.getStartDate() != null ? item.getStartDate().toString() : "");
                row.createCell(4).setCellValue(item.getEndDate() != null ? item.getEndDate().toString() : "Indefinite");
                row.createCell(5).setCellValue(item.getReason());
            }

            // --- SHEET 2: ABSENCE ---
            Sheet sheetAbsence = workbook.createSheet("Absence List");
            createHeader(sheetAbsence, new String[] { "ID", "Resident ID", "Full Name", "Start Date", "End Date", "Reason" });

            // Note: Ensure your DB data uses "Absence" or update this string (e.g. "TamVang")
            List<ResidencyRecord> listAbsence = residencyRecordDAO.getByType("Absence");

            rowIdx = 1;
            for (ResidencyRecord item : listAbsence) {
                Row row = sheetAbsence.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getResidentId());
                row.createCell(2).setCellValue(item.getResidentName());
                row.createCell(3).setCellValue(item.getStartDate() != null ? item.getStartDate().toString() : "");
                row.createCell(4).setCellValue(item.getEndDate() != null ? item.getEndDate().toString() : "");
                row.createCell(5).setCellValue(item.getReason());
            }

            // Auto-size columns for better visibility
            for (int i = 0; i < 6; i++) {
                sheetTemp.autoSizeColumn(i);
                sheetAbsence.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            logger.info("[ReportController] Excel export successful.");
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "[ReportController] IO Error during export", e);
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[ReportController] SQL Error during data fetch", e);
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