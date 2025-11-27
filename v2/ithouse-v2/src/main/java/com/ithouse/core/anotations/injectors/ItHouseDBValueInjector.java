package com.ithouse.core.anotations.injectors;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithouse.core.anotations.ItHouseDBValue;
import com.ithouse.core.anotations.services.ItHouseDBValueService;

@Component
public class ItHouseDBValueInjector implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext context;
    private final ItHouseDBValueService itHouseDBValueService;
    private final ObjectMapper objectMapper;

    public ItHouseDBValueInjector(ItHouseDBValueService itHouseDBValueService, ObjectMapper objectMapper) {
        this.itHouseDBValueService = itHouseDBValueService;
        this.objectMapper = objectMapper;
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

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ItHouseDBValue.class)) {
                ItHouseDBValue annotation = field.getAnnotation(ItHouseDBValue.class);
                String value = itHouseDBValueService.getConfigValue(
                        annotation.configGroup(),
                        annotation.configSubGroup(),
                        annotation.defaultValue());

                try {
                    field.setAccessible(true);

                    switch (field.getType().getSimpleName()) {
                        case "List" -> field.set(bean, parseList(value, field));
                        case "Map" -> field.set(bean, parse2Map(value, field));
                        case "String" -> field.set(bean, value);
                        default -> field.set(bean, objectMapper.convertValue(value, field.getType()));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject ItHouseDBValue for field: " + field.getName(), e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(
                            "Failed to convert value for field: " + field.getName() + " value: " + value, e);
                }
            }
        }
    }

    private List<?> parseList(String rawValue, Field field) {
        ParameterizedType pt = (ParameterizedType) field.getGenericType();
        Class<?> elementType = (Class<?>) pt.getActualTypeArguments()[0];
        try {
            return objectMapper.readValue(rawValue,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (Exception e) {
            return Arrays.stream(rawValue.split("[,;]"))
                    .map(m -> castValue(m, elementType)).toList();
        }
    }

    private Object parse2Map(String rawValue, Field field) {
        ParameterizedType pt = (ParameterizedType) field.getGenericType();
        Class<?> key = (Class<?>) pt.getActualTypeArguments()[0];
        Class<?> value = (Class<?>) pt.getActualTypeArguments()[1];

        try {
            return objectMapper.readValue(rawValue,
                    objectMapper.getTypeFactory().constructMapType(Map.class, key, value));
        } catch (Exception e) {
            if (rawValue == null || rawValue.trim().isEmpty()) {
                return new HashMap<>();
            }
            Map<Object, Object> map = new HashMap<>();
            String[] pairs = rawValue.split("[,;]");
            for (String pair : pairs) {
                String[] entry = pair.split("[=:]", 2);
                if (entry.length != 2) {
                    continue;
                }
                map.put(castValue(entry[0], key), castValue(entry[1], value));
            }
            return map;
        }
    }

    private Object castValue(String value, Class<?> type) {
        if (value == null)
            return null;
        String str = value.toString().trim();

        return switch (type.getSimpleName()) {
            case "String" -> str;
            case "Integer", "int" -> Integer.parseInt(str);
            case "Boolean", "boolean" -> Boolean.parseBoolean(str);
            case "Long", "long" -> Long.parseLong(str);
            case "Double", "double" -> Double.parseDouble(str);
            case "Float", "float" -> Float.parseFloat(str);
            case "Short", "short" -> Short.parseShort(str);
            case "Byte", "byte" -> Byte.parseByte(str);
            default -> value;
        };
    }

}
