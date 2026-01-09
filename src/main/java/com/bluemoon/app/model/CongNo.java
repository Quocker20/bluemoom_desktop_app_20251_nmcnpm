package com.bluemoon.app.model;

public class CongNo {
    private int maCongNo;
    private int maHo;
    private int maKhoanPhi;
    private double soTienPhaiDong;
    private double soTienDaDong;
    private int thang;
    private int nam;
    private int trangThai; // 0: Chưa xong, 1: Đã xong

    // Các trường hiển thị (DTO)
    private String tenKhoanPhi;
    private String soCanHo; 
       
    

    public CongNo() {
    }

    // Constructor đầy đủ (Cập nhật thêm soCanHo)
    public CongNo(int maCongNo, int maHo, String soCanHo, int maKhoanPhi, String tenKhoanPhi, int thang, int nam,
            double soTienPhaiDong, double soTienDaDong, int trangThai) {
        this.maCongNo = maCongNo;
        this.maHo = maHo;
        this.soCanHo = soCanHo;
        this.maKhoanPhi = maKhoanPhi;
        this.tenKhoanPhi = tenKhoanPhi;
        this.thang = thang;
        this.nam = nam;
        this.soTienPhaiDong = soTienPhaiDong;
        this.soTienDaDong = soTienDaDong;
        this.trangThai = trangThai;
    }

    public double getSoTienConThieu() {
        return Math.max(0, soTienPhaiDong - soTienDaDong);
    }

    // Getters & Setters
    public int getMaCongNo() {
        return maCongNo;
    }

    public void setMaCongNo(int maCongNo) {
        this.maCongNo = maCongNo;
    }

    public int getMaHo() {
        return maHo;
    }

    public void setMaHo(int maHo) {
        this.maHo = maHo;
    }

    public String getSoCanHo() {
        return soCanHo;
    } // [MỚI]

    public void setSoCanHo(String soCanHo) {
        this.soCanHo = soCanHo;
    } // [MỚI]

    public int getMaKhoanPhi() {
        return maKhoanPhi;
    }

    public void setMaKhoanPhi(int maKhoanPhi) {
        this.maKhoanPhi = maKhoanPhi;
    }

    public String getTenKhoanPhi() {
        return tenKhoanPhi;
    }

    public void setTenKhoanPhi(String tenKhoanPhi) {
        this.tenKhoanPhi = tenKhoanPhi;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public double getSoTienPhaiDong() {
        return soTienPhaiDong;
    }

    public void setSoTienPhaiDong(double soTienPhaiDong) {
        this.soTienPhaiDong = soTienPhaiDong;
    }

    public double getSoTienDaDong() {
        return soTienDaDong;
    }

    public void setSoTienDaDong(double soTienDaDong) {
        this.soTienDaDong = soTienDaDong;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}