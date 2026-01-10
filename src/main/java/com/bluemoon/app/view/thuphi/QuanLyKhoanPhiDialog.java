package com.bluemoon.app.view.thuphi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Calendar;
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
import com.bluemoon.app.util.AppConstants;

public class QuanLyKhoanPhiDialog extends JDialog {

    private JTextField txtTenKhoan;
    private JTextField txtDonGia;
    private JComboBox<String> cbDonVi; 
    private JComboBox<String> cbLoaiPhi;
    private JButton btnSave;
    private JLabel lblTitle;

    private final ThuPhiController controller;
    private ThuPhiPanel parentThuPhi;
    private CauHinhPhiPanel parentCauHinh;

    private boolean isEditMode = false;
    private KhoanPhi currentKhoanPhi = null;


    private final String[] DON_VI_TINH = { "m2", "lần", "Phương tiện (Ô tô)", "Phương tiện (Xe đạp/Xe máy)" };

    public QuanLyKhoanPhiDialog(JFrame parentFrame, ThuPhiPanel parentPanel) {
        super(parentFrame, "Quản lý Khoản Thu", true);
        this.parentThuPhi = parentPanel;
        this.controller = new ThuPhiController();
        initComponents();
    }

    public QuanLyKhoanPhiDialog(JFrame parentFrame, CauHinhPhiPanel parentPanel) {
        super(parentFrame, "Quản lý Khoản Thu", true);
        this.parentCauHinh = parentPanel;
        this.controller = new ThuPhiController();
        initComponents();
    }

    private void initComponents() {
        setSize(550, 480);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        lblTitle = new JLabel("TẠO KHOẢN THU / CẤU HÌNH PHÍ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 10, 470, 30);
        mainPanel.add(lblTitle);

        addLabel(mainPanel, "Tên khoản thu *", 60);
        txtTenKhoan = addTextField(mainPanel, 85, 470);

        addLabel(mainPanel, "Loại phí *", 145);
        cbLoaiPhi = new JComboBox<>(new String[] { "Phí Bắt buộc", "Đóng góp Tự nguyện" });
        cbLoaiPhi.setBounds(40, 170, 190, 35);
        cbLoaiPhi.setBackground(Color.WHITE);
        cbLoaiPhi.addActionListener(e -> updateDonGiaVisibility());
        mainPanel.add(cbLoaiPhi);

        addLabel(mainPanel, "Đơn vị tính *", 145, 250);
        cbDonVi = new JComboBox<>(DON_VI_TINH);
        cbDonVi.setBounds(250, 170, 260, 35);
        cbDonVi.setBackground(Color.WHITE);
        mainPanel.add(cbDonVi);

        addLabel(mainPanel, "Đơn giá (VNĐ/ĐVT) *", 225);
        txtDonGia = addTextField(mainPanel, 250, 470);

        updateDonGiaVisibility();

        btnSave = new JButton("Lưu khoản thu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52, 152, 219));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setBounds(40, 330, 470, 45);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.addActionListener(e -> handleSave());
        mainPanel.add(btnSave);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setBounds(40, 385, 470, 30);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void updateDonGiaVisibility() {
        boolean isMandatory = cbLoaiPhi.getSelectedIndex() == 0;
        txtDonGia.setVisible(isMandatory);
        txtDonGia.setText(isMandatory ? txtDonGia.getText() : "0");
    }

    public void setEditData(KhoanPhi kp) {
        this.isEditMode = true;
        this.currentKhoanPhi = kp;
        lblTitle.setText("CẬP NHẬT KHOẢN THU");
        btnSave.setText("Lưu thay đổi");
        txtTenKhoan.setText(kp.getTenKhoanPhi());
        txtDonGia.setText(String.valueOf(kp.getDonGia()));
        cbDonVi.setSelectedItem(kp.getDonViTinh());
        cbLoaiPhi.setSelectedIndex(kp.getLoaiPhi() == AppConstants.PHI_BAT_BUOC ? 0 : 1);
        updateDonGiaVisibility();
    }

    private void handleSave() {
        try {
            String tenKhoan = txtTenKhoan.getText().trim();
            String donVi = (String) cbDonVi.getSelectedItem();
            String donGiaStr = txtDonGia.getText().trim();
            int loaiPhi = cbLoaiPhi.getSelectedIndex() == 0 ? AppConstants.PHI_BAT_BUOC : AppConstants.PHI_TU_NGUYEN;

            if (tenKhoan.isEmpty() || donVi == null || donVi.isEmpty()
                    || (loaiPhi == AppConstants.PHI_BAT_BUOC && donGiaStr.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin bắt buộc!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double donGia = loaiPhi == AppConstants.PHI_BAT_BUOC ? Double.parseDouble(donGiaStr) : 0;
            if (donGia < 0)
                throw new NumberFormatException();

            KhoanPhi kp;
            if (isEditMode) {
                kp = currentKhoanPhi;
                kp.setTenKhoanPhi(tenKhoan);
                kp.setDonGia(donGia);
                kp.setDonViTinh(donVi);
                kp.setLoaiPhi(loaiPhi);
            } else {
                kp = new KhoanPhi(tenKhoan, donGia, donVi, loaiPhi);
            }

            boolean success = isEditMode ? controller.updateKhoanPhi(kp) : controller.insertKhoanPhi(kp);

            if (success) {
                if (!isEditMode && loaiPhi == AppConstants.PHI_BAT_BUOC) {
                    List<KhoanPhi> listAll = controller.getAllKhoanPhi();
                    for (KhoanPhi dbKp : listAll) {
                        if (dbKp.getTenKhoanPhi().equals(tenKhoan)) {
                            kp.setMaKhoanPhi(dbKp.getMaKhoanPhi());
                            break;
                        }
                    }
                    Calendar cal = Calendar.getInstance();
                    if (kp.getDonViTinh().equals("m2") || kp.getDonViTinh().equals("lần")) {
                        controller.tinhPhiTuDongChoKhoanPhi(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), kp);
                    }
                    else {
                        controller.chotSoPhiGuiXe(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
                    }
                    
                }

                JOptionPane.showMessageDialog(this, "Lưu khoản thu thành công!");

                if (parentThuPhi != null)
                    parentThuPhi.loadData();
                if (parentCauHinh != null)
                    parentCauHinh.loadData("");

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!", "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabel(JPanel p, String text, int y, int xOffset) {
        JLabel l = new JLabel(text);
        l.setBounds(xOffset, y, 400, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
    }

    private void addLabel(JPanel p, String text, int y) {
        addLabel(p, text, y, 40);
    }

    private JTextField addTextField(JPanel p, int y, int width, int xOffset) {
        JTextField t = new JTextField();
        t.setBounds(xOffset, y, width, 35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new RoundedBorder(8));
        t.setOpaque(false);
        p.add(t);
        return t;
    }

    private JTextField addTextField(JPanel p, int y, int width) {
        return addTextField(p, y, width, 40);
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