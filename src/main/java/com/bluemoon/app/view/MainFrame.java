package com.bluemoon.app.view;

import com.bluemoon.app.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class MainFrame extends JFrame {

    private User currentUser;

    // Màu sắc từ V1 (Dùng cho Sidebar và Header)
    private final Color COL_SIDEBAR_BG = new Color(44, 62, 80);
    private final Color COL_MENU_ACTIVE = new Color(52, 152, 219);
    private final Color COL_MAIN_BG = new Color(245, 247, 250);
    private final Color COL_TEXT_SIDEBAR = new Color(230, 230, 230);

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ thống Quản lý Chung cư BlueMoon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 768));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==================================================================
        // 1. SIDEBAR (CÓ SỬA NÚT LOGOUT)
        // ==================================================================
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(COL_SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());

                URL bgUrl = getClass().getResource("/images/bg_sidebar.png");
                if (bgUrl != null) {
                    ImageIcon icon = new ImageIcon(bgUrl);
                    Graphics2D g2d = (Graphics2D) g.create();
                    int iconWidth = icon.getIconWidth();
                    int panelWidth = getWidth();
                    int x = (panelWidth - iconWidth) / 2;

                    g2d.setClip(0, 0, panelWidth, getHeight());
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    g2d.drawImage(icon.getImage(), x, 0, this);
                    g2d.dispose();
                }
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(260, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());

        JPanel topSidebar = new JPanel();
        topSidebar.setOpaque(false);
        topSidebar.setLayout(new BoxLayout(topSidebar, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel("BLUEMOON ADMIN");
        lblLogo.setFont(new Font("Inter", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(30, 25, 30, 0));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSidebar.add(lblLogo);

        topSidebar.add(createMenuItem("Tổng quan", "/images/icon_overview.png", true));
        topSidebar.add(createMenuItem("Quản lý Cư dân", "/images/icon_resident.png", false));
        topSidebar.add(createMenuItem("Quản lý Thu phí", "/images/icon_fee.png", false));
        topSidebar.add(createMenuItem("Báo cáo & Thống kê", "/images/icon_report.png", false));
        topSidebar.add(createMenuItem("Hệ thống", "/images/icon_system.png", false));

        sidebarPanel.add(topSidebar, BorderLayout.NORTH);

        // Nút Logout nằm ở dưới cùng
        JButton btnLogout = createLogoutButton();
        JPanel bottomSidebar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        bottomSidebar.setOpaque(false);
        bottomSidebar.setBorder(new EmptyBorder(0, 40, 10, 0)); // Padding trái để căn giữa đẹp hơn
        bottomSidebar.add(btnLogout);
        sidebarPanel.add(bottomSidebar, BorderLayout.SOUTH);

        // ==================================================================
        // 2. HEADER (GIỮ NGUYÊN)
        // ==================================================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        String roleName = currentUser != null ? currentUser.getVaiTro() : "Admin";
        String username = currentUser != null ? currentUser.getTenDangNhap() : "User";
        String displayRoleName;
        switch (roleName) {
            case "QuanLy":
                displayRoleName = "Quản lý";
                break;
            case "KeToan":
                displayRoleName = "Kế toán";
                break;
            default:
                displayRoleName = "Thư ký";
        }
        JLabel lblUserInfo = new JLabel("Xin chào, " + displayRoleName + " (" + username + ")  ");
        lblUserInfo.setFont(new Font("Inter", Font.BOLD, 20));

        Icon avatarIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar.png")).getImage()
                .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        lblUserInfo.setIcon(avatarIcon);
        lblUserInfo.setBorder(new EmptyBorder(0, 0, 0, 20));
        headerPanel.add(lblUserInfo, BorderLayout.EAST);

        // ==================================================================
        // 3. BODY CONTENT (GIỮ NGUYÊN CẤU TRÚC V3.1.1)
        // ==================================================================
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(COL_MAIN_BG);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // A. Hàng Thẻ Thống kê
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setOpaque(false);
        GridBagConstraints gbcCards = new GridBagConstraints();
        gbcCards.fill = GridBagConstraints.BOTH;
        gbcCards.weightx = 1.0;
        gbcCards.insets = new Insets(0, 0, 0, 20);

        gbcCards.gridx = 0;
        cardsPanel.add(createDashboardCard("Hộ cư dân", "150", "/images/icon_house.png"), gbcCards);

        gbcCards.gridx = 1;
        cardsPanel.add(createDashboardCard("Nhân khẩu", "450", "/images/icon_people.png"), gbcCards);

        gbcCards.gridx = 2;
        cardsPanel.add(createDashboardCard("Tổng thu", "50.0 tr", "/images/icon_money.png"), gbcCards);

        gbcCards.gridx = 3;
        gbcCards.insets = new Insets(0, 0, 0, 0);
        cardsPanel.add(createDashboardCard("Công nợ", "5.0 tr", "/images/icon_card.png"), gbcCards);

        // B. Khu vực Biểu đồ & Thông báo (GridBagLayout - V3.1.1)
        JPanel mainSection = new JPanel(new GridBagLayout());
        mainSection.setOpaque(false);
        mainSection.setBorder(new EmptyBorder(30, 0, 0, 0));

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;

        // 1. Biểu đồ thu phí (~65% width)
        gbcMain.gridx = 0;
        gbcMain.weightx = 0.65;
        gbcMain.insets = new Insets(0, 0, 0, 20);
        mainSection.add(createPlaceholderPanel("Biểu đồ thu phí theo tháng", 0, 400), gbcMain);
        
        // 2. Thông báo mới (~35% width)
        gbcMain.gridx = 1;
        gbcMain.weightx = 0.35;
        gbcMain.insets = new Insets(0, 0, 0, 0);
        mainSection.add(createPlaceholderPanel("Thông báo mới", 0, 400), gbcMain);

        JPanel bodyContainer = new JPanel(new BorderLayout());
        bodyContainer.setOpaque(false);
        bodyContainer.add(cardsPanel, BorderLayout.NORTH);
        bodyContainer.add(mainSection, BorderLayout.CENTER);

        contentPanel.add(bodyContainer, BorderLayout.NORTH);

        // ==================================================================
        // 4. LẮP RÁP TỔNG THỂ
        // ==================================================================
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(headerPanel, BorderLayout.NORTH);
        centerContainer.add(contentPanel, BorderLayout.CENTER);

        this.add(sidebarPanel, BorderLayout.WEST);
        this.add(centerContainer, BorderLayout.CENTER);
    }

    // ==================================================================
    // CÁC HÀM HELPER
    // ==================================================================

    // 1. Helper cho Menu
    private JButton createMenuItem(String text, String iconPath, boolean isActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isActive) {
                    g2d.setColor(COL_MENU_ACTIVE);
                    g2d.fill(new java.awt.geom.RoundRectangle2D.Float(
                            0, 0, getWidth(), getHeight(), 20, 20));
                    g2d.setColor(new Color(100, 200, 255));
                    g2d.fill(new java.awt.geom.RoundRectangle2D.Float(
                            0, 0, 6, getHeight(), 20, 20));
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setMaximumSize(new Dimension(260, 50));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Inter", Font.CENTER_BASELINE, 20));
        btn.setForeground(COL_TEXT_SIDEBAR);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 25, 10, 0));
        btn.setIconTextGap(15);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        URL url = getClass().getResource(iconPath);
        if (url != null)
            btn.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isActive)
                    btn.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isActive)
                    btn.setForeground(COL_TEXT_SIDEBAR);
            }
        });
        return btn;
    }

    // 2. Helper cho Logout (ĐÃ CẬP NHẬT LINK CLONE)
    private JButton createLogoutButton() {
        JButton btn = new JButton(" Đăng xuất");
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFont(new Font("Inter", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIconTextGap(10); // Khoảng cách giữa icon và chữ

        // LOGIC LẤY ICON:
        try {
            // Cách 1: Thử lấy từ Resource nội bộ (của bạn)
            URL url = getClass().getResource("/images/icon_logout.png");

            // Cách 2: Nếu không có, dùng Link Clone (Icon online màu trắng từ Icons8)
            if (url == null) {
                // Đây là link clone, bạn có thể thay bằng resource sau khi có file
                url = new URL("https://img.icons8.com/ios-glyphs/30/ffffff/exit.png"); 
            }
            
            ImageIcon icon = new ImageIcon(url);
            // Resize về 20x20
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi nếu không tải được icon
        }

        btn.addActionListener(e -> logout());
        return btn;
    }

    // 3. Helper cho Dashboard Card
    private JPanel createDashboardCard(String title, String value, String iconPath) {
        RoundedPanel card = new RoundedPanel(15, new Color(225, 225, 225));
        card.setPreferredSize(new Dimension(200, 100));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 15));

        JLabel lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(50, 50));
        URL url = getClass().getResource(iconPath);
        if (url != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            lblIcon.setIcon(icon);
        } else {
            lblIcon.setText("Icon");
            lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
            lblIcon.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel lblValue = new JLabel(value, SwingConstants.RIGHT);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(Color.BLACK);

        JLabel lblTitle = new JLabel(title, SwingConstants.RIGHT);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(80, 80, 80));

        textPanel.add(lblValue);
        textPanel.add(lblTitle);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    // 4. Helper cho Placeholder
    private JPanel createPlaceholderPanel(String text, int width, int height) {
        RoundedPanel panel = new RoundedPanel(15, new Color(225, 225, 225));
        panel.setPreferredSize(new Dimension(width, height)); 
        panel.setLayout(new BorderLayout());

        JLabel lblHeader = new JLabel(text);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setBorder(new EmptyBorder(15, 20, 0, 0));

        panel.add(lblHeader, BorderLayout.NORTH);
        return panel;
    }

    private void logout() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }

    // Class tiện ích vẽ panel bo tròn
    class RoundedPanel extends JPanel {
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }
        java.awt.EventQueue.invokeLater(() -> {
            User dummyUser = new User(1, "admin_test", "", "QuanLy");
            new MainFrame(dummyUser).setVisible(true);
        });
    }
}