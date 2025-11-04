package com.ithouse.core.security.permission;

public interface PermissionChecker {
    boolean hasPermission(String[] permission, boolean allRequired);
}
