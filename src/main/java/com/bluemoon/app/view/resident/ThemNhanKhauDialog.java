package com.bluemoon.app.view.resident;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import com.bluemoon.app.controller.resident.ResidentController;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Resident;
import com.bluemoon.app.util.AppConstants;

public class ThemNhanKhauDialog extends JDialog {

    private JTextField txtHoTen, txtNgaySinh, txtCCCD;
    private JComboBox<String> cbGioiTinh, cbQuanHe;
    private JButton btnSave;
    private JLabel lblTitle;

    private ResidentController controller;
    
    // Biến để xác định màn hình cha gọi dialog này
    private QuanLyNhanKhauDialog parentDialog; 
    private NhanKhauPanel nhanKhauPanel;
    
    private Household hoKhauHienTai;
    
    private boolean isEditMode = false;
    private Resident currentNK = null;

    // Constructor 1: Gọi từ màn hình Chi tiết hộ khẩu (QuanLyNhanKhauDialog)
    public ThemNhanKhauDialog(JFrame parentFrame, QuanLyNhanKhauDialog parentDialog, Household hoKhau) {
        super(parentFrame, "Thông tin Nhân khẩu", true);
        this.parentDialog = parentDialog;
        this.hoKhauHienTai = hoKhau;
        this.controller = new ResidentController();
        initComponents();
    }

    // Constructor 2: Gọi từ màn hình Danh sách nhân khẩu tổng (NhanKhauPanel)
    public ThemNhanKhauDialog(JFrame parentFrame, NhanKhauPanel nhanKhauPanel, Household hoKhau) {
        super(parentFrame, "Thông tin Nhân khẩu", true);
        this.nhanKhauPanel = nhanKhauPanel;
        this.hoKhauHienTai = hoKhau; // Lưu ý: Khi Sửa thì hoKhau này không được null
        this.controller = new ResidentController();
        initComponents();
    }

    private void initComponents() {
        setSize(500, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        lblTitle = new JLabel("THÊM NHÂN KHẨU MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBounds(0, 10, 480, 30);
        mainPanel.add(lblTitle);

        addLabel(mainPanel, "Họ và tên *", 60);
        txtHoTen = addTextField(mainPanel, 85);

        addLabel(mainPanel, "Ngày sinh (dd/MM/yyyy) *", 140);
        txtNgaySinh = addTextField(mainPanel, 165);

        JLabel lblGT = new JLabel("Giới tính *");
        lblGT.setBounds(40, 220, 100, 20);
        mainPanel.add(lblGT);
        
        cbGioiTinh = new JComboBox<>(new String[]{
            AppConstants.GIOI_TINH_NAM, AppConstants.GIOI_TINH_NU, AppConstants.GIOI_TINH_KHAC
        });
        cbGioiTinh.setBounds(40, 245, 190, 35);
        cbGioiTinh.setBackground(Color.WHITE);
        mainPanel.add(cbGioiTinh);

        JLabel lblQH = new JLabel("Quan hệ với chủ hộ *");
        lblQH.setBounds(250, 220, 150, 20);
        mainPanel.add(lblQH);

        cbQuanHe = new JComboBox<>(new String[]{
            AppConstants.QH_CHU_HO, AppConstants.QH_VO, AppConstants.QH_CHONG, 
            AppConstants.QH_CON, AppConstants.QH_BO_ME, AppConstants.QH_ANH_CHI_EM, AppConstants.QH_KHAC
        });
        cbQuanHe.setBounds(250, 245, 190, 35);
        cbQuanHe.setBackground(Color.WHITE);
        mainPanel.add(cbQuanHe);

        addLabel(mainPanel, "Số CCCD (Để trống nếu không có)", 300);
        txtCCCD = addTextField(mainPanel, 325);

        btnSave = new ColoredButton("Lưu thông tin", new Color(52, 152, 219));
        btnSave.setBounds(40, 400, 400, 45);
        btnSave.addActionListener(e -> handleSave());
        mainPanel.add(btnSave);
        
        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setBounds(40, 455, 400, 30);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void setEditData(Resident nk) {
        this.isEditMode = true;
        this.currentNK = nk;
        lblTitle.setText("CẬP NHẬT NHÂN KHẨU");
        btnSave.setText("Lưu thay đổi");
        txtHoTen.setText(nk.getFullName());
        txtCCCD.setText(nk.getIdentityCard());
        cbGioiTinh.setSelectedItem(nk.getGender());
        cbQuanHe.setSelectedItem(nk.getRelationship());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtNgaySinh.setText(sdf.format(nk.getDob()));
        } catch (Exception e) {}
    }

    private void handleSave() {
        try {
            String hoTen = txtHoTen.getText().trim();
            String ngaySinhStr = txtNgaySinh.getText().trim();
            String cccd = txtCCCD.getText().trim();
            String gioiTinh = cbGioiTinh.getSelectedItem().toString();
            String quanHe = cbQuanHe.getSelectedItem().toString();
            
            if (hoTen.isEmpty() || ngaySinhStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên và ngày sinh!");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false); 
            Date ngaySinh = sdf.parse(ngaySinhStr);
            if (cccd.isEmpty()) cccd = null;

            if (isEditMode) {
                currentNK.setFullName(hoTen);
                currentNK.setDob(ngaySinh);
                currentNK.setGender(gioiTinh);
                currentNK.setRelationship(quanHe);
                currentNK.setIdentityCard(cccd);
                
                if (controller.update(currentNK)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    
                    // [FIX] Cập nhật lại màn hình cha tương ứng
                    if (parentDialog != null) parentDialog.loadData();
                    if (nhanKhauPanel != null) nhanKhauPanel.loadData();
                    
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Logic Thêm mới
                if (hoKhauHienTai == null) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Chưa xác định hộ khẩu để thêm thành viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Resident nk = new Resident(hoKhauHienTai.getId(), hoTen, ngaySinh, gioiTinh, cccd, quanHe);
                if (controller.add(nk)) {
                    JOptionPane.showMessageDialog(this, "Thêm mới thành công!");
                    
                    // [FIX] Cập nhật lại màn hình cha tương ứng
                    if (parentDialog != null) parentDialog.loadData();
                    if (nhanKhauPanel != null) nhanKhauPanel.loadData();
                    
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Có thể trùng CCCD)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (dd/MM/yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

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