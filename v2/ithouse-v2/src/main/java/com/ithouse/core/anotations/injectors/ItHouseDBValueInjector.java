package com.ithouse.core.anotations.injectors;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithouse.core.anotations.ItHouseDBValue;
import com.ithouse.core.anotations.services.ItHouseDBValueService;

public class ItHouseDBValueInjector implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext context;
    private final ItHouseDBValueService itHouseDBValueService;
    private final ObjectMapper objectMapper;

    // Cache for storing annotated fields to avoid expensive scans on refresh
    private final List<CachedField> cachedFields = new ArrayList<>();

    private static final Map<Class<?>, Function<String, Object>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(String.class, s -> s);
        CONVERTERS.put(Integer.class, Integer::parseInt);
        CONVERTERS.put(int.class, Integer::parseInt);
        CONVERTERS.put(Boolean.class, Boolean::parseBoolean);
        CONVERTERS.put(boolean.class, Boolean::parseBoolean);
        CONVERTERS.put(Long.class, Long::parseLong);
        CONVERTERS.put(long.class, Long::parseLong);
        CONVERTERS.put(Double.class, Double::parseDouble);
        CONVERTERS.put(double.class, Double::parseDouble);
        CONVERTERS.put(Float.class, Float::parseFloat);
        CONVERTERS.put(float.class, Float::parseFloat);
        CONVERTERS.put(Short.class, Short::parseShort);
        CONVERTERS.put(short.class, Short::parseShort);
        CONVERTERS.put(Byte.class, Byte::parseByte);
        CONVERTERS.put(byte.class, Byte::parseByte);
    }

    @Autowired
    private Environment env;

    public ItHouseDBValueInjector(ItHouseDBValueService itHouseDBValueService, ObjectMapper objectMapper) {
        this.itHouseDBValueService = itHouseDBValueService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        populateCacheAndInject();
    }

    /**
     * Scans all beans for @ItHouseDBValue annotations, populates the cache,
     * and performs initial injection.
     */
    private void populateCacheAndInject() {
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Object bean = context.getBean(beanName);
                processBean(bean, true);
            } catch (Exception e) {
                // Skip beans that cannot be instantiated or processed
            }
        }
    }

    /**
     * Re-injects values into cached fields only.
     * Extremely efficient compared to a full bean scan.
     */
    public void refresh() {
        for (CachedField cachedField : cachedFields) {
            injectValue(cachedField.targetBean, cachedField.field, cachedField.annotation);
        }
    }

    private void processBean(Object bean, boolean caching) {
        Object targetBean = AopProxyUtils.getSingletonTarget(bean);
        if (targetBean == null) {
            targetBean = bean;
        }

        for (Field field : targetBean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ItHouseDBValue.class)) {
                ItHouseDBValue annotation = field.getAnnotation(ItHouseDBValue.class);
                if (caching) {
                    cachedFields.add(new CachedField(targetBean, field, annotation));
                }
                injectValue(targetBean, field, annotation);
            }
        }
    }

    private void injectValue(Object targetBean, Field field, ItHouseDBValue annotation) {
        String value = itHouseDBValueService.getConfigValue(
                annotation.configGroup(),
                annotation.configSubGroup(),
                env.resolvePlaceholders(annotation.defaultValue()));

        try {
            field.setAccessible(true);
            Object convertedValue = convert(value, field);
            field.set(targetBean, convertedValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject ItHouseDBValue for field: " + field.getName()
                    + " in bean " + targetBean.getClass().getSimpleName(), e);
        }
    }

    private Object convert(String value, Field field) throws Exception {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            return value;
        }

        if (List.class.isAssignableFrom(fieldType)) {
            return parseList(value, field);
        }

        if (Map.class.isAssignableFrom(fieldType)) {
            return parse2Map(value, field);
        }

        return objectMapper.convertValue(value, fieldType);
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
        Class<?> keyType = (Class<?>) pt.getActualTypeArguments()[0];
        Class<?> valueType = (Class<?>) pt.getActualTypeArguments()[1];

        try {
            return objectMapper.readValue(rawValue,
                    objectMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType));
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
                map.put(castValue(entry[0], keyType), castValue(entry[1], valueType));
            }
            return map;
        }
    }

    private Object castValue(String value, Class<?> type) {
        if (value == null) {
            return null;
        }

        String str = value.trim();
        Function<String, Object> converter = CONVERTERS.get(type);

        if (converter != null) {
            return converter.apply(str);
        }

        return str;
    }

    /**
     * Internal class to hold cached field information.
     */
    private static record CachedField(Object targetBean, Field field, ItHouseDBValue annotation) {
    }
}
