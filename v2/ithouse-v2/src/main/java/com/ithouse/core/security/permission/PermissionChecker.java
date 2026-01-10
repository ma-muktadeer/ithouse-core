package com.ithouse.core.security.permission;

public interface PermissionChecker {
    void hasPermission(String[] permission, boolean allRequired);
}
