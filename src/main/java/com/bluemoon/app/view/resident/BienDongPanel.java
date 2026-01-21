package com.bluemoon.app.view.resident;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.bluemoon.app.controller.resident.ResidencyRecordController;
import com.bluemoon.app.model.ResidencyRecord;
import com.bluemoon.app.util.AppConstants;

public class BienDongPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFilter;
    private ResidencyRecordController controller;
    
    JButton btnCleanup = new JButton("Dọn dẹp");

    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public BienDongPanel() {
        this.controller = new ResidencyRecordController();
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

        btnCleanup.setPreferredSize(new Dimension(90, 40));
        btnCleanup.setBackground(new Color(240, 240, 240));
        btnCleanup.setFocusPainted(false);
        btnCleanup.setBorderPainted(false);
        btnCleanup.setFont(new Font("Inter", Font.BOLD, 14));
        btnCleanup.setForeground(new Color(192, 57, 43));
        btnCleanup.addActionListener(e -> handleDeleteCleanup());
        toolBox.add(btnCleanup);

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
        updateTable(controller.getAll());
    }

    private void handleFilter() {
        String selected = (String) cbFilter.getSelectedItem();
        List<ResidencyRecord> list;
        if (selected == null || selected.equals("Tất cả")) {
            list = controller.getAll();
        } else {
            String loaiHinh = "";
            switch (selected) {
                case "Tạm trú":
                    loaiHinh = AppConstants.TAM_TRU;
                    break;
                case "Tạm vắng":
                    loaiHinh = AppConstants.TAM_VANG;
                    break;
            }
            list = controller.getByType(loaiHinh);
        }
        updateTable(list);
        txtSearch.setText("");
    }

    private void handleDeleteCleanup() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tất cả các hồ sơ Tạm trú/Tạm vắng đã HẾT HẠN không?",
                "Xác nhận Dọn dẹp Dữ liệu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new CleanupWorker().execute();
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        List<ResidencyRecord> list;
        if (keyword.isEmpty()) {
            list = controller.getAll();
            cbFilter.setSelectedIndex(0);
        } else {
            list = controller.getByResidentName(keyword);
        }
        updateTable(list);
    }

    private void updateTable(List<ResidencyRecord> list) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int stt = 1;
        for (ResidencyRecord item : list) {
            String tenHienThi = item.getResidentName() != null ? item.getResidentName()
                    : "Mã NK: " + item.getResidentId();

            String loaiHienThi = item.getType();
            

            tableModel.addRow(new Object[] {
                    stt++, item.getId(), tenHienThi, loaiHienThi,
                    (item.getStartDate() != null ? sdf.format(item.getStartDate()) : ""),
                    (item.getEndDate() != null ? sdf.format(item.getEndDate()) : ""),
                    item.getReason()
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

    /**
     * SwingWorker thực hiện thao tác xóa CSDL trong luồng nền
     * và quản lý hiển thị hộp thoại chờ trên EDT một cách an toàn.
     */
    private class CleanupWorker extends SwingWorker<Boolean, Void> {
        
        private JDialog loadingDialog; 

        public CleanupWorker() {
            // Chuẩn bị hộp thoại chờ (chạy trên EDT)
            loadingDialog = new JDialog(SwingUtilities.getWindowAncestor(BienDongPanel.this), "Đang dọn dẹp...");
            loadingDialog.setLayout(new FlowLayout());
            loadingDialog.add(new JLabel("Đang xử lý. Vui lòng chờ..."));
            loadingDialog.pack();
            loadingDialog.setLocationRelativeTo(BienDongPanel.this);
            loadingDialog.setResizable(false);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        }
                
        @Override
        protected Boolean doInBackground() {
            // 1. Vô hiệu hóa nút và hiển thị Dialog
            // CHẠY TÁC VỤ UI TRÊN EDT TRƯỚC KHI THỰC HIỆN LOGIC NỀN
            SwingUtilities.invokeLater(() -> {
                btnCleanup.setEnabled(false);
                loadingDialog.setVisible(true); // Dòng này sẽ chặn EDT, nhưng không sao vì luồng nền đã bắt đầu
            });
            
            // 2. Thao tác nặng (DAO) chạy trong luồng nền
            // Nó chạy ngay sau khi lệnh hiển thị dialog được đẩy lên EDT.
            Boolean result = controller.deleteExpired();
            
            // 3. Đóng dialog ngay sau khi tác vụ nền xong, để giải phóng EDT
            SwingUtilities.invokeLater(() -> {
                if (loadingDialog != null) {
                    loadingDialog.dispose();
                }
            });

            return result;
        }
        
        @Override
        protected void done() {
            // Luôn chạy trên EDT
            
            // Kích hoạt lại nút (Sau khi dialog đã được đóng)
            btnCleanup.setEnabled(true); 
            
            try {
                boolean result = get();
                
                if (result) {
                    JOptionPane.showMessageDialog(BienDongPanel.this, 
                        "Dọn dẹp thành công! Đã xóa các hồ sơ Tạm trú/Tạm vắng hết hạn.", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Tải lại dữ liệu để cập nhật bảng
                } else {
                    JOptionPane.showMessageDialog(BienDongPanel.this, 
                        "Dọn dẹp hoàn tất. Có thể không có hồ sơ nào hết hạn hoặc đã xảy ra lỗi CSDL.", 
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(BienDongPanel.this, 
                    "Lỗi nghiêm trọng khi dọn dẹp dữ liệu: " + e.getMessage(), 
                    "Lỗi CSDL", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}