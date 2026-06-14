-- ============================================================
-- Script khởi tạo CSDL: QUANLYPHONGTRO
-- MySQL 8.0+ | Engine: InnoDB | Charset: utf8mb4
-- ============================================================

DROP DATABASE IF EXISTS QUANLYPHONGTRO;
CREATE DATABASE QUANLYPHONGTRO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE QUANLYPHONGTRO;

-- ============================================================
-- 1. BẢNG PHÒNG TRỌ (rooms)
--    Bảng gốc — nhiều bảng khác FK vào đây
-- ============================================================
CREATE TABLE rooms (
    room_id     VARCHAR(50)    PRIMARY KEY,
    status      VARCHAR(20)    NOT NULL DEFAULT 'Trống',
    price       DECIMAL(15,2)  NOT NULL,
    area        DOUBLE         DEFAULT 0,
    description VARCHAR(1000)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_rooms_status ON rooms(status);

-- ============================================================
-- 2. BẢNG KHÁCH THUÊ (tenants)
-- ============================================================
CREATE TABLE tenants (
    tenant_id  INT            AUTO_INCREMENT PRIMARY KEY,
    full_name  VARCHAR(100)   NOT NULL,
    phone      VARCHAR(100),
    cccd       VARCHAR(100),
    gender     VARCHAR(10),
    birth_date DATE,
    address    TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tenants_name ON tenants(full_name);

-- ============================================================
-- 3. BẢNG TÀI KHOẢN NGƯỜI DÙNG (users)
--    Liên kết tùy chọn với tenants (khách thuê có tài khoản)
-- ============================================================
CREATE TABLE USERS (
    id          INT            AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)    UNIQUE NOT NULL,
    password    VARCHAR(100)   NOT NULL,
    role        VARCHAR(20)    NOT NULL DEFAULT 'USER',
    full_name   VARCHAR(100),
    phone       VARCHAR(20),
    tenant_id   INT            DEFAULT NULL,

    CONSTRAINT fk_user_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(tenant_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_role      ON USERS(role);
CREATE INDEX idx_users_tenant_id ON USERS(tenant_id);

-- ============================================================
-- 4. BẢNG HỢP ĐỒNG (contracts)
--    Liên kết rooms ↔ tenants
-- ============================================================
CREATE TABLE contracts (
    contract_id INT            AUTO_INCREMENT PRIMARY KEY,
    room_id     VARCHAR(50)    NOT NULL,
    tenant_id   INT            NOT NULL,
    start_date  DATE           NOT NULL,
    end_date    DATE,
    deposit     DECIMAL(15,2)  DEFAULT 0,

    CONSTRAINT fk_contract_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_contract_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(tenant_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_contracts_room   ON contracts(room_id);
CREATE INDEX idx_contracts_tenant ON contracts(tenant_id);

-- ============================================================
-- 5. BẢNG DỊCH VỤ (services)
--    Danh mục dịch vụ (điện, nước, rác, internet...)
-- ============================================================
CREATE TABLE services (
    service_id   INT            AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100)   NOT NULL UNIQUE,
    unit_price   DECIMAL(15,2)  NOT NULL DEFAULT 0,
    unit         VARCHAR(20)    DEFAULT 'lần'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 6. BẢNG HÓA ĐƠN (invoices)
--    FK: room_id → rooms | tenant_name giữ nguyên để tương thích
-- ============================================================
CREATE TABLE invoices (
    invoice_id   INT            AUTO_INCREMENT PRIMARY KEY,
    room_id      VARCHAR(50)    NOT NULL,
    tenant_name  VARCHAR(150)   NOT NULL,
    issue_date   DATE           NOT NULL,
    due_date     DATE           NOT NULL,
    rent         DECIMAL(15,2)  NOT NULL DEFAULT 0,
    elec_usage   DOUBLE         NOT NULL DEFAULT 0,
    water_usage  DOUBLE         NOT NULL DEFAULT 0,
    other_fee    DECIMAL(15,2)  NOT NULL DEFAULT 0,
    status       VARCHAR(50)    NOT NULL DEFAULT 'Chua thanh toan',
    payment_date DATE           DEFAULT NULL,
    created_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_invoice_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_invoice_room   ON invoices(room_id);
CREATE INDEX idx_invoice_status ON invoices(status);
CREATE INDEX idx_invoice_date   ON invoices(issue_date);

-- ============================================================
-- 7. BẢNG YÊU CẦU SỬA CHỮA (maintenance_requests)
--    FK: room_id → rooms
-- ============================================================
CREATE TABLE maintenance_requests (
    id           INT            AUTO_INCREMENT PRIMARY KEY,
    room_id      VARCHAR(50)    NOT NULL,
    description  TEXT           NOT NULL,
    priority     VARCHAR(20)    NOT NULL DEFAULT 'Thấp',
    report_date  DATE           NOT NULL,
    status       VARCHAR(50)    NOT NULL DEFAULT 'Chờ xử lý',
    created_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_maint_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_maint_room   ON maintenance_requests(room_id);
CREATE INDEX idx_maint_status ON maintenance_requests(status);
CREATE INDEX idx_maint_date   ON maintenance_requests(report_date);

-- ============================================================
-- 8. BẢNG THÔNG BÁO (notifications)
--    room_id NULL = thông báo toàn hệ thống
--    room_id có giá trị = thông báo riêng cho phòng đó
-- ============================================================
CREATE TABLE notifications (
    id         INT            AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255)   NOT NULL,
    content    TEXT           NOT NULL,
    room_id    VARCHAR(50)    DEFAULT NULL,
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notif_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notif_date ON notifications(created_at DESC);
CREATE INDEX idx_notif_room ON notifications(room_id);

-- ============================================================
-- 9. BẢNG PHẢN HỒI / ĐÁNH GIÁ (feedbacks)
--    FK: room_id → rooms
-- ============================================================
CREATE TABLE feedbacks (
    id         INT            AUTO_INCREMENT PRIMARY KEY,
    room_id    VARCHAR(50)    NOT NULL,
    title      VARCHAR(255)   NOT NULL,
    content    TEXT           NOT NULL,
    rating     INT            DEFAULT 5,
    status     VARCHAR(50)    DEFAULT 'Chờ xử lý',
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_feedback_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_feedback_room   ON feedbacks(room_id);
CREATE INDEX idx_feedback_status ON feedbacks(status);

-- ============================================================
-- 10. BẢNG ĐĂNG KÝ DỊCH VỤ (service_subscriptions)
--     FK: room_id → rooms
-- ============================================================
CREATE TABLE service_subscriptions (
    id              INT            AUTO_INCREMENT PRIMARY KEY,
    room_id         VARCHAR(50)    NOT NULL,
    service_id      INT            DEFAULT NULL,
    service_name    VARCHAR(100)   NOT NULL,
    status          VARCHAR(50)    DEFAULT 'Chờ duyệt',
    registered_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_subscription_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_subscription_service
        FOREIGN KEY (service_id)
        REFERENCES services(service_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sub_room     ON service_subscriptions(room_id);
CREATE INDEX idx_sub_service  ON service_subscriptions(service_id);
CREATE INDEX idx_sub_status   ON service_subscriptions(status);

-- ============================================================
-- DỮ LIỆU MẪU
-- ============================================================

-- Tài khoản mặc định
-- admin -> 'admin123' -> SHA-256: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- user  -> 'user123'  -> SHA-256: e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446
INSERT INTO USERS (username, password, role, full_name, phone) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'Quản trị viên', '0987654321'),
('user',  'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER',  'Nhân viên quản lý', '0901234567');

-- Dịch vụ mặc định
INSERT INTO services (service_name, unit_price, unit) VALUES
('Điện',     3500,  'kWh'),
('Nước',     10000, 'm3'),
('Internet', 80000, 'tháng'),
('Rác',      20000, 'tháng');
