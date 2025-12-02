package com.bluemoon.app.view;

import com.bluemoon.app.controller.HoKhauController;
import com.bluemoon.app.controller.NhanKhauController;
import com.bluemoon.app.model.HoKhau;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bluemoon.app.model.NhanKhau;

public class ThemHoKhauDialog extends JDialog {

    private JTextField txtMaHo;
    private JTextField txtTenChuHo;
    private JTextField txtDienTich;
    private JTextField txtSDT;
    
    // --- CÁC TRƯỜNG MỚI (UI ONLY) ---
    private JTextField txtNgaySinh;
    private JTextField txtCCCD;
    private JComboBox<String> cbGioiTinh;

    private JButton btnSave;
    private JButton btnCancel;
    private JLabel lblTitle;

    private HoKhauController hkController;
    private NhanKhauController nkcontroller = new NhanKhauController();
    private HoKhauPanel parentPanel;
    
    private boolean isEditMode = false;
    private HoKhau currentHoKhau = null;

    public ThemHoKhauDialog(JFrame parentFrame, HoKhauPanel parentPanel) {
        super(parentFrame, "Quản lý Hộ khẩu", true);
        this.parentPanel = parentPanel;
        this.hkController = new HoKhauController();
        initComponents();
    }

    private void initComponents() {
        setSize(550, 650); // Tăng chiều cao để chứa thêm trường
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
        lblTitle.setBounds(0, 15, 530, 40);
        mainPanel.add(lblTitle);

        // --- CÁC TRƯỜNG NHẬP LIỆU (SẮP XẾP LẠI VỊ TRÍ) ---
        
        // Hàng 1: Mã hộ & Diện tích
        JLabel lblMaHo = new JLabel("Mã hộ *");
        lblMaHo.setBounds(40, 70, 200, 20);
        mainPanel.add(lblMaHo);

        txtMaHo = new JTextField();
        txtMaHo.setBounds(40, 95, 210, 40);
        setupTextField(txtMaHo);
        mainPanel.add(txtMaHo);

        JLabel lblDienTich = new JLabel("Diện tích (m2) *");
        lblDienTich.setBounds(280, 70, 200, 20);
        mainPanel.add(lblDienTich);

        txtDienTich = new JTextField();
        txtDienTich.setBounds(280, 95, 210, 40);
        setupTextField(txtDienTich);
        mainPanel.add(txtDienTich);

        // Hàng 2: Họ tên chủ hộ & Số điện thoại
        JLabel lblTen = new JLabel("Họ và Tên chủ hộ *");
        lblTen.setBounds(40, 150, 200, 20);
        mainPanel.add(lblTen);

        txtTenChuHo = new JTextField();
        txtTenChuHo.setBounds(40, 175, 210, 40);
        setupTextField(txtTenChuHo);
        mainPanel.add(txtTenChuHo);

        JLabel lblSDT = new JLabel("Số điện thoại");
        lblSDT.setBounds(280, 150, 200, 20);
        mainPanel.add(lblSDT);

        txtSDT = new JTextField();
        txtSDT.setBounds(280, 175, 210, 40);
        setupTextField(txtSDT);
        mainPanel.add(txtSDT);

        // Hàng 3: Giới tính & Ngày sinh (MỚI)
        JLabel lblGioiTinh = new JLabel("Giới tính *");
        lblGioiTinh.setBounds(40, 230, 200, 20);
        mainPanel.add(lblGioiTinh);

        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cbGioiTinh.setBounds(40, 255, 210, 40);
        cbGioiTinh.setBackground(Color.WHITE);
        mainPanel.add(cbGioiTinh);

        JLabel lblNgaySinh = new JLabel("Nhập Ngày sinh (dd/MM/yyyy)*");
        lblNgaySinh.setBounds(280, 230, 200, 20);
        mainPanel.add(lblNgaySinh);

        txtNgaySinh = new JTextField();
        txtNgaySinh.setBounds(280, 255, 210, 40);
        setupTextField(txtNgaySinh);
        mainPanel.add(txtNgaySinh);

        // Hàng 4: Số CCCD (MỚI - Full width)
        JLabel lblCCCD = new JLabel("Số CCCD/CMND");
        lblCCCD.setBounds(40, 310, 200, 20);
        mainPanel.add(lblCCCD);

        txtCCCD = new JTextField();
        txtCCCD.setBounds(40, 335, 450, 40);
        setupTextField(txtCCCD);
        txtCCCD.putClientProperty("JTextField.placeholderText", "Nhập Số CCCD/CMND (nếu có)");
        mainPanel.add(txtCCCD);

        // --- Nút Lưu ---
        btnSave = new JButton("Thêm hộ khẩu mới") {
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
        btnSave.setBounds(40, 410, 450, 45); // Dời xuống dưới
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSave.addActionListener(e -> handleSaveAction());
        mainPanel.add(btnSave);

        // Nút Hủy
        btnCancel = new JButton("Quay lại");
        btnCancel.setBounds(40, 470, 450, 30); // Dời xuống dưới
        btnCancel.setForeground(Color.GRAY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void setEditData(HoKhau hk) {
        this.isEditMode = true;
        this.currentHoKhau = hk;

        txtMaHo.setText(hk.getSoCanHo());
        txtMaHo.setEditable(false);
        txtMaHo.setForeground(Color.GRAY);
        
        txtTenChuHo.setText(hk.getTenChuHo());
        txtDienTich.setText(String.valueOf(hk.getDienTich()));
        txtSDT.setText(hk.getSdt());

        // Các trường mới chưa có data từ model cũ nên để trống hoặc logic khác tùy bạn sau này
        
        lblTitle.setText("CẬP NHẬT THÔNG TIN");
        btnSave.setText("Lưu thay đổi");
        setTitle("Cập nhật Hộ khẩu");
    }

    private void handleSaveAction() {
        // LOGIC CŨ GIỮ NGUYÊN (Chưa validate/xử lý các trường mới)
        String maHo = txtMaHo.getText().trim();
        String tenChuHo = txtTenChuHo.getText().trim();
        String sdt = txtSDT.getText().trim();
        String dienTichStr = txtDienTich.getText().trim();
        String gioiTinh = (String) cbGioiTinh.getSelectedItem();
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String cccd = txtCCCD.getText().trim();

        if (maHo.isEmpty() || tenChuHo.isEmpty() || dienTichStr.isEmpty() || gioiTinh.isEmpty() || ngaySinhStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double dienTich = Double.parseDouble(dienTichStr);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false); 
            Date ngaySinh = sdf.parse(ngaySinhStr);
            

            if (isEditMode) {
                updateHoKhau(tenChuHo, dienTich, sdt);
            } else {
                addHoKhau(maHo, tenChuHo, dienTich, sdt);
                NhanKhau chuHo = new NhanKhau(hkController.getMaxMaHo(), tenChuHo, ngaySinh, gioiTinh, cccd, "Chủ hộ");
                boolean isAddingSuccessed = nkcontroller.addNhanKhau(chuHo);
                if (!isAddingSuccessed) {
                    JOptionPane.showMessageDialog(this, "Thêm chủ hộ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "da loi" + ex.getMessage(), "loi", JOptionPane.ERROR_MESSAGE);
            
        }
    }

    private void addHoKhau(String maHo, String ten, double dt, String sdt) {
        HoKhau hk = new HoKhau(maHo, ten, dt, sdt);
        boolean success = hkController.addHoKhau(hk);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm hộ khẩu thành công!\nVui lòng bổ sung thông tin chủ hộ.");
            closeAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Mã hộ '" + maHo + "' có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHoKhau(String ten, double dt, String sdt) {
        currentHoKhau.setTenChuHo(ten);
        currentHoKhau.setDienTich(dt);
        currentHoKhau.setSdt(sdt);

        boolean success = hkController.updateHoKhau(currentHoKhau);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
            closeAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeAndRefresh() {
        parentPanel.loadData();
        dispose();
    }

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