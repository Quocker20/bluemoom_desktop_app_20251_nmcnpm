package com.bluemoon.app.view.hethong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import com.bluemoon.app.controller.hethong.UserController;

public class DangKyPanel extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnRegister;

    private final UserController controller;

    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_PRIMARY = new Color(52, 152, 219);

    public DangKyPanel() {
        this.controller = new UserController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Form Content (Căn giữa)
        JPanel contentContainer = new JPanel(new GridBagLayout());
        contentContainer.setOpaque(false);

        RoundedPanel formPanel = new RoundedPanel(20, Color.WHITE);
        formPanel.setPreferredSize(new Dimension(500, 450));
        formPanel.setLayout(null);

        JLabel lblSubTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN MỚI", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSubTitle.setForeground(COL_PRIMARY);
        lblSubTitle.setBounds(0, 30, 500, 30);
        formPanel.add(lblSubTitle);

        // Trường 1: Tên đăng nhập
        addLabel(formPanel, "Tên đăng nhập *", 100);
        txtUsername = addTextField(formPanel, 125);

        // Trường 2: Mật khẩu
        addLabel(formPanel, "Mật khẩu *", 190);
        txtPassword = addPasswordField(formPanel, 215);

        // Trường 3: Vai trò (Role)
        addLabel(formPanel, "Vai trò hệ thống *", 280);
        cbRole = new JComboBox<>(new String[] { "Quản lý", "Kế toán", "Thư ký" });
        cbRole.setBounds(50, 305, 400, 40);
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbRole.setBackground(Color.WHITE);
        formPanel.add(cbRole);

        // Nút Đăng ký
        btnRegister = new JButton("Đăng ký") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COL_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRegister.setBounds(50, 380, 400, 45);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> handleRegister());
        formPanel.add(btnRegister);

        contentContainer.add(formPanel);
        add(contentContainer, BorderLayout.CENTER);
    }

    private void handleRegister() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String selectedRoleUI = (String) cbRole.getSelectedItem();

        // Mapping giá trị hiển thị sang giá trị lưu DB
        String userRole = "ThuKy"; // Mặc định
        if ("Quản lý".equals(selectedRoleUI)) {
            userRole = "QuanLy";
        } else if ("Kế toán".equals(selectedRoleUI)) {
            userRole = "KeToan";
        } else if ("Thư ký".equals(selectedRoleUI)) {
            userRole = "ThuKy";
        }

        // Gọi Controller
        String result = controller.register(username, password, userRole);

        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công!");
            // Reset form sau khi thành công
            txtUsername.setText("");
            txtPassword.setText("");
            cbRole.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(50, y, 400, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.GRAY);
        p.add(l);
    }

    private JTextField addTextField(JPanel p, int y) {
        JTextField t = new JTextField();
        t.setBounds(50, y, 400, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new RoundedBorder(8));
        p.add(t);
        return t;
    }

    private JPasswordField addPasswordField(JPanel p, int y) {
        JPasswordField t = new JPasswordField();
        t.setBounds(50, y, 400, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new RoundedBorder(8));
        p.add(t);
        return t;
    }

    // Custom UI Classes (Giữ nguyên style như DoiMatKhauPanel)
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