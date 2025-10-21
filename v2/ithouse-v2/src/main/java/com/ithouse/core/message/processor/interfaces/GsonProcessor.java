package com.ithouse.core.message.processor.interfaces;

import com.ithouse.core.message.interfaces.Message;

import java.util.Map;

public interface GsonProcessor {
    <T> Message<T> fromJson(String json) throws Exception;

    Map<String, String> getClassMap(String classMap);

    void addToClassMap(String key, String value);
}
