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
import javax.swing.table.DefaultCellEditor;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.List;

public class HoKhauPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private HoKhauController controller;
    private JTextField txtSearch;

    // Màu sắc chủ đạo
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

        // ==================================================================
        // 1. HEADER (GIỮ NGUYÊN)
        // ==================================================================
        RoundedPanel headerPanel = new RoundedPanel(20, COL_HEADER_BG);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Danh sách hộ khẩu");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBox.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 10, 0, 0)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        toolBox.add(txtSearch);

        JButton btnSearch = new JButton();
        btnSearch.setPreferredSize(new Dimension(45, 40));
        btnSearch.setBackground(new Color(240, 240, 240));
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        URL searchUrl = getClass().getResource("/images/icon_search.png");
        if (searchUrl != null) {
            btnSearch.setIcon(new ImageIcon(new ImageIcon(searchUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } else {
            btnSearch.setText("🔍");
        }
        toolBox.add(btnSearch);

        RoundedButton btnAdd = new RoundedButton("Thêm hộ mới");
        btnAdd.setPreferredSize(new Dimension(140, 40));
        btnAdd.setBackground(COL_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Inter", Font.BOLD, 14));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Thêm đang phát triển!");
        });
        toolBox.add(btnAdd);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ==================================================================
        // 2. BẢNG DỮ LIỆU
        // ==================================================================
        String[] columnNames = {"STT", "Mã hộ", "Tên chủ hộ", "Diện tích (m2)", "Số điện thoại", "Thao tác"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Cho phép sửa cột thao tác
            }
        };

        table = new JTable(tableModel);

        // --- 2.1 CẤU HÌNH KÍCH THƯỚC CỘT (ĐÃ CHỈNH SỬA) ---
        TableColumnModel columnModel = table.getColumnModel();
        
        // Cột 0: STT (Nhỏ, cố định)
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(0).setMaxWidth(80); 

        // Cột 1: Mã hộ (Nhỏ vừa phải)
        columnModel.getColumn(1).setPreferredWidth(120);
        columnModel.getColumn(1).setMaxWidth(150);

        // Cột 2: Tên chủ hộ (ĐÃ GIẢM TỪ 250 -> 200)
        columnModel.getColumn(2).setPreferredWidth(200);

        // Cột 3: Diện tích (Set thêm để cân đối)
        columnModel.getColumn(3).setPreferredWidth(120);

        // Cột 4: Số điện thoại (Set thêm để chứa đủ tiêu đề dài)
        columnModel.getColumn(4).setPreferredWidth(150);

        // Cột 5: Thao tác (Cố định)
        columnModel.getColumn(5).setMinWidth(150);
        columnModel.getColumn(5).setMaxWidth(150);


        // --- 2.2 CẤU HÌNH HEADER VÀ BODY ---
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setFont(new Font("Inter", Font.BOLD, 24));
                lbl.setBackground(COL_TABLE_HEADER);
                lbl.setForeground(Color.BLACK);
                lbl.setHorizontalAlignment(JLabel.LEFT);
                lbl.setBorder(new EmptyBorder(10, 15, 10, 0));
                
                if (column == 5) lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        header.setPreferredSize(new Dimension(header.getWidth(), 50));

        table.setFont(new Font("Inter", Font.PLAIN, 20));
        table.setRowHeight(60);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.LEFT);
                lbl.setBorder(new EmptyBorder(0, 15, 0, 0));
                return lbl;
            }
        };
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(5).setCellEditor(new TableActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 3. PHÂN TRANG (GIỮ NGUYÊN)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JLabel lblPage = new JLabel("1-5 của 150   ");
        lblPage.setFont(new Font("Inter", Font.PLAIN, 14));
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        footerPanel.add(btnPrev);
        footerPanel.add(lblPage);
        footerPanel.add(btnNext);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<HoKhau> list = controller.getAllHoKhau();
        int stt = 1;
        for (HoKhau hk : list) {
            tableModel.addRow(new Object[]{
                    stt++,
                    hk.getSoCanHo(),
                    hk.getTenChuHo(),
                    hk.getDienTich(),
                    hk.getSdt(),
                    ""
            });
        }
    }

    // ==================================================================
    // CÁC CLASS UI CUSTOM
    // ==================================================================

    class PanelAction extends JPanel {
        private JButton btnAdd, btnEdit, btnDelete;

        public PanelAction() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setOpaque(false);

            btnAdd = createBtn("/images/icon_add_resident.png");
            btnEdit = createBtn("/images/icon_edit.png");
            btnDelete = createBtn("/images/icon_delete.png");
            
            add(btnAdd);
            add(btnEdit);
            add(btnDelete);
        }

        private JButton createBtn(String iconPath) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(35, 35));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(null);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            URL url = getClass().getResource(iconPath);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText("●"); 
            }
            return btn;
        }
        
        public void initEvent(int row) {
            btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this, "Thêm thành viên vào hộ dòng: " + (row + 1)));
            btnEdit.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sửa hộ dòng: " + (row + 1)));
            btnDelete.addActionListener(e -> JOptionPane.showMessageDialog(this, "Xóa hộ dòng: " + (row + 1)));
        }
    }

    class TableActionCellRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            PanelAction action = new PanelAction();
            if (isSelected) {
                action.setBackground(new Color(232, 240, 254));
            } else {
                action.setBackground(Color.WHITE);
            }
            return action;
        }
    }

    class TableActionCellEditor extends DefaultCellEditor {
        public TableActionCellEditor() {
            super(new JCheckBox());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            PanelAction action = new PanelAction();
            action.initEvent(row);
            action.setBackground(new Color(232, 240, 254));
            return action;
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) { this.radius = radius; this.bgColor = bgColor; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    class RoundedButton extends JButton {
        public RoundedButton(String text) { super(text); setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
            g2.setColor(getForeground()); FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4; g2.drawString(getText(), x, y); g2.dispose();
        }
    }
}