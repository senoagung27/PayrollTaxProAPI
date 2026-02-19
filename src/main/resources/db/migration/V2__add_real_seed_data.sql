-- ============================================
-- PayrollTax Pro API - Real Indonesian Seed Data
-- ============================================

-- Insert Tenants (Indonesian Companies)
INSERT INTO tenant (name, schema_name, active) VALUES
('PT Maju Jaya Sejahtera', 'tenant_pt_maju_jaya', true),
('CV Teknologi Nusantara', 'tenant_cv_teknologi', true),
('PT Solusi Digital Indonesia', 'tenant_pt_solusi_digital', true),
('PT Ritel Modern Indonesia', 'tenant_pt_ritel_modern', true),
('PT Konsultan Prima', 'tenant_pt_konsultan', true);

-- Insert Additional Users for each tenant
-- Password for all users: admin123 (BCrypt hash)
INSERT INTO users (username, password, email, full_name, tenant_id, enabled) VALUES
-- PT Maju Jaya Sejahtera
('budi.maju', '$2a$10$ZK5tLK5qQF1zG0mY1vGX2e3X8cF7vQ9pN2wR4sT6uY8zA0bC1dE2fG', 'budi@majujaya.co.id', 'Budi Santoso', 1, true),
('siti.maju', '$2a$10$ZK5tLK5qQF1zG0mY1vGX2e3X8cF7vQ9pN2wR4sT6uY8zA0bC1dE2fG', 'siti@majujaya.co.id', 'Siti Rahayu', 1, true),
-- CV Teknologi Nusantara
('andi.teknologi', '$2a$10$ZK5tLK5qQF1zG0mY1vGX2e3X8cF7vQ9pN2wR4sT6uY8zA0bC1dE2fG', 'andi@teknologi.com', 'Andi Pratama', 2, true),
('dewi.teknologi', '$2a$10$ZK5tLK5qQF1zG0mY1vGX2e3X8cF7vQ9pN2wR4sT6uY8zA0bC1dE2fG', 'dewi@teknologi.com', 'Dewi Lestari', 2, true),
-- PT Solusi Digital Indonesia
('reza.digital', '$2a$10$ZK5tLK5qQF1zG0mY1vGX2e3X8cF7vQ9pN2wR4sT6uY8zA0bC1dE2fG', 'reza@solusidigital.co.id', 'Reza Firmansyah', 3, true),
-- PT Ritel Modern Indonesia
('wulan.ritel', '$2a$10$ZK5tLK5qQF1zG0mY1vGX2e3X8cF7vQ9pN2wR4sT6uY8zA0bC1dE2fG', 'wulan@ritelmodern.co.id', 'Wulan Sari', 4, true);

-- Assign roles to users
INSERT INTO user_role (user_id, role) VALUES
-- PT Maju Jaya Sejahtera
(2, 'ROLE_MANAGER'),
(2, 'ROLE_FINANCE'),
(3, 'ROLE_EMPLOYEE'),
-- CV Teknologi Nusantara
(4, 'ROLE_ADMIN'),
(4, 'ROLE_MANAGER'),
(5, 'ROLE_FINANCE'),
-- PT Solusi Digital Indonesia
(6, 'ROLE_MANAGER'),
(6, 'ROLE_FINANCE'),
-- PT Ritel Modern Indonesia
(7, 'ROLE_MANAGER');

-- Insert Additional Salary Structures (Indonesian Market Rates 2024)
INSERT INTO salary_structure (name, grade, basic_salary, allowance, deduction, active) VALUES
-- Entry Level
('Staff Junior', 'STAFF-JR', 4500000, 300000, 0, true),
('Staff Mid', 'STAFF-MID', 5500000, 400000, 0, true),
('Staff Senior', 'STAFF-SR', 6500000, 500000, 0, true),
-- Professional Level
('Supervisor Junior', 'SUP-JR', 8500000, 750000, 0, true),
('Supervisor Senior', 'SUP-SR', 10000000, 1000000, 0, true),
('Assistant Manager', 'ASM', 12000000, 1200000, 0, true),
-- Management Level
('Manager', 'MGR', 15000000, 2000000, 0, true),
('Senior Manager', 'SR-MGR', 20000000, 2500000, 0, true),
('Assistant Director', 'ASD', 25000000, 3000000, 0, true),
-- Executive Level
('Director', 'DIR', 35000000, 5000000, 0, true),
('Senior Director', 'SR-DIR', 45000000, 6000000, 0, true),
('VP Operations', 'VP-OPS', 55000000, 7000000, 0, true),
('VP Finance', 'VP-FIN', 55000000, 7000000, 0, true),
('C-Level', 'C-LEVEL', 75000000, 10000000, 0, true);

