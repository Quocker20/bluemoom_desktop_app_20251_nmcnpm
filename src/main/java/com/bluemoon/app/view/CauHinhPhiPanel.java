package com.bluemoon.app.view;

import com.bluemoon.app.controller.ThuPhiController;
import com.bluemoon.app.model.KhoanPhi;
import com.bluemoon.app.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.awt.geom.RoundRectangle2D;

public class CauHinhPhiPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ThuPhiController controller;
    private JTextField txtSearch;
    private List<KhoanPhi> currentList;

    private final Color COL_PRIMARY = new Color(52, 152, 219);
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public CauHinhPhiPanel() {
        controller = new ThuPhiController();
        initComponents();
        loadData("");
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        RoundedPanel headerPanel = new RoundedPanel(20, COL_HEADER_BG);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel lblTitle = new JLabel("Cấu hình Khoản thu & Đơn giá");
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
                new EmptyBorder(0, 10, 0, 10)));
        txtSearch.addActionListener(e -> loadData(txtSearch.getText()));
        toolBox.add(txtSearch);

        RoundedButton btnAdd = new RoundedButton("+ Thêm khoản phí");
        btnAdd.setPreferredSize(new Dimension(160, 40));
        btnAdd.setBackground(COL_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openDialog(null));

        toolBox.add(btnAdd);
        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] cols = { "STT", "Tên khoản phí", "Đơn giá (VNĐ)", "Đơn vị", "Loại phí", "Thao tác" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;
            } // Chỉ cột thao tác
        };

        table = new JTable(tableModel);
        setupTableStyle();

        // [ĐÃ BỎ LOGIC MOUSE LISTENER DOUBLE CLICK]

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableStyle() {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setMaxWidth(60); // STT
        cm.getColumn(1).setPreferredWidth(250); // Tên
        cm.getColumn(5).setMinWidth(120); // Thao tác

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 20));
        cm.getColumn(2).setCellRenderer(rightRenderer); // Đơn giá

        // Setup Action Column
        cm.getColumn(5).setCellRenderer(new TableActionCellRender());
        cm.getColumn(5).setCellEditor(new TableActionCellEditor());
    }

    public void loadData(String keyword) {
        tableModel.setRowCount(0);
        currentList = controller.getListKhoanPhi(keyword);
        DecimalFormat df = new DecimalFormat("#,###");

        int stt = 1;
        for (KhoanPhi kp : currentList) {
            tableModel.addRow(new Object[] {
                    stt++,
                    kp.getTenKhoanPhi(),
                    df.format(kp.getDonGia()),
                    kp.getDonViTinh(),
                    kp.getLoaiPhi() == AppConstants.PHI_BAT_BUOC ? "Bắt buộc" : "Tự nguyện",
                    "" // Placeholder cho nút
            });
        }
    }

    private void openDialog(KhoanPhi kp) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        QuanLyKhoanPhiDialog dialog = new QuanLyKhoanPhiDialog(frame, this);
        if (kp != null)
            dialog.setEditData(kp);
        dialog.setVisible(true);
    }

    // ==================================================================
    // ACTION BUTTONS UI & LOGIC
    // ==================================================================

    class PanelAction extends JPanel {
        private JButton btnEdit, btnDelete;

        public PanelAction() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setOpaque(false);

            // Nút Sửa (Cam)
            btnEdit = createBtn("/images/icon_edit.png", new Color(243, 156, 18));
            // Nút Xóa (Đỏ)
            btnDelete = createBtn("/images/icon_delete.png", new Color(231, 76, 60));

            add(btnEdit);
            add(btnDelete);
        }

        private Icon loadIcon(String path) {
            try {
                URL url = getClass().getResource(path);
                if (url != null)
                    return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
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
                btn.setText("•");
                btn.setForeground(color);
            }
            return btn;
        }

        public void initEvent(int row) {
            btnEdit.addActionListener(e -> {
                // Dừng edit bảng
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                if (row >= 0 && row < currentList.size()) {
                    KhoanPhi selected = currentList.get(row);
                    // Check xem có công nợ chưa để cảnh báo
                    boolean isInUse = controller.checkKhoanPhiDangSuDung(selected.getMaKhoanPhi());
                    if (isInUse) {
                        int confirm = JOptionPane.showConfirmDialog(CauHinhPhiPanel.this,
                                "Khoản phí này ĐÃ CÓ người đóng tiền/ghi nợ.\nViệc sửa giá tiền sẽ không ảnh hưởng công nợ cũ, nhưng sẽ áp dụng cho đợt mới.\nBạn có chắc chắn muốn sửa?",
                                "Cảnh báo dữ liệu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (confirm != JOptionPane.YES_OPTION)
                            return;
                    }
                    openDialog(selected);
                }
            });

            btnDelete.addActionListener(e -> {
                // Dừng edit bảng (Fix lỗi đơ)
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                if (row >= 0 && row < currentList.size()) {
                    KhoanPhi selected = currentList.get(row);

                    // [LOGIC CHECK CÔNG NỢ]
                    boolean isInUse = controller.checkKhoanPhiDangSuDung(selected.getMaKhoanPhi());
                    if (isInUse) {
                        JOptionPane.showMessageDialog(CauHinhPhiPanel.this,
                                "KHÔNG THỂ XÓA!\nKhoản phí này đã phát sinh dữ liệu công nợ trong hệ thống.",
                                "Chặn xóa", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(CauHinhPhiPanel.this,
                            "Bạn có chắc chắn muốn xóa khoản phí: " + selected.getTenKhoanPhi() + "?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (controller.deleteKhoanPhi(selected.getMaKhoanPhi())) {
                            JOptionPane.showMessageDialog(CauHinhPhiPanel.this, "Đã xóa thành công!");
                            loadData(txtSearch.getText());
                        } else {
                            JOptionPane.showMessageDialog(CauHinhPhiPanel.this, "Xóa thất bại!", "Lỗi",
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

    // --- Inner UI Classes (Style chuẩn) ---
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