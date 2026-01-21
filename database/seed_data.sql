USE bluemoon_db;

-- 1. Seed Apartments
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

-- 2. Seed Users
INSERT INTO users (username, password, role) VALUES 
('admin', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'Manager'),
('thuky', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'Secretary'),
('ketoan', '2830a50677ada5e2d715eab68e09c75500058060d3e77c2dd5ca095c143784c2', 'Accountant');

-- 3. Seed Households and Residents
INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (1, 'A-101', 'Nguyễn Văn An', '0912345678', '2023-01-10');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (1, 'Nguyễn Văn An', '1985-05-20', 'Male', '001085001234', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (2, 'A-102', 'Trần Minh Tuấn', '0988765432', '2023-02-15');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (2, 'Trần Minh Tuấn', '1990-08-12', 'Male', '001090005678', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (3, 'A-201', 'Lê Thị Mai', '0909123456', '2023-03-01');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (3, 'Lê Thị Mai', '1992-11-05', 'Female', '001192009876', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (4, 'A-303', 'Phạm Quốc Bảo', '0913579246', '2023-01-20');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (4, 'Phạm Quốc Bảo', '1988-02-28', 'Male', '001088004321', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (5, 'A-402', 'Nguyễn Thị Lan', '0933445566', '2023-04-10');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (5, 'Nguyễn Thị Lan', '1980-09-15', 'Female', '001180001122', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (6, 'A-501', 'Hoàng Văn Hùng', '0977889900', '2023-05-05');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (6, 'Hoàng Văn Hùng', '1975-12-30', 'Male', '001075003344', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (7, 'B-101', 'Vũ Đức Đam', '0966778899', '2023-02-20');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (7, 'Vũ Đức Đam', '1983-07-25', 'Male', '001083007788', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (8, 'B-103', 'Trần Thị Ngọc', '0911223344', '2023-06-15');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (8, 'Trần Thị Ngọc', '1995-04-04', 'Female', '001195006655', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (9, 'B-202', 'Đặng Văn Lâm', '0988112233', '2023-03-10');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (9, 'Đặng Văn Lâm', '1993-08-13', 'Male', '001093009900', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (10, 'B-301', 'Bùi Tiến Dũng', '0905123456', '2023-07-01');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (10, 'Bùi Tiến Dũng', '1997-02-28', 'Male', '001097002211', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (11, 'B-303', 'Phạm Thị Hương', '0934567890', '2023-01-05');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (11, 'Phạm Thị Hương', '1989-10-10', 'Female', '001189004455', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (12, 'B-401', 'Đỗ Hùng Dũng', '0971234567', '2023-08-20');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (12, 'Đỗ Hùng Dũng', '1994-09-08', 'Male', '001094008899', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (13, 'B-403', 'Nguyễn Quang Hải', '0969888777', '2023-09-02');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (13, 'Nguyễn Quang Hải', '1997-04-12', 'Male', '001097001133', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (14, 'B-502', 'Vũ Thị Thu', '0919998888', '2023-05-15');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (14, 'Vũ Thị Thu', '1986-06-20', 'Female', '001186005566', 'Owner');

INSERT INTO households (id, room_number, owner_name, phone_number, created_at) VALUES (15, 'A-503', 'Trương Văn Cam', '0901112223', '2023-10-25');
INSERT INTO residents (household_id, full_name, dob, gender, identity_card, relationship) VALUES (15, 'Trương Văn Cam', '1970-01-01', 'Male', '001070009988', 'Owner');