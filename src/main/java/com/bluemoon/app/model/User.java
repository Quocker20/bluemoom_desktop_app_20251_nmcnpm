package com.bluemoon.app.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role; // 'Manager', 'Secretary', 'Accountant'

    public User() {
    }

    public User(int maTK, String tenDangNhap, String matKhau, String vaiTro) {
        this.id = maTK;
        this.username = tenDangNhap;
        this.password = matKhau;
        this.role = vaiTro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}