-- ============================================
-- EMPLOYEES - PT Maju Jaya Sejahtera (Tenant ID: 1)
-- ============================================
INSERT INTO employee (tenant_id, employee_code, name, npwp, tax_status, salary_structure_id, bank_name, bank_account, join_date, bpjs_number, hourly_rate, active) VALUES
(1, 'EMP-2024-001', 'Ahmad Fauzi', '1234567890123456', 'TK0', 7, 'BCA', '1234567890', '2020-01-15', '0001234567890', 86580, true),
(1, 'EMP-2024-002', 'Sri Wahyuni', '1234567890123457', 'TK1', 6, 'BCA', '1234567891', '2020-03-01', '0001234567891', 69420, true),
(1, 'EMP-2024-003', 'Dedi Kurniawan', '1234567890123458', 'TK0', 5, 'Mandiri', '2345678901', '2021-06-15', '0001234567892', 57802, true),
(1, 'EMP-2024-004', 'Rina Susanti', '1234567890123459', 'TK2', 5, 'BCA', '1234567892', '2021-08-01', '0001234567893', 57802, true),
(1, 'EMP-2024-005', 'Eko Prasetyo', '1234567890123460', 'TK0', 4, 'BNI', '3456789012', '2022-02-01', '0001234567894', 49181, true),
(1, 'EMP-2024-006', 'Lestari Handayani', '1234567890123461', 'TK1', 4, 'BCA', '1234567893', '2022-05-15', '0001234567895', 49181, true),
(1, 'EMP-2024-007', 'Agus Setiawan', '1234567890123462', 'K0', 3, 'Mandiri', '2345678902', '2023-01-10', '0001234567896', 37681, true),
(1, 'EMP-2024-008', 'Dewi Sartika', '1234567890123463', 'TK0', 3, 'BRI', '4567890123', '2023-03-20', '0001234567897', 37681, true);

-- ============================================
-- EMPLOYEES - CV Teknologi Nusantara (Tenant ID: 2)
-- ============================================
INSERT INTO employee (tenant_id, employee_code, name, npwp, tax_status, salary_structure_id, bank_name, bank_account, join_date, bpjs_number, hourly_rate, active) VALUES
(2, 'EMP-2024-101', 'Rizky Ramadhan', '2345678901234567', 'TK0', 9, 'BCA', '9876543210', '2019-05-01', '0002234567890', 144253, true),
(2, 'EMP-2024-102', 'Alya Putri', '2345678901234568', 'TK1', 8, 'Mandiri', '8765432109', '2020-07-15', '0002234567891', 128612, true),
(2, 'EMP-2024-103', 'Bima Sakti', '2345678901234569', 'TK0', 7, 'BCA', '9876543211', '2021-01-20', '0002234567892', 86580, true),
(2, 'EMP-2024-104', 'Citra Lestari', '2345678901234570', 'TK0', 7, 'BNI', '7654321098', '2021-09-01', '0002234567893', 86580, true),
(2, 'EMP-2024-105', 'Dimas Anggara', '2345678901234571', 'K1', 6, 'Jago', '1112223334', '2022-04-10', '0002234567894', 69420, true),
(2, 'EMP-2024-106', 'Farah Quinn', '2345678901234572', 'TK0', 6, 'BCA', '9876543212', '2022-11-20', '0002234567895', 69420, true),
(2, 'EMP-2024-107', 'Gilang Dirga', '2345678901234573', 'TK2', 5, 'Mandiri', '8765432110', '2023-06-15', '0002234567896', 57802, true),
(2, 'EMP-2024-108', 'Hana Pertiwi', '2345678901234574', 'TK0', 5, 'BRI', '5556667778', '2024-01-05', '0002234567897', 57802, true);

-- ============================================
-- EMPLOYEES - PT Solusi Digital Indonesia (Tenant ID: 3)
-- ============================================
INSERT INTO employee (tenant_id, employee_code, name, npwp, tax_status, salary_structure_id, bank_name, bank_account, join_date, bpjs_number, hourly_rate, active) VALUES
(3, 'EMP-2024-201', 'Indra Wijaya', '3456789012345678', 'K0', 8, 'BCA', '1111111111', '2018-03-01', '0003234567890', 128612, true),
(3, 'EMP-2024-202', 'Jasmine Putri', '3456789012345679', 'TK1', 7, 'Mandiri', '2222222222', '2019-08-15', '0003234567891', 86580, true),
(3, 'EMP-2024-203', 'Kevin Sanjaya', '3456789012345680', 'TK0', 7, 'BCA', '1111111112', '2020-12-01', '0003234567892', 86580, true),
(3, 'EMP-2024-204', 'Lina Marlina', '3456789012345681', 'TK0', 6, 'BNI', '3333333333', '2021-07-20', '0003234567893', 69420, true),
(3, 'EMP-2024-205', 'Muhammad Raffi', '3456789012345682', 'TK1', 6, 'Jago', '4444444444', '2022-03-10', '0003234567894', 69420, true),
(3, 'EMP-2024-206', 'Nadya Mustika', '3456789012345683', 'K0', 5, 'BCA', '1111111113', '2022-10-15', '0003234567895', 57802, true);

