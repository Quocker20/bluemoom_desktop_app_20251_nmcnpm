-- =============================================
-- Script tạo CSDL cho Dự án BlueMoon (Desktop)
-- Phiên bản: 1.0
-- Hệ quản trị: MySQL
-- =============================================

-- 1. Tạo CSDL và Sử dụng
DROP DATABASE IF EXISTS bluemoon_db;
CREATE DATABASE bluemoon_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon_db;

-- 2. Tạo bảng TAI_KHOAN (Admin users)
-- Lưu trữ thông tin đăng nhập của BQT
CREATE TABLE TAI_KHOAN (
    MaTK INT AUTO_INCREMENT PRIMARY KEY,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL, -- Mật khẩu (nên được mã hóa MD5/SHA trong code Java)
    VaiTro VARCHAR(20) NOT NULL -- 'KeToan', 'ThuKy', 'QuanLy'
);

-- 3. Tạo bảng HO_KHAU
-- Lưu trữ thông tin hộ gia đình
CREATE TABLE HO_KHAU (
    MaHo INT AUTO_INCREMENT PRIMARY KEY,
    SoCanHo VARCHAR(20) NOT NULL UNIQUE, -- VD: A-101, B-205
    TenChuHo VARCHAR(100) NOT NULL,
    DienTich DOUBLE NOT NULL, -- Diện tích (m2) để tính phí dịch vụ
    SDT VARCHAR(15),
    NgayTao DATE DEFAULT (CURRENT_DATE)
);

-- 4. Tạo bảng NHAN_KHAU
-- Lưu trữ thông tin thành viên trong hộ
CREATE TABLE NHAN_KHAU (
    MaNhanKhau INT AUTO_INCREMENT PRIMARY KEY,
    MaHo INT NOT NULL,
    HoTen VARCHAR(100) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh VARCHAR(10) NOT NULL, -- 'Nam', 'Nu', 'Khac'
    CCCD VARCHAR(20) UNIQUE, -- Có thể NULL (nếu là trẻ em chưa có CCCD)
    QuanHe VARCHAR(50) NOT NULL, -- Quan hệ với chủ hộ (Vo, Con...)
    FOREIGN KEY (MaHo) REFERENCES HO_KHAU(MaHo) ON DELETE CASCADE
);

-- 5. Tạo bảng TAM_TRU_TAM_VANG
-- Lưu trữ lịch sử biến động cư trú
CREATE TABLE TAM_TRU_TAM_VANG (
    MaTTTV INT AUTO_INCREMENT PRIMARY KEY,
    MaNhanKhau INT NOT NULL,
    LoaiHinh VARCHAR(20) NOT NULL, -- 'TamTru', 'TamVang'
    TuNgay DATE NOT NULL,
    DenNgay DATE,
    LyDo VARCHAR(255),
    FOREIGN KEY (MaNhanKhau) REFERENCES NHAN_KHAU(MaNhanKhau) ON DELETE CASCADE
);

-- 6. Tạo bảng KHOAN_PHI (Danh mục phí)
-- Định nghĩa các loại phí và đơn giá
CREATE TABLE KHOAN_PHI (
    MaKhoanPhi INT AUTO_INCREMENT PRIMARY KEY,
    TenKhoanPhi VARCHAR(100) NOT NULL,
    DonGia DOUBLE DEFAULT 0, -- 0 nếu là khoản thu tự nguyện
    DonViTinh VARCHAR(20), -- 'm2', 'ho', 'nguoi'
    LoaiPhi TINYINT NOT NULL DEFAULT 0 -- 0: Bắt buộc, 1: Tự nguyện
);

