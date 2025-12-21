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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import com.bluemoon.app.controller.UserController;
import com.bluemoon.app.model.User;

public class DoiMatKhauPanel extends JPanel {

    private JPasswordField txtPassOld;
    private JPasswordField txtPassNew;
    private JPasswordField txtPassConfirm;
    private JButton btnSave;

    private final User currentUser;
    private final UserController controller;

    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_HEADER_BG = Color.WHITE;
    private final Color COL_PRIMARY = new Color(52, 152, 219);

    public DoiMatKhauPanel(User user) {
        this.currentUser = user;
        this.controller = new UserController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 2. Form Content (Căn giữa)
        JPanel contentContainer = new JPanel(new GridBagLayout());
        contentContainer.setOpaque(false);

        RoundedPanel formPanel = new RoundedPanel(20, Color.WHITE);
        formPanel.setPreferredSize(new Dimension(500, 450));
        formPanel.setLayout(null);

        JLabel lblSubTitle = new JLabel("THAY ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSubTitle.setForeground(COL_PRIMARY);
        lblSubTitle.setBounds(0, 30, 500, 30);
        formPanel.add(lblSubTitle);

        addLabel(formPanel, "Mật khẩu hiện tại *", 100);
        txtPassOld = addPasswordField(formPanel, 125);

        addLabel(formPanel, "Mật khẩu mới *", 190);
        txtPassNew = addPasswordField(formPanel, 215);

        addLabel(formPanel, "Xác nhận mật khẩu mới *", 280);
        txtPassConfirm = addPasswordField(formPanel, 305);

        btnSave = new JButton("Lưu thay đổi") {
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
        btnSave.setBounds(50, 380, 400, 45);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleChangePassword());
        formPanel.add(btnSave);

        contentContainer.add(formPanel);
        add(contentContainer, BorderLayout.CENTER);
    }

    private void handleChangePassword() {
        String oldPass = new String(txtPassOld.getPassword());
        String newPass = new String(txtPassNew.getPassword());
        String confirmPass = new String(txtPassConfirm.getPassword());

        String result = controller.doiMatKhau(currentUser, oldPass, newPass, confirmPass);

        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            // Reset form
            txtPassOld.setText("");
            txtPassNew.setText("");
            txtPassConfirm.setText("");
        } else {
            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(50, y, 400, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.GRAY);
        p.add(l);
    }

    private JPasswordField addPasswordField(JPanel p, int y) {
        JPasswordField t = new JPasswordField();
        t.setBounds(50, y, 400, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new RoundedBorder(8));
        p.add(t);
        return t;
    }

    // Custom UI Classes
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