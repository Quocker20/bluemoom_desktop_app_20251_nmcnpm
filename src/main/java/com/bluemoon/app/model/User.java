package com.bluemoon.app.model;

public class User {
    private int maTK;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; // "QuanLy", "KeToan", "ThuKy"

    // Contructors
    public User() {
    }

    public User(int maTK, String matKhau, String tenDangNhap, String vaiTro) {
        this.maTK = maTK;
        this.matKhau = matKhau;
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
    }

    // Getters and Setters
    public int getMaTK() {
        return maTK;
    }

    public void setMaTK(int maTK) {
        this.maTK = maTK;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + tenDangNhap + ", role=" + vaiTro + '}';
    }

}
