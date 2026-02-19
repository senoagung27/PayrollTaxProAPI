#
## Product Requirement Document (PRD)

---

## 1. Product Overview

**Product Name:** PayrollTax Pro API  
**Type:** Multi-Company SaaS Payroll Engine  
**Deployment:** On-Premise / Local Production  
**Target Market:** SME, Enterprise, Fintech-grade

---

## 2. Problem Statement

Companies require:

- Accurate progressive tax calculation
- BPJS simulation
- Overtime calculation
- Payroll locking per month
- Approval workflow
- Audit log tracking
- Payslip PDF generation
- CSV export for bank processing

The system must be secure, compliant, traceable, and scalable.

---

## 3. Core Features (MVP Scope)

### Employee Management
- CRUD employee
- Tax profile binding
- Salary structure binding

### Salary Structure
- Basic salary
- Allowances
- Deductions
- Configurable components

### Tax Engine
- Progressive tax bracket
- Configurable tax rules
- Annual aggregation

### BPJS Simulation
- Employer & employee contribution
- Configurable percentage

### Overtime
- Hour-based rate calculation
- Configurable multiplier

### Payroll Processing
- Monthly payroll generation
- Draft → Approved → Locked state
- Payslip PDF generation
- CSV export

---

## 4. Advanced Features

- Multi-tenant architecture
- Payroll locking per month
- Revision & audit log
- Scheduled payroll (cron)
- Approval workflow

---

## 5. Non-Functional Requirements

- Multi-tenant isolation
- Transactional consistency
- Horizontal scalability
- Audit compliance
- Role-based access control
- High availability (local cluster ready)
