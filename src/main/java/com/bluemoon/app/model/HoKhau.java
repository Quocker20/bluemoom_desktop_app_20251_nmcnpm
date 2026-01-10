package com.bluemoon.app.model;

import java.util.Date;

public class HoKhau {
    private int maHo;
    private String soCanHo;
    private String tenChuHo;
    private double dienTich; // Vẫn giữ để hiển thị (lấy từ bảng CAN_HO)
    private String sdt;
    private Date ngayTao;
    private int isDeleted;

    public HoKhau() {
    }

    // [QUAN TRỌNG] Constructor này đã bỏ tham số 'dienTich'
    public HoKhau(String soCanHo, String tenChuHo, String sdt) {
        this.soCanHo = soCanHo;
        this.tenChuHo = tenChuHo;
        this.sdt = sdt;
    }

    // Constructor đầy đủ (dùng khi map dữ liệu từ DB lên)
    public HoKhau(int maHo, String soCanHo, String tenChuHo, double dienTich, String sdt, Date ngayTao, int isDeleted) {
        this.maHo = maHo;
        this.soCanHo = soCanHo;
        this.tenChuHo = tenChuHo;
        this.dienTich = dienTich;
        this.sdt = sdt;
        this.ngayTao = ngayTao;
        this.isDeleted = isDeleted;
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

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}