

-- =============================================
-- BlueMoon Database Seed Script
-- Schema: English | Data Values: Vietnamese/Legacy Code
-- =============================================

DROP DATABASE IF EXISTS bluemoon_db;
CREATE DATABASE bluemoon_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon_db;

-- 1. Users (Giữ role mã cũ: QuanLy, ThuKy, KeToan)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 2. Apartments
CREATE TABLE apartments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    area DOUBLE NOT NULL,
    status INT DEFAULT 0 -- 0: Trong, 1: Co nguoi o
);

-- 3. Fee Types (Giữ unit mã cũ: m2, Oto, XeMay, Ho)
CREATE TABLE fee_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_price DOUBLE DEFAULT 0,
    unit VARCHAR(20), 
    type INT DEFAULT 0, -- 0: Bat buoc, 1: Tu nguyen
    status INT DEFAULT 1
);

-- 4. Households
CREATE TABLE households (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    created_at DATE DEFAULT (CURRENT_DATE),
    is_deleted INT DEFAULT 0,
    FOREIGN KEY (room_number) REFERENCES apartments(room_number) ON UPDATE CASCADE
);

-- 5. Residents (Giữ gender: Nam/Nu, relationship: ChuHo, Vo, Chong, Con...)
CREATE TABLE residents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT,
    full_name VARCHAR(100) NOT NULL,
    dob DATE,
    gender VARCHAR(10),
    identity_card VARCHAR(20),
    relationship VARCHAR(30),
    is_deleted INT DEFAULT 0,
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- 6. Residency Records (Giữ type: TamTru, TamVang, KhaiTu)
CREATE TABLE residency_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    reason TEXT,
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

-- 7. Invoices (Cong No)
CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    fee_type_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    amount_due DOUBLE DEFAULT 0,
    amount_paid DOUBLE DEFAULT 0,
    status INT DEFAULT 0,
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id)
);

-- 8. Payments (Giao Dich)
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    fee_type_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payer_name VARCHAR(100),
    note TEXT,
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id)
);

-- 9. Vehicles (Type: 1=Oto, 2=XeMay, 0=XeDap)
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    license_plate VARCHAR(50) NOT NULL,
    vehicle_type INT NOT NULL,
    status INT DEFAULT 1,
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- TRIGGERS
DELIMITER $$
CREATE TRIGGER after_insert_household AFTER INSERT ON households
FOR EACH ROW UPDATE apartments SET status = 1 WHERE room_number = NEW.room_number;
$$
CREATE TRIGGER after_soft_delete_household AFTER UPDATE ON households
FOR EACH ROW BEGIN
    IF NEW.is_deleted = 1 AND OLD.is_deleted = 0 THEN
        UPDATE apartments SET status = 0 WHERE room_number = NEW.room_number;
    END IF;
END$$
DELIMITER ;

-- =============================================
-- SEED DATA
-- =============================================

-- 1. Apartments (Căn hộ)
INSERT INTO apartments (room_number, area, status) VALUES 
('A-101', 120, 0), ('A-102', 120, 0), ('A-103', 120, 0),
('A-201', 120, 0), ('A-202', 120, 0), ('A-203', 120, 0),
('A-301', 120, 0), ('A-302', 120, 0), ('A-303', 120, 0),
('A-401', 120, 0), ('A-402', 120, 0), ('A-403', 120, 0),
('A-501', 120, 0), ('A-502', 120, 0), ('A-503', 120, 0),
('B-101', 100, 0), ('B-102', 100, 0), ('B-103', 100, 0),
('B-201', 100, 0), ('B-202', 100, 0), ('B-203', 100, 0),
('B-301', 100, 0), ('B-302', 100, 0), ('B-303', 100, 0),
('B-401', 100, 0), ('B-402', 100, 0), ('B-403', 100, 0),
('B-501', 100, 0), ('B-502', 100, 0), ('B-503', 100, 0);

-- 2. Users (Role: QuanLy, ThuKy, KeToan - Pass hash từ yêu cầu cũ)
INSERT INTO users (username, password, role) VALUES 
('admin', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'QuanLy'),
('thuky', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'ThuKy'),
('ketoan', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'KeToan');

-- 3. Fee Types (Unit: m2, Oto, XeMay, Ho)
INSERT INTO fee_types (name, unit_price, unit, type, status) VALUES 
('Phí quản lý chung cư', 7000, 'm2', 0, 1),
('Phí gửi ô tô', 1200000, 'Oto', 0, 1),
('Phí gửi xe máy', 70000, 'XeMay', 0, 1),
('Quỹ vì người nghèo', 0, 'Ho', 1, 1),
('Quỹ tổ dân phố', 0, 'Ho', 1, 1);

-- 4. Households & Residents (Thêm thành viên gia đình)

-- Hộ 1: A-101 (Gia đình chuẩn 3 người)
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-101', 'Nguyễn Văn An', '0912345678', '2023-01-10');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(1, 'Nguyễn Văn An', '1985-05-20', 'Nam', '001085001234', 'ChuHo'),
(1, 'Lê Thị Bích', '1987-08-15', 'Nu', '001087002345', 'Vo'),
(1, 'Nguyễn An Bình', '2015-02-10', 'Nam', '', 'Con');

-- Hộ 2: A-102 (Gia đình 4 người)
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-102', 'Trần Minh Tuấn', '0988765432', '2023-02-15');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(2, 'Trần Minh Tuấn', '1990-08-12', 'Nam', '001090005678', 'ChuHo'),
(2, 'Phạm Thu Hương', '1992-04-22', 'Nu', '001092006789', 'Vo'),
(2, 'Trần Minh Khôi', '2018-11-05', 'Nam', '', 'Con'),
(2, 'Trần Minh Châu', '2020-01-15', 'Nu', '', 'Con');

