package com.payrolltaxpro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_structure")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "allowance", precision = 15, scale = 2)
    private BigDecimal allowance = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal deduction = BigDecimal.ZERO;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String grade;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
