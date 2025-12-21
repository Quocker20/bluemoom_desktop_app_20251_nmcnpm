package com.bluemoon.app.view.thuphi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.bluemoon.app.controller.thuphi.ThuPhiController;
import com.bluemoon.app.model.CongNo;

public class ThuPhiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final ThuPhiController Controller;

    private JTextField txtSearch;
    private List<CongNo> currentList;

    private int currentMonth;
    private int currentYear;

    // Colors

    private final Color COL_WARNING = new Color(230, 126, 34); // Màu cam (Tạo đợt)
    private final Color COL_PURPLE = new Color(155, 89, 182); // Màu tím (Thêm lẻ)
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public ThuPhiPanel() {
        // [QUAN TRỌNG] Khởi tạo Controller để tránh lỗi NullPointerException
        this.Controller = new ThuPhiController();

        Calendar cal = Calendar.getInstance();
        this.currentMonth = cal.get(Calendar.MONTH) + 1;
        this.currentYear = cal.get(Calendar.YEAR);

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

        JLabel lblTitle = new JLabel("Quản lý Công nợ (T" + currentMonth + "/" + currentYear + ")");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBox.setOpaque(false);

        // Ô tìm kiếm
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(30, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 10, 0, 10)));
        txtSearch.addActionListener(e -> loadData()); // Enter để tìm
        toolBox.add(txtSearch);

        // [NÚT 1] Thêm công nợ đơn lẻ (Màu Tím)
        RoundedButton btnAddSingle = new RoundedButton("Thêm công nợ");
        btnAddSingle.setPreferredSize(new Dimension(140, 40));
        btnAddSingle.setBackground(COL_PURPLE);
        btnAddSingle.setForeground(Color.WHITE);
        btnAddSingle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddSingle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddSingle.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            ThemCongNoDialog dialog = new ThemCongNoDialog(parent, this, currentMonth, currentYear);
            dialog.setVisible(true);
        });
        toolBox.add(btnAddSingle);

        // [NÚT 2] Tạo đợt thu mới (Màu Cam)
        RoundedButton btnCreatePeriod = new RoundedButton("Tạo đợt thu mới");
        btnCreatePeriod.setPreferredSize(new Dimension(150, 40));
        btnCreatePeriod.setBackground(COL_WARNING);
        btnCreatePeriod.setForeground(Color.WHITE);
        btnCreatePeriod.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCreatePeriod.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreatePeriod.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn tạo công nợ cho TOÀN BỘ hộ dân tháng " + currentMonth + "/" + currentYear + "?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int recordsCreated = Controller.tinhPhiTuDong(currentMonth, currentYear);
                if (recordsCreated > 0) {
                    JOptionPane.showMessageDialog(this, "Thành công! Đã tạo " + recordsCreated + " bản ghi công nợ.");
                    loadData();
                } else if (recordsCreated == -1) {
                    JOptionPane.showMessageDialog(this, "Tháng này đã được tính phí rồi!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu hộ khẩu hoặc phí bắt buộc để tính.");
                }
            }
        });
        toolBox.add(btnCreatePeriod);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. TABLE
        String[] columnNames = { "STT", "Căn hộ", "Tên khoản phí", "Phải đóng", "Đã đóng", "Còn thiếu", "Trạng thái",
                "Thao tác" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6)
                    return Integer.class; // Cột trạng thái dùng INT (0/1)
                return super.getColumnClass(columnIndex);
            }
        };

        table = new JTable(tableModel);
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);
        cm.getColumn(1).setPreferredWidth(80);
        cm.getColumn(2).setPreferredWidth(200);
        cm.getColumn(7).setMinWidth(120);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 20));

        cm.getColumn(3).setCellRenderer(rightRenderer);
        cm.getColumn(4).setCellRenderer(rightRenderer);
        cm.getColumn(5).setCellRenderer(rightRenderer);
        cm.getColumn(6).setCellRenderer(new StatusCellRenderer());
        cm.getColumn(7).setCellRenderer(new TableActionCellRender());
        cm.getColumn(7).setCellEditor(new TableActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        String keyword = txtSearch.getText().trim();
        currentList = Controller.getDanhSachCongNo(currentMonth, currentYear, keyword);

        DecimalFormat df = new DecimalFormat("#,###");
        int stt = 1;

        for (CongNo item : currentList) {
            String soCanHo = item.getSoCanHo();
            if (soCanHo == null)
                soCanHo = "N/A";

            tableModel.addRow(new Object[] {
                    stt++,
                    soCanHo,
                    item.getTenKhoanPhi(),
                    df.format(item.getSoTienPhaiDong()),
                    df.format(item.getSoTienDaDong()),
                    df.format(item.getSoTienConThieu()),
                    item.getTrangThai(),
                    ""
            });
        }
    }

    // ==================================================================
    // UI COMPONENTS & RENDERERS
    // ==================================================================

    class PanelAction extends JPanel {
        private JButton btnPay;

        public PanelAction() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setOpaque(false);
            btnPay = createBtn("/images/icon_pay.png", new Color(46, 204, 113));
            add(btnPay);
        }

        // Load Icon an toàn (tránh lỗi URL null)
        private Icon loadIcon(String path) {
            try {
                URL url = getClass().getResource(path);
                if (url != null) {
                    return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                }
            } catch (Exception e) {
            }
            return null;
        }

        private JButton createBtn(String iconPath, Color color) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(30, 30));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(new LineBorder(color, 1));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            Icon icon = loadIcon(iconPath);
            if (icon != null)
                btn.setIcon(icon);
            else {
                btn.setText("$");
                btn.setForeground(color);
            }
            return btn;
        }

        public void updateStatus(int trangThai) {
            this.removeAll();
            if (trangThai == 0)
                this.add(btnPay); // Chưa đóng -> Hiện nút Thu tiền
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

                    // Dừng edit để tránh lỗi table
                    if (table.getCellEditor() != null)
                        table.getCellEditor().stopCellEditing();
                    loadData();
                }
            });

        }
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel lbl = new JLabel();
            int trangThai = (value instanceof Integer) ? (int) value : 0;
            if (trangThai == 1) {
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
            int trangThai = (int) table.getValueAt(row, 6);
            action.updateStatus(trangThai);
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
            int trangThai = (int) table.getValueAt(row, 6);
            action.updateStatus(trangThai);
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