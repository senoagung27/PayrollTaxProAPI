package com.payrolltaxpro.controller;

import com.payrolltaxpro.domain.TaxBracket;
import com.payrolltaxpro.dto.ApiResponse;
import com.payrolltaxpro.service.TaxBracketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tax-brackets")
@RequiredArgsConstructor
@Slf4j
public class TaxBracketController {

    private final TaxBracketService taxBracketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<List<TaxBracket>>> getAllTaxBrackets() {
        List<TaxBracket> brackets = taxBracketService.getAllTaxBrackets();
        return ResponseEntity.ok(ApiResponse.success(brackets));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<TaxBracket>>> getActiveTaxBrackets() {
        List<TaxBracket> brackets = taxBracketService.getActiveTaxBrackets();
        return ResponseEntity.ok(ApiResponse.success(brackets));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<TaxBracket>> getTaxBracketById(@PathVariable Long id) {
        TaxBracket bracket = taxBracketService.getTaxBracketById(id);
        return ResponseEntity.ok(ApiResponse.success(bracket));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TaxBracket>> createTaxBracket(@Valid @RequestBody TaxBracket taxBracket) {
        TaxBracket created = taxBracketService.createTaxBracket(taxBracket);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tax bracket created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TaxBracket>> updateTaxBracket(
            @PathVariable Long id,
            @Valid @RequestBody TaxBracket taxBracket
    ) {
        TaxBracket updated = taxBracketService.updateTaxBracket(id, taxBracket);
        return ResponseEntity.ok(ApiResponse.success("Tax bracket updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTaxBracket(@PathVariable Long id) {
        taxBracketService.deleteTaxBracket(id);
        return ResponseEntity.ok(ApiResponse.success("Tax bracket deleted successfully", null));
    }
}
