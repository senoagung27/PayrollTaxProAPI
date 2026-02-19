-- Create Enum Types
CREATE TYPE payroll_status AS ENUM ('DRAFT', 'APPROVED', 'LOCKED');
CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

-- Create tenant table
CREATE TABLE tenant (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    schema_name VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    tenant_id BIGINT REFERENCES tenant(id) ON DELETE SET NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_role table
CREATE TABLE user_role (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Create salary_structure table
CREATE TABLE salary_structure (
    id BIGSERIAL PRIMARY KEY,
    basic_salary DECIMAL(15,2) NOT NULL,
    allowance DECIMAL(15,2) DEFAULT 0,
    deduction DECIMAL(15,2) DEFAULT 0,
    name VARCHAR(100),
    grade VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create tax_bracket table
CREATE TABLE tax_bracket (
    id BIGSERIAL PRIMARY KEY,
    min_income DECIMAL(15,2) NOT NULL,
    max_income DECIMAL(15,2),
    percentage DECIMAL(5,4) NOT NULL,
    bracket_order INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create employee table
CREATE TABLE employee (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    npwp VARCHAR(20),
    tax_status VARCHAR(20) DEFAULT 'TK0',
    salary_structure_id BIGINT REFERENCES salary_structure(id) ON DELETE SET NULL,
    bank_name VARCHAR(50),
    bank_account VARCHAR(50),
    join_date DATE,
    bpjs_number VARCHAR(50),
    hourly_rate DECIMAL(10,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create payroll table
CREATE TABLE payroll (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    basic_salary DECIMAL(15,2) NOT NULL,
    gross_salary DECIMAL(15,2) NOT NULL,
    tax DECIMAL(15,2) DEFAULT 0,
    bpjs DECIMAL(15,2) DEFAULT 0,
    overtime DECIMAL(15,2) DEFAULT 0,
    allowance DECIMAL(15,2) DEFAULT 0,
    deduction DECIMAL(15,2) DEFAULT 0,
    net_salary DECIMAL(15,2) NOT NULL,
    overtime_hours DECIMAL(5,2) DEFAULT 0,
    taxable_income DECIMAL(15,2) DEFAULT 0,
    status payroll_status NOT NULL DEFAULT 'DRAFT',
    payment_date DATE,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(employee_id, month, year)
);

-- Create payroll_audit_log table
CREATE TABLE payroll_audit_log (
    id BIGSERIAL PRIMARY KEY,
    payroll_id BIGINT NOT NULL REFERENCES payroll(id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_by VARCHAR(100),
    changed_by_id BIGINT,
    change_details JSONB,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50)
);

-- Create approval table
CREATE TABLE approval (
    id BIGSERIAL PRIMARY KEY,
    payroll_id BIGINT NOT NULL REFERENCES payroll(id) ON DELETE CASCADE,
    approver_id BIGINT,
    approver_name VARCHAR(100),
    status approval_status NOT NULL,
    approved_at TIMESTAMP,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_employee_tenant ON employee(tenant_id);
CREATE INDEX idx_employee_code ON employee(employee_code);
CREATE INDEX idx_payroll_employee ON payroll(employee_id);
CREATE INDEX idx_payroll_month_year ON payroll(month, year);
CREATE INDEX idx_payroll_status ON payroll(status);
CREATE INDEX idx_payroll_tenant_month_year ON payroll(employee_id, month, year);

CREATE INDEX idx_audit_log_payroll ON payroll_audit_log(payroll_id);
CREATE INDEX idx_audit_log_timestamp ON payroll_audit_log(timestamp);
CREATE INDEX idx_approval_payroll ON approval(payroll_id);
CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_tax_bracket_order ON tax_bracket(bracket_order);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_tenant_updated_at BEFORE UPDATE ON tenant
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_salary_structure_updated_at BEFORE UPDATE ON salary_structure
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tax_bracket_updated_at BEFORE UPDATE ON tax_bracket
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_employee_updated_at BEFORE UPDATE ON employee
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payroll_updated_at BEFORE UPDATE ON payroll
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert initial tax brackets (Indonesia progressive tax rates)
-- These are based on Indonesia's tax regulations for 2024
INSERT INTO tax_bracket (min_income, max_income, percentage, bracket_order) VALUES
(0, 60000000, 0.05, 1),
(60000001, 250000000, 0.15, 2),
(25000000001, 500000000, 0.25, 3),
(500000001, 5000000000, 0.30, 4),
(5000000001, NULL, 0.35, 5);

-- Insert default admin user (password: admin - should be changed on first login)
-- The password is BCrypt encoded for 'admin'
INSERT INTO users (username, password, email, full_name) VALUES
('admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'admin@payrolltax.local', 'System Administrator');

INSERT INTO user_role (user_id, role) VALUES
(1, 'ROLE_ADMIN');

-- Insert default salary structures
INSERT INTO salary_structure (name, grade, basic_salary, allowance, deduction) VALUES
('Staff Level 1', 'STAFF-1', 5000000, 500000, 0),
('Staff Level 2', 'STAFF-2', 7000000, 750000, 0),
('Supervisor', 'SUP', 10000000, 1000000, 0),
('Manager', 'MGR', 15000000, 2000000, 0),
('Director', 'DIR', 25000000, 5000000, 0);
