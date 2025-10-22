package com.ithouse.core.message.processor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ithouse.core.message.GenericMessage;
import com.ithouse.core.message.GenericMessageHeader;
import com.ithouse.core.message.interfaces.Message;
import com.ithouse.core.message.processor.interfaces.JsonProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractProcessor implements JsonProcessor {

    private static final Logger logger = LogManager.getLogger(AbstractProcessor.class);
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();
    Map<String, String> classMap;

    private final ObjectMapper mapper;

    protected AbstractProcessor() {
        mapper = createDefaultMapper();
    }

    public void setClassMap(Map<String, String> classMap) {
        this.classMap = classMap;
    }

    protected AbstractProcessor(ObjectMapper mapper) {
//        this.mapper = mapper1;

        this.mapper = mapper != null ? mapper : createDefaultMapper();
    }

    protected ObjectMapper createDefaultMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Override
    public String toJson(Message<?> message) throws Exception {
        Objects.requireNonNull(message, "Json message cannot be null");

        return mapper.writeValueAsString(message);
    }

    @Override
    public String toJson(Object obj) throws Exception {
        Objects.requireNonNull(obj, "Json message cannot be null");

        return mapper.writeValueAsString(obj);
    }

    @Override
    public <T> Message<T> fromJson(String json) throws Exception {
        Objects.requireNonNull(json, "Json message cannot be null");

        return doFormJson(json);
    }

    @SuppressWarnings("unchecked")
    private <T> Message<T> doFormJson(String json) throws JsonProcessingException {
        GenericMessage<T> message = new GenericMessage<T>();
        logger.debug("Processing JSON object: {}", json);

        final JsonNode rootNode = mapper.readTree(json);

        final JsonNode headerNode = rootNode.path("header");

        if (headerNode.isMissingNode() || headerNode.isNull()) {
            logger.error("Json object is missing header");
            throw new IllegalArgumentException("Missing 'header' in JSON message'");
        }

        final GenericMessageHeader header = mapper.treeToValue(headerNode, GenericMessageHeader.class);

        validRequest(header);

        message.setHeader(header);

        final String contentType = header.getContentType();
        final String className = classMap.get(contentType);
        Objects.requireNonNull(className, String.format("Object type '%s' not include in the class map", contentType));

        final Class<?> payloadClass = resolvePayloadClass(contentType, className);
        final JsonNode payloadNode = rootNode.path("payload");
        if (payloadNode.isMissingNode() || payloadNode.isNull()) {
            message.setPayload(null);
            return (Message<T>) message;
        }

        if (payloadNode.isArray()) {
            final JavaType listType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, payloadClass);
            final List<T> payloadList = mapper.convertValue(payloadNode, listType);
            message.setPayload((T) payloadList);
        } else {
            final T payload = mapper.treeToValue(payloadNode, (Class<? extends T>) payloadClass);
            message.setPayload((T) Collections.singletonList(payload));
        }
        return message;
    }

    private Class<?> resolvePayloadClass(String contentType, String className) {
        return classCache.computeIfAbsent(contentType, key -> {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Unknown class for content type: " + key, e);
            }
        });
    }

    private void validRequest(GenericMessageHeader messageHeader) {
        Objects.requireNonNull(messageHeader, "Message header is null");
        Objects.requireNonNull(messageHeader.getActionType(), "No action type is provided");
        Objects.requireNonNull(messageHeader.getContentType(), "No content type is provided");
    }


    @Override
    public Map<String, String> getClassMap(String classMap) {
        return Map.of();
    }

    @Override
    public void addToClassMap(String key, String value) {

    }

}
