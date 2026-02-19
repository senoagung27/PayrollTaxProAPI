package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    List<Payroll> findByEmployeeId(Long employeeId);

    Page<Payroll> findByEmployeeId(Long employeeId, Pageable pageable);

    @Query("SELECT p FROM Payroll p WHERE p.employee.tenant.id = :tenantId AND p.month = :month AND p.year = :year")
    List<Payroll> findByTenantIdAndMonthAndYear(@Param("tenantId") Long tenantId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT p FROM Payroll p WHERE p.employee.tenant.id = :tenantId AND p.month = :month AND p.year = :year AND p.status = :status")
    List<Payroll> findByTenantIdAndMonthAndYearAndStatus(@Param("tenantId") Long tenantId, @Param("month") Integer month, @Param("year") Integer year, @Param("status") Payroll.PayrollStatus status);

    @Query("SELECT p FROM Payroll p WHERE p.employee.tenant.id = :tenantId AND p.status = :status")
    Page<Payroll> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") Payroll.PayrollStatus status, Pageable pageable);

    @Query("SELECT p FROM Payroll p WHERE p.employee.tenant.id = :tenantId AND p.locked = :locked")
    List<Payroll> findByTenantIdAndLocked(@Param("tenantId") Long tenantId, @Param("locked") Boolean locked);

    @Query("SELECT p FROM Payroll p WHERE p.employee.id = :employeeId AND p.month = :month AND p.year = :year AND p.locked = false")
    Optional<Payroll> findEditablePayroll(@Param("employeeId") Long employeeId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.employee.tenant.id = :tenantId AND p.month = :month AND p.year = :year")
    Long countByTenantIdAndMonthAndYear(@Param("tenantId") Long tenantId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT p FROM Payroll p WHERE p.employee.id IN :employeeIds AND p.month = :month AND p.year = :year")
    List<Payroll> findByEmployeeIdsAndMonthAndYear(@Param("employeeIds") List<Long> employeeIds, @Param("month") Integer month, @Param("year") Integer year);
}
