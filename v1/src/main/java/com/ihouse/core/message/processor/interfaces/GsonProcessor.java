package com.ihouse.core.message.processor.interfaces;

import com.ihouse.core.message.interfaces.Message;

import java.util.Map;

public interface GsonProcessor {
    <T>Message<T> fromJson(String json) throws Exception;

    Map<String, String> getClassMap();

    void setClassMap(Map<String, String> classMap);

    void addToClassMap(String key, String value);
}
