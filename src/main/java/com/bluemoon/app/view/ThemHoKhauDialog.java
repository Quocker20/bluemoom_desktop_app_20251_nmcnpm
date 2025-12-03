package com.bluemoon.app.view;

import com.bluemoon.app.controller.HoKhauController;
import com.bluemoon.app.controller.NhanKhauController;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ThemHoKhauDialog extends JDialog {

    private JTextField txtMaHo;
    private JTextField txtTenChuHo;
    private JTextField txtDienTich;
    private JTextField txtSDT;
    
    // --- CÁC TRƯỜNG MỚI ---
    private JTextField txtNgaySinh;
    private JComboBox<String> cbGioiTinh;
    private JTextField txtCCCD;

    private JButton btnSave;
    private JButton btnCancel;
    private JLabel lblTitle;

    private HoKhauController hkController;
    private NhanKhauController nkController;
    private HoKhauPanel parentPanel;
    
    private boolean isEditMode = false;
    private HoKhau currentHoKhau = null;

    public ThemHoKhauDialog(JFrame parentFrame, HoKhauPanel parentPanel) {
        super(parentFrame, "Quản lý Hộ khẩu", true);
        this.parentPanel = parentPanel;
        this.hkController = new HoKhauController();
        this.nkController = new NhanKhauController();
        initComponents();
    }

    private void initComponents() {
        setSize(550, 680);
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

        // --- CÁC TRƯỜNG NHẬP LIỆU ---
        
        // Hàng 1: Mã hộ & Diện tích
        addLabel(mainPanel, "Mã hộ *", 40, 70);
        txtMaHo = addTextField(mainPanel, 40, 95, 210);

        addLabel(mainPanel, "Diện tích (m2) *", 280, 70);
        txtDienTich = addTextField(mainPanel, 280, 95, 210);

        // Hàng 2: Tên chủ hộ & Số điện thoại
        addLabel(mainPanel, "Họ và Tên chủ hộ *", 40, 150);
        txtTenChuHo = addTextField(mainPanel, 40, 175, 210);

        addLabel(mainPanel, "Số điện thoại", 280, 150);
        txtSDT = addTextField(mainPanel, 280, 175, 210);

        // Hàng 3: Giới tính & Ngày sinh
        addLabel(mainPanel, "Giới tính *", 40, 230);
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cbGioiTinh.setBounds(40, 255, 210, 40);
        cbGioiTinh.setBackground(Color.WHITE);
        mainPanel.add(cbGioiTinh);

        addLabel(mainPanel, "Nhập Ngày sinh (dd/MM/yyyy)*", 280, 230);
        txtNgaySinh = addTextField(mainPanel, 280, 255, 210);

        // Hàng 4: Số CCCD
        addLabel(mainPanel, "Số CCCD/CMND", 40, 310);
        txtCCCD = addTextField(mainPanel, 40, 335, 450);
        txtCCCD.putClientProperty("JTextField.placeholderText", "Nhập Số CCCD/CMND (nếu có)");

        // --- Buttons ---
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
        btnSave.setBounds(40, 430, 450, 45);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSaveAction());
        mainPanel.add(btnSave);

        btnCancel = new JButton("Quay lại");
        btnCancel.setBounds(40, 490, 450, 30);
        btnCancel.setForeground(Color.GRAY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    // --- LOGIC CHUYỂN CHẾ ĐỘ SỬA (ĐÃ CẬP NHẬT) ---
    public void setEditData(HoKhau hk) {
        this.isEditMode = true;
        this.currentHoKhau = hk;

        // 1. Điền dữ liệu cơ bản
        txtMaHo.setText(hk.getSoCanHo());
        txtTenChuHo.setText(hk.getTenChuHo());
        txtDienTich.setText(String.valueOf(hk.getDienTich()));
        txtSDT.setText(hk.getSdt());

        // 2. Điền dữ liệu bổ sung (Ngày sinh, CCCD...)
        fillChuHoData(hk.getMaHo());

        // 3. Cấu hình trạng thái Edit (FIX Mã hộ & Diện tích)
        
        // --- Fixed Fields (Không cho sửa) ---
        txtMaHo.setEditable(false);
        txtMaHo.setForeground(Color.GRAY);
        
        txtDienTich.setEditable(false); 
        txtDienTich.setForeground(Color.GRAY);

        // --- Editable Fields (Cho phép sửa) ---
        txtTenChuHo.setEditable(true);
        txtSDT.setEditable(true);
        txtNgaySinh.setEditable(true);
        txtCCCD.setEditable(true);
        cbGioiTinh.setEnabled(true);

        // Đổi tiêu đề
        lblTitle.setText("CẬP NHẬT THÔNG TIN HỘ");
        btnSave.setText("Lưu thay đổi");
        setTitle("Cập nhật Hộ khẩu");
    }

    private void fillChuHoData(int maHo) {
        List<NhanKhau> listNK = nkController.getNhanKhauByHoKhau(maHo);
        for (NhanKhau nk : listNK) {
            if (nk.getQuanHe().equalsIgnoreCase("Chủ hộ") || nk.getQuanHe().equalsIgnoreCase("ChuHo")) {
                txtCCCD.setText(nk.getCccd());
                cbGioiTinh.setSelectedItem(nk.getGioiTinh());
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    txtNgaySinh.setText(sdf.format(nk.getNgaySinh()));
                } catch (Exception e) {
                    txtNgaySinh.setText("");
                }
                break;
            }
        }
    }

    // --- LOGIC XỬ LÝ CHÍNH (GIỮ NGUYÊN) ---
    private void handleSaveAction() {
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

            if (isEditMode) {
                updateHoKhau(tenChuHo, dienTich, sdt);
            } else {
                addHoKhau(maHo, tenChuHo, dienTich, sdt);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Diện tích phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!\nVui lòng cập nhật lại thông tin nhân khẩu nếu cần.");
            closeAndRefresh();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeAndRefresh() {
        parentPanel.loadData();
        dispose();
    }

    // --- Helper UI ---
    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 200, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
    }

    private JTextField addTextField(JPanel p, int x, int y, int width) {
        JTextField txt = new JTextField();
        txt.setBounds(x, y, width, 40);
        setupTextField(txt);
        p.add(txt);
        return txt;
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