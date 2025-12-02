package com.bluemoon.app.view;

import com.bluemoon.app.controller.TamTruTamVangController;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.model.TamTruTamVang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DangKyBienDongDialog extends JDialog {

    private JComboBox<String> cbLoaiHinh;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JTextArea txtLyDo;
    private JButton btnSave;
    private JButton btnCancel;

    private TamTruTamVangController controller;
    private NhanKhau nhanKhau; 

    public DangKyBienDongDialog(JFrame parentFrame, NhanKhau nhanKhau) {
        super(parentFrame, "Đăng ký Biến động", true);
        this.nhanKhau = nhanKhau;
        this.controller = new TamTruTamVangController();
        initComponents();
    }

    private void initComponents() {
        setSize(650, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("KHAI BÁO TẠM TRÚ / TẠM VẮNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 10, 480, 30);
        mainPanel.add(lblTitle);

        JLabel lblName = new JLabel("Nhân khẩu: " + nhanKhau.getHoTen() + " (" + nhanKhau.getCccd() + ")");
        lblName.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblName.setForeground(Color.DARK_GRAY);
        lblName.setBounds(40, 50, 400, 20);
        mainPanel.add(lblName);

        addLabel(mainPanel, "Loại biến động *", 80);
        cbLoaiHinh = new JComboBox<>(new String[]{"Tạm trú", "Tạm vắng"});
        cbLoaiHinh.setBounds(40, 105, 400, 35);
        cbLoaiHinh.setBackground(Color.WHITE);
        mainPanel.add(cbLoaiHinh);

        addLabel(mainPanel, "Từ ngày (dd/MM/yyyy) *", 150);
        txtTuNgay = addTextField(mainPanel, 175);

        addLabel(mainPanel, "Đến ngày (dd/MM/yyyy)", 220);
        txtDenNgay = addTextField(mainPanel, 245);

        addLabel(mainPanel, "Lý do *", 290);
        txtLyDo = new JTextArea();
        txtLyDo.setBounds(40, 315, 400, 50);
        txtLyDo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtLyDo);

        // Nút Lưu (Dùng ColoredButton để có nền xanh, chữ đen)
        btnSave = new ColoredButton("Lưu hồ sơ", new Color(52, 152, 219));
        btnSave.setBounds(260, 380, 180, 40);
        btnSave.addActionListener(e -> handleSave());
        mainPanel.add(btnSave);

        btnCancel = new JButton("Hủy");
        btnCancel.setBounds(40, 380, 100, 40);
        btnCancel.setContentAreaFilled(false); // Trong suốt
        btnCancel.setFocusPainted(false);
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

            TamTruTamVang tttv = new TamTruTamVang(nhanKhau.getMaNhanKhau(), loai, tuNgay, denNgay, lyDo);
            
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
        t.setBounds(40, y, 180, 35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(t);
        return t;
    }

    // --- INNER CLASS: Nút màu tùy chỉnh ---
    class ColoredButton extends JButton {
        public ColoredButton(String text, Color bgColor) {
            super(text);
            setBackground(bgColor);
            setForeground(Color.BLACK); // Chữ màu đen
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            setContentAreaFilled(false); // Tắt vẽ mặc định
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền màu
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10)); // Bo góc 10px
            
            super.paintComponent(g);
            g2.dispose();
        }
    }
}