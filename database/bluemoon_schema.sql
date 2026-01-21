DROP DATABASE IF EXISTS bluemoon_db;
CREATE DATABASE bluemoon_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon_db;

-- Users table for authentication and authorization
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL -- Expected: 'Manager', 'Board', 'Accountant'
);

-- Management fee configurations
CREATE TABLE fee_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_price DOUBLE DEFAULT 0,
    unit VARCHAR(20),
    type INT DEFAULT 0, -- 0: Mandatory, 1: Voluntary
    status INT DEFAULT 1 -- 1: Active, 0: Inactive
);

-- Apartment information
CREATE TABLE apartments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    area DOUBLE NOT NULL,
    status INT DEFAULT 0 -- 0: Vacant, 1: Occupied
);

-- Household information (linked to an apartment)
CREATE TABLE households (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    created_at DATE DEFAULT (CURRENT_DATE),
    is_deleted INT DEFAULT 0, -- Soft delete flag
    FOREIGN KEY (room_number) REFERENCES apartments(room_number) ON UPDATE CASCADE
);

-- Residents living in households
CREATE TABLE residents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT,
    full_name VARCHAR(100) NOT NULL,
    dob DATE,
    gender VARCHAR(10), -- 'Male', 'Female'
    identity_card VARCHAR(20),
    relationship VARCHAR(30), -- Relationship to the household owner
    is_deleted INT DEFAULT 0,
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- Temporary residence or absence records
CREATE TABLE residency_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'Temporary', 'Absence'
    start_date DATE NOT NULL,
    end_date DATE,
    reason TEXT,
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

-- Monthly invoices for households
CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    fee_type_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    amount_due DOUBLE DEFAULT 0,
    amount_paid DOUBLE DEFAULT 0,
    status INT DEFAULT 0, -- 0: Unpaid, 1: Paid, 2: Partial
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id)
);

-- Payment transactions history
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

-- Registered vehicles
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    license_plate VARCHAR(50) NOT NULL,
    vehicle_type INT NOT NULL COMMENT '1: Car, 2: Motorbike/Bicycle',
    status INT DEFAULT 1 COMMENT '1: Active, 0: Inactive',
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- Triggers for automatic status updates
DELIMITER $$

/*
 * Trigger: after_insert_household
 * Description: Automatically sets apartment status to Occupied (1) when a new household is assigned.
 */
CREATE TRIGGER after_insert_household AFTER INSERT ON households
FOR EACH ROW
BEGIN
    UPDATE apartments SET status = 1 WHERE room_number = NEW.room_number;
END$$

/*
 * Trigger: after_soft_delete_household
 * Description: Sets apartment status to Vacant (0) if the household is soft-deleted.
 */
CREATE TRIGGER after_soft_delete_household AFTER UPDATE ON households
FOR EACH ROW
BEGIN
    IF NEW.is_deleted = 1 AND OLD.is_deleted = 0 THEN
        UPDATE apartments SET status = 0 WHERE room_number = NEW.room_number;
    END IF;
END$$

DELIMITER ;