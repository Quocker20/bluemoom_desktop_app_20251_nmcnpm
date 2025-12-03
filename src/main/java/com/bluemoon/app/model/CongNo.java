package com.bluemoon.app.model;

public class CongNo {
    private int maCongNo;
    private int maHo;
    private int maKhoanPhi;
    private String tenKhoanPhi; // (Thêm để hiển thị cho tiện, lấy từ JOIN)
    private int thang;
    private int nam;
    private double soTienPhaiDong;
    private double soTienDaDong;
    boolean isDone; // true nếu đã đóng đủ, false nếu chưa đóng đủ

    public CongNo() {
    }

    // Getter/Setter...
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

    public boolean getDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }
}