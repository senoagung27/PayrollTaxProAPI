# Technical Design Document (TDD)

---

## 1. Application Structure

src/main/java/com/payrolltaxpro

- config
- controller
- service
- domain
- repository
- scheduler
- batch
- security
- util

---

## 2. Payroll Engine Flow

1. Fetch employees
2. Calculate gross salary
3. Apply overtime
4. Calculate tax (progressive)
5. Calculate BPJS
6. Compute net salary
7. Save payroll record (DRAFT)
8. Approval
9. Lock month

---

## 3. Progressive Tax Algorithm

For each tax bracket:

if income > bracket.max:
taxable += (bracket.max - bracket.min) * rate
else:
taxable += (income - bracket.min) * rate
break

---

## 4. Payroll Status Lifecycle

DRAFT → APPROVED → LOCKED

LOCKED payroll cannot be edited.

---

## 5. Audit Logging

Every payroll change stored in:

payroll_audit_log

Fields:
- payroll_id
- action
- old_value
- new_value
- changed_by
- timestamp
