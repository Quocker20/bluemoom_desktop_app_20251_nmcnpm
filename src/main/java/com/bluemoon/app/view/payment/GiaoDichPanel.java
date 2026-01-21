package com.bluemoon.app.view.payment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.bluemoon.app.controller.payment.GiaoDichController;
import com.bluemoon.app.controller.payment.ThuPhiController;
import com.bluemoon.app.controller.resident.HoKhauController;
import com.bluemoon.app.model.Fee;
import com.bluemoon.app.model.Payment;

public class GiaoDichPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // Controllers
    private final GiaoDichController gdController;
    private final HoKhauController hkController;
    private final ThuPhiController tpController; // Để lấy tên khoản phí

    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public GiaoDichPanel() {
        this.gdController = new GiaoDichController();
        this.hkController = new HoKhauController();
        this.tpController = new ThuPhiController();

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. HEADER
        RoundedPanel headerPanel = new RoundedPanel(20, COL_HEADER_BG);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Lịch sử Giao dịch");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // ToolBox: Chỉ có ô tìm kiếm
        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBox.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 10, 0, 10)));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập số căn hộ để tìm...");
        txtSearch.addActionListener(e -> loadData()); // Enter để tìm
        toolBox.add(txtSearch);

        // Nút Tìm (Icon kính lúp hoặc text)
        JButton btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 40));
        btnSearch.setBackground(new Color(240, 240, 240));
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> loadData());
        toolBox.add(btnSearch);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. TABLE
        String[] columnNames = { "STT", "Mã GD", "Căn hộ", "Khoản thu", "Số tiền (VNĐ)", "Người nộp", "Ngày nộp",
                "Ghi chú" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp lịch sử
            }
        };

        table = new JTable(tableModel);

        // Style Table (Giống ThuPhiPanel)
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        // Column Widths & Alignments
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setMaxWidth(60); // STT
        cm.getColumn(1).setMaxWidth(80); // Mã GD
        cm.getColumn(2).setPreferredWidth(100); // Căn hộ
        cm.getColumn(3).setPreferredWidth(200); // Khoản thu

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 20));
        cm.getColumn(4).setCellRenderer(rightRenderer); // Số tiền căn phải

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        String keyword = txtSearch.getText().trim();
        List<Payment> list = null;

        if (keyword.isEmpty()) {
            list = gdController.getAll();
        } else {
            list = gdController.getAllBySoCanHo(keyword);
        }

        if (list == null) {
            return;
        }

        DecimalFormat df = new DecimalFormat("#,###");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        int stt = 1;

        // Cache danh sách khoản phí để đỡ query nhiều lần (Simple Optimization)
        List<Fee> listPhi = tpController.getAllKhoanPhi();

        for (Payment gd : list) {
            // Mapping ID -> Name
            String soCanHo = "N/A";
            soCanHo = gd.getRoomNumber();

            String tenKhoanPhi = "N/A";
            // Tìm trong list cache
            for (Fee kp : listPhi) {
                if (kp.getId() == gd.getFeeId()) {
                    tenKhoanPhi = kp.getName();
                    break;
                }
            }

            tableModel.addRow(new Object[] {
                    stt++,
                    gd.getId(),
                    soCanHo,
                    tenKhoanPhi,
                    df.format(gd.getAmount()),
                    gd.getPayerName(),
                    sdf.format(gd.getPaymentDate()),
                    gd.getNote()
            });
        }
    }

    // --- Inner Class RoundedPanel (UI Helper) ---
    class RoundedPanel extends JPanel {
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