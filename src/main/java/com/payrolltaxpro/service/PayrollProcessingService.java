package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.*;
import com.payrolltaxpro.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollProcessingService {

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final TaxCalculationService taxCalculationService;
    private final BpjsCalculationService bpjsCalculationService;
    private final OvertimeCalculationService overtimeCalculationService;
    private final PayrollAuditLogService auditLogService;
    private final CacheManager cacheManager;

    /**
     * Process payroll for multiple employees for a given month and year.
     *
     * @param employeeIds List of employee IDs to process payroll for
     * @param month       Month (1-12)
     * @param year        Year
     * @param overtimeHours Default overtime hours for all employees
     * @return List of processed payroll records
     */
    @Transactional
    public List<Payroll> processPayrollForEmployees(Set<Long> employeeIds, Integer month, Integer year, BigDecimal overtimeHours) {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        List<Payroll> processedPayrolls = new ArrayList<>();

        for (Employee employee : employees) {
            try {
                Payroll payroll = processPayrollForEmployee(employee, month, year, overtimeHours);
                processedPayrolls.add(payroll);
            } catch (Exception e) {
                log.error("Error processing payroll for employee {}: {}", employee.getId(), e.getMessage(), e);
            }
        }

        log.info("Processed {} payroll records for {}/{}", processedPayrolls.size(), month, year);
        return processedPayrolls;
    }

    /**
     * Process payroll for a single employee.
     */
    @Transactional
    public Payroll processPayrollForEmployee(Employee employee, Integer month, Integer year, BigDecimal overtimeHours) {
        // Check if payroll already exists for this employee, month, and year
        Optional<Payroll> existingPayroll = payrollRepository
                .findByEmployeeIdAndMonthAndYear(employee.getId(), month, year);

        if (existingPayroll.isPresent() && existingPayroll.get().getLocked()) {
            throw new IllegalStateException("Payroll is already locked for employee " + employee.getName() + " for " + month + "/" + year);
        }

        // Get salary structure
        SalaryStructure salaryStructure = employee.getSalaryStructure();
        if (salaryStructure == null) {
            throw new IllegalStateException("No salary structure assigned to employee " + employee.getName());
        }

        // Calculate salary components
        BigDecimal basicSalary = salaryStructure.getBasicSalary();
        BigDecimal allowance = salaryStructure.getAllowance() != null ? salaryStructure.getAllowance() : BigDecimal.ZERO;
        BigDecimal deduction = salaryStructure.getDeduction() != null ? salaryStructure.getDeduction() : BigDecimal.ZERO;

        // Calculate gross salary
        BigDecimal grossSalary = basicSalary.add(allowance);

        // Calculate BPJS
        BigDecimal bpjs = bpjsCalculationService.calculateEmployeeBpjs(grossSalary);

        // Calculate overtime
        BigDecimal hourlyRate = employee.getHourlyRate() != null ? employee.getHourlyRate() : basicSalary.divide(BigDecimal.valueOf(173), 2, RoundingMode.HALF_UP);
        BigDecimal overtime = overtimeCalculationService.calculateOvertime(hourlyRate, overtimeHours != null ? overtimeHours : BigDecimal.ZERO);

        // Calculate final gross salary with overtime
        BigDecimal totalGrossSalary = grossSalary.add(overtime);

        // Calculate tax
        BigDecimal tax = taxCalculationService.calculateMonthlyTax(totalGrossSalary.subtract(bpjs));

        // Calculate net salary
        BigDecimal netSalary = totalGrossSalary.subtract(tax).subtract(bpjs).add(deduction.negate());

        // Create or update payroll record
        Payroll payroll = existingPayroll.orElseGet(() -> Payroll.builder()
                .employee(employee)
                .month(month)
                .year(year)
                .status(Payroll.PayrollStatus.DRAFT)
                .locked(false)
                .build());

        payroll.setBasicSalary(basicSalary);
        payroll.setGrossSalary(totalGrossSalary);
        payroll.setAllowance(allowance);
        payroll.setDeduction(deduction);
        payroll.setTax(tax);
        payroll.setBpjs(bpjs);
        payroll.setOvertime(overtime);
        payroll.setOvertimeHours(overtimeHours != null ? overtimeHours : BigDecimal.ZERO);
        payroll.setNetSalary(netSalary);
        payroll.setTaxableIncome(taxCalculationService.calculateTaxableIncome(totalGrossSalary.subtract(bpjs)));
        payroll.setUpdatedAt(LocalDateTime.now());

        Payroll savedPayroll = payrollRepository.save(payroll);

        // Audit log
        auditLogService.logChange(savedPayroll, "CREATE", null,
                "Payroll processed for employee " + employee.getName(),
                "system", null);

        log.info("Processed payroll for employee {} for {}/{}: Net Salary = {}",
                employee.getName(), month, year, netSalary);

        return savedPayroll;
    }

    /**
     * Approve a payroll record.
     */
    @Transactional
    public Payroll approvePayroll(Long payrollId, Long approverId, String approverName, String notes) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found: " + payrollId));

        if (payroll.getLocked()) {
            throw new IllegalStateException("Payroll is already locked and cannot be approved");
        }

        if (payroll.getStatus() != Payroll.PayrollStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT payroll can be approved");
        }

        // Create approval record
        Approval approval = Approval.builder()
                .payroll(payroll)
                .approverId(approverId)
                .approverName(approverName)
                .status(Approval.ApprovalStatus.APPROVED)
                .approvedAt(LocalDateTime.now())
                .notes(notes)
                .build();

        // Update payroll status
        payroll.setStatus(Payroll.PayrollStatus.APPROVED);
        payroll.setPaymentDate(LocalDate.now());

        Payroll approvedPayroll = payrollRepository.save(payroll);

        // Audit log
        auditLogService.logChange(approvedPayroll, "APPROVE",
                "Status: " + Payroll.PayrollStatus.DRAFT,
                "Status: " + Payroll.PayrollStatus.APPROVED,
                approverName, approverId);

        log.info("Approved payroll {} for employee {} for {}/{}",
                payrollId, payroll.getEmployee().getName(), payroll.getMonth(), payroll.getYear());

        return approvedPayroll;
    }

    /**
     * Reject a payroll record.
     */
    @Transactional
    public Payroll rejectPayroll(Long payrollId, Long approverId, String approverName, String notes) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found: " + payrollId));

        if (payroll.getLocked()) {
            throw new IllegalStateException("Payroll is already locked");
        }

        // Create approval record
        Approval approval = Approval.builder()
                .payroll(payroll)
                .approverId(approverId)
                .approverName(approverName)
                .status(Approval.ApprovalStatus.REJECTED)
                .approvedAt(LocalDateTime.now())
                .notes(notes)
                .build();

        // Audit log
        auditLogService.logChange(payroll, "REJECT",
                null,
                "Payroll rejected. Notes: " + notes,
                approverName, approverId);

        log.info("Rejected payroll {} for employee {} for {}/{}",
                payrollId, payroll.getEmployee().getName(), payroll.getMonth(), payroll.getYear());

        return payroll;
    }

    /**
     * Lock payroll for a given month and year for a tenant.
     */
    @Transactional
    public void lockPayrollForMonth(Long tenantId, Integer month, Integer year) {
        List<Payroll> payrolls = payrollRepository.findByTenantIdAndMonthAndYear(tenantId, month, year);

        for (Payroll payroll : payrolls) {
            if (payroll.getStatus() != Payroll.PayrollStatus.APPROVED) {
                throw new IllegalStateException("Cannot lock payroll. All payrolls must be approved first. Payroll ID: " + payroll.getId());
            }

            payroll.setLocked(true);
            payroll.setStatus(Payroll.PayrollStatus.LOCKED);
            payrollRepository.save(payroll);

            // Audit log
            auditLogService.logChange(payroll, "LOCK",
                    "Status: " + Payroll.PayrollStatus.APPROVED,
                    "Status: " + Payroll.PayrollStatus.LOCKED,
                    "system", null);
        }

        // Cache the lock status in Redis
        String lockKey = "payroll:lock:" + tenantId + ":" + month + ":" + year;
        cacheManager.getCache("payrollLocks").put(lockKey, true);

        log.info("Locked {} payroll records for tenant {} for {}/{}", payrolls.size(), tenantId, month, year);
    }

    /**
     * Check if payroll is locked for a given month and year.
     */
    public boolean isPayrollLocked(Long tenantId, Integer month, Integer year) {
        String lockKey = "payroll:lock:" + tenantId + ":" + month + ":" + year;
        Boolean locked = cacheManager.getCache("payrollLocks").get(lockKey, Boolean.class);
        return locked != null && locked;
    }

    /**
     * Recalculate payroll for an existing payroll record.
     */
    @Transactional
    public Payroll recalculatePayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found: " + payrollId));

        if (payroll.getLocked()) {
            throw new IllegalStateException("Cannot recalculate locked payroll");
        }

        Employee employee = payroll.getEmployee();
        BigDecimal overtimeHours = payroll.getOvertimeHours();

        return processPayrollForEmployee(employee, payroll.getMonth(), payroll.getYear(), overtimeHours);
    }

    /**
     * Get payroll summary for a tenant for a given month and year.
     */
    public Map<String, Object> getPayrollSummary(Long tenantId, Integer month, Integer year) {
        List<Payroll> payrolls = payrollRepository.findByTenantIdAndMonthAndYear(tenantId, month, year);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEmployees", payrolls.size());
        summary.put("month", month);
        summary.put("year", year);

        BigDecimal totalGrossSalary = payrolls.stream()
                .map(Payroll::getGrossSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNetSalary = payrolls.stream()
                .map(Payroll::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = payrolls.stream()
                .map(Payroll::getTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBpjs = payrolls.stream()
                .map(Payroll::getBpjs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOvertime = payrolls.stream()
                .map(Payroll::getOvertime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.put("totalGrossSalary", totalGrossSalary);
        summary.put("totalNetSalary", totalNetSalary);
        summary.put("totalTax", totalTax);
        summary.put("totalBpjs", totalBpjs);
        summary.put("totalOvertime", totalOvertime);

        // Count by status
        Map<Payroll.PayrollStatus, Long> statusCount = payrolls.stream()
                .collect(Collectors.groupingBy(Payroll::getStatus, Collectors.counting()));

        summary.put("statusCount", statusCount);

        return summary;
    }
}
