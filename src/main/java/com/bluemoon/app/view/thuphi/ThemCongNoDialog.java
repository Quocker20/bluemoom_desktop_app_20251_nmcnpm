package com.bluemoon.app.view.thuphi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import com.bluemoon.app.controller.thuphi.ThuPhiController;
import com.bluemoon.app.model.KhoanPhi;

public class ThemCongNoDialog extends JDialog {

    private JTextField txtSoCanHo;
    private JComboBox<KhoanPhi> cbKhoanPhi;
    private JButton btnSave, btnCancel;

    private final ThuPhiController controller;
    private final ThuPhiPanel parentPanel;
    private final int thang, nam;

    public ThemCongNoDialog(JFrame parent, ThuPhiPanel parentPanel, int thang, int nam) {
        super(parent, "Thêm Công Nợ Đơn Lẻ", true);
        this.parentPanel = parentPanel;
        this.thang = thang;
        this.nam = nam;
        this.controller = new ThuPhiController();
        initComponents();
    }

    private void initComponents() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("THÊM CÔNG NỢ THÁNG " + thang + "/" + nam, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 10, 430, 30);
        mainPanel.add(lblTitle);

        // Input Số căn hộ
        addLabel(mainPanel, "Nhập Số căn hộ (VD: A-101) *", 60);
        txtSoCanHo = new JTextField();
        txtSoCanHo.setBounds(30, 85, 370, 40);
        txtSoCanHo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSoCanHo.setBorder(new RoundedBorder(8));
        mainPanel.add(txtSoCanHo);

        // Input Loại phí
        addLabel(mainPanel, "Chọn Khoản phí *", 145);
        cbKhoanPhi = new JComboBox<>();
        loadComboBoxData();
        cbKhoanPhi.setBounds(30, 170, 370, 40);
        cbKhoanPhi.setBackground(Color.WHITE);
        mainPanel.add(cbKhoanPhi);

        // Button Save
        btnSave = new JButton("Thêm công nợ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113)); // Màu xanh lá
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setBounds(30, 250, 370, 45);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSave());
        mainPanel.add(btnSave);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadComboBoxData() {
        List<KhoanPhi> list = controller.getAllKhoanPhi();
        for (KhoanPhi kp : list) {
            cbKhoanPhi.addItem(kp); // KhoanPhi đã có toString() trả về tên
        }
    }

    private void handleSave() {
        String soCanHo = txtSoCanHo.getText().trim();
        KhoanPhi selectedPhi = (KhoanPhi) cbKhoanPhi.getSelectedItem();

        if (soCanHo.isEmpty() || selectedPhi == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String result = controller.themCongNoDonLe(soCanHo, selectedPhi, thang, nam);

        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            parentPanel.loadData();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(30, y, 300, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
    }

    static class RoundedBorder extends AbstractBorder {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }
    }
}