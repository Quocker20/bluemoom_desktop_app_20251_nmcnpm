package com.bluemoon.app.view;

import com.bluemoon.app.controller.TamTruTamVangController;
import com.bluemoon.app.model.TamTruTamVang;

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
    
    // Controller để lấy dữ liệu thật
    private TamTruTamVangController controller;

    // Màu sắc chủ đạo
    private final Color COL_PRIMARY = new Color(52, 152, 219);
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_TABLE_HEADER = new Color(217, 217, 217);

    public BienDongPanel() {
        this.controller = new TamTruTamVangController();
        initComponents();
        loadData(); // Load dữ liệu từ CSDL
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- 1. HEADER ---
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

        // Filter
        cbFilter = new JComboBox<>(new String[]{"Tất cả", "Tạm trú", "Tạm vắng", "Khai tử"});
        cbFilter.setPreferredSize(new Dimension(150, 40));
        cbFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        cbFilter.setBackground(Color.WHITE);
        cbFilter.setFocusable(false);
        toolBox.add(cbFilter);

        // Search
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 40));
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 10, 0, 0)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên nhân khẩu...");
        toolBox.add(txtSearch);

        // Button Search
        JButton btnSearch = new JButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(60, 40));
        btnSearch.setBackground(new Color(240, 240, 240));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        toolBox.add(btnSearch);

        headerPanel.add(toolBox, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. TABLE (CẬP NHẬT CỘT CHO KHỚP DB) ---
        // Lưu ý: Cột "Họ tên" có thể cần JOIN bảng để lấy, nếu model TamTruTamVang chỉ có maNhanKhau
        String[] columnNames = {"STT", "Mã giấy", "Mã NK/Tên", "Loại hình", "Từ ngày", "Đến ngày", "Lý do"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        
        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 16));
        header.setBackground(COL_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        
        // Body Style
        table.setFont(new Font("Inter", Font.PLAIN, 16));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        // --- CẤU HÌNH RENDERER & WIDTH ---
        TableColumnModel columnModel = table.getColumnModel();
        
        // Renderer chung: Căn trái + Padding 15px
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 15, 0, 0)); 

        // 1. STT
        columnModel.getColumn(0).setCellRenderer(leftRenderer);
        columnModel.getColumn(0).setMaxWidth(60);
        
        // 2. Mã giấy (Mã Tạm trú/vắng)
        columnModel.getColumn(1).setCellRenderer(leftRenderer);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(1).setMaxWidth(150);
        
        // 3. Mã NK/Họ tên
        columnModel.getColumn(2).setCellRenderer(leftRenderer);
        columnModel.getColumn(2).setPreferredWidth(200); 
        
        // 4. Loại hình (Tạm trú/Tạm vắng) -> Có màu
        columnModel.getColumn(3).setCellRenderer(new TypeCellRenderer());
        columnModel.getColumn(3).setMinWidth(150); 
        columnModel.getColumn(3).setMaxWidth(180);

        // 5. Từ ngày
        columnModel.getColumn(4).setCellRenderer(leftRenderer);
        columnModel.getColumn(4).setPreferredWidth(110);
        columnModel.getColumn(4).setMaxWidth(130);

        // 6. Đến ngày
        columnModel.getColumn(5).setCellRenderer(leftRenderer);
        columnModel.getColumn(5).setPreferredWidth(110);
        columnModel.getColumn(5).setMaxWidth(130);
        
        // 7. Lý do (Tự giãn phần còn lại)
        columnModel.getColumn(6).setCellRenderer(leftRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Hàm load dữ liệu thực tế từ Controller
     * Đã sửa getter/setter để khớp với Model TamTruTamVang
     */
    public void loadData() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);
        
        // Lấy list từ DB
        List<TamTruTamVang> list = controller.getAllTamTruTamVang();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        int stt = 1;
        for (TamTruTamVang item : list) {
            // [SỬA ĐỔI] Gọi đúng Getter của Model TamTruTamVang
            int maTTTV = item.getMaTTTV(); 
            
            // Nếu Model chưa có getHoTen(), tạm thời hiển thị Mã Nhân Khẩu
            // String hoTen = item.getHoTen(); 
            String doiTuong = String.valueOf(item.getMaNhanKhau()); 

            String loai = item.getLoaiHinh(); 
            String tuNgay = item.getTuNgay() != null ? sdf.format(item.getTuNgay()) : "";
            String denNgay = item.getDenNgay() != null ? sdf.format(item.getDenNgay()) : "";
            String lyDo = item.getLyDo();

            tableModel.addRow(new Object[]{
                stt++, 
                maTTTV, 
                doiTuong, // Hiển thị Mã NK (hoặc Tên nếu có)
                loai, 
                tuNgay, 
                denNgay, 
                lyDo   
            });
        }
    }

    // --- CUSTOM COMPONENTS ---

    // Renderer tô màu chữ dựa trên Loại hình
    class TypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String type = (String) value;
            setFont(new Font("Inter", Font.BOLD, 14));
            
            // Căn trái + Padding
            setHorizontalAlignment(JLabel.LEFT); 
            setBorder(new EmptyBorder(0, 15, 0, 0));
            
            if (type != null) {
                if (type.toLowerCase().contains("tạm trú")) {
                    setForeground(new Color(46, 204, 113)); // Xanh lá
                } else if (type.toLowerCase().contains("tạm vắng")) {
                    setForeground(new Color(243, 156, 18)); // Cam
                } else if (type.toLowerCase().contains("khai tử")) {
                    setForeground(Color.RED); // Đỏ
                } else {
                    setForeground(Color.BLACK);
                }
            }
            
            if (isSelected) setForeground(Color.BLACK);
            
            return c;
        }
    }

    class RoundedPanel extends JPanel {
        private int radius; private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) { this.radius = radius; this.bgColor = bgColor; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); }
    }
}