package com.payrolltaxpro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EmployeeDTO {

    private Long id;

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code must not exceed 50 characters")
    private String employeeCode;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 20, message = "NPWP must not exceed 20 characters")
    private String npwp;

    private String taxStatus;

    private Long salaryStructureId;

    private SalaryStructureDTO salaryStructure;

    @Size(max = 50, message = "Bank name must not exceed 50 characters")
    private String bankName;

    @Size(max = 50, message = "Bank account must not exceed 50 characters")
    private String bankAccount;

    private LocalDate joinDate;

    @Size(max = 50, message = "BPJS number must not exceed 50 characters")
    private String bpjsNumber;

    private BigDecimal hourlyRate;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
