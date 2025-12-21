package com.bluemoon.app.view.hethong;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.bluemoon.app.controller.thongke.DashboardController;
import com.bluemoon.app.model.User;
import com.bluemoon.app.view.dancu.BienDongPanel;
import com.bluemoon.app.view.dancu.HoKhauPanel;
import com.bluemoon.app.view.thongke.BaoCaoPanel;
import com.bluemoon.app.view.thuphi.CauHinhPhiPanel;
import com.bluemoon.app.view.thuphi.GiaoDichPanel;
import com.bluemoon.app.view.thuphi.ThuPhiPanel;

public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private JPanel mainDashboardPanel;
    private Map<String, MenuButton> menuButtons = new HashMap<>();

    // Controller cho Dashboard
    private final DashboardController dashboardController;

    // Các Label hiển thị số liệu (Để cập nhật động)
    private JLabel lblValHoKhau, lblValNhanKhau, lblValTongThu, lblValCongNo;

    // Colors
    private final Color COL_SIDEBAR_BG = new Color(44, 62, 80);
    private final Color COL_MENU_ACTIVE = new Color(52, 152, 219);
    private final Color COL_MAIN_BG = new Color(245, 247, 250);
    private final Color COL_TEXT_SIDEBAR = new Color(230, 230, 230);

    public MainFrame(User user) {
        this.currentUser = user;
        this.dashboardController = new DashboardController();
        initComponents();
        refreshDashboardData(); // Load dữ liệu ngay khi mở
    }

    private void initComponents() {
        setTitle("Hệ thống Quản lý Chung cư BlueMoon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 768));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. SIDEBAR
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
                    int x = (getWidth() - icon.getIconWidth()) / 2;
                    g2d.setClip(0, 0, getWidth(), getHeight());
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
        topSidebar.add(createMenuItem("Cấu hình Phí", "/images/icon_settings.png", false));
        topSidebar.add(createMenuItem("Lịch sử Giao dịch", "/images/transaction_history.png", false));
        topSidebar.add(createMenuItem("Báo cáo & Thống kê", "/images/icon_report.png", false));
        topSidebar.add(createMenuItem("Hệ thống", "/images/icon_system.png", false));

        sidebarPanel.add(topSidebar, BorderLayout.NORTH);

        JButton btnLogout = createLogoutButton();
        JPanel bottomSidebar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        bottomSidebar.setOpaque(false);
        bottomSidebar.setBorder(new EmptyBorder(0, 40, 10, 0));
        bottomSidebar.add(btnLogout);
        sidebarPanel.add(bottomSidebar, BorderLayout.SOUTH);

        // 2. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        String roleName = currentUser != null ? currentUser.getVaiTro() : "Admin";
        String username = currentUser != null ? currentUser.getTenDangNhap() : "User";
        JLabel lblUserInfo = new JLabel("Xin chào, " + roleName + " (" + username + ")  ");
        lblUserInfo.setFont(new Font("Inter", Font.BOLD, 20));
        URL avatarUrl = getClass().getResource("/images/avatar.png");
        if (avatarUrl != null) {
            Icon avatarIcon = new ImageIcon(
                    new ImageIcon(avatarUrl).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            lblUserInfo.setIcon(avatarIcon);
        }
        lblUserInfo.setBorder(new EmptyBorder(0, 0, 0, 20));
        headerPanel.add(lblUserInfo, BorderLayout.EAST);

        // 3. BODY
        contentPanel = new JPanel();
        contentPanel.setBackground(COL_MAIN_BG);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        mainDashboardPanel = createDashboardUI();
        contentPanel.add(mainDashboardPanel, BorderLayout.CENTER);

        // 4. LẮP RÁP
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(headerPanel, BorderLayout.NORTH);
        centerContainer.add(contentPanel, BorderLayout.CENTER);

        this.add(sidebarPanel, BorderLayout.WEST);
        this.add(centerContainer, BorderLayout.CENTER);
    }

    // --- LOGIC LẤY DỮ LIỆU THẬT ---
    private void refreshDashboardData() {
        if (lblValHoKhau != null)
            lblValHoKhau.setText(String.valueOf(dashboardController.getSoLuongHo()));
        if (lblValNhanKhau != null)
            lblValNhanKhau.setText(String.valueOf(dashboardController.getSoLuongNguoi()));
        if (lblValTongThu != null)
            lblValTongThu.setText(dashboardController.getTongThuThangNay());
        if (lblValCongNo != null)
            lblValCongNo.setText(dashboardController.getCongNoThangNay());
    }

    private JPanel createDashboardUI() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setOpaque(false);

        // A. Cards Panel (Hiển thị số liệu thật)
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 20);

        // Khởi tạo các Label giá trị
        lblValHoKhau = new JLabel("...", SwingConstants.RIGHT);
        lblValNhanKhau = new JLabel("...", SwingConstants.RIGHT);
        lblValTongThu = new JLabel("...", SwingConstants.RIGHT);
        lblValCongNo = new JLabel("...", SwingConstants.RIGHT);

        gbc.gridx = 0;
        cardsPanel.add(createDashboardCard("Hộ cư dân", lblValHoKhau, "/images/icon_house.png"), gbc);
        gbc.gridx = 1;
        cardsPanel.add(createDashboardCard("Nhân khẩu", lblValNhanKhau, "/images/icon_people.png"), gbc);
        gbc.gridx = 2;
        cardsPanel.add(createDashboardCard("Tổng thu (T)", lblValTongThu, "/images/icon_money.png"), gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardsPanel.add(createDashboardCard("Công nợ (T)", lblValCongNo, "/images/icon_card.png"), gbc);

        // B. Charts Area
        JPanel mainSection = new JPanel(new GridBagLayout());
        mainSection.setOpaque(false);
        mainSection.setBorder(new EmptyBorder(30, 0, 0, 0));

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;

        // Placeholder for Charts (Sẽ được thay bằng BaoCaoPanel khi click menu)
        gbcMain.gridx = 0;
        gbcMain.weightx = 0.65;
        gbcMain.insets = new Insets(0, 0, 0, 20);
        mainSection.add(createPlaceholderPanel("Truy cập 'Báo cáo & Thống kê' để xem biểu đồ chi tiết", 0, 400),
                gbcMain);

        gbcMain.gridx = 1;
        gbcMain.weightx = 0.35;
        gbcMain.insets = new Insets(0, 0, 0, 0);
        mainSection.add(createPlaceholderPanel("Hệ thống hoạt động ổn định", 0, 400), gbcMain);

        dashboard.add(cardsPanel, BorderLayout.NORTH);
        dashboard.add(mainSection, BorderLayout.CENTER);

        return dashboard;
    }

    private void handleMenuClick(String menuTitle) {
        String userRole = currentUser.getVaiTro();
        MenuButton btn = menuButtons.get(menuTitle);
        if (btn != null && btn.isMenuActive())
            return;

        contentPanel.removeAll();
        switch (menuTitle) {
            case "Tổng quan":
                refreshDashboardData();
                contentPanel.add(mainDashboardPanel, BorderLayout.CENTER);
                break;
            case "Quản lý Cư dân":
                if (userRole.equals("KeToan")) {
                    showAccessDenied();
                    break;
                }
                contentPanel.add(new HoKhauPanel(), BorderLayout.CENTER);
                break;
            case "Quản lý Biến động":
                if (userRole.equals("KeToan")) {
                    showAccessDenied();
                    break;
                }
                contentPanel.add(new BienDongPanel(), BorderLayout.CENTER);
                break;
            case "Quản lý Thu phí":
                if (userRole.equals("ThuKy")) {
                    showAccessDenied();
                    break;
                }
                contentPanel.add(new ThuPhiPanel(), BorderLayout.CENTER);
                break;
            case "Cấu hình Phí":
                if (userRole.equals("ThuKy")) {
                    showAccessDenied();
                    break;
                }
                contentPanel.add(new CauHinhPhiPanel(), BorderLayout.CENTER);
                break;
            case "Lịch sử Giao dịch":
                if (userRole.equals("ThuKy")) {
                    showAccessDenied();
                    break;
                }                
                contentPanel.add(new GiaoDichPanel(), BorderLayout.CENTER);
                break;
            case "Báo cáo & Thống kê":
                contentPanel.add(new BaoCaoPanel(), BorderLayout.CENTER);
                break;
            case "Hệ thống":
                contentPanel.add(new DoiMatKhauPanel(currentUser), BorderLayout.CENTER);
                break;
            default:
                showDevelopingFeature(menuTitle);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
        updateActiveMenu(menuTitle);
    }

    // --- Helpers UI ---

    // Sửa hàm này để nhận JLabel thay vì String value cứng
    private JPanel createDashboardCard(String title, JLabel lblValueObj, String iconPath) {
        RoundedPanel card = new RoundedPanel(15, new Color(225, 225, 225));
        card.setPreferredSize(new Dimension(200, 100));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 15));

        JLabel lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(50, 50));
        try {
            URL url = getClass().getResource(iconPath);
            if (url != null)
                lblIcon.setIcon(
                        new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
        }

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        // Setup font cho Label giá trị
        lblValueObj.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValueObj.setForeground(Color.BLACK);

        JLabel lblTitle = new JLabel(title, SwingConstants.RIGHT);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(80, 80, 80));

        textPanel.add(lblValueObj);
        textPanel.add(lblTitle);
        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // Các hàm phụ trợ khác (giữ nguyên)
    private void showAccessDenied() {
        JLabel lbl = new JLabel("Bạn không có quyền truy cập chức năng này", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        lbl.setForeground(Color.GRAY);
        contentPanel.add(lbl, BorderLayout.CENTER);
    }

    private void showDevelopingFeature(String title) {
        JLabel lbl = new JLabel("Chức năng " + title + " đang phát triển", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        lbl.setForeground(Color.GRAY);
        contentPanel.add(lbl, BorderLayout.CENTER);
    }

    private void updateActiveMenu(String activeTitle) {
        for (Map.Entry<String, MenuButton> entry : menuButtons.entrySet()) {
            if (entry.getKey().equals(activeTitle))
                entry.getValue().setActive(true);
            else
                entry.getValue().setActive(false);
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
            if (url != null)
                btn.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
        }
        btn.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        return btn;
    }

    private JPanel createPlaceholderPanel(String text, int width, int height) {
        RoundedPanel panel = new RoundedPanel(15, new Color(225, 225, 225));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setLayout(new BorderLayout());
        JLabel lblHeader = new JLabel(text, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblHeader, BorderLayout.CENTER);
        return panel;
    }

    // Inner Classes (MenuButton, RoundedPanel) - Giữ nguyên như cũ
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
            try {
                URL url = getClass().getResource(iconPath);
                if (url != null)
                    setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
            }
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!isActive)
                        setForeground(Color.WHITE);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!isActive)
                        setForeground(COL_TEXT_SIDEBAR);
                }
            });
        }

        public boolean isMenuActive() {
            return this.isActive;
        }

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
}