-- 7. Tạo bảng CONG_NO (Hóa đơn hàng tháng)
-- Lưu trữ công nợ của từng hộ cho từng loại phí
CREATE TABLE CONG_NO (
    MaCongNo INT AUTO_INCREMENT PRIMARY KEY,
    MaHo INT NOT NULL,
    MaKhoanPhi INT NOT NULL,
    Thang INT NOT NULL,
    Nam INT NOT NULL,
    SoTienPhaiDong DOUBLE NOT NULL, -- Tính toán từ Diện tích * Đơn giá (nếu là phí bắt buộc)
    SoTienDaDong DOUBLE DEFAULT 0, -- Cập nhật khi có giao dịch nộp tiền
    TrangThai TINYINT DEFAULT 0, -- 0: Chưa xong, 1: Đã xong
    FOREIGN KEY (MaHo) REFERENCES HO_KHAU(MaHo),
    FOREIGN KEY (MaKhoanPhi) REFERENCES KHOAN_PHI(MaKhoanPhi)
);

-- 8. Tạo bảng GIAO_DICH_NOP_TIEN (Lịch sử nộp)
-- Lưu vết chi tiết từng lần nộp tiền
CREATE TABLE GIAO_DICH_NOP_TIEN (
    MaGiaoDich INT AUTO_INCREMENT PRIMARY KEY,
    MaHo INT NOT NULL,
    MaKhoanPhi INT NOT NULL,
    NgayNop TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    SoTien DOUBLE NOT NULL,
    NguoiNop VARCHAR(100),
    GhiChu VARCHAR(255),
    FOREIGN KEY (MaHo) REFERENCES HO_KHAU(MaHo),
    FOREIGN KEY (MaKhoanPhi) REFERENCES KHOAN_PHI(MaKhoanPhi)
);

-- =============================================
-- DỮ LIỆU MẪU (SEED DATA) - Để test ứng dụng
-- =============================================

-- 1. Tài khoản Admin (Mật khẩu giả định: 123456)
INSERT INTO TAI_KHOAN (TenDangNhap, MatKhau, VaiTro) VALUES 
('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'QuanLy'),
('ketoan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'KeToan'),
('thuky', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ThuKy');

-- 2. Cấu hình Phí cơ bản
INSERT INTO KHOAN_PHI (TenKhoanPhi, DonGia, DonViTinh, LoaiPhi) VALUES 
('Phí Dịch vụ Chung cư', 7000, 'm2', 0), -- Bắt buộc, 7k/m2
('Phí Quản lý', 3000, 'm2', 0),          -- Bắt buộc, 3k/m2
('Quỹ Vì người nghèo', 0, 'ho', 1);      -- Tự nguyện

-- 3. Hộ khẩu mẫu
INSERT INTO HO_KHAU (SoCanHo, TenChuHo, DienTich, SDT) VALUES 
('A-101', 'Nguyen Van An', 100.0, '0987654321'),
('B-202', 'Tran Thi Binh', 80.5, '0912345678'),
('C-303', 'Le Van Cuong', 120.0, '0909090909');

-- 4. Nhân khẩu mẫu
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES 
-- Hộ A-101
(1, 'Nguyen Van An', '1980-01-01', 'Nam', '001080000001', 'ChuHo'),
(1, 'Pham Thi Dung', '1982-05-05', 'Nu', '001082000002', 'Vo'),
(1, 'Nguyen Van Em', '2010-10-10', 'Nam', NULL, 'Con'), -- Trẻ em không có CCCD
-- Hộ B-202
(2, 'Tran Thi Binh', '1990-10-10', 'Nu', '001090000003', 'ChuHo');

-- 5. Công nợ mẫu (Giả sử đã chạy tính phí tháng 11/2025)
-- Hộ A (100m2): Phí DV (7000) = 700k, Phí QL (3000) = 300k
INSERT INTO CONG_NO (MaHo, MaKhoanPhi, Thang, Nam, SoTienPhaiDong, SoTienDaDong, TrangThai) VALUES 
(1, 1, 11, 2025, 700000, 0, 0), -- Chưa đóng
(1, 2, 11, 2025, 300000, 300000, 1); -- Đã đóng đủ