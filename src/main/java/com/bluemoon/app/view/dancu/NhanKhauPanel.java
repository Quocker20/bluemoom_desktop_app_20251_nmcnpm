package com.bluemoon.app.view.dancu;

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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import com.bluemoon.app.controller.dancu.HoKhauController;
import com.bluemoon.app.controller.dancu.NhanKhauController;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;

public class NhanKhauPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private NhanKhauController nkController;
    private HoKhauController hkController;
    private JTextField txtSearch;
    private List<NhanKhau> currentList;

    // Màu sắc chủ đạo (Giống HoKhauPanel)
    private final Color COL_PRIMARY = new Color(52, 152, 219);
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public NhanKhauPanel() {
        this.nkController = new NhanKhauController();
        this.hkController = new HoKhauController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- Header Section ---
        RoundedPanel headerPanel = new RoundedPanel(20, COL_HEADER_BG);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Danh sách nhân khẩu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel toolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBox.setOpaque(false);

        // Search Box
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

        // Add Button
        RoundedButton btnAdd = new RoundedButton("Thêm nhân khẩu");
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.setBackground(COL_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            // Mở dialog thêm nhân khẩu (Giả định bạn đã có class ThemNhanKhauDialog)
            // JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // ThemNhanKhauDialog dialog = new ThemNhanKhauDialog(parentFrame, this);
            // dialog.setVisible(true);
            JOptionPane.showMessageDialog(this, "Tính năng mở Dialog thêm nhân khẩu đang được phát triển.");
        });
        toolBox.add(btnAdd);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Table Section ---
        // Cập nhật tên cột cho phù hợp với Nhân Khẩu
        String[] columnNames = { "STT", "Mã NK", "Họ tên", "Ngày sinh", "Giới tính", "CCCD", "Thao tác" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho phép edit cột thao tác
            }
        };

        table = new JTable(tableModel);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // STT
        columnModel.getColumn(1).setPreferredWidth(80); // Mã NK
        columnModel.getColumn(2).setPreferredWidth(180); // Họ tên
        columnModel.getColumn(3).setPreferredWidth(100); // Ngày sinh
        columnModel.getColumn(4).setPreferredWidth(70); // Giới tính
        columnModel.getColumn(5).setPreferredWidth(120); // CCCD
        columnModel.getColumn(6).setMinWidth(150); // Thao tác

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        // Setup Renderer & Editor cho cột Thao tác
        table.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // --- Footer Section ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JLabel lblPage = new JLabel("Hiển thị tất cả kết quả   ");
        lblPage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footerPanel.add(lblPage);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        // Gọi controller lấy danh sách
        currentList = nkController.getAllNhanKhau();
        int stt = 1;
        for (NhanKhau nk : currentList) {
            HoKhau hk = hkController.getById(nk.getMaHo());
            tableModel.addRow(new Object[] {
                    stt++,
                    hk.getSoCanHo(),
                    nk.getHoTen(),
                    nk.getNgaySinh(),
                    nk.getGioiTinh(),
                    nk.getCccd(),
                    "" // Placeholder cho cột Thao tác
            });
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        currentList = nkController.searchNhanKhau(keyword);
        tableModel.setRowCount(0);
        int stt = 1;
        for (NhanKhau nk : currentList) {
            HoKhau hk = hkController.getById(nk.getMaHo());
            tableModel.addRow(new Object[] {
                    stt++,
                    hk.getSoCanHo(),
                    nk.getHoTen(),
                    nk.getNgaySinh(),
                    nk.getGioiTinh(),
                    nk.getCccd(),
                    ""
            });
        }
    }

    // --- Inner Classes cho Custom UI ---

    class PanelAction extends JPanel {
        private JButton btnEdit, btnDelete; // Có thể bỏ btnShowResidentList vì đây đã là danh sách chi tiết

        public PanelAction() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setOpaque(false);

            // Icon Edit
            btnEdit = createBtn("/images/icon_edit.png", new Color(243, 156, 18));
            // Icon Delete
            btnDelete = createBtn("/images/icon_delete.png", new Color(231, 76, 60));

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
                // Fallback text nếu không tìm thấy icon
                btn.setText("•");
                btn.setForeground(color);
            }
            return btn;
        }

        

        public void initEvent(int row) {
            // Xử lý sự kiện Sửa
            btnEdit.addActionListener(e -> {
                if (row >= 0 && row < currentList.size()) {
                    NhanKhau selectedNk = currentList.get(row);

                    // JFrame parentFrame = (JFrame);
                    // SwingUtilities.getWindowAncestor(NhanKhauPanel.this);
                    // ThemNhanKhauDialog dialog = new ThemNhanKhauDialog(parentFrame,
                    // NhanKhauPanel.this);
                    // dialog.setEditData(selectedNk);
                    // dialog.setVisible(true);

                    if (table.getCellEditor() != null)
                        table.getCellEditor().stopCellEditing();

                    JOptionPane.showMessageDialog(NhanKhauPanel.this, "Đang sửa nhân khẩu: " + selectedNk.getHoTen());
                }
            });

            // Xử lý sự kiện Xóa
            btnDelete.addActionListener(e -> {
                if (row >= 0 && row < currentList.size()) {
                    if (table.getCellEditor() != null)
                        table.getCellEditor().stopCellEditing();

                    NhanKhau selectedNk = currentList.get(row);
                    int confirm = JOptionPane.showConfirmDialog(NhanKhauPanel.this,
                            "Bạn có chắc chắn muốn xóa nhân khẩu: " + selectedNk.getHoTen() + "?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = nkController.deleteNhanKhau(selectedNk.getMaNhanKhau());
                        if (deleted) {
                            JOptionPane.showMessageDialog(NhanKhauPanel.this, "Đã xóa thành công!");
                            loadData(); // Reload lại bảng
                        } else {
                            JOptionPane.showMessageDialog(NhanKhauPanel.this, "Xóa thất bại!", "Lỗi",
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

    // Class vẽ Panel bo tròn
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

    // Class vẽ Button bo tròn
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