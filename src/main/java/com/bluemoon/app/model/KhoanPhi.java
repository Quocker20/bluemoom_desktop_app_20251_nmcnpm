package com.bluemoon.app.model;

public class KhoanPhi {
    private int maKhoanPhi;
    private String tenKhoanPhi;
    private double donGia;
    private String donViTinh;
    private int loaiPhi; // 0: Bắt buộc, 1: Tự nguyện
    private int trangThai; // 0: Da dung thu, 1: Van con thu

    public KhoanPhi() {
    }

    public KhoanPhi(String tenKhoanPhi, double donGia, String donViTinh, int loaiPhi) {
        this.tenKhoanPhi = tenKhoanPhi;
        this.donGia = donGia;
        this.donViTinh = donViTinh;
        this.loaiPhi = loaiPhi;
    }

    // Getters and Setters...
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

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getLoaiPhi() {
        return loaiPhi;
    }

    public void setLoaiPhi(int loaiPhi) {
        this.loaiPhi = loaiPhi;
    }

    @Override
    public String toString() {
        return tenKhoanPhi;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

}