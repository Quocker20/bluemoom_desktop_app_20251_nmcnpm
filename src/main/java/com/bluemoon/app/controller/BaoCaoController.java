package com.bluemoon.app.controller;

import com.bluemoon.app.dao.BaoCaoDAO;
import com.bluemoon.app.model.CongNo;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BaoCaoController {
    private final BaoCaoDAO baoCaoDAO;

    public BaoCaoController() {
        this.baoCaoDAO = new BaoCaoDAO();
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

    // --- LOGIC XUẤT EXCEL (CSV) ---
    public void xuatBaoCaoExcel(JTable table, File file) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Thêm BOM để Excel nhận diện đúng tiếng Việt UTF-8
            bw.write("\ufeff");

            TableModel model = table.getModel();

            // 1. Viết tiêu đề cột
            for (int i = 0; i < model.getColumnCount(); i++) {
                bw.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1)
                    bw.write(",");
            }
            bw.newLine();

            // 2. Viết dữ liệu
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    String data = (value == null) ? "" : value.toString();

                    // Xử lý nếu dữ liệu có dấu phẩy (bọc trong ngoặc kép)
                    if (data.contains(",")) {
                        data = "\"" + data + "\"";
                    }
                    // Xử lý xóa các ký tự HTML nếu có (do renderer)
                    data = data.replaceAll("\\<.*?\\>", "");

                    bw.write(data);
                    if (j < model.getColumnCount() - 1)
                        bw.write(",");
                }
                bw.newLine();
            }

            JOptionPane.showMessageDialog(null, "Xuất báo cáo thành công!\nĐường dẫn: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất file: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}