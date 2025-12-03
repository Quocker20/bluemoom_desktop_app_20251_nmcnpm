package com.bluemoon.app.view;

import com.bluemoon.app.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private JPanel mainDashboardPanel;
    private Map<String, MenuButton> menuButtons = new HashMap<>();

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
        topSidebar.add(createMenuItem("Quản lý Biến động", "/images/icon_change.png", false));
        topSidebar.add(createMenuItem("Quản lý Thu phí", "/images/icon_fee.png", false));
        topSidebar.add(createMenuItem("Báo cáo & Thống kê", "/images/icon_report.png", false));
        topSidebar.add(createMenuItem("Hệ thống", "/images/icon_system.png", false));

        sidebarPanel.add(topSidebar, BorderLayout.NORTH);

        JButton btnLogout = createLogoutButton();
        JPanel bottomSidebar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        bottomSidebar.setOpaque(false);
        bottomSidebar.setBorder(new EmptyBorder(0, 40, 10, 0));
        bottomSidebar.add(btnLogout);
        sidebarPanel.add(bottomSidebar, BorderLayout.SOUTH);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        String roleName = currentUser != null ? currentUser.getVaiTro() : "Admin";
        String username = currentUser != null ? currentUser.getTenDangNhap() : "User";
        JLabel lblUserInfo = new JLabel("Xin chào, " + roleName + " (" + username + ")  ");
        lblUserInfo.setFont(new Font("Inter", Font.BOLD, 20));

        URL avatarUrl = getClass().getResource("/images/avatar.png");
        if(avatarUrl != null) {
             Icon avatarIcon = new ImageIcon(new ImageIcon(avatarUrl).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
             lblUserInfo.setIcon(avatarIcon);
        }
        lblUserInfo.setBorder(new EmptyBorder(0, 0, 0, 20));
        headerPanel.add(lblUserInfo, BorderLayout.EAST);

        contentPanel = new JPanel();
        contentPanel.setBackground(COL_MAIN_BG);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        mainDashboardPanel = createDashboardUI();
        contentPanel.add(mainDashboardPanel, BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(headerPanel, BorderLayout.NORTH);
        centerContainer.add(contentPanel, BorderLayout.CENTER);

        this.add(sidebarPanel, BorderLayout.WEST);
        this.add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createDashboardUI() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setOpaque(false);

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

        JPanel mainSection = new JPanel(new GridBagLayout());
        mainSection.setOpaque(false);
        mainSection.setBorder(new EmptyBorder(30, 0, 0, 0));

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;

        gbcMain.gridx = 0;
        gbcMain.weightx = 0.65;
        gbcMain.insets = new Insets(0, 0, 0, 20);
        mainSection.add(createPlaceholderPanel("Biểu đồ thu phí theo tháng", 0, 400), gbcMain);

        gbcMain.gridx = 1;
        gbcMain.weightx = 0.35;
        gbcMain.insets = new Insets(0, 0, 0, 0);
        mainSection.add(createPlaceholderPanel("Thông báo mới", 0, 400), gbcMain);

        dashboard.add(cardsPanel, BorderLayout.NORTH);
        dashboard.add(mainSection, BorderLayout.CENTER);

        return dashboard;
    }

    private void handleMenuClick(String menuTitle) {
        MenuButton btn = menuButtons.get(menuTitle);
        if (btn != null && btn.isMenuActive()) {
            return;
        }

        contentPanel.removeAll();
        switch (menuTitle) {
            case "Tổng quan":
                contentPanel.add(mainDashboardPanel, BorderLayout.CENTER);
                break;
            case "Quản lý Cư dân":
                contentPanel.add(new HoKhauPanel(), BorderLayout.CENTER);
                break;
            case "Quản lý Biến động":
                contentPanel.add(new BienDongPanel(), BorderLayout.CENTER);
                break;
            case "Quản lý Thu phí":
                // contentPanel.add(new ThuPhiPanel(), BorderLayout.CENTER);
                JLabel lbl = new JLabel("Chức năng đang bảo trì", SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.ITALIC, 24));
                contentPanel.add(lbl, BorderLayout.CENTER);
                break;
            default:
                JLabel lblDef = new JLabel("Chức năng " + menuTitle + " đang phát triển", SwingConstants.CENTER);
                lblDef.setFont(new Font("Segoe UI", Font.ITALIC, 24));
                lblDef.setForeground(Color.GRAY);
                contentPanel.add(lblDef, BorderLayout.CENTER);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
        updateActiveMenu(menuTitle);
    }

    private void updateActiveMenu(String activeTitle) {
        for (Map.Entry<String, MenuButton> entry : menuButtons.entrySet()) {
            if (entry.getKey().equals(activeTitle)) {
                entry.getValue().setActive(true);
            } else {
                entry.getValue().setActive(false);
            }
        }
    }

    private JButton createMenuItem(String text, String iconPath, boolean initActive) {
        MenuButton btn = new MenuButton(text, iconPath, initActive);
        btn.addActionListener(e -> handleMenuClick(text));
        menuButtons.put(text, btn);
        return btn;
    }

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
        btn.setIconTextGap(10);
        try {
            URL url = getClass().getResource("/images/icon_logout.png");
            if(url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}
        btn.addActionListener(e -> logout());
        return btn;
    }

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

    class MenuButton extends JButton {
        private boolean isActive;
        public MenuButton(String text, String iconPath, boolean isActive) {
            super(text);
            this.isActive = isActive;
            setMaximumSize(new Dimension(260, 50));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setFont(new Font("Inter", Font.CENTER_BASELINE, 20));
            setForeground(isActive ? Color.WHITE : COL_TEXT_SIDEBAR);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 25, 10, 0));
            setIconTextGap(15);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            URL url = getClass().getResource(iconPath);
            if (url != null)
                setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!isActive) setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!isActive) setForeground(COL_TEXT_SIDEBAR);
                }
            });
        }
        public boolean isMenuActive() { return this.isActive; }
        public void setActive(boolean active) {
            this.isActive = active;
            this.setForeground(active ? Color.WHITE : COL_TEXT_SIDEBAR);
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isActive) {
                g2d.setColor(COL_MENU_ACTIVE);
                g2d.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(new Color(100, 200, 255));
                g2d.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, 6, getHeight(), 20, 20));
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }

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
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { }
        java.awt.EventQueue.invokeLater(() -> {
            User dummyUser = new User(1, "admin_test", "", "QuanLy");
            new MainFrame(dummyUser).setVisible(true);
        });
    }
}