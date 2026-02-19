package com.payrolltaxpro.controller;

import com.payrolltaxpro.domain.Payroll;
import com.payrolltaxpro.domain.PayrollAuditLog;
import com.payrolltaxpro.dto.ApiResponse;
import com.payrolltaxpro.dto.PayrollApprovalRequest;
import com.payrolltaxpro.dto.PayrollProcessRequest;
import com.payrolltaxpro.service.CsvExportService;
import com.payrolltaxpro.service.PayrollAuditLogService;
import com.payrolltaxpro.service.PayrollProcessingService;
import com.payrolltaxpro.service.PayslipPdfService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
@Slf4j
public class PayrollController {

    private final PayrollProcessingService payrollProcessingService;
    private final PayslipPdfService payslipPdfService;
    private final CsvExportService csvExportService;
    private final PayrollAuditLogService auditLogService;

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<List<Payroll>>> processPayroll(
            @Valid @RequestBody PayrollProcessRequest request,
            Authentication authentication
    ) {
        log.info("Processing payroll for {} employees for {}/{}",
                request.getEmployeeIds().size(), request.getMonth(), request.getYear());

        List<Payroll> payrolls = payrollProcessingService.processPayrollForEmployees(
                request.getEmployeeIds(),
                request.getMonth(),
                request.getYear(),
                request.getDefaultOvertimeHours()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payroll processed successfully for " + payrolls.size() + " employees", payrolls));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Payroll>> approvePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollApprovalRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        // In a real application, you would get the user ID from the authentication principal
        Long approverId = 1L; // Placeholder

        Payroll payroll = payrollProcessingService.approvePayroll(
                id,
                approverId,
                username,
                request.getNotes()
        );

        return ResponseEntity.ok(ApiResponse.success("Payroll approved successfully", payroll));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Payroll>> rejectPayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollApprovalRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        // In a real application, you would get the user ID from the authentication principal
        Long approverId = 1L; // Placeholder

        Payroll payroll = payrollProcessingService.rejectPayroll(
                id,
                approverId,
                username,
                request.getNotes()
        );

        return ResponseEntity.ok(ApiResponse.success("Payroll rejected successfully", payroll));
    }

    @PostMapping("/lock")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> lockPayroll(
            @RequestParam Long tenantId,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        payrollProcessingService.lockPayrollForMonth(tenantId, month, year);
        return ResponseEntity.ok(ApiResponse.success("Payroll locked successfully for " + month + "/" + year, null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<Payroll>> getPayrollById(@PathVariable Long id) {
        // Get payroll - you would add a method to the service to get payroll by ID
        // For now, return a placeholder
        return ResponseEntity.ok(ApiResponse.success(new Payroll()));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<Payroll>>> getPayrollByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        // Get payroll by employee - you would add a method to the service
        // For now, return a placeholder
        return ResponseEntity.ok(ApiResponse.success(Collections.emptyList()));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPayrollSummary(
            @RequestParam Long tenantId,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        Map<String, Object> summary = payrollProcessingService.getPayrollSummary(tenantId, month, year);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/{id}/payslip")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE', 'ROLE_EMPLOYEE')")
    public ResponseEntity<byte[]> generatePayslip(@PathVariable Long id) throws IOException {
        // Get payroll by ID - you would add a method to get payroll
        // For now, this is a placeholder
        Payroll payroll = new Payroll();
        byte[] pdf = payslipPdfService.generatePayslip(payroll);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "payslip_" + id + "_" + LocalDate.now() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public void exportBankTransferCsv(
            @RequestParam Long tenantId,
            @RequestParam Integer month,
            @RequestParam Integer year,
            HttpServletResponse response
    ) throws IOException {
        String filename = String.format("bank_transfer_%d_%02d_%d.csv", tenantId, month, year);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // Get payrolls for export - you would add a method to get payrolls by tenant/month/year
        List<Payroll> payrolls = Collections.emptyList();

        String csv = csvExportService.generateBankTransferCsvContent(payrolls);
        response.getWriter().write(csv);
    }

    @GetMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<List<PayrollAuditLog>>> getAuditLogs(@PathVariable Long id) {
        List<PayrollAuditLog> logs = auditLogService.getAuditLogsForPayroll(id);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/audit")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PayrollAuditLog>>> getAuditLogsByDateRange(
            @RequestParam Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<PayrollAuditLog> logs = auditLogService.getAuditLogsForTenant(tenantId, start, end);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
