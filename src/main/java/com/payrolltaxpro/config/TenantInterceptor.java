package com.payrolltaxpro.config;

import com.payrolltaxpro.domain.Tenant;
import com.payrolltaxpro.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantRepository tenantRepository;

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantIdHeader = request.getHeader(TENANT_HEADER);

        if (tenantIdHeader != null) {
            try {
                Long tenantId = Long.parseLong(tenantIdHeader);
                Tenant tenant = tenantRepository.findById(tenantId).orElse(null);

                if (tenant != null && tenant.getActive()) {
                    TenantContext.setTenantId(tenantId);
                    TenantContext.setTenantName(tenant.getName());
                    log.debug("Set tenant context: {} for request: {}", tenant.getName(), request.getRequestURI());
                } else {
                    log.warn("Tenant not found or inactive: {}", tenantId);
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid tenant ID format: {}", tenantIdHeader);
            }
        } else {
            // For authentication endpoints, allow without tenant
            String path = request.getRequestURI();
            if (path.contains("/auth/") || path.contains("/public/") || path.contains("/health")) {
                return true;
            }
            log.debug("No tenant header provided for request: {}", request.getRequestURI());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        TenantContext.clear();
    }
}
