package com.ithouse.core.security.permission.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.ithouse.core.security.permission.aspect",
        "com.ithouse.core.security.permission.annotations"
})
public class SecurityLibAutoConfiguration {
}
