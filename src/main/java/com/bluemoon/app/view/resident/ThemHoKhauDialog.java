package com.bluemoon.app.view.resident;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.bluemoon.app.controller.resident.HoKhauController;
import com.bluemoon.app.dao.resident.CanHoDAO;
import com.bluemoon.app.model.Apartment;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Resident;
import com.bluemoon.app.util.AppConstants;

public class ThemHoKhauDialog extends JDialog {

    private JComboBox<Apartment> cbCanHo; 
    private JTextField txtTenChuHo, txtDienTich, txtSDT;
    private JTextField txtNgaySinh, txtCCCD;
    private JComboBox<String> cbGioiTinh;

    private JButton btnSave, btnCancel;
    private HoKhauController controller;
    private CanHoDAO canHoDAO;

    private final Color COL_PRIMARY = new Color(52, 152, 219);
    private final Color COL_SECONDARY = new Color(149, 165, 166);

    public ThemHoKhauDialog(JFrame parent) {
        super(parent, "Thêm Hộ Khẩu Mới", true);
        this.controller = new HoKhauController();
        this.canHoDAO = new CanHoDAO(); 
        
        setSize(550, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("NHẬP THÔNG TIN HỘ KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel, BorderLayout.CENTER);

        int y = 20;
        int gap = 70;

        // 1. Chọn Căn Hộ
        addLabel(panel, "Chọn Căn Hộ (Phòng trống):", 40, y);
        cbCanHo = new JComboBox<>();
        cbCanHo.setBounds(40, y + 25, 200, 35);
        cbCanHo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbCanHo.setBackground(Color.WHITE);
        panel.add(cbCanHo);

        cbCanHo.addActionListener(e -> {
            Apartment selected = (Apartment) cbCanHo.getSelectedItem();
            if (selected != null) {
                txtDienTich.setText(String.valueOf(selected.getArea()));
            } else {
                txtDienTich.setText("");
            }
        });

        // 2. Diện tích (Read-only)
        addLabel(panel, "Diện Tích (m2):", 280, y);
        txtDienTich = addTextField(panel, 280, y + 25, 200);
        txtDienTich.setEditable(false);
        txtDienTich.setBackground(new Color(245, 245, 245));

        y += gap;

        // 3. Tên Chủ Hộ
        addLabel(panel, "Tên Chủ Hộ:", 40, y);
        txtTenChuHo = addTextField(panel, 40, y + 25, 440);

        y += gap;

        // 4. SĐT & CCCD
        addLabel(panel, "Số Điện Thoại:", 40, y);
        txtSDT = addTextField(panel, 40, y + 25, 200);

        addLabel(panel, "Số CCCD/CMND:", 280, y);
        txtCCCD = addTextField(panel, 280, y + 25, 200);

        y += gap;

        // 5. Ngày Sinh & Giới Tính
        addLabel(panel, "Ngày Sinh (dd/MM/yyyy):", 40, y);
        txtNgaySinh = addTextField(panel, 40, y + 25, 200);

        addLabel(panel, "Giới Tính:", 280, y);
        cbGioiTinh = new JComboBox<>(new String[] { AppConstants.GIOI_TINH_NAM, AppConstants.GIOI_TINH_NU, AppConstants.GIOI_TINH_KHAC });
        cbGioiTinh.setBounds(280, y + 25, 200, 35);
        cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbGioiTinh.setBackground(Color.WHITE);
        panel.add(cbGioiTinh);

        loadDanhSachPhongTrong();

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        btnSave = new ColoredButton("Lưu Dữ Liệu", COL_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        
        btnCancel = new ColoredButton("Hủy Bỏ", COL_SECONDARY);
        btnCancel.setForeground(Color.WHITE);

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> btnSaveActionPerformed());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadDanhSachPhongTrong() {
        try {
            List<Apartment> list = canHoDAO.getDanhSachPhongTrong();
            DefaultComboBoxModel<Apartment> model = new DefaultComboBoxModel<>();
            for (Apartment ch : list) {
                model.addElement(ch);
            }
            cbCanHo.setModel(model);
            
            if (model.getSize() > 0) {
                cbCanHo.setSelectedIndex(0);
            } else {
                txtDienTich.setText("");
                JOptionPane.showMessageDialog(this, "Không còn căn hộ trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                btnSave.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách phòng: " + e.getMessage());
        }
    }

    private void btnSaveActionPerformed() {
        Apartment selectedRoom = (Apartment) cbCanHo.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn căn hộ!");
            return;
        }

        if (txtTenChuHo.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên chủ hộ và CCCD!");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date ngaySinh = sdf.parse(txtNgaySinh.getText().trim());

            // Tạo Hộ khẩu (Không truyền diện tích nữa)
            Household hk = new Household(
                selectedRoom.getRoomNumber(), 
                txtTenChuHo.getText().trim(), 
                txtSDT.getText().trim()
            );

            // Tạo Chủ hộ
            Resident chuHo = new Resident();
            chuHo.setFullName(txtTenChuHo.getText().trim());
            chuHo.setIdentityCard(txtCCCD.getText().trim());
            chuHo.setDob(ngaySinh);
            chuHo.setGender((String) cbGioiTinh.getSelectedItem());
            chuHo.setRelationship(AppConstants.QH_CHU_HO);

            // [SỬA LỖI]: Gọi đúng tên phương thức như bạn yêu cầu
            if (controller.addHoKhauWithChuHo(hk, chuHo)) {
                JOptionPane.showMessageDialog(this, "Thêm mới thành công!");
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Dữ liệu ngày tháng không hợp lệ (dd/MM/yyyy)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
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
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(t);
        return t;
    }

    // Class con ColoredButton để vẽ nút đẹp
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
            g2.setColor(getForeground());
            java.awt.FontMetrics fm = g2.getFontMetrics(); // Dùng java.awt.FontMetrics
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}