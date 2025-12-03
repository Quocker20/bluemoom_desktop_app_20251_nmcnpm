package com.bluemoon.app.view;

import com.bluemoon.app.controller.HoKhauController;
import com.bluemoon.app.model.HoKhau;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.List;

public class HoKhauPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private HoKhauController controller;
    private JTextField txtSearch;
    private List<HoKhau> currentList;

    private final Color COL_PRIMARY = new Color(52, 152, 219);
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public HoKhauPanel() {
        this.controller = new HoKhauController();
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

        JLabel lblTitle = new JLabel("Danh sách hộ khẩu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBox.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 10, 0, 0)));
        txtSearch.addActionListener(e -> handleSearch());
        toolBox.add(txtSearch);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(60, 40));
        btnSearch.setBackground(new Color(240, 240, 240));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> handleSearch());
        toolBox.add(btnSearch);

        RoundedButton btnAdd = new RoundedButton("Thêm hộ mới");
        btnAdd.setPreferredSize(new Dimension(140, 40));
        btnAdd.setBackground(COL_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ThemHoKhauDialog dialog = new ThemHoKhauDialog(parentFrame, this);
            dialog.setVisible(true);
        });
        toolBox.add(btnAdd);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "STT", "Mã hộ", "Tên chủ hộ", "Diện tích (m2)", "Số điện thoại", "Thao tác" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(tableModel);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(5).setMinWidth(180);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(5).setCellEditor(new TableActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JLabel lblPage = new JLabel("Hiển thị tất cả kết quả   ");
        lblPage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footerPanel.add(lblPage);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        currentList = controller.getAllHoKhau();
        int stt = 1;
        for (HoKhau hk : currentList) {
            tableModel.addRow(
                    new Object[] { stt++, hk.getSoCanHo(), hk.getTenChuHo(), hk.getDienTich(), hk.getSdt(), "" });
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        currentList = controller.searchHoKhau(keyword);
        tableModel.setRowCount(0);
        int stt = 1;
        for (HoKhau hk : currentList) {
            tableModel.addRow(
                    new Object[] { stt++, hk.getSoCanHo(), hk.getTenChuHo(), hk.getDienTich(), hk.getSdt(), "" });
        }
    }

    class PanelAction extends JPanel {
        private JButton btnShowResidentList, btnEdit, btnDelete;

        public PanelAction() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setOpaque(false);
            btnShowResidentList = createBtn("/images/icon_information.png", new Color(46, 204, 113));
            btnEdit = createBtn("/images/icon_edit.png", new Color(243, 156, 18));
            btnDelete = createBtn("/images/icon_delete.png", new Color(231, 76, 60));
            add(btnShowResidentList);
            add(btnEdit);
            add(btnDelete);
        }

        private JButton createBtn(String iconPath, Color color) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(30, 30));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(color, 1));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setToolTipText("Thao tác");
            URL url = getClass().getResource(iconPath);
            if (url != null) {
                ImageIcon icon = new ImageIcon(
                        new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                btn.setIcon(icon);
            } else {
                btn.setText("•");
                btn.setForeground(color);
            }
            return btn;
        }

        public void initEvent(int row) {
            btnShowResidentList.addActionListener(e -> {
                if (row >= 0 && row < currentList.size()) {
                    HoKhau selectedHk = currentList.get(row);
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(HoKhauPanel.this);
                    QuanLyNhanKhauDialog dialog = new QuanLyNhanKhauDialog(parentFrame, selectedHk);
                    dialog.setVisible(true);
                }
            });
            btnEdit.addActionListener(e -> {
                if (row >= 0 && row < currentList.size()) {
                    HoKhau selectedHk = currentList.get(row);
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(HoKhauPanel.this);
                    ThemHoKhauDialog dialog = new ThemHoKhauDialog(parentFrame, HoKhauPanel.this);
                    dialog.setEditData(selectedHk);
                    dialog.setVisible(true);
                    if (table.getCellEditor() != null)
                        table.getCellEditor().stopCellEditing();
                }
            });
            btnDelete.addActionListener(e -> {
                if (row >= 0 && row < currentList.size()) {
                    if (table.getCellEditor() != null)
                        table.getCellEditor().stopCellEditing();
                    HoKhau selectedHk = currentList.get(row);
                    int confirm = JOptionPane.showConfirmDialog(HoKhauPanel.this,
                            "Bạn có chắc chắn muốn xóa hộ " + selectedHk.getSoCanHo() + "?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = controller.deleteHoKhau(selectedHk.getMaHo());
                        if (deleted) {
                            JOptionPane.showMessageDialog(HoKhauPanel.this, "Đã xóa thành công!");
                            loadData();
                        } else {
                            JOptionPane.showMessageDialog(HoKhauPanel.this, "Xóa thất bại!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }
    }

    class TableActionCellRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            PanelAction action = new PanelAction();
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