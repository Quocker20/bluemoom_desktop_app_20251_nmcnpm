package com.bluemoon.app.view;

import com.bluemoon.app.controller.ThuPhiController;

import com.bluemoon.app.model.CongNo;

import javax.swing.*;

import javax.swing.border.EmptyBorder;

import javax.swing.border.LineBorder;

import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;

import javax.swing.table.JTableHeader;

import javax.swing.table.TableColumnModel;

import javax.swing.DefaultCellEditor;

import java.awt.*;

import java.awt.geom.RoundRectangle2D;

import java.net.URL;

import java.text.DecimalFormat;

import java.util.ArrayList;

import java.util.List;

public class ThuPhiPanel extends JPanel {

    private JTable table;

    private DefaultTableModel tableModel;

    private ThuPhiController controller;

    private JTextField txtSearch;

    // Danh sách lưu trữ dữ liệu hiện tại (CongNo)

    private List<CongNo> currentList;

    private final Color COL_PRIMARY = new Color(52, 152, 219);

    private final Color COL_SUCCESS = new Color(46, 204, 113);

    private final Color COL_BG = new Color(245, 247, 250);

    private final Color COL_HEADER_BG = Color.WHITE;

    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public ThuPhiPanel() {

        this.controller = new ThuPhiController();

        initComponents();

        loadData(); // Load data thật từ DAO

    }

    private void initComponents() {

        setLayout(new BorderLayout(0, 20));

        setBackground(COL_BG);

        setBorder(new EmptyBorder(30, 30, 30, 30));

        // ==================================================================

        // 1. HEADER

        // ==================================================================

        RoundedPanel headerPanel = new RoundedPanel(20, COL_HEADER_BG);

        headerPanel.setLayout(new BorderLayout());

        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Quản lý Thu phí & Công nợ");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblTitle.setForeground(new Color(50, 50, 50));

        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        toolBox.setOpaque(false);

        txtSearch = new JTextField(15);

        txtSearch.setPreferredSize(new Dimension(250, 40));

        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtSearch.setLayout(new BorderLayout());

        txtSearch.setBorder(BorderFactory.createCompoundBorder(

                new LineBorder(new Color(220, 220, 220), 1),

                new EmptyBorder(0, 10, 0, 10)

        ));

        JLabel lblIconSearch = new JLabel();

        URL iconUrl = getClass().getResource("/images/icon_search.png");

        if (iconUrl != null) {

            ImageIcon icon = new ImageIcon(
                    new ImageIcon(iconUrl).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));

            lblIconSearch.setIcon(icon);

        } else {

            lblIconSearch.setText("🔍");

        }

        lblIconSearch.setBorder(new EmptyBorder(0, 5, 0, 5));

        lblIconSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        txtSearch.add(lblIconSearch, BorderLayout.EAST);

        toolBox.add(txtSearch);

        RoundedButton btnAddFee = new RoundedButton("Thêm đợt thu");

        btnAddFee.setPreferredSize(new Dimension(130, 40));

        btnAddFee.setBackground(COL_PRIMARY);

        btnAddFee.setForeground(Color.WHITE);

        btnAddFee.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAddFee.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAddFee.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mở dialog Tạo khoản thu mới"));

        toolBox.add(btnAddFee);

        // Đã bỏ nút Thanh toán ở đây theo yêu cầu cũ

