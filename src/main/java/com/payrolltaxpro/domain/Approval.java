package com.payrolltaxpro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_name", length = 100)
    private String approverName;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(length = 500)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
