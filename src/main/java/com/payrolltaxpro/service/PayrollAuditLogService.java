package com.payrolltaxpro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payrolltaxpro.domain.Payroll;
import com.payrolltaxpro.domain.PayrollAuditLog;
import com.payrolltaxpro.repository.PayrollAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PayrollAuditLogService {

    private final PayrollAuditLogRepository auditLogRepository;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    public PayrollAuditLogService(PayrollAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Log a change to a payroll record.
     *
     * @param payroll     The payroll record that was changed
     * @param action      The action that was performed (CREATE, UPDATE, DELETE, APPROVE, LOCK, etc.)
     * @param oldValue    The old value (before the change)
     * @param newValue    The new value (after the change)
     * @param changedBy   The username of the person who made the change
     * @param changedById The ID of the person who made the change
     */
    @Async
    public void logChange(Payroll payroll, String action, String oldValue, String newValue,
                         String changedBy, Long changedById) {

        PayrollAuditLog auditLog = PayrollAuditLog.builder()
                .payroll(payroll)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(changedBy)
                .changedById(changedById)
                .timestamp(LocalDateTime.now())
                .ipAddress(getClientIpAddress())
                .build();

        // Add additional details as JSON
        Map<String, Object> details = new HashMap<>();
        details.put("employeeId", payroll.getEmployee().getId());
        details.put("employeeName", payroll.getEmployee().getName());
        details.put("month", payroll.getMonth());
        details.put("year", payroll.getYear());
        details.put("action", action);

        try {
            auditLog.setChangeDetails(objectMapper.writeValueAsString(details));
        } catch (JsonProcessingException e) {
            log.error("Error serializing audit log details", e);
        }

        auditLogRepository.save(auditLog);
        log.debug("Audit log created for payroll {}: {} by {}", payroll.getId(), action, changedBy);
    }

    /**
     * Get audit logs for a specific payroll.
     */
    public List<PayrollAuditLog> getAuditLogsForPayroll(Long payrollId) {
        return auditLogRepository.findByPayrollIdOrderByTimestampDesc(payrollId);
    }

    /**
     * Get audit logs for a tenant within a date range.
     */
    public List<PayrollAuditLog> getAuditLogsForTenant(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTenantIdAndTimestampBetween(tenantId, start, end);
    }

    /**
     * Get audit logs for a specific user.
     */
    public List<PayrollAuditLog> getAuditLogsByUser(String username) {
        return auditLogRepository.findByChangedBy(username);
    }

    /**
     * Get the client IP address from the current request.
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not get client IP address", e);
        }
        return null;
    }
}
