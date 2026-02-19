package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.Tenant;
import com.payrolltaxpro.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + id));
    }

    public Tenant getTenantBySchemaName(String schemaName) {
        return tenantRepository.findBySchemaName(schemaName)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + schemaName));
    }

    @Transactional
    public Tenant createTenant(Tenant tenant) {
        if (tenantRepository.existsBySchemaName(tenant.getSchemaName())) {
            throw new IllegalArgumentException("Schema name already exists: " + tenant.getSchemaName());
        }
        if (tenantRepository.existsByName(tenant.getName())) {
            throw new IllegalArgumentException("Tenant name already exists: " + tenant.getName());
        }

        Tenant saved = tenantRepository.save(tenant);
        log.info("Created tenant: {} with schema: {}", saved.getName(), saved.getSchemaName());
        return saved;
    }

    @Transactional
    public Tenant updateTenant(Long id, Tenant tenant) {
        Tenant existing = getTenantById(id);

        existing.setName(tenant.getName());
        existing.setSchemaName(tenant.getSchemaName());
        existing.setActive(tenant.getActive());

        Tenant updated = tenantRepository.save(existing);
        log.info("Updated tenant: {} with schema: {}", updated.getName(), updated.getSchemaName());
        return updated;
    }

    @Transactional
    public void deleteTenant(Long id) {
        Tenant tenant = getTenantById(id);
        tenantRepository.delete(tenant);
        log.info("Deleted tenant: {} with schema: {}", tenant.getName(), tenant.getSchemaName());
    }
}
