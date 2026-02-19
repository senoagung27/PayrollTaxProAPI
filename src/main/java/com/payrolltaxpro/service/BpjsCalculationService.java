package com.payrolltaxpro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class BpjsCalculationService {

    @Value("${payroll.bpjs.employer-percentage:0.05}")
    private BigDecimal employerPercentage;

    @Value("${payroll.bpjs.employee-percentage:0.02}")
    private BigDecimal employeePercentage;

    private static final BigDecimal BPJS_SALARY_CAP = BigDecimal.valueOf(12000000); // Maximum salary for BPJS calculation
    private static final int BPJS_SALARY_CAP_OVERRIDE = 12000000;

    /**
     * Calculate BPJS contribution for an employee.
     * Based on employee percentage of salary (up to cap).
     *
     * @param grossMonthlySalary Monthly gross salary
     * @return Employee BPJS contribution
     */
    public BigDecimal calculateEmployeeBpjs(BigDecimal grossMonthlySalary) {
        if (grossMonthlySalary == null || grossMonthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal baseSalary = grossMonthlySalary.min(BigDecimal.valueOf(BPJS_SALARY_CAP_OVERRIDE));
        BigDecimal employeeContribution = baseSalary.multiply(employeePercentage);

        log.debug("Employee BPJS - Base salary: {}, Percentage: {}, Contribution: {}",
                baseSalary, employeePercentage, employeeContribution);

        return employeeContribution.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate BPJS contribution for an employer.
     * Based on employer percentage of salary (up to cap).
     *
     * @param grossMonthlySalary Monthly gross salary
     * @return Employer BPJS contribution
     */
    public BigDecimal calculateEmployerBpjs(BigDecimal grossMonthlySalary) {
        if (grossMonthlySalary == null || grossMonthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal baseSalary = grossMonthlySalary.min(BigDecimal.valueOf(BPJS_SALARY_CAP_OVERRIDE));
        BigDecimal employerContribution = baseSalary.multiply(employerPercentage);

        log.debug("Employer BPJS - Base salary: {}, Percentage: {}, Contribution: {}",
                baseSalary, employerPercentage, employerContribution);

        return employerContribution.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total BPJS contribution (employee + employer).
     *
     * @param grossMonthlySalary Monthly gross salary
     * @return Total BPJS contribution
     */
    public BigDecimal calculateTotalBpjs(BigDecimal grossMonthlySalary) {
        BigDecimal employee = calculateEmployeeBpjs(grossMonthlySalary);
        BigDecimal employer = calculateEmployerBpjs(grossMonthlySalary);
        return employee.add(employer);
    }

    /**
     * Get the configured employee percentage.
     */
    public BigDecimal getEmployeePercentage() {
        return employeePercentage;
    }

    /**
     * Get the configured employer percentage.
     */
    public BigDecimal getEmployerPercentage() {
        return employerPercentage;
    }
}
