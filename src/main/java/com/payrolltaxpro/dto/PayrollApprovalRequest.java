package com.payrolltaxpro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollApprovalRequest {

    @NotNull(message = "Payroll ID is required")
    private Long payrollId;

    @NotBlank(message = "Action is required")
    private String action; // APPROVE, REJECT

    private String notes;
}
