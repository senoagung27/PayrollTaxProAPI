package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByPayrollId(Long payrollId);

    Optional<Approval> findFirstByPayrollIdOrderByCreatedAtDesc(Long payrollId);

    @Query("SELECT a FROM Approval a WHERE a.payroll.id IN :payrollIds AND a.status = :status")
    List<Approval> findByPayrollIdsAndStatus(@Param("payrollIds") List<Long> payrollIds, @Param("status") Approval.ApprovalStatus status);

    @Query("SELECT a FROM Approval a WHERE a.payroll.employee.tenant.id = :tenantId AND a.status = :status")
    List<Approval> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") Approval.ApprovalStatus status);

    @Query("SELECT a FROM Approval a WHERE a.approverId = :approverId AND a.status = 'PENDING'")
    List<Approval> findPendingApprovalsByApproverId(@Param("approverId") Long approverId);
}