        headerPanel.add(toolBox, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ==================================================================

        // 2. BẢNG DỮ LIỆU

        // ==================================================================

        // Cấu trúc bảng khớp với CongNo

        String[] columnNames = { "STT", "Mã hộ", "Mã khoản phí", "Số tiền (VNĐ)", "Đợt thu", "Trạng thái", "Thao tác" };

        tableModel = new DefaultTableModel(columnNames, 0) {

            @Override

            public boolean isCellEditable(int row, int column) {

                return column == 6;

            }

        };

        table = new JTable(tableModel);

        TableColumnModel cm = table.getColumnModel();

        cm.getColumn(0).setPreferredWidth(50);

        cm.getColumn(1).setPreferredWidth(80);

        cm.getColumn(2).setPreferredWidth(200); // Mã khoản phí (hoặc tên nếu join)

        cm.getColumn(3).setPreferredWidth(120); // Số tiền

        cm.getColumn(4).setPreferredWidth(100); // Đợt thu

        cm.getColumn(5).setPreferredWidth(100); // Trạng thái

        cm.getColumn(6).setMinWidth(120);

        JTableHeader header = table.getTableHeader();

        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.setBackground(COL_TABLE_HEADER);

        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.setRowHeight(45);

        table.setSelectionBackground(new Color(232, 240, 254));

        table.setSelectionForeground(Color.BLACK);

        table.setShowVerticalLines(false);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();

        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        leftRenderer.setBorder(new EmptyBorder(0, 20, 0, 0));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();

        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 20));

        cm.getColumn(0).setCellRenderer(leftRenderer);

        cm.getColumn(1).setCellRenderer(leftRenderer);

        cm.getColumn(2).setCellRenderer(leftRenderer);

        cm.getColumn(3).setCellRenderer(rightRenderer);

        cm.getColumn(4).setCellRenderer(leftRenderer);

        cm.getColumn(5).setCellRenderer(new StatusCellRenderer());

        cm.getColumn(6).setCellRenderer(new TableActionCellRender());

        cm.getColumn(6).setCellEditor(new TableActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        footerPanel.setOpaque(false);

        JLabel lblPage = new JLabel("Hiển thị tất cả kết quả   ");

        lblPage.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        footerPanel.add(lblPage);

        add(footerPanel, BorderLayout.SOUTH);

    }

    public void loadData() {

        tableModel.setRowCount(0);

        // Gọi Controller lấy danh sách CongNo (DAO trả về List<CongNo>)

        currentList = controller.getDanhSachCongNo();

        DecimalFormat df = new DecimalFormat("#,###");

        int stt = 1;

        for (CongNo item : currentList) {

            String dotThu = item.getThang() + "/" + item.getNam();

            // Vì DAO chỉ trả về Mã khoản phí (int), ta hiển thị mã hoặc cần join bảng để
            // lấy tên

            // Tạm thời hiển thị Mã khoản phí + ID

            String tenKhoan = item.getTenKhoanPhi();

            tableModel.addRow(new Object[] {

                    stt++,

                    item.getMaHo(),

                    tenKhoan,

                    df.format(item.getSoTienPhaiDong()),

                    dotThu,

                    item.getDone(), // Trạng thái boolean (mapped to Text/Color in Renderer)

                    "" // Button panel

            });

        }

    }

    // ==================================================================

    // CÁC CLASS UI CUSTOM (Giữ nguyên logic hiển thị nút theo trạng thái)

    // ==================================================================

    class PanelAction extends JPanel {

        private JButton btnPay, btnPrint;

        public PanelAction() {

            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

            setOpaque(false);

            btnPay = createBtn("/images/icon_pay.png", new Color(46, 204, 113));

            btnPrint = createBtn("/images/icon_print.png", Color.GRAY);

            add(btnPay);

            add(btnPrint);

        }

        private JButton createBtn(String iconPath, Color color) {

            JButton btn = new JButton();

            btn.setPreferredSize(new Dimension(30, 30));

            btn.setContentAreaFilled(false);

            btn.setFocusPainted(false);

            btn.setBorder(new LineBorder(color, 1));

            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            URL url = getClass().getResource(iconPath);

            if (url != null) {

                ImageIcon icon = new ImageIcon(
                        new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

                btn.setIcon(icon);

            } else {

                btn.setText("$");

                btn.setForeground(color);

            }

            return btn;

        }

        public void updateStatus(boolean isPaid) {

            this.removeAll();

            if (isPaid) {

                this.add(btnPrint);

            } else {

                this.add(btnPay);

            }

            this.revalidate();

            this.repaint();

        }

        public void initEvent(int row) {

            btnPay.addActionListener(e -> {

                if (row >= 0 && row < currentList.size()) {

                    CongNo item = currentList.get(row);

                    JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(ThuPhiPanel.this);

                    ThanhToanDialog dialog = new ThanhToanDialog(parent, ThuPhiPanel.this, item);

                    dialog.setVisible(true);

                    if (table.getCellEditor() != null)
                        table.getCellEditor().stopCellEditing();

                }

            });

            btnPrint.addActionListener(
                    e -> JOptionPane.showMessageDialog(ThuPhiPanel.this, "In biên lai dòng " + (row + 1)));

        }

    }

    class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {

            JLabel lbl = new JLabel();

            boolean isPaid = (boolean) value;

            if (isPaid) {

                lbl.setText("●  Đã đóng");

                lbl.setForeground(new Color(46, 204, 113));

            } else {

                lbl.setText("●  Chưa đóng");

                lbl.setForeground(new Color(231, 76, 60));

            }

            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

            lbl.setHorizontalAlignment(JLabel.CENTER);

            if (isSelected) {

                lbl.setOpaque(true);

                lbl.setBackground(table.getSelectionBackground());

            }

            return lbl;

        }

    }

    class TableActionCellRender extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {

            PanelAction action = new PanelAction();

            boolean isPaid = (boolean) table.getValueAt(row, 5);

            action.updateStatus(isPaid);

            if (isSelected)
                action.setBackground(table.getSelectionBackground());

            else
                action.setBackground(Color.WHITE);

            return action;

        }

    }

    class TableActionCellEditor extends DefaultCellEditor {

        public TableActionCellEditor() {
            super(new JCheckBox());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {

            PanelAction action = new PanelAction();

            boolean isPaid = (boolean) table.getValueAt(row, 5);

            action.updateStatus(isPaid);

            action.initEvent(row);

            action.setBackground(table.getSelectionBackground());

            return action;

        }

    }

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

    class RoundedButton extends JButton {

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

    }

}