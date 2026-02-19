package com.payrolltaxpro.dto;

import com.payrolltaxpro.domain.Payroll;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDTO {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private EmployeeDTO employee;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    private Integer year;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary must be positive")
    private BigDecimal basicSalary;

    @NotNull(message = "Gross salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Gross salary must be positive")
    private BigDecimal grossSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax must be positive")
    private BigDecimal tax;

    @DecimalMin(value = "0.0", inclusive = true, message = "BPJS must be positive")
    private BigDecimal bpjs;

    @DecimalMin(value = "0.0", inclusive = true, message = "Overtime must be positive")
    private BigDecimal overtime;

    @DecimalMin(value = "0.0", inclusive = true, message = "Allowance must be positive")
    private BigDecimal allowance;

    @DecimalMin(value = "0.0", inclusive = true, message = "Deduction must be positive")
    private BigDecimal deduction;

    @NotNull(message = "Net salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Net salary must be positive")
    private BigDecimal netSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Overtime hours must be positive")
    private BigDecimal overtimeHours;

    @DecimalMin(value = "0.0", inclusive = true, message = "Taxable income must be positive")
    private BigDecimal taxableIncome;

    private Payroll.PayrollStatus status;

    private LocalDate paymentDate;

    private Boolean locked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
