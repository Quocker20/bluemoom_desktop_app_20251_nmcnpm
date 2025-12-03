package com.bluemoon.app.model;

import java.util.Date;

public class GiaoDich {
    private int maGiaoDich;
    private int maHo;
    private int maKhoanPhi;
    private Date ngayNop;
    private double soTien;
    private String nguoiNop;
    private String ghiChu;

    // Trường hiển thị (optional - dùng khi hiển thị lịch sử)
    private String tenKhoanPhi;

    public GiaoDich() {
    }

    // Constructor đầy đủ
    public GiaoDich(int maGiaoDich, int maHo, int maKhoanPhi, Date ngayNop, double soTien, String nguoiNop,
            String ghiChu) {
        this.maGiaoDich = maGiaoDich;
        this.maHo = maHo;
        this.maKhoanPhi = maKhoanPhi;
        this.ngayNop = ngayNop;
        this.soTien = soTien;
        this.nguoiNop = nguoiNop;
        this.ghiChu = ghiChu;
    }

    // Constructor ngắn (dùng khi tạo giao dịch mới)
    public GiaoDich(int maHo, int maKhoanPhi, double soTien, String nguoiNop, String ghiChu) {
        this.maHo = maHo;
        this.maKhoanPhi = maKhoanPhi;
        this.soTien = soTien;
        this.nguoiNop = nguoiNop;
        this.ghiChu = ghiChu;
        this.ngayNop = new Date(); // Mặc định là hiện tại
    }

    public int getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(int maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
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

    public Date getNgayNop() {
        return ngayNop;
    }

    public void setNgayNop(Date ngayNop) {
        this.ngayNop = ngayNop;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getNguoiNop() {
        return nguoiNop;
    }

    public void setNguoiNop(String nguoiNop) {
        this.nguoiNop = nguoiNop;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenKhoanPhi() {
        return tenKhoanPhi;
    }

    public void setTenKhoanPhi(String tenKhoanPhi) {
        this.tenKhoanPhi = tenKhoanPhi;
    }
}