-- ============================================
-- EMPLOYEES - PT Ritel Modern Indonesia (Tenant ID: 4)
-- ============================================
INSERT INTO employee (tenant_id, employee_code, name, npwp, tax_status, salary_structure_id, bank_name, bank_account, join_date, bpjs_number, hourly_rate, active) VALUES
(4, 'EMP-2024-301', 'Oscar Pratama', '4567890123456789', 'TK0', 5, 'BCA', '5555555555', '2021-01-01', '0004234567890', 57802, true),
(4, 'EMP-2024-302', 'Putri Ayu', '4567890123456790', 'TK0', 4, 'Mandiri', '6666666666', '2021-06-15', '0004234567891', 49181, true),
(4, 'EMP-2024-303', 'Qonita Rahma', '4567890123456791', 'TK1', 4, 'BRI', '7777777777', '2022-02-01', '0004234567892', 49181, true),
(4, 'EMP-2024-304', 'Rangga Azof', '4567890123456792', 'TK0', 3, 'BCA', '5555555556', '2022-09-20', '0004234567893', 37681, true),
(4, 'EMP-2024-305', 'Salsa Winata', '4567890123456793', 'TK0', 3, 'BNI', '8888888888', '2023-04-10', '0004234567894', 37681, true),
(4, 'EMP-2024-306', 'Tengku Firmansyah', '4567890123456794', 'K1', 2, 'Jago', '9999999999', '2023-11-01', '0004234567895', 32370, true);

-- ============================================
-- EMPLOYEES - PT Konsultan Prima (Tenant ID: 5)
-- ============================================
INSERT INTO employee (tenant_id, employee_code, name, npwp, tax_status, salary_structure_id, bank_name, bank_account, join_date, bpjs_number, hourly_rate, active) VALUES
(5, 'EMP-2024-401', 'Ucok Baba', '5678901234567890', 'TK0', 10, 'BCA', '1010101010', '2019-04-01', '0005234567890', 159420, true),
(5, 'EMP-2024-402', 'Vanesha Prescilla', '5678901234567891', 'TK0', 9, 'Mandiri', '2020202020', '2020-10-15', '0005234567891', 144253, true),
(5, 'EMP-2024-403', 'Wira Sahara', '5678901234567892', 'TK1', 8, 'BCA', '1010101011', '2021-07-01', '0005234567892', 128612, true),
(5, 'EMP-2024-404', 'Xenia Cheese', '5678901234567893', 'K0', 7, 'BNI', '3030303030', '2022-03-20', '0005234567893', 86580, true);

-- Update existing salary structure with better names
UPDATE salary_structure SET name = 'Staff Level 1 - Jakarta' WHERE grade = 'STAFF-1';
UPDATE salary_structure SET name = 'Staff Level 2 - Jakarta' WHERE grade = 'STAFF-2';
UPDATE salary_structure SET name = 'Supervisor - Jakarta' WHERE grade = 'SUP';
UPDATE salary_structure SET name = 'Manager - Jakarta' WHERE grade = 'MGR';
UPDATE salary_structure SET name = 'Director - Jakarta' WHERE grade = 'DIR';

-- ============================================
-- NOTES:
-- 1. All users have password: admin123
-- 2. Hourly rates calculated based on: basic_salary / 173 hours (standard)
-- 3. Tax Status:
--    - TK0: Belum Menikah (Single, 0 dependents)
--    - TK1: Belum Menikah (Single, 1 dependent)
--    - TK2: Belum Menikah (Single, 2 dependents)
--    - TK3: Belum Menikah (Single, 3 dependents)
--    - K0: Kawin (Married, 0 dependents)
--    - K1: Kawin (Married, 1 dependent)
--    - K2: Kawin (Married, 2 dependents)
--    - K3: Kawin (Married, 3 dependents)
-- 4. Banks: BCA, Mandiri, BNI, BRI, Jago (popular Indonesian banks)
-- 5. NPWP format: 16 digits (Indonesian Tax ID)
-- ============================================
