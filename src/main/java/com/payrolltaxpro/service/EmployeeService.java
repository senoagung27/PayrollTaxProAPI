package com.payrolltaxpro.service;

import com.payrolltaxpro.domain.Employee;
import com.payrolltaxpro.domain.SalaryStructure;
import com.payrolltaxpro.domain.Tenant;
import com.payrolltaxpro.repository.EmployeeRepository;
import com.payrolltaxpro.repository.SalaryStructureRepository;
import com.payrolltaxpro.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TenantRepository tenantRepository;
    private final SalaryStructureRepository salaryStructureRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Page<Employee> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public List<Employee> getEmployeesByTenant(Long tenantId) {
        return employeeRepository.findByTenantId(tenantId);
    }

    public Page<Employee> getEmployeesByTenant(Long tenantId, Pageable pageable) {
        return employeeRepository.findByTenantId(tenantId, pageable);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    public Employee getEmployeeByCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + employeeCode));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        // Validate tenant exists
        if (employee.getTenant() != null && employee.getTenant().getId() != null) {
            Tenant tenant = tenantRepository.findById(employee.getTenant().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + employee.getTenant().getId()));
            employee.setTenant(tenant);
        }

        // Validate salary structure if provided
        if (employee.getSalaryStructure() != null && employee.getSalaryStructure().getId() != null) {
            SalaryStructure salaryStructure = salaryStructureRepository.findById(employee.getSalaryStructure().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Salary structure not found: " + employee.getSalaryStructure().getId()));
            employee.setSalaryStructure(salaryStructure);
        }

        // Check if employee code already exists
        if (employeeRepository.existsByEmployeeCode(employee.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee code already exists: " + employee.getEmployeeCode());
        }

        // Calculate hourly rate if not provided
        if (employee.getHourlyRate() == null && employee.getSalaryStructure() != null) {
            BigDecimal basicSalary = employee.getSalaryStructure().getBasicSalary();
            // Standard working hours per month: 173 hours
            employee.setHourlyRate(basicSalary.divide(BigDecimal.valueOf(173), 2, RoundingMode.HALF_UP));
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Created employee: {} with code: {}", saved.getName(), saved.getEmployeeCode());
        return saved;
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);

        existing.setName(employee.getName());
        existing.setNpwp(employee.getNpwp());
        existing.setTaxStatus(employee.getTaxStatus());
        existing.setBankName(employee.getBankName());
        existing.setBankAccount(employee.getBankAccount());
        existing.setJoinDate(employee.getJoinDate());
        existing.setBpjsNumber(employee.getBpjsNumber());
        existing.setActive(employee.getActive());

        // Update salary structure if provided
        if (employee.getSalaryStructure() != null && employee.getSalaryStructure().getId() != null) {
            SalaryStructure salaryStructure = salaryStructureRepository.findById(employee.getSalaryStructure().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Salary structure not found: " + employee.getSalaryStructure().getId()));
            existing.setSalaryStructure(salaryStructure);

            // Recalculate hourly rate
            if (existing.getHourlyRate() == null) {
                BigDecimal basicSalary = salaryStructure.getBasicSalary();
                existing.setHourlyRate(basicSalary.divide(BigDecimal.valueOf(173), 2, RoundingMode.HALF_UP));
            }
        }

        Employee updated = employeeRepository.save(existing);
        log.info("Updated employee: {} with code: {}", updated.getName(), updated.getEmployeeCode());
        return updated;
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
        log.info("Deleted employee: {} with code: {}", employee.getName(), employee.getEmployeeCode());
    }

    public List<Employee> searchEmployeesByName(Long tenantId, String name, Pageable pageable) {
        return employeeRepository.findByTenantIdAndNameContaining(tenantId, name, pageable).getContent();
    }
}
