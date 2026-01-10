package com.bluemoon.app.model;

public class PhuongTien {
    // Các trường map với Database
    private int maPhuongTien;
    private int maHo;
    private String bienSo;
    private int loaiXe; // 1: Ô tô, 2: Xe máy/Xe đạp
    private int trangThai; // 1: Đang gửi, 0: Đã hủy/Xóa mềm

    // Các trường phụ (Dùng để hiển thị lên bảng)
    private String soCanHo;
    private String tenChuHo;

    // Constructor mặc định
    public PhuongTien() {
    }

    // Constructor đầy đủ (Dùng khi thêm mới)
    public PhuongTien(int maHo, String bienSo, int loaiXe, int trangThai) {
        this.maHo = maHo;
        this.bienSo = bienSo;
        this.loaiXe = loaiXe;
        this.trangThai = trangThai;
    }

    // Constructor đầy đủ (Dùng khi lấy từ DB ra, kèm thông tin hiển thị)
    public PhuongTien(int maPhuongTien, int maHo, String bienSo, int loaiXe, int trangThai, String soCanHo,
            String tenChuHo) {
        this.maPhuongTien = maPhuongTien;
        this.maHo = maHo;
        this.bienSo = bienSo;
        this.loaiXe = loaiXe;
        this.trangThai = trangThai;
        this.soCanHo = soCanHo;
        this.tenChuHo = tenChuHo;
    }

    // --- Getters & Setters ---

    public int getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(int maPhuongTien) {
        this.maPhuongTien = maPhuongTien;
    }

    public int getMaHo() {
        return maHo;
    }

    public void setMaHo(int maHo) {
        this.maHo = maHo;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public int getLoaiXe() {
        return loaiXe;
    }

    public void setLoaiXe(int loaiXe) {
        this.loaiXe = loaiXe;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getSoCanHo() {
        return soCanHo;
    }

    public void setSoCanHo(String soCanHo) {
        this.soCanHo = soCanHo;
    }

    public String getTenChuHo() {
        return tenChuHo;
    }

    public void setTenChuHo(String tenChuHo) {
        this.tenChuHo = tenChuHo;
    }

    public String getTenLoaiXe() {
        return (this.loaiXe == 1) ? "Ô tô" : "Xe máy/Xe đạp";
    }
}