package com.ithouse.core.security.permission.aspect;


import com.ithouse.core.security.permission.PermissionChecker;
import com.ithouse.core.security.permission.RoleChecker;
import com.ithouse.core.security.permission.annotations.RequirePermissions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {
    private final static Logger logger = LogManager.getLogger(PermissionAspect.class);

    @Autowired(required = false)
    private PermissionChecker permissionChecker;
    @Autowired(required = false)
    private RoleChecker roleChecker;

    @Before("@annotation(requirePermissions)")
    public void checkPermissions(JoinPoint joinPoint, RequirePermissions requirePermissions) {
        if (permissionChecker == null) {
            logger.error("No PermissionChecker bean found! Please implement it in your application.");
            return;
        }
        try {
            if (requirePermissions.checkRole()) {
                roleChecker.checkRole(requirePermissions.roles());
            }
            else {
                permissionChecker.hasPermission(requirePermissions.value(), requirePermissions.allRequired());
            }
        } catch (Exception e) {
            logger.error("getting error=>{}\n{}", e.getMessage(), e);
            throw new RuntimeException("Access denied");
        }
    }

}
