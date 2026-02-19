package com.payrolltaxpro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "basic_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "gross_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "tax", precision = 15, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal bpjs = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal overtime = BigDecimal.ZERO;

    @Column(name = "allowance", precision = 15, scale = 2)
    private BigDecimal allowance = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal deduction = BigDecimal.ZERO;

    @Column(name = "net_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "taxable_income", precision = 15, scale = 2)
    private BigDecimal taxableIncome = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PayrollStatus status = PayrollStatus.DRAFT;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "locked", nullable = false)
    @Builder.Default
    private Boolean locked = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum PayrollStatus {
        DRAFT, APPROVED, LOCKED
    }
}
