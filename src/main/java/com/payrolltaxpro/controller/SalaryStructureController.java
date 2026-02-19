package com.payrolltaxpro.controller;

import com.payrolltaxpro.domain.SalaryStructure;
import com.payrolltaxpro.dto.ApiResponse;
import com.payrolltaxpro.service.SalaryStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-structures")
@RequiredArgsConstructor
@Slf4j
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<List<SalaryStructure>>> getAllSalaryStructures() {
        List<SalaryStructure> structures = salaryStructureService.getAllSalaryStructures();
        return ResponseEntity.ok(ApiResponse.success(structures));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<List<SalaryStructure>>> getActiveSalaryStructures() {
        List<SalaryStructure> structures = salaryStructureService.getActiveSalaryStructures();
        return ResponseEntity.ok(ApiResponse.success(structures));
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<Page<SalaryStructure>>> getSalaryStructuresPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "grade") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<SalaryStructure> structures = salaryStructureService.getSalaryStructures(pageable);
        return ResponseEntity.ok(ApiResponse.success(structures));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<SalaryStructure>> getSalaryStructureById(@PathVariable Long id) {
        SalaryStructure structure = salaryStructureService.getSalaryStructureById(id);
        return ResponseEntity.ok(ApiResponse.success(structure));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<SalaryStructure>> createSalaryStructure(@Valid @RequestBody SalaryStructure salaryStructure) {
        SalaryStructure created = salaryStructureService.createSalaryStructure(salaryStructure);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Salary structure created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<SalaryStructure>> updateSalaryStructure(
            @PathVariable Long id,
            @Valid @RequestBody SalaryStructure salaryStructure
    ) {
        SalaryStructure updated = salaryStructureService.updateSalaryStructure(id, salaryStructure);
        return ResponseEntity.ok(ApiResponse.success("Salary structure updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSalaryStructure(@PathVariable Long id) {
        salaryStructureService.deleteSalaryStructure(id);
        return ResponseEntity.ok(ApiResponse.success("Salary structure deleted successfully", null));
    }
}
