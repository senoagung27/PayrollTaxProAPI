package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCode(String employeeCode);

    List<Employee> findByTenantId(Long tenantId);

    Page<Employee> findByTenantId(Long tenantId, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.tenant.id = :tenantId AND e.active = true")
    List<Employee> findActiveByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT e FROM Employee e WHERE e.tenant.id = :tenantId AND e.name LIKE %:name%")
    Page<Employee> findByTenantIdAndNameContaining(@Param("tenantId") Long tenantId, @Param("name") String name, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.tenant.id = :tenantId AND (:active IS NULL OR e.active = :active)")
    Page<Employee> findByTenantIdAndActive(@Param("tenantId") Long tenantId, @Param("active") Boolean active, Pageable pageable);
}
