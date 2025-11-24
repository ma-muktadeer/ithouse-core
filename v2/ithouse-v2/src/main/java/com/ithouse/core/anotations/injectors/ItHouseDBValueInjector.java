package com.ithouse.core.anotations.injectors;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.ithouse.core.anotations.services.ItHouseDBValueService;

@Component
public class ItHouseDBValueInjector implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext context;
    private final ItHouseDBValueService itHouseDBValueService;

    public ItHouseDBValueInjector(ItHouseDBValueService itHouseDBValueService) {
        this.itHouseDBValueService = itHouseDBValueService;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void afterPropertiesSet() {
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            processBean(bean);
        }
    }

    private void processBean(Object bean) {
        Class<?> clazz = bean.getClass();
        // Handle CGLIB proxies
        if (clazz.getName().contains("$$")) {
            clazz = clazz.getSuperclass();
        }

        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(com.ithouse.core.anotations.ItHouseDBValue.class)) {
                com.ithouse.core.anotations.ItHouseDBValue annotation = field
                        .getAnnotation(com.ithouse.core.anotations.ItHouseDBValue.class);
                String value = itHouseDBValueService.getConfigValue(
                        annotation.configGroup(),
                        annotation.configSubGroup(),
                        annotation.defaultValue());

                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject ItHouseDBValue for field: " + field.getName(), e);
                }
            }
        }
    }
}
