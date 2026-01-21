-- =============================================
-- Script tạo CSDL cho Dự án BlueMoon (Desktop)
-- Phiên bản: 2.0 (Refactored English Schema)
-- Hệ quản trị: MySQL
-- =============================================

-- 1. Tạo CSDL và Sử dụng
DROP DATABASE IF EXISTS bluemoon_db;
CREATE DATABASE bluemoon_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon_db;

-- 2. Tạo bảng TAI_KHOAN (Admin users) -> users
-- Lưu trữ thông tin đăng nhập của BQT
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaTK
    username VARCHAR(50) NOT NULL UNIQUE,       -- TenDangNhap
    password VARCHAR(255) NOT NULL,             -- MatKhau
    role VARCHAR(20) NOT NULL                   -- VaiTro (QuanLy, ThuKy, KeToan)
);

-- [MỚI] Bảng CAN_HO (Apartments)
-- Tách riêng thông tin phòng và diện tích để quản lý trạng thái tốt hơn
CREATE TABLE apartments (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaCanHo
    room_number VARCHAR(20) NOT NULL UNIQUE,    -- SoCanHo (VD: 101, 102)
    area DOUBLE NOT NULL,                       -- DienTich (m2)
    status INT DEFAULT 0                        -- TrangThai (0: Trống, 1: Có người ở)
);

-- 3. Tạo bảng HO_KHAU -> households
-- Lưu trữ thông tin hộ gia đình
CREATE TABLE households (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaHo
    room_number VARCHAR(20) NOT NULL,           -- SoCanHo (Khóa ngoại lỏng sang apartments)
    owner_name VARCHAR(100) NOT NULL,           -- TenChuHo
    phone_number VARCHAR(15),                   -- SDT
    created_at DATE DEFAULT (CURRENT_DATE),     -- NgayTao
    is_deleted INT DEFAULT 0,                   -- IsDeleted (Xóa mềm)
    FOREIGN KEY (room_number) REFERENCES apartments(room_number) ON UPDATE CASCADE
);

-- 4. Tạo bảng NHAN_KHAU -> residents
-- Lưu trữ thành viên trong gia đình
CREATE TABLE residents (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaNhanKhau
    household_id INT,                           -- MaHo
    full_name VARCHAR(100) NOT NULL,            -- HoTen
    dob DATE,                                   -- NgaySinh
    gender VARCHAR(10),                         -- GioiTinh (Nam, Nu, Khac)
    identity_card VARCHAR(20),                  -- CCCD
    relationship VARCHAR(30),                   -- QuanHe (ChuHo, Vo, Chong, Con...)
    is_deleted INT DEFAULT 0,                   -- IsDeleted
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- 5. Tạo bảng TAM_TRU_TAM_VANG -> residency_records
-- Quản lý biến động dân cư
CREATE TABLE residency_records (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaTTTV
    resident_id INT NOT NULL,                   -- MaNhanKhau
    type VARCHAR(20) NOT NULL,                  -- LoaiHinh (TamTru, TamVang, KhaiTu)
    start_date DATE NOT NULL,                   -- TuNgay
    end_date DATE,                              -- DenNgay
    reason TEXT,                                -- LyDo
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

-- 6. Tạo bảng KHOAN_PHI -> fee_types
-- Danh mục các khoản phí cần thu
CREATE TABLE fee_types (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaKhoanPhi
    name VARCHAR(100) NOT NULL,                 -- TenKhoanPhi
    unit_price DOUBLE DEFAULT 0,                -- DonGia
    unit VARCHAR(50),                           -- DonVi (m2, Oto, XeMay, Ho...)
    type INT DEFAULT 0,                         -- LoaiPhi (0: Bắt buộc, 1: Tự nguyện)
    status INT DEFAULT 1                        -- TrangThai (1: Còn thu, 0: Ngừng)
);

-- 7. Tạo bảng CONG_NO -> invoices
-- Lưu trữ công nợ của từng hộ cho từng loại phí (Hóa đơn hàng tháng)
CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaCongNo
    household_id INT NOT NULL,                  -- MaHo
    fee_type_id INT NOT NULL,                   -- MaKhoanPhi
    month INT NOT NULL,                         -- Thang
    year INT NOT NULL,                          -- Nam
    amount_due DOUBLE DEFAULT 0,                -- SoTienPhaiDong
    amount_paid DOUBLE DEFAULT 0,               -- SoTienDaDong
    status INT DEFAULT 0,                       -- TrangThai (0: Chưa xong, 1: Đã xong)
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id)
);

-- 8. Tạo bảng GIAO_DICH_NOP_TIEN -> payments
-- Lưu vết chi tiết từng lần nộp tiền
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaGiaoDich
    household_id INT NOT NULL,                  -- MaHo
    fee_type_id INT NOT NULL,                   -- MaKhoanPhi
    amount DOUBLE NOT NULL,                     -- SoTien
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- NgayNop
    payer_name VARCHAR(100),                    -- NguoiNop
    note TEXT,                                  -- GhiChu
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id)
);

-- 9. Tạo bảng PHUONG_TIEN -> vehicles
-- Quản lý xe của cư dân
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- MaPhuongTien
    household_id INT NOT NULL,                  -- MaHo
    license_plate VARCHAR(50) NOT NULL,         -- BienSo
    vehicle_type INT NOT NULL COMMENT '1: Oto, 2: XeMay, 0: XeDap', -- LoaiXe (Giữ int để xử lý logic)
    status INT DEFAULT 1,                       -- TrangThai
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- =============================================
-- TRIGGERS (Tự động cập nhật trạng thái)
-- =============================================
DELIMITER $$

-- Trigger: Khi thêm hộ khẩu -> Cập nhật trạng thái phòng thành "Có người ở"
CREATE TRIGGER after_insert_household AFTER INSERT ON households
FOR EACH ROW
BEGIN
    UPDATE apartments SET status = 1 WHERE room_number = NEW.room_number;
END$$

-- Trigger: Khi xóa mềm hộ khẩu -> Cập nhật trạng thái phòng thành "Trống"
CREATE TRIGGER after_soft_delete_household AFTER UPDATE ON households
FOR EACH ROW
BEGIN
    IF NEW.is_deleted = 1 AND OLD.is_deleted = 0 THEN
        UPDATE apartments SET status = 0 WHERE room_number = NEW.room_number;
    END IF;
END$$

DELIMITER ;

