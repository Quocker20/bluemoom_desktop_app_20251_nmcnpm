package com.bluemoon.app.view;

import com.bluemoon.app.controller.ThuPhiController;
import com.bluemoon.app.model.CongNo;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class ThanhToanDialog extends JDialog {

    private JTextField txtNguoiNop;
    private JTextField txtSoTien;
    private JComboBox<String> cbHinhThuc;
    private JTextArea txtGhiChu;
    private JButton btnConfirm;
    private JButton btnCancel;

    private CongNo congNo; // Khoản nợ đang được thanh toán
    private ThuPhiController controller;
    private ThuPhiPanel parentPanel;

    public ThanhToanDialog(JFrame parentFrame, ThuPhiPanel parentPanel, CongNo congNo) {
        super(parentFrame, "Ghi nhận thanh toán", true);
        this.parentPanel = parentPanel;
        this.congNo = congNo;
        this.controller = new ThuPhiController();
        initComponents();
    }

    private void initComponents() {
        setSize(500, 580);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Tiêu đề
        JLabel lblTitle = new JLabel("GHI NHẬN THANH TOÁN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(46, 204, 113)); // Màu xanh lá (Tiền)
        lblTitle.setBounds(0, 15, 480, 30);
        mainPanel.add(lblTitle);

        // --- PHẦN THÔNG TIN (READ-ONLY) ---
        JPanel infoPanel = new RoundedPanel(15, new Color(245, 247, 250));
        infoPanel.setBounds(40, 60, 400, 110);
        infoPanel.setLayout(null);

        JLabel lblMaHo = new JLabel("Mã hộ: " + congNo.getMaHo()); // (Thực tế nên query lấy số phòng: A-101)
        lblMaHo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMaHo.setBounds(20, 15, 300, 20);
        infoPanel.add(lblMaHo);

        JLabel lblKhoanThu = new JLabel("Khoản thu: " + congNo.getTenKhoanPhi());
        lblKhoanThu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblKhoanThu.setBounds(20, 40, 360, 20);
        infoPanel.add(lblKhoanThu);

        // Định dạng tiền
        DecimalFormat df = new DecimalFormat("#,###");
        JLabel lblSoTien = new JLabel("Phải thu: " + df.format(congNo.getSoTienPhaiDong()) + " VNĐ");
        lblSoTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSoTien.setForeground(new Color(231, 76, 60)); // Màu đỏ
        lblSoTien.setBounds(20, 70, 360, 25);
        infoPanel.add(lblSoTien);

        mainPanel.add(infoPanel);

        // --- PHẦN NHẬP LIỆU ---

        // 1. Người nộp
        addLabel(mainPanel, "Họ và tên người nộp *", 190);
        txtNguoiNop = addTextField(mainPanel, 215);

        // 2. Hình thức & Số tiền thực thu
        JLabel lblHinhThuc = new JLabel("Hình thức *");
        lblHinhThuc.setBounds(40, 270, 150, 20);
        lblHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblHinhThuc);

        cbHinhThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        cbHinhThuc.setBounds(40, 295, 190, 40);
        cbHinhThuc.setBackground(Color.WHITE);
        mainPanel.add(cbHinhThuc);

        JLabel lblThucThu = new JLabel("Số tiền thực thu (VNĐ) *");
        lblThucThu.setBounds(250, 270, 180, 20);
        lblThucThu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblThucThu);

        txtSoTien = new JTextField();
        txtSoTien.setBounds(250, 295, 190, 40);
        txtSoTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSoTien.setBorder(new RoundedBorder(8));
        txtSoTien.setText(String.valueOf((int)congNo.getSoTienPhaiDong())); // Điền sẵn số tiền
        mainPanel.add(txtSoTien);

        // 3. Ghi chú
        addLabel(mainPanel, "Ghi chú giao dịch", 350);
        txtGhiChu = new JTextArea();
        txtGhiChu.setBounds(40, 375, 400, 50);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(txtGhiChu);

        // --- BUTTONS ---
        btnConfirm = new JButton("Xác nhận Thanh toán") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113)); // Xanh lá
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConfirm.setBounds(40, 450, 400, 45);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> handlePayment());
        mainPanel.add(btnConfirm);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setBounds(40, 505, 400, 30);
        btnCancel.setForeground(Color.GRAY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handlePayment() {
        try {
            String nguoiNop = txtNguoiNop.getText().trim();
            String soTienStr = txtSoTien.getText().trim();
            String hinhThuc = cbHinhThuc.getSelectedItem().toString();
            String ghiChu = txtGhiChu.getText().trim();

            if (nguoiNop.isEmpty() || soTienStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người nộp và số tiền!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double soTien = Double.parseDouble(soTienStr);
            if (soTien <= 0) throw new NumberFormatException();

            // Gộp ghi chú
            String fullGhiChu = hinhThuc + " - " + ghiChu;

            // Gọi Controller
            boolean success = controller.thanhToan(congNo.getMaCongNo(), soTien, nguoiNop, fullGhiChu);

            if (success) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // parentPanel.loadData(); // Refresh bảng thu phí (nếu có hàm này)
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu giao dịch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helpers UI
    private void addLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(40, y, 400, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
    }
    private JTextField addTextField(JPanel p, int y) {
        JTextField t = new JTextField();
        t.setBounds(40, y, 400, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new RoundedBorder(8));
        t.setOpaque(false);
        p.add(t);
        return t;
    }

    // Classes con (RoundedPanel, RoundedBorder) - Copy từ LoginFrame hoặc tạo file Utils dùng chung
    static class RoundedPanel extends JPanel {
        private int radius; private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) { this.radius = radius; this.bgColor = bgColor; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); }
    }
    static class RoundedBorder extends AbstractBorder {
        private int radius; public RoundedBorder(int radius) { this.radius = radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(new Color(200, 200, 200)); g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius); }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius, radius/2, radius); }
    }
}