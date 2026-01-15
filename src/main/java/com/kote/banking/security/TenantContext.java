package com.kote.banking.security;

public class TenantContext {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setTenantName(String tenantName) {
        if (validateName(tenantName)) {
            threadLocal.set(tenantName);
        }
    }
    public static String getTenant(){
        return threadLocal.get();
    }
    private static boolean validateName(String name){
        return name.matches("^[A-Za-z0-9_]+$");
    }
    public static void clear(){
        threadLocal.remove();
    }
}
