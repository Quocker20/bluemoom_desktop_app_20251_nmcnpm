package com.bluemoon.app.model;

import java.util.Date;

public class NhanKhau {
    private int maNhanKhau;
    private int maHo; // Khóa ngoại liên kết với HoKhau
    private String hoTen;
    private Date ngaySinh;
    private String gioiTinh; // 'Nam', 'Nu', 'Khac'
    private String cccd;     // Có thể null (trẻ em)
    private String quanHe;   // 'ChuHo', 'Vo', 'Con', ...

    // Constructor mặc định
    public NhanKhau() {
    }

    // Constructor đầy đủ (khi đọc từ DB)
    public NhanKhau(int maNhanKhau, int maHo, String hoTen, Date ngaySinh, String gioiTinh, String cccd, String quanHe) {
        this.maNhanKhau = maNhanKhau;
        this.maHo = maHo;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.quanHe = quanHe;
    }

    // Constructor ngắn gọn (khi thêm mới)
    public NhanKhau(int maHo, String hoTen, Date ngaySinh, String gioiTinh, String cccd, String quanHe) {
        this.maHo = maHo;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.quanHe = quanHe;
    }

    // --- Getters và Setters ---

    public int getMaNhanKhau() { return maNhanKhau; }
    public void setMaNhanKhau(int maNhanKhau) { this.maNhanKhau = maNhanKhau; }

    public int getMaHo() { return maHo; }
    public void setMaHo(int maHo) { this.maHo = maHo; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getQuanHe() { return quanHe; }
    public void setQuanHe(String quanHe) { this.quanHe = quanHe; }

    @Override
    public String toString() {
        return hoTen + " (" + quanHe + ")";
    }
}