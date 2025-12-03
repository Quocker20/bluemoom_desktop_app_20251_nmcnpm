package com.bluemoon.app.view;

import com.bluemoon.app.controller.LoginController;
import com.bluemoon.app.model.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private LoginController controller;

    private static final String BG_PATH = "/images/Login_Screen_Background.png";

    public LoginFrame() {
        controller = new LoginController();
        initComponents();
    }

    private void initComponents() {
        setTitle("BlueMoon Admin - Đăng nhập hệ thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL imgUrl = getClass().getResource(BG_PATH);
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(65, 88, 208), 
                            getWidth(), getHeight(), new Color(200, 80, 192));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel cardPanel = new RoundedPanel(25, Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(420, 500));
        cardPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Đăng nhập", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI Light", Font.BOLD, 32));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setBounds(0, 40, 420, 50);
        cardPanel.add(lblTitle);

        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(Color.GRAY);
        lblUser.setBounds(40, 120, 340, 20);
        cardPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(40, 145, 340, 45);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setOpaque(false);
        txtUsername.setBorder(new RoundedBorder(10));
        cardPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(Color.GRAY);
        lblPass.setBounds(40, 210, 340, 20);
        cardPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 235, 340, 45);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setOpaque(false);
        txtPassword.setBorder(new RoundedBorder(10));
        cardPanel.add(txtPassword);

        btnLogin = new JButton("ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(74, 144, 226)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setBounds(40, 320, 340, 50);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.add(btnLogin);

        btnExit = new JButton("Thoát ứng dụng");
        btnExit.setBounds(40, 390, 340, 30);
        btnExit.setContentAreaFilled(false);
        btnExit.setBorderPainted(false);
        btnExit.setForeground(Color.GRAY);
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.add(btnExit);

        backgroundPanel.add(cardPanel);

        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        try {
            User user = controller.login(username, password);
            if (user != null) {
                try {
                    MainFrame mainFrame = new MainFrame(user);
                    mainFrame.setVisible(true);
                    this.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color backgroundColor;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    class RoundedBorder extends AbstractBorder {
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
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius, radius/2, radius);
        }
    }
}