-- Hộ 3: A-201 (Chủ hộ Nữ)
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-201', 'Lê Thị Mai', '0909123456', '2023-03-01');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(3, 'Lê Thị Mai', '1992-11-05', 'Nu', '001192009876', 'ChuHo'),
(3, 'Hoàng Quốc Việt', '1990-09-09', 'Nam', '001090008765', 'Chong');

-- Hộ 4: A-303 (Sống cùng Bố Mẹ)
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-303', 'Phạm Quốc Bảo', '0913579246', '2023-01-20');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(4, 'Phạm Quốc Bảo', '1988-02-28', 'Nam', '001088004321', 'ChuHo'),
(4, 'Phạm Văn Hùng', '1960-05-15', 'Nam', '001060001111', 'BoMe'),
(4, 'Nguyễn Thị Cúc', '1962-10-20', 'Nu', '001062002222', 'BoMe');

-- Hộ 5: A-402 (Độc thân)
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-402', 'Nguyễn Thị Lan', '0933445566', '2023-04-10');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(5, 'Nguyễn Thị Lan', '1980-09-15', 'Nu', '001180001122', 'ChuHo');

-- Hộ 6: A-501
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-501', 'Hoàng Văn Hùng', '0977889900', '2023-05-05');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(6, 'Hoàng Văn Hùng', '1975-12-30', 'Nam', '001075003344', 'ChuHo'),
(6, 'Đỗ Thị Hạnh', '1978-03-03', 'Nu', '001078004455', 'Vo');

-- Hộ 7: B-101
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-101', 'Vũ Đức Đam', '0966778899', '2023-02-20');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(7, 'Vũ Đức Đam', '1983-07-25', 'Nam', '001083007788', 'ChuHo');

-- Hộ 8: B-103
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-103', 'Trần Thị Ngọc', '0911223344', '2023-06-15');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(8, 'Trần Thị Ngọc', '1995-04-04', 'Nu', '001195006655', 'ChuHo'),
(8, 'Lê Văn Tùng', '1994-12-12', 'Nam', '001094007766', 'Chong');

-- Hộ 9: B-202
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-202', 'Đặng Văn Lâm', '0988112233', '2023-03-10');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(9, 'Đặng Văn Lâm', '1993-08-13', 'Nam', '001093009900', 'ChuHo');

-- Hộ 10: B-301
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-301', 'Bùi Tiến Dũng', '0905123456', '2023-07-01');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(10, 'Bùi Tiến Dũng', '1997-02-28', 'Nam', '001097002211', 'ChuHo'),
(10, 'Nguyễn Khánh Linh', '1998-05-05', 'Nu', '001098003322', 'Vo');

-- Hộ 11: B-303
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-303', 'Phạm Thị Hương', '0934567890', '2023-01-05');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(11, 'Phạm Thị Hương', '1989-10-10', 'Nu', '001189004455', 'ChuHo');

-- Hộ 12: B-401
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-401', 'Đỗ Hùng Dũng', '0971234567', '2023-08-20');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(12, 'Đỗ Hùng Dũng', '1994-09-08', 'Nam', '001094008899', 'ChuHo'),
(12, 'Triệu Mộc Trinh', '1995-11-11', 'Nu', '001095001122', 'Vo'),
(12, 'Đỗ Gia Bảo', '2021-06-01', 'Nam', '', 'Con');

-- Hộ 13: B-403
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-403', 'Nguyễn Quang Hải', '0969888777', '2023-09-02');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(13, 'Nguyễn Quang Hải', '1997-04-12', 'Nam', '001097001133', 'ChuHo'),
(13, 'Chu Thanh Huyền', '1999-08-08', 'Nu', '001099002244', 'Vo');

-- Hộ 14: B-502
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('B-502', 'Vũ Thị Thu', '0919998888', '2023-05-15');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(14, 'Vũ Thị Thu', '1986-06-20', 'Nu', '001186005566', 'ChuHo');

-- Hộ 15: A-503
INSERT INTO households (room_number, owner_name, phone_number, created_at) VALUES ('A-503', 'Trương Văn Cam', '0901112223', '2023-10-25');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES 
(15, 'Trương Văn Cam', '1970-01-01', 'Nam', '001070009988', 'ChuHo');

-- 5. Vehicles (Type: 1=Oto, 2=XeMay)
INSERT INTO vehicles (household_id, license_plate, vehicle_type) VALUES 
(1, '30A-111.11', 1), (1, '29B1-123.45', 2), -- Hộ 1
(2, '29B1-678.90', 2), (2, '29B1-555.55', 2), -- Hộ 2 (2 xe máy)
(3, '30E-222.22', 1), -- Hộ 3
(4, '30F-333.33', 1), (4, '29C1-111.22', 2), -- Hộ 4
(6, '29D1-444.55', 2), -- Hộ 6
(10, '30H-999.88', 1), -- Hộ 10
(12, '30K-777.66', 1), (12, '29E1-888.99', 2); -- Hộ 12

-- 6. Residency Records (Type: TamTru, TamVang)
INSERT INTO residency_records (resident_id, type, start_date, end_date, reason) VALUES 
(3, 'TamTru', '2024-01-01', '2024-12-31', 'Con chu ho ve o cung'), -- Con hộ 1
(17, 'TamVang', '2024-06-01', '2024-09-01', 'Di cong tac nuoc ngoai'); -- Con h