package com.payrolltaxpro.repository;

import com.payrolltaxpro.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySchemaName(String schemaName);

    boolean existsBySchemaName(String schemaName);

    boolean existsByName(String name);
}
