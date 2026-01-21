package com.bluemoon.app.view.resident;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.bluemoon.app.controller.resident.NhanKhauController;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Resident;

public class QuanLyNhanKhauDialog extends JDialog {

    private Household hoKhau;
    private NhanKhauController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Resident> currentList;

    public QuanLyNhanKhauDialog(JFrame parent, Household hoKhau) {
        super(parent, "Quản lý Nhân khẩu - Hộ: " + hoKhau.getRoomNumber(), true);
        this.hoKhau = hoKhau;
        this.controller = new NhanKhauController();
        initComponents();
        loadData();
    }

    

    private void initComponents() {
        setSize(950, 550);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Thành viên hộ: " + hoKhau.getOwnerName());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        ColoredButton btnAdd = new ColoredButton("+ Thêm thành viên", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> {
             ThemNhanKhauDialog dialog = new ThemNhanKhauDialog((JFrame) getParent(), this, hoKhau);
             dialog.setVisible(true);
        });
        headerPanel.add(btnAdd, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"STT", "Họ tên", "Ngày sinh", "Giới tính", "Quan hệ", "CCCD"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK); 
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        ColoredButton btnBienDong = new ColoredButton("Đăng ký Biến động", new Color(155, 89, 182));
        btnBienDong.addActionListener(e -> handleBienDong());

        ColoredButton btnEdit = new ColoredButton("Sửa thông tin", new Color(243, 156, 18));
        btnEdit.addActionListener(e -> handleEdit());
        
        ColoredButton btnDelete = new ColoredButton("Xóa thành viên", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> handleDelete());
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(80, 35));
        btnClose.addActionListener(e -> dispose());

        footerPanel.add(btnBienDong);
        footerPanel.add(btnEdit);
        footerPanel.add(btnDelete);
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        currentList = controller.getNhanKhauByHoKhau(hoKhau.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        int stt = 1;
        for (Resident nk : currentList) {
            tableModel.addRow(new Object[]{
                stt++,
                nk.getFullName(),
                sdf.format(nk.getDob()),
                nk.getGender(),
                nk.getRelationship(),
                (nk.getIdentityCard() == null ? "-" : nk.getIdentityCard())
            });
        }
    }

    private void handleBienDong() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân khẩu cần đăng ký!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Resident selectedNK = currentList.get(row);
        DangKyBienDongDialog dialog = new DangKyBienDongDialog((JFrame) getParent(), selectedNK);
        dialog.setVisible(true);
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân khẩu cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Resident selectedNK = currentList.get(row);
        ThemNhanKhauDialog dialog = new ThemNhanKhauDialog((JFrame) getParent(), this, hoKhau);
        dialog.setEditData(selectedNK);
        dialog.setVisible(true);
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân khẩu cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Resident selectedNK = currentList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa thành viên: " + selectedNK.getFullName() + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteNhanKhau(selectedNK.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            setBorder(new EmptyBorder(8, 15, 8, 15));
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