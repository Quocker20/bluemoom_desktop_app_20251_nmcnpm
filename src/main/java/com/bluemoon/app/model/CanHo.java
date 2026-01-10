package com.bluemoon.app.model;

public class CanHo {
    private int maCanHo;
    private String soCanHo;
    private double dienTich;
    private int trangThai; // 0: Trống, 1: Đã ở

    public CanHo() {
    }

    public CanHo(int maCanHo, String soCanHo, double dienTich, int trangThai) {
        this.maCanHo = maCanHo;
        this.soCanHo = soCanHo;
        this.dienTich = dienTich;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public int getMaCanHo() {
        return maCanHo;
    }

    public void setMaCanHo(int maCanHo) {
        this.maCanHo = maCanHo;
    }

    public String getSoCanHo() {
        return soCanHo;
    }

    public void setSoCanHo(String soCanHo) {
        this.soCanHo = soCanHo;
    }

    public double getDienTich() {
        return dienTich;
    }

    public void setDienTich(double dienTich) {
        this.dienTich = dienTich;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return soCanHo;
    }
}