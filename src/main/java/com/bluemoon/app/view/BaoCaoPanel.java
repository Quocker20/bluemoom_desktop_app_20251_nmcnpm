package com.bluemoon.app.view;

import com.bluemoon.app.controller.BaoCaoController;
import com.bluemoon.app.model.CongNo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class BaoCaoPanel extends JPanel {

    private JComboBox<Integer> cbThang;
    private JComboBox<Integer> cbNam;
    private JButton btnExport;
    private JTable table;
    private DefaultTableModel tableModel;

    // Custom Charts
    private BarChartPanel barChart;
    private PieChartPanel pieChart;

    private final BaoCaoController controller;
    private final Color COL_BG = new Color(245, 247, 250);

    public BaoCaoPanel() {
        this.controller = new BaoCaoController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- 1. HEADER (Filter & Export) ---
        JPanel headerPanel = new RoundedPanel(20, Color.WHITE);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Báo cáo & Thống kê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        toolBox.setOpaque(false);

        toolBox.add(new JLabel("Tháng:"));
        cbThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++)
            cbThang.addItem(i);
        cbThang.setSelectedItem(Calendar.getInstance().get(Calendar.MONTH) + 1);
        cbThang.addActionListener(e -> loadData());
        toolBox.add(cbThang);

        toolBox.add(new JLabel("Năm:"));
        cbNam = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 2; i <= currentYear + 2; i++)
            cbNam.addItem(i);
        cbNam.setSelectedItem(currentYear);
        cbNam.addActionListener(e -> loadData());
        toolBox.add(cbNam);

        // [SỬA] Nút Xuất Excel dùng ColoredButton
        btnExport = new ColoredButton("Xuất Excel", new Color(46, 204, 113));
        btnExport.addActionListener(e -> handleExport());
        toolBox.add(btnExport);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. BODY (Charts & Table) ---
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 20));
        bodyPanel.setOpaque(false);

        // A. Charts Area
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsContainer.setOpaque(false);
        chartsContainer.setPreferredSize(new Dimension(getWidth(), 300));

        // Biểu đồ cột (Doanh thu)
        barChart = new BarChartPanel("DOANH THU & CÔNG NỢ");
        chartsContainer.add(barChart);

        // Biểu đồ tròn (Dân cư)
        pieChart = new PieChartPanel("CƠ CẤU DÂN CƯ");
        chartsContainer.add(pieChart);

        bodyPanel.add(chartsContainer, BorderLayout.NORTH);

        // B. Table Detail
        String[] cols = { "STT", "Mã Hộ (Phòng)", "Chủ Hộ", "Phải Đóng", "Đã Đóng", "Còn Nợ", "Trạng Thái" };
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Bọc table trong panel có tiêu đề
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        JLabel lblTable = new JLabel("CHI TIẾT SỐ LIỆU");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        tablePanel.add(lblTable, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        bodyPanel.add(tablePanel, BorderLayout.CENTER);
        add(bodyPanel, BorderLayout.CENTER);
    }

    private void setupTable() {
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(217, 217, 217));

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(right);
        table.getColumnModel().getColumn(4).setCellRenderer(right);
        table.getColumnModel().getColumn(5).setCellRenderer(right);
    }

    private void loadData() {
        int thang = (int) cbThang.getSelectedItem();
        int nam = (int) cbNam.getSelectedItem();

        // 1. Load Data cho Bar Chart
        Map<String, Double> taiChinh = controller.getThongKeTaiChinh(thang, nam);
        barChart.setData(taiChinh.getOrDefault("TongThu", 0.0), taiChinh.getOrDefault("TongNo", 0.0));

        // 2. Load Data cho Pie Chart
        Map<String, Integer> danCu = controller.getThongKeDanCu();
        pieChart.setData(danCu);

        // 3. Load Data cho Table
        List<CongNo> list = controller.getChiTietBaoCao(thang, nam);
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,###");
        int stt = 1;
        for (CongNo cn : list) {
            String trangThai = cn.getTrangThai() == 1 ? "Đã xong" : "Chưa xong";
            tableModel.addRow(new Object[] {
                    stt++,
                    cn.getSoCanHo(),
                    cn.getTenKhoanPhi(), // Đang dùng tạm field này lưu Tên Chủ Hộ từ DAO
                    df.format(cn.getSoTienPhaiDong()),
                    df.format(cn.getSoTienDaDong()),
                    df.format(cn.getSoTienConThieu()),
                    trangThai
            });
        }
    }

    private void handleExport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("BaoCao_Thang" + cbThang.getSelectedItem() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Đảm bảo đuôi .csv
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }
            controller.xuatBaoCaoExcel(table, fileToSave);
        }
    }

    // =======================================================
    // CUSTOM CHART COMPONENTS
    // =======================================================

    class BarChartPanel extends RoundedPanel {
        private String title;
        private double val1, val2; // Thu, No

        public BarChartPanel(String title) {
            super(20, Color.WHITE);
            this.title = title;
        }

        public void setData(double v1, double v2) {
            this.val1 = v1;
            this.val2 = v2;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int barWidth = 60;
            int maxBarHeight = h - 100;

            // Tiêu đề
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString(title, 20, 30);

            if (val1 == 0 && val2 == 0) {
                g2.drawString("(Chưa có dữ liệu)", w / 2 - 50, h / 2);
                return;
            }

            double maxVal = Math.max(val1, val2);
            if (maxVal == 0)
                maxVal = 1;

            // Cột 1: Thu (Xanh)
            int h1 = (int) ((val1 / maxVal) * maxBarHeight);
            int x1 = w / 3 - barWidth / 2;
            int y1 = h - 50 - h1;
            g2.setColor(new Color(46, 204, 113));
            g2.fillRect(x1, y1, barWidth, h1);
            g2.setColor(Color.BLACK);
            g2.drawString("Thu", x1 + 15, h - 30);
            g2.drawString(formatK(val1), x1, y1 - 5);

            // Cột 2: Nợ (Đỏ)
            int h2 = (int) ((val2 / maxVal) * maxBarHeight);
            int x2 = 2 * w / 3 - barWidth / 2;
            int y2 = h - 50 - h2;
            g2.setColor(new Color(231, 76, 60));
            g2.fillRect(x2, y2, barWidth, h2);
            g2.setColor(Color.BLACK);
            g2.drawString("Nợ", x2 + 20, h - 30);
            g2.drawString(formatK(val2), x2, y2 - 5);

            // Trục hoành
            g2.setColor(Color.GRAY);
            g2.drawLine(20, h - 50, w - 20, h - 50);
        }

        private String formatK(double val) {
            if (val >= 1000000)
                return String.format("%.1f Tr", val / 1000000);
            if (val >= 1000)
                return String.format("%.0f K", val / 1000);
            return String.valueOf((int) val);
        }
    }

    class PieChartPanel extends RoundedPanel {
        private String title;
        private Map<String, Integer> data;
        private final Color[] colors = { new Color(52, 152, 219), new Color(236, 112, 99), new Color(241, 196, 15) };

        public PieChartPanel(String title) {
            super(20, Color.WHITE);
            this.title = title;
        }

        public void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString(title, 20, 30);

            if (data == null || data.isEmpty()) {
                g2.drawString("(Chưa có dữ liệu)", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0)
                return;

            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int x = 40;
            int y = 60;

            int startAngle = 0;
            int i = 0;
            int legendY = 80;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int angle = (int) (entry.getValue() * 360.0 / total);
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);

                // Vẽ chú thích (Legend) bên phải
                g2.fillRect(x + diameter + 30, legendY, 15, 15);
                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey() + ": " + entry.getValue(), x + diameter + 55, legendY + 12);

                startAngle += angle;
                legendY += 30;
                i++;
            }
        }
    }

    // Helper UI Class
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

    class ColoredButton extends JButton {
        public ColoredButton(String text, Color bgColor) {
            super(text);
            setBackground(bgColor);
            setForeground(Color.BLACK); // Chữ đen
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            super.paintComponent(g);
            g2.dispose();
        }
    }
}