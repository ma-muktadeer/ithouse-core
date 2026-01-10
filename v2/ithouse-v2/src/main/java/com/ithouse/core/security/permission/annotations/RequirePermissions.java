package com.ithouse.core.security.permission.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermissions {
    String[] value();

    boolean allRequired() default false;

    String[] roles() default {};
    boolean checkRole() default false;
}
