package com.bluemoon.app.view;

import com.bluemoon.app.controller.NhanKhauController;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.model.TamTruTamVang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bluemoon.app.controller.TamTruTamVangController;

public class DangKyBienDongDialog extends JDialog {

    private JComboBox<String> cbLoaiHinh;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JTextArea txtLyDo;
    private JButton btnSave;
    private JButton btnCancel;

    private TamTruTamVangController controller;
    private NhanKhau nhanKhau; // Nhân khẩu đang được đăng ký

    public DangKyBienDongDialog(JFrame parentFrame, NhanKhau nhanKhau) {
        super(parentFrame, "Đăng ký Biến động", true);
        this.nhanKhau = nhanKhau;
        this.controller = new TamTruTamVangController();
        initComponents();
    }

    private void initComponents() {
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Tiêu đề
        JLabel lblTitle = new JLabel("KHAI BÁO TẠM TRÚ / TẠM VẮNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 10, 480, 30);
        mainPanel.add(lblTitle);

        // Thông tin nhân khẩu (Read-only)
        JLabel lblName = new JLabel("Nhân khẩu: " + nhanKhau.getHoTen() + " (" + nhanKhau.getCccd() + ")");
        lblName.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblName.setForeground(Color.DARK_GRAY);
        lblName.setBounds(40, 50, 400, 20);
        mainPanel.add(lblName);

        // 1. Loại hình
        addLabel(mainPanel, "Loại biến động *", 80);
        cbLoaiHinh = new JComboBox<>(new String[]{"Tạm trú", "Tạm vắng"});
        cbLoaiHinh.setBounds(40, 105, 400, 35);
        cbLoaiHinh.setBackground(Color.WHITE);
        mainPanel.add(cbLoaiHinh);

        // 2. Từ ngày
        addLabel(mainPanel, "Từ ngày (dd/MM/yyyy) *", 150);
        txtTuNgay = addTextField(mainPanel, 175);

        // 3. Đến ngày
        addLabel(mainPanel, "Đến ngày (dd/MM/yyyy)", 220);
        txtDenNgay = addTextField(mainPanel, 245);

        // 4. Lý do
        addLabel(mainPanel, "Lý do *", 290);
        txtLyDo = new JTextArea();
        txtLyDo.setBounds(40, 315, 400, 50);
        txtLyDo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtLyDo);

        // Buttons
        btnSave = new JButton("Lưu hồ sơ");
        btnSave.setBounds(260, 380, 180, 40);
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> handleSave());
        mainPanel.add(btnSave);

        btnCancel = new JButton("Hủy");
        btnCancel.setBounds(40, 380, 100, 40);
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleSave() {
        try {
            String loai = cbLoaiHinh.getSelectedItem().toString();
            String tuNgayStr = txtTuNgay.getText().trim();
            String denNgayStr = txtDenNgay.getText().trim();
            String lyDo = txtLyDo.getText().trim();

            if (tuNgayStr.isEmpty() || lyDo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu và lý do!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            
            Date tuNgay = sdf.parse(tuNgayStr);
            Date denNgay = null;
            if (!denNgayStr.isEmpty()) {
                denNgay = sdf.parse(denNgayStr);
                if (denNgay.before(tuNgay)) {
                    JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Tạo đối tượng TamTruTamVang
            TamTruTamVang tttv = new TamTruTamVang(nhanKhau.getMaNhanKhau(), loai, tuNgay, denNgay, lyDo);
            
            // Gọi Controller
            if (controller.addTamTruTamVang(tttv)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày tháng không hợp lệ (dd/MM/yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(40, y, 400, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
    }

    private JTextField addTextField(JPanel p, int y) {
        JTextField t = new JTextField();
        t.setBounds(40, y, 180, 35); // Input ngày ngắn hơn
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(t);
        return t;
    }
}