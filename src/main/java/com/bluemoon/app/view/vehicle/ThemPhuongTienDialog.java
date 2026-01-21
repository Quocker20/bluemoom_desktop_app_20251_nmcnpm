package com.bluemoon.app.view.vehicle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.bluemoon.app.controller.resident.HoKhauController;
import com.bluemoon.app.controller.vehicle.PhuongTienController;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Vehicle;

public class ThemPhuongTienDialog extends JDialog {

    private JComboBox<Household> cbHoKhau;
    private JComboBox<String> cbLoaiXe;
    private JTextField txtBienSo;
    
    private JButton btnSave, btnCancel;
    private JLabel lblTitle;
    
    private final PhuongTienController ptController;
    private final HoKhauController hkController;
    private final GuiXePanel parentPanel;

    // [EDIT MODE] Biến cờ và dữ liệu cũ
    private boolean isEditMode = false;
    private Vehicle currentPhuongTien = null;

    private final Color COL_PRIMARY = new Color(52, 152, 219);
    private final Color COL_SECONDARY = new Color(149, 165, 166);

    public ThemPhuongTienDialog(JFrame parentFrame, GuiXePanel parentPanel) {
        super(parentFrame, "Thêm Phương Tiện Mới", true);
        this.parentPanel = parentPanel;
        this.ptController = new PhuongTienController();
        this.hkController = new HoKhauController();

        setSize(500, 450);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setResizable(false);

        initComponents();
        loadDanhSachHoDan();
    }

    private void initComponents() {
        lblTitle = new JLabel("ĐĂNG KÝ GỬI XE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel, BorderLayout.CENTER);

        int xLabel = 40;
        int xField = 40;
        int width = 400;
        int y = 20;
        int gap = 80;

        addLabel(panel, "Chọn Căn hộ / Chủ xe *", xLabel, y);
        cbHoKhau = new JComboBox<>();
        cbHoKhau.setBounds(xField, y + 30, width, 40);
        cbHoKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbHoKhau.setBackground(Color.WHITE);
        
        cbHoKhau.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Household) {
                    Household hk = (Household) value;
                    setText("Phòng " + hk.getRoomNumber() + " - " + hk.getOwnerName());
                }
                return this;
            }
        });
        panel.add(cbHoKhau);

        y += gap;

        int wHalf = 190;
        addLabel(panel, "Loại xe *", xLabel, y);
        cbLoaiXe = new JComboBox<>(new String[] { "Ô tô", "Xe máy / Xe đạp" });
        cbLoaiXe.setBounds(xField, y + 30, wHalf, 40);
        cbLoaiXe.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLoaiXe.setBackground(Color.WHITE);
        panel.add(cbLoaiXe);

        addLabel(panel, "Biển số xe *", xLabel + 210, y);
        txtBienSo = new JTextField();
        txtBienSo.setBounds(xField + 210, y + 30, wHalf, 40);
        txtBienSo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtBienSo);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        btnSave = new ColoredButton("Lưu Dữ Liệu", COL_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new java.awt.Dimension(150, 40));
        
        btnCancel = new ColoredButton("Hủy Bỏ", COL_SECONDARY);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new java.awt.Dimension(120, 40));

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadDanhSachHoDan() {
        List<Household> list = hkController.getAll();
        DefaultComboBoxModel<Household> model = new DefaultComboBoxModel<>();
        for (Household hk : list) {
            model.addElement(hk);
        }
        cbHoKhau.setModel(model);
    }

    // [NEW] Hàm thiết lập dữ liệu để sửa
    public void setEditData(Vehicle pt) {
        this.isEditMode = true;
        this.currentPhuongTien = pt;
        
        lblTitle.setText("CẬP NHẬT BIỂN SỐ XE");
        btnSave.setText("Lưu Thay Đổi");
        
        // Disable các trường không được sửa
        cbHoKhau.setEnabled(false);
        cbLoaiXe.setEnabled(false);
        
        // Fill dữ liệu cũ
        txtBienSo.setText(pt.getLicensePlate());
        cbLoaiXe.setSelectedIndex(pt.getType() == 1 ? 0 : 1);
        
        // Tìm và chọn đúng hộ khẩu trong combobox
        DefaultComboBoxModel<Household> model = (DefaultComboBoxModel<Household>) cbHoKhau.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Household hk = model.getElementAt(i);
            if (hk.getId() == pt.getHouseholdId()) {
                cbHoKhau.setSelectedIndex(i);
                break;
            }
        }
    }

    private void handleSave() {
        String bienSoMoi = txtBienSo.getText().trim();
        
        if (bienSoMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số xe!");
            return;
        }

        if (isEditMode) {
            // --- LOGIC SỬA ---
            String bienSoCu = currentPhuongTien.getLicensePlate();
            
            // Nếu biển số không đổi -> đóng luôn
            if (bienSoMoi.equalsIgnoreCase(bienSoCu)) {
                dispose();
                return;
            }
            
            // Nếu đổi biển số -> check trùng
            if (ptController.isBienSoExist(bienSoMoi)) {
                JOptionPane.showMessageDialog(this, "Biển số " + bienSoMoi + " đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Cập nhật object
            currentPhuongTien.setLicensePlate(bienSoMoi);
            
            boolean success = ptController.updatePhuongTien(currentPhuongTien);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                if (parentPanel != null) parentPanel.loadData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } else {
            // --- LOGIC THÊM MỚI (GIỮ NGUYÊN) ---
            Household selectedHk = (Household) cbHoKhau.getSelectedItem();
            if (selectedHk == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn căn hộ!");
                return;
            }
            int loaiXeIndex = cbLoaiXe.getSelectedIndex(); 
            int loaiXeValue = (loaiXeIndex == 0) ? 1 : 2;

            Vehicle pt = new Vehicle();
            pt.setHouseholdId(selectedHk.getId());
            pt.setLicensePlate(bienSoMoi);
            pt.setVehicleType(loaiXeValue);
            pt.setStatus(1);

            int result = ptController.addPhuongTien(pt);

            if (result == 1) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                if (parentPanel != null) parentPanel.loadData();
                dispose();
            } else if (result == -2) {
                JOptionPane.showMessageDialog(this, "Biển số đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 200, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(80, 80, 80));
        p.add(l);
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
            g2.setColor(getForeground());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}