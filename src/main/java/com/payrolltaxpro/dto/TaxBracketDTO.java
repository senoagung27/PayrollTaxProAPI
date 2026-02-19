package com.payrolltaxpro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class TaxBracketDTO {

    private Long id;

    @NotNull(message = "Minimum income is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum income must be positive")
    private BigDecimal minIncome;

    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum income must be positive")
    private BigDecimal maxIncome;

    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentage must be positive")
    private BigDecimal percentage;

    @NotNull(message = "Bracket order is required")
    @PositiveOrZero(message = "Bracket order must be zero or positive")
    private Integer bracketOrder;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
