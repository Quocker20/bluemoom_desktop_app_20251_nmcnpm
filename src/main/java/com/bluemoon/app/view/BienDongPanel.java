package com.bluemoon.app.view;

import com.bluemoon.app.controller.TamTruTamVangController;
import com.bluemoon.app.model.TamTruTamVang;
import com.bluemoon.app.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BienDongPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFilter;
    private TamTruTamVangController controller;

    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public BienDongPanel() {
        this.controller = new TamTruTamVangController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        RoundedPanel headerPanel = new RoundedPanel(20, COL_HEADER_BG);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Quản lý Biến động cư trú");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        toolBox.setOpaque(false);

        cbFilter = new JComboBox<>(new String[] { "Tất cả", "Tạm trú", "Tạm vắng", "Khai tử" });
        cbFilter.setPreferredSize(new Dimension(150, 40));
        cbFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        cbFilter.setBackground(Color.WHITE);
        cbFilter.setFocusable(false);
        cbFilter.addActionListener(e -> handleFilter());
        toolBox.add(cbFilter);

        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 40));
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 10, 0, 0)));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên nhân khẩu...");
        txtSearch.addActionListener(e -> handleSearch());
        toolBox.add(txtSearch);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(60, 40));
        btnSearch.setBackground(new Color(240, 240, 240));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        btnSearch.addActionListener(e -> handleSearch());
        toolBox.add(btnSearch);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "STT", "Mã giấy", "Mã NK/Tên", "Loại hình", "Từ ngày", "Đến ngày", "Lý do" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableStyle() {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 16));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.setFont(new Font("Inter", Font.PLAIN, 16));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        TableColumnModel colModel = table.getColumnModel();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));

        int[] leftCols = { 0, 1, 2, 4, 5, 6 };
        for (int i : leftCols)
            colModel.getColumn(i).setCellRenderer(leftRenderer);

        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));

        colModel.getColumn(0).setCellRenderer(leftRenderer);
        colModel.getColumn(0).setMaxWidth(60);

        colModel.getColumn(1).setCellRenderer(leftRenderer);
        colModel.getColumn(1).setPreferredWidth(100);
        colModel.getColumn(1).setMaxWidth(150);

        colModel.getColumn(2).setCellRenderer(leftRenderer);
        colModel.getColumn(2).setPreferredWidth(200);

        colModel.getColumn(3).setCellRenderer(new TypeCellRenderer());
        colModel.getColumn(3).setMinWidth(150);
        colModel.getColumn(3).setMaxWidth(180);

        colModel.getColumn(4).setCellRenderer(leftRenderer);
        colModel.getColumn(4).setPreferredWidth(110);
        colModel.getColumn(4).setMaxWidth(130);

        colModel.getColumn(5).setCellRenderer(leftRenderer);
        colModel.getColumn(5).setPreferredWidth(110);
        colModel.getColumn(5).setMaxWidth(130);

    }

    public void loadData() {
        updateTable(controller.getAllTamTruTamVang());
    }

    private void handleFilter() {
        String selected = (String) cbFilter.getSelectedItem();
        List<TamTruTamVang> list;
        if (selected == null || selected.equals("Tất cả")) {
            list = controller.getAllTamTruTamVang();
        } else {
            String loaiHinh = "";
            switch (selected) {
                case "Tạm trú":
                    loaiHinh = AppConstants.TAM_TRU;
                    break;
                case "Tạm vắng":
                    loaiHinh = AppConstants.TAM_VANG;
                    break;
                case "Khai tử":
                    loaiHinh = AppConstants.KHAI_TU;
                    break;
            }
            list = controller.getByLoaiHinh(loaiHinh);
        }
        updateTable(list);
        txtSearch.setText("");
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        List<TamTruTamVang> list;
        if (keyword.isEmpty()) {
            list = controller.getAllTamTruTamVang();
            cbFilter.setSelectedIndex(0);
        } else {
            list = controller.getByHoTen(keyword);
        }
        updateTable(list);
    }

    private void updateTable(List<TamTruTamVang> list) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int stt = 1;
        for (TamTruTamVang item : list) {
            String tenHienThi = item.getHoTenNhanKhau() != null ? item.getHoTenNhanKhau()
                    : "Mã NK: " + item.getMaNhanKhau();

            String loaiHienThi = item.getLoaiHinh();
            if (AppConstants.TAM_TRU.equalsIgnoreCase(loaiHienThi))
                loaiHienThi = "Tạm trú";
            else if (AppConstants.TAM_VANG.equalsIgnoreCase(loaiHienThi))
                loaiHienThi = "Tạm vắng";
            else if (AppConstants.KHAI_TU.equalsIgnoreCase(loaiHienThi))
                loaiHienThi = "Khai tử";

            tableModel.addRow(new Object[] {
                    stt++, item.getMaTTTV(), tenHienThi, loaiHienThi,
                    (item.getTuNgay() != null ? sdf.format(item.getTuNgay()) : ""),
                    (item.getDenNgay() != null ? sdf.format(item.getDenNgay()) : ""),
                    item.getLyDo()
            });
        }
    }

    static class TypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String type = (String) value;
            setFont(new Font("Inter", Font.BOLD, 14));
            setHorizontalAlignment(JLabel.LEFT);
            setBorder(new EmptyBorder(0, 15, 0, 0));

            if (type != null && !isSelected) {
                if (type.contains("Tạm trú"))
                    setForeground(new Color(46, 204, 113));
                else if (type.contains("Tạm vắng"))
                    setForeground(new Color(243, 156, 18));
                else if (type.contains("Khai tử"))
                    setForeground(Color.RED);
                else
                    setForeground(Color.BLACK);
            } else if (isSelected) {
                setForeground(Color.BLACK);
            }
            return c;
        }
    }

    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}