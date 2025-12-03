package com.bluemoon.app.view;

import com.bluemoon.app.controller.TamTruTamVangController;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.model.TamTruTamVang;
import com.bluemoon.app.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DangKyBienDongDialog extends JDialog {

    private JComboBox<String> cbLoaiHinh;
    private JTextField txtTuNgay, txtDenNgay;
    private JTextArea txtLyDo;
    private JButton btnSave, btnCancel;
    private TamTruTamVangController controller;
    private NhanKhau nhanKhau; 

    public DangKyBienDongDialog(JFrame parentFrame, NhanKhau nhanKhau) {
        super(parentFrame, "Đăng ký Biến động", true);
        this.nhanKhau = nhanKhau;
        this.controller = new TamTruTamVangController();
        initComponents();
    }

    private void initComponents() {
        setSize(650, 520);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("KHAI BÁO TẠM TRÚ / TẠM VẮNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 15, 630, 30);
        mainPanel.add(lblTitle);

        JLabel lblName = new JLabel("Nhân khẩu: " + nhanKhau.getHoTen() + " (" + (nhanKhau.getCccd() == null ? "N/A" : nhanKhau.getCccd()) + ")");
        lblName.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblName.setForeground(Color.DARK_GRAY);
        lblName.setBounds(60, 60, 500, 20);
        mainPanel.add(lblName);

        addLabel(mainPanel, "Loại biến động *", 90);
        cbLoaiHinh = new JComboBox<>(new String[]{AppConstants.TAM_TRU, AppConstants.TAM_VANG, AppConstants.KHAI_TU});
        cbLoaiHinh.setBounds(60, 115, 500, 35);
        cbLoaiHinh.setBackground(Color.WHITE);
        mainPanel.add(cbLoaiHinh);

        addLabel(mainPanel, "Từ ngày (dd/MM/yyyy) *", 170);
        txtTuNgay = addTextField(mainPanel, 195);

        addLabel(mainPanel, "Đến ngày (dd/MM/yyyy)", 330);
        txtDenNgay = addTextField(mainPanel, 355);

        addLabel(mainPanel, "Lý do *", 250);
        txtLyDo = new JTextArea();
        txtLyDo.setBounds(60, 385, 500, 60);
        txtLyDo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtLyDo);

        // Chỉnh lại vị trí y của label và textfield cho cân đối
        mainPanel.removeAll();
        mainPanel.add(lblTitle);
        mainPanel.add(lblName);
        mainPanel.add(cbLoaiHinh);
        
        JLabel l1 = new JLabel("Loại biến động *"); l1.setBounds(60, 90, 200, 20); l1.setFont(new Font("Segoe UI", Font.PLAIN, 13)); mainPanel.add(l1);
        
        JLabel l2 = new JLabel("Từ ngày (dd/MM/yyyy) *"); l2.setBounds(60, 170, 200, 20); l2.setFont(new Font("Segoe UI", Font.PLAIN, 13)); mainPanel.add(l2);
        txtTuNgay.setBounds(60, 195, 220, 35); mainPanel.add(txtTuNgay);

        JLabel l3 = new JLabel("Đến ngày (dd/MM/yyyy)"); l3.setBounds(340, 170, 200, 20); l3.setFont(new Font("Segoe UI", Font.PLAIN, 13)); mainPanel.add(l3);
        txtDenNgay.setBounds(340, 195, 220, 35); mainPanel.add(txtDenNgay);

        JLabel l4 = new JLabel("Lý do *"); l4.setBounds(60, 250, 200, 20); l4.setFont(new Font("Segoe UI", Font.PLAIN, 13)); mainPanel.add(l4);
        txtLyDo.setBounds(60, 275, 500, 80); mainPanel.add(txtLyDo);


        btnSave = new ColoredButton("Lưu hồ sơ", new Color(52, 152, 219));
        btnSave.setBounds(360, 400, 200, 45);
        btnSave.addActionListener(e -> handleSave());
        mainPanel.add(btnSave);

        btnCancel = new JButton("Hủy");
        btnCancel.setBounds(60, 400, 120, 45);
        btnCancel.setContentAreaFilled(false);
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
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu và lý do!");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date tuNgay = sdf.parse(tuNgayStr);
            Date denNgay = null;
            if (!denNgayStr.isEmpty()) {
                denNgay = sdf.parse(denNgayStr);
                if (denNgay.before(tuNgay)) {
                    JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!");
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
        l.setBounds(60, y, 400, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
    }

    private JTextField addTextField(JPanel p, int y) {
        JTextField t = new JTextField();
        t.setBounds(60, y, 200, 35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(t);
        return t;
    }

    class ColoredButton extends JButton {
        public ColoredButton(String text, Color bgColor) {
            super(text);
            setBackground(bgColor);
            setForeground(Color.BLACK);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            super.paintComponent(g);
            g2.dispose();
        }
    }
}