package com.bluemoon.app.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class GiaoDich {
    private int maGiaoDich;
    private int maHo;
    private int maKhoanPhi;

    private Timestamp ngayNop;

    private double soTien;
    private String nguoiNop;
    private String ghiChu;

    // Trường hiển thị (DTO)
    private String tenKhoanPhi;
    private String soCanHo;

    public GiaoDich() {
    }

    // Constructor đầy đủ
    public GiaoDich(int maGiaoDich, int maHo, int maKhoanPhi, Timestamp ngayNop, double soTien, String nguoiNop,
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
        this.ngayNop = new Timestamp(System.currentTimeMillis());
    }

    // --- Getter & Setter ---

    public Timestamp getNgayNop() {
        return ngayNop;
    }

    public void setNgayNop(Timestamp ngayNop) {
        this.ngayNop = ngayNop;
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

    // Bổ sung Getter/Setter cho soCanHo (bạn đang thiếu trong code mẫu gửi lên)
    public String getSoCanHo() {
        return soCanHo;
    }

    public void setSoCanHo(String soCanHo) {
        this.soCanHo = soCanHo;
    }

    /**
     * Helper method: Trả về chuỗi ngày giờ đã format đẹp để hiển thị lên Table
     * Ví dụ output: "20/10/2025 14:30:00"
     */
    public String getNgayNopHienThi() {
        if (ngayNop == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(ngayNop);
    }
}