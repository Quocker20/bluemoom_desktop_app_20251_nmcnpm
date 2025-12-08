package com.bluemoon.app.model;

import java.util.Date;

public class HoKhau {
    private int maHo;
    private String soCanHo;
    private String tenChuHo;
    private double dienTich;
    private String sdt;
    private Date ngayTao;
    private int isDeleted;

    public HoKhau() {
    }

    // Constructor dùng để thêm mới (không cần maHo và ngayTao)
    public HoKhau(String soCanHo, String tenChuHo, double dienTich, String sdt) {
        this.soCanHo = soCanHo;
        this.tenChuHo = tenChuHo;
        this.dienTich = dienTich;
        this.sdt = sdt;
    }

    public int getMaHo() {
        return maHo;
    }

    public void setMaHo(int maHo) {
        this.maHo = maHo;
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

    public double getDienTich() {
        return dienTich;
    }

    public void setDienTich(double dienTich) {
        this.dienTich = dienTich;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return soCanHo + " - " + tenChuHo;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}