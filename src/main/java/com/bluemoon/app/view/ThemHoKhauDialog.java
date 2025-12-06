package com.bluemoon.app.view;

import com.bluemoon.app.controller.HoKhauController;
import com.bluemoon.app.controller.NhanKhauController;
import com.bluemoon.app.model.HoKhau;
import com.bluemoon.app.model.NhanKhau;
import com.bluemoon.app.util.AppConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ThemHoKhauDialog extends JDialog {

    private JTextField txtMaHo, txtTenChuHo, txtDienTich, txtSDT;
    private JTextField txtNgaySinh, txtCCCD;
    private JComboBox<String> cbGioiTinh;

    private final HoKhauController hkController;
    private final NhanKhauController nkController;
    private final HoKhauPanel parentPanel;

    private boolean isEditMode = false;
    private HoKhau currentHoKhau = null;

    public ThemHoKhauDialog(JFrame parentFrame, HoKhauPanel parentPanel) {
        super(parentFrame, "Thông tin Hộ khẩu", true);
        this.parentPanel = parentPanel;
        this.hkController = new HoKhauController();
        this.nkController = new NhanKhauController();
        initComponents();
    }

    private void initComponents() {
        setSize(600, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("THÔNG TIN HỘ KHẨU & CHỦ HỘ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 10, 580, 30);
        mainPanel.add(lblTitle);

        addLabel(mainPanel, "Số Căn hộ (Mã hộ) *", 40, 60);
        txtMaHo = addTextField(mainPanel, 40, 85, 240);

        addLabel(mainPanel, "Diện tích (m2) *", 300, 60);
        txtDienTich = addTextField(mainPanel, 300, 85, 240);

        addLabel(mainPanel, "Họ tên Chủ hộ *", 40, 140);
        txtTenChuHo = addTextField(mainPanel, 40, 165, 240);

        addLabel(mainPanel, "Số điện thoại", 300, 140);
        txtSDT = addTextField(mainPanel, 300, 165, 240);

        addLabel(mainPanel, "Ngày sinh (dd/MM/yyyy) *", 40, 220);
        txtNgaySinh = addTextField(mainPanel, 40, 245, 240);

        addLabel(mainPanel, "Giới tính *", 300, 220);
        cbGioiTinh = new JComboBox<>(new String[] {
                AppConstants.GIOI_TINH_NAM, AppConstants.GIOI_TINH_NU, AppConstants.GIOI_TINH_KHAC
        });
        cbGioiTinh.setBounds(300, 245, 240, 35);
        cbGioiTinh.setBackground(Color.WHITE);
        mainPanel.add(cbGioiTinh);

        addLabel(mainPanel, "Số CCCD/CMND *", 40, 300);
        txtCCCD = addTextField(mainPanel, 40, 325, 500);

        ColoredButton btnSave = new ColoredButton("Lưu dữ liệu", new Color(52, 152, 219));
        btnSave.setBounds(40, 420, 500, 45);
        btnSave.addActionListener(e -> handleSaveAction());
        mainPanel.add(btnSave);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setBounds(40, 480, 500, 30);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void setEditData(HoKhau hk) {
        this.isEditMode = true;
        this.currentHoKhau = hk;

        txtMaHo.setText(hk.getSoCanHo());
        txtTenChuHo.setText(hk.getTenChuHo());
        txtDienTich.setText(String.valueOf(hk.getDienTich()));
        txtSDT.setText(hk.getSdt());

        fillChuHoData(hk.getMaHo());

        txtMaHo.setEditable(false);
        txtDienTich.setEditable(false);
        txtNgaySinh.setEnabled(true);
        cbGioiTinh.setEnabled(false);
        txtCCCD.setEnabled(true);
    }

    private void fillChuHoData(int maHo) {
        List<NhanKhau> list = nkController.getNhanKhauByHoKhau(maHo);
        for (NhanKhau nk : list) {
            if (nk.getQuanHe().equalsIgnoreCase(AppConstants.QH_CHU_HO)) {
                txtCCCD.setText(nk.getCccd());
                cbGioiTinh.setSelectedItem(nk.getGioiTinh());
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    txtNgaySinh.setText(sdf.format(nk.getNgaySinh()));
                } catch (Exception e) {
                }
                break;
            }
        }
    }

    private void handleSaveAction() {
        try {
            String maHo = txtMaHo.getText().trim();
            String tenChuHo = txtTenChuHo.getText().trim();
            String sdt = txtSDT.getText().trim();
            double dienTich = Double.parseDouble(txtDienTich.getText().trim());

            if (maHo.isEmpty() || tenChuHo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã hộ và Tên chủ hộ!");
                return;
            }

            if (isEditMode) {
                currentHoKhau.setTenChuHo(tenChuHo);
                currentHoKhau.setDienTich(dienTich);
                currentHoKhau.setSdt(sdt);
                if (hkController.updateHoKhau(currentHoKhau)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    parentPanel.loadData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String ngaySinhStr = txtNgaySinh.getText().trim();
                String cccd = txtCCCD.getText().trim();
                String gioiTinh = cbGioiTinh.getSelectedItem().toString();

                if (ngaySinhStr.isEmpty() || cccd.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin Chủ hộ!");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinh = sdf.parse(ngaySinhStr);

                HoKhau hk = new HoKhau(maHo, tenChuHo, dienTich, sdt);
                NhanKhau chuHo = new NhanKhau(0, tenChuHo, ngaySinh, gioiTinh, cccd, AppConstants.QH_CHU_HO);

                if (hkController.addHoKhauWithChuHo(hk, chuHo)) {
                    JOptionPane.showMessageDialog(this, "Thêm Hộ khẩu & Chủ hộ thành công!");
                    parentPanel.loadData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại! Kiểm tra lại Mã hộ hoặc CCCD.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ (Ngày tháng hoặc Diện tích)!");
        }
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 200, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l);
    }

    private JTextField addTextField(JPanel p, int x, int y, int w) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, 35);
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