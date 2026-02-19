# Database Design (ERD Level)

---

## tenant
- id (PK)
- name
- schema_name
- created_at

## employee
- id (PK)
- tenant_id (FK)
- employee_code
- name
- npwp
- tax_status
- salary_structure_id

## salary_structure
- id (PK)
- basic_salary
- allowance
- deduction

## tax_bracket
- id (PK)
- min_income
- max_income
- percentage

## payroll
- id (PK)
- employee_id (FK)
- month
- year
- gross_salary
- tax
- bpjs
- overtime
- net_salary
- status

## payroll_audit_log
- id (PK)
- payroll_id (FK)
- action
- old_value
- new_value
- changed_by
- timestamp

## approval
- id
- payroll_id
- approver_id
- status
- approved_at
