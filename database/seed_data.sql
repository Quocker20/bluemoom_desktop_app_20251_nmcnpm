USE bluemoon_db;

INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-101', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-102', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-103', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-201', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-202', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-203', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-301', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-302', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-303', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-401', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-402', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-403', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-501', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-502', 120, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('A-503', 120, 0);

INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-101', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-102', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-103', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-201', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-202', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-203', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-301', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-302', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-303', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-401', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-402', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-403', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-501', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-502', 100, 0);
INSERT INTO CAN_HO (SoCanHo, DienTich, TrangThai) VALUES ('B-503', 100, 0);

INSERT INTO TAI_KHOAN (TenDangNhap, MatKhau, VaiTro) VALUES 
('admin', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'QuanLy'),
('thuky', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'ThuKy'),
('ketoan', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'KeToan');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (1, 'A-101', 'Nguyễn Văn An', '0912345678', '2023-01-10');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (1, 'Nguyễn Văn An', '1985-05-20', 'Nam', '001085001234', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (2, 'A-102', 'Trần Minh Tuấn', '0988765432', '2023-02-15');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (2, 'Trần Minh Tuấn', '1990-08-12', 'Nam', '001090005678', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (3, 'A-201', 'Lê Thị Mai', '0909123456', '2023-03-01');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (3, 'Lê Thị Mai', '1992-11-05', 'Nữ', '001192009876', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (4, 'A-303', 'Phạm Quốc Bảo', '0913579246', '2023-01-20');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (4, 'Phạm Quốc Bảo', '1988-02-28', 'Nam', '001088004321', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (5, 'A-402', 'Nguyễn Thị Lan', '0933445566', '2023-04-10');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (5, 'Nguyễn Thị Lan', '1980-09-15', 'Nữ', '001180001122', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (6, 'A-501', 'Hoàng Văn Hùng', '0977889900', '2023-05-05');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (6, 'Hoàng Văn Hùng', '1975-12-30', 'Nam', '001075003344', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (7, 'B-101', 'Vũ Đức Đam', '0966778899', '2023-02-20');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (7, 'Vũ Đức Đam', '1983-07-25', 'Nam', '001083007788', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (8, 'B-103', 'Trần Thị Ngọc', '0911223344', '2023-06-15');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (8, 'Trần Thị Ngọc', '1995-04-04', 'Nữ', '001195006655', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (9, 'B-202', 'Đặng Văn Lâm', '0988112233', '2023-03-10');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (9, 'Đặng Văn Lâm', '1993-08-13', 'Nam', '001093009900', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (10, 'B-301', 'Bùi Tiến Dũng', '0905123456', '2023-07-01');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (10, 'Bùi Tiến Dũng', '1997-02-28', 'Nam', '001097002211', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (11, 'B-303', 'Phạm Thị Hương', '0934567890', '2023-01-05');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (11, 'Phạm Thị Hương', '1989-10-10', 'Nữ', '001189004455', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (12, 'B-401', 'Đỗ Hùng Dũng', '0971234567', '2023-08-20');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (12, 'Đỗ Hùng Dũng', '1994-09-08', 'Nam', '001094008899', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (13, 'B-403', 'Nguyễn Quang Hải', '0969888777', '2023-09-02');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (13, 'Nguyễn Quang Hải', '1997-04-12', 'Nam', '001097001133', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (14, 'B-502', 'Vũ Thị Thu', '0919998888', '2023-05-15');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (14, 'Vũ Thị Thu', '1986-06-20', 'Nữ', '001186005566', 'Chủ hộ');

INSERT INTO HO_KHAU (MaHo, SoCanHo, TenChuHo, SDT, NgayTao) VALUES (15, 'A-503', 'Trương Văn Cam', '0901112223', '2023-10-25');
INSERT INTO NHAN_KHAU (MaHo, HoTen, NgaySinh, GioiTinh, CCCD, QuanHe) VALUES (15, 'Trương Văn Cam', '1970-01-01', 'Nam', '001070009988', 'Chủ hộ');