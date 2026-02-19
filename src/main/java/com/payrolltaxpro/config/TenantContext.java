package com.payrolltaxpro.config;

public class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_NAME = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setTenantName(String tenantName) {
        TENANT_NAME.set(tenantName);
    }

    public static String getTenantName() {
        return TENANT_NAME.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_NAME.remove();
    }
}
