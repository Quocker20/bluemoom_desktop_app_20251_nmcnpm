package com.bluemoon.app.model;

import java.util.Date;

public class TamTruTamVang {
    private int maTTTV;
    private int maNhanKhau;
    private String loaiHinh; // 'TamTru', 'TamVang', 'KhaiTu'
    private Date tuNgay;
    private Date denNgay;
    private String lyDo;

    // Trường hiển thị (DTO)
    private String hoTenNhanKhau;

    public TamTruTamVang() {
    }

    public TamTruTamVang(int maNhanKhau, String loaiHinh, Date tuNgay, Date denNgay, String lyDo) {
        this.maNhanKhau = maNhanKhau;
        this.loaiHinh = loaiHinh;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.lyDo = lyDo;
    }

    public int getMaTTTV() {
        return maTTTV;
    }

    public void setMaTTTV(int maTTTV) {
        this.maTTTV = maTTTV;
    }

    public int getMaNhanKhau() {
        return maNhanKhau;
    }

    public void setMaNhanKhau(int maNhanKhau) {
        this.maNhanKhau = maNhanKhau;
    }

    public String getLoaiHinh() {
        return loaiHinh;
    }

    public void setLoaiHinh(String loaiHinh) {
        this.loaiHinh = loaiHinh;
    }

    public Date getTuNgay() {
        return tuNgay;
    }

    public void setTuNgay(Date tuNgay) {
        this.tuNgay = tuNgay;
    }

    public Date getDenNgay() {
        return denNgay;
    }

    public void setDenNgay(Date denNgay) {
        this.denNgay = denNgay;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getHoTenNhanKhau() {
        return hoTenNhanKhau;
    }

    public void setHoTenNhanKhau(String hoTenNhanKhau) {
        this.hoTenNhanKhau = hoTenNhanKhau;
    }
}