package com.bluemoon.app.model;

public class KhoanPhi {
    private int maKhoanPhi;
    private String tenKhoanPhi;
    private double donGia;
    private String donViTinh;
    private boolean isMandatory; // true nếu là phí bắt buộc, false nếu là phí tự nguyện

    public KhoanPhi() {
    }

    public KhoanPhi(int maKhoanPhi, String tenKhoanPhi, double donGia, String donViTinh, boolean isMandatory) {
        this.maKhoanPhi = maKhoanPhi;
        this.tenKhoanPhi = tenKhoanPhi;
        this.donGia = donGia;
        this.donViTinh = donViTinh;
        this.isMandatory = isMandatory;
    }

    // Các getter/setter...
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

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    @Override
    public String toString() {
        return tenKhoanPhi;
    }
}