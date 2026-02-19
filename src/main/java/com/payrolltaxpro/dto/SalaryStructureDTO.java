package com.payrolltaxpro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureDTO {

    private Long id;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary must be positive")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Allowance must be positive")
    private BigDecimal allowance;

    @DecimalMin(value = "0.0", inclusive = true, message = "Deduction must be positive")
    private BigDecimal deduction;

    private String name;

    private String grade;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
