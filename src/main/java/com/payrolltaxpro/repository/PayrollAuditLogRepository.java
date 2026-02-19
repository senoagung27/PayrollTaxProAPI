package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.PayrollAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PayrollAuditLogRepository extends JpaRepository<PayrollAuditLog, Long> {

    List<PayrollAuditLog> findByPayrollIdOrderByTimestampDesc(Long payrollId);

    Page<PayrollAuditLog> findByPayrollId(Long payrollId, Pageable pageable);

    @Query("SELECT al FROM PayrollAuditLog al WHERE al.payroll.employee.tenant.id = :tenantId AND al.timestamp BETWEEN :start AND :end")
    List<PayrollAuditLog> findByTenantIdAndTimestampBetween(@Param("tenantId") Long tenantId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT al FROM PayrollAuditLog al WHERE al.changedBy = :username ORDER BY al.timestamp DESC")
    List<PayrollAuditLog> findByChangedBy(@Param("username") String username);

    @Query("SELECT al FROM PayrollAuditLog al WHERE al.payroll.id IN :payrollIds ORDER BY al.timestamp DESC")
    List<PayrollAuditLog> findByPayrollIds(@Param("payrollIds") List<Long> payrollIds);
}
