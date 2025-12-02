package com.bluemoon.app.view;

import com.bluemoon.app.controller.HoKhauController;
import com.bluemoon.app.model.HoKhau;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemHoKhauDialog extends JDialog {

    private JTextField txtMaHo;
    private JTextField txtTenChuHo;
    private JTextField txtDienTich;
    private JTextField txtSDT;
    private JButton btnSave;
    private JButton btnCancel;
    private JLabel lblTitle;

    private HoKhauController controller;
    private HoKhauPanel parentPanel;
    
    // Biến cờ để xác định chế độ
    private boolean isEditMode = false;
    private HoKhau currentHoKhau = null;

    public ThemHoKhauDialog(JFrame parentFrame, HoKhauPanel parentPanel) {
        super(parentFrame, "Quản lý Hộ khẩu", true);
        this.parentPanel = parentPanel;
        this.controller = new HoKhauController();
        initComponents();
    }

    private void initComponents() {
        setSize(500, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        lblTitle = new JLabel("THÊM HỘ KHẨU MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 20, 480, 40);
        mainPanel.add(lblTitle);

        // --- Các trường nhập liệu (UI Giữ nguyên) ---
        
        // 1. Mã hộ
        JLabel lblMaHo = new JLabel("Mã hộ *");
        lblMaHo.setBounds(40, 80, 200, 20);
        mainPanel.add(lblMaHo);

        txtMaHo = new JTextField();
        txtMaHo.setBounds(40, 105, 190, 40);
        setupTextField(txtMaHo);
        mainPanel.add(txtMaHo);

        // 2. Tên chủ hộ
        JLabel lblTen = new JLabel("Họ và Tên chủ hộ *");
        lblTen.setBounds(250, 80, 200, 20);
        mainPanel.add(lblTen);

        txtTenChuHo = new JTextField();
        txtTenChuHo.setBounds(250, 105, 190, 40);
        setupTextField(txtTenChuHo);
        mainPanel.add(txtTenChuHo);

        // 3. Diện tích
        JLabel lblDienTich = new JLabel("Diện tích (m2) *");
        lblDienTich.setBounds(40, 170, 200, 20);
        mainPanel.add(lblDienTich);

        txtDienTich = new JTextField();
        txtDienTich.setBounds(40, 195, 190, 40);
        setupTextField(txtDienTich);
        mainPanel.add(txtDienTich);

        // 4. Số điện thoại
        JLabel lblSDT = new JLabel("Số điện thoại");
        lblSDT.setBounds(250, 170, 200, 20);
        mainPanel.add(lblSDT);

        txtSDT = new JTextField();
        txtSDT.setBounds(250, 195, 190, 40);
        setupTextField(txtSDT);
        mainPanel.add(txtSDT);

        // --- Nút Lưu ---
        btnSave = new JButton("Lưu thông tin") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52, 152, 219)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setBounds(40, 280, 400, 45);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // SỰ KIỆN: Gọi hàm điều phối chung
        btnSave.addActionListener(e -> handleSaveAction());
        
        mainPanel.add(btnSave);

        // Nút Hủy
        btnCancel = new JButton("Quay lại");
        btnCancel.setBounds(40, 340, 400, 30);
        btnCancel.setForeground(Color.GRAY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    // --- LOGIC CHUYỂN CHẾ ĐỘ (Dùng khi bấm nút Sửa) ---
    public void setEditData(HoKhau hk) {
        this.isEditMode = true;
        this.currentHoKhau = hk;

        // Điền dữ liệu cũ
        txtMaHo.setText(hk.getSoCanHo());
        txtMaHo.setEditable(false); // Khóa trường Mã hộ (Primary Key logic)
        txtMaHo.setForeground(Color.GRAY);
        
        txtTenChuHo.setText(hk.getTenChuHo());
        txtDienTich.setText(String.valueOf(hk.getDienTich()));
        txtSDT.setText(hk.getSdt());

        // Đổi giao diện
        lblTitle.setText("CẬP NHẬT THÔNG TIN");
        btnSave.setText("Lưu thay đổi");
        setTitle("Cập nhật Hộ khẩu");
    }

    // --- LOGIC XỬ LÝ CHÍNH ---
    private void handleSaveAction() {
        // 1. Validate dữ liệu (Dùng chung cho cả Thêm và Sửa)
        String maHo = txtMaHo.getText().trim();
        String tenChuHo = txtTenChuHo.getText().trim();
        String sdt = txtSDT.getText().trim();
        String dienTichStr = txtDienTich.getText().trim();

        if (maHo.isEmpty() || tenChuHo.isEmpty() || dienTichStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double dienTich = Double.parseDouble(dienTichStr);

            // 2. Điều hướng logic
            if (isEditMode) {
                updateHoKhau(tenChuHo, dienTich, sdt);
            } else {
                addHoKhau(maHo, tenChuHo, dienTich, sdt);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Diện tích phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- LOGIC RIÊNG CHO THÊM MỚI ---
    private void addHoKhau(String maHo, String ten, double dt, String sdt) {
        HoKhau hk = new HoKhau(maHo, ten, dt, sdt);
        boolean success = controller.addHoKhau(hk);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm hộ khẩu thành công!");
            closeAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Mã hộ '" + maHo + "' có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- LOGIC RIÊNG CHO CẬP NHẬT ---
    private void updateHoKhau(String ten, double dt, String sdt) {
        // Cập nhật thông tin mới vào đối tượng hiện tại
        currentHoKhau.setTenChuHo(ten);
        currentHoKhau.setDienTich(dt);
        currentHoKhau.setSdt(sdt);
        // Lưu ý: Không setSoCanHo vì đã bị khóa

        boolean success = controller.updateHoKhau(currentHoKhau);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
            closeAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeAndRefresh() {
        parentPanel.loadData(); // Refresh bảng ở màn hình cha
        dispose(); // Đóng dialog
    }

    // --- Helper UI ---
    private void setupTextField(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(new RoundedBorder(8));
        txt.setOpaque(false);
    }

    static class RoundedBorder extends AbstractBorder {
        private int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius, radius/2, radius); }
    }
}