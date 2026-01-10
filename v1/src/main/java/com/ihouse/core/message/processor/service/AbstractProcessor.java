package com.ihouse.core.message.processor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ihouse.core.message.GenericMessage;
import com.ihouse.core.message.GenericMessageHeader;
import com.ihouse.core.message.interfaces.Message;
import com.ihouse.core.message.processor.interfaces.GsonProcessor;
import com.ihouse.core.message.processor.interfaces.JsonProcessor;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbstractProcessor implements GsonProcessor, JsonProcessor {
    private static final Logger log = LogManager.getLogger(AbstractProcessor.class);

    Map<String, String> classMap;

    ObjectMapper mapper;

    public AbstractProcessor() {
        this.mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String toJson(Message<?> message) throws Exception {
        Validate.notNull(message, "Exception Making Json with null Object.", new Object[0]);

        return this.mapper.writeValueAsString(message);
    }

    @Override
    public String toJson(Object obj) throws Exception {
        Validate.notNull(obj, "Exception Making Json with null Object.", new Object[0]);

        return this.mapper.writeValueAsString(obj);
    }

    @Override
    public <T> Message<T> fromJson(String json) throws Exception {
        Validate.notNull(json, "Exception Making Json with null Object.", new Object[0]);

        return this.doFromJson(json);
    }

    public List<?> fromJson(String arg0, Class<?> arg1) throws Exception {
        Validate.notNull(arg0, "Exception Making Json with null object.", new Object[0]);
        return this.doFromJson(arg0, arg1);
    }


    public Object fromJsonSingle(String arg0, Class<?> arg1) throws JsonProcessingException {
        return this.mapper.treeToValue(mapper.readTree(arg0), arg1);
    }

    public Object fromJson(JsonNode jsonObject, Class<?> clazz) throws Exception {
        return this.mapper.treeToValue(jsonObject, clazz);
    }


    public List<Object> fromJsonMulti(String arg0, Class<?> arg1) throws Exception {

        JsonNode jsonNodeList = mapper.readTree(arg0);
        List<Object> objectList = new LinkedList<>();
        if (jsonNodeList.isArray()) {
            ArrayNode nodes = (ArrayNode) jsonNodeList;
            for (JsonNode node : nodes) {
                objectList.add(mapper.treeToValue(node, arg1));
            }

        }
        return objectList;
    }

    public ArrayNode getJsonArray(String json) throws Exception {
        return (ArrayNode) mapper.readTree(json);
    }

    public ArrayNode getJsonArray(String json, String memberName) throws Exception {
        JsonNode rootNode = mapper.readTree(json);
        return this.findArrayNode(rootNode, memberName);

    }

    private ArrayNode findArrayNode(JsonNode rootNode, String memberName) {
        JsonNode arrayNode = rootNode.get(memberName);
        if (arrayNode != null && arrayNode.isArray()) {
            return (ArrayNode) arrayNode;
        } else {
            return null;
        }
    }

    public ArrayNode getJsonArray(JsonNode obj, String memberName) throws Exception {
        return this.findArrayNode(obj, memberName);
    }

    @Override
    public Map<String, String> getClassMap() {
        return this.classMap;
    }

    @Override
    public void setClassMap(Map<String, String> classMap) {
        this.classMap = classMap;
    }

    @Override
    public void addToClassMap(String key, String value) {
        this.classMap.put(key, value);
    }


    @SuppressWarnings("unchecked")
    private <T> Message<T> doFromJson(String json) throws JsonProcessingException, ClassNotFoundException {
        GenericMessageHeader messageHeader = null;
        GenericMessage<Object> message = new GenericMessage<>();

        log.debug("Processing json object: {}", json);
        JsonNode rootNode = mapper.readTree(json);

        JsonNode headerNode = rootNode.get("header");
        messageHeader = mapper.treeToValue(headerNode, GenericMessageHeader.class);

        this.validRequest(messageHeader);
        message.setHeader(messageHeader);
        String contentType = messageHeader.getContentType();
        Validate.notNull((String) this.classMap.get(contentType), "Object type [" + contentType + "] not included in classMap.", new Object[0]);

        JsonNode array = rootNode.get("payload");
        if (array == null) {
            message.setPayload((Object) null);
        } else {
            List<Object> objectList = new LinkedList<>();
            Class<?> clazz = Class.forName((String) this.classMap.get(contentType));

            for (int i = 0; i < array.size(); i++) {
                objectList.add(this.mapper.treeToValue(array.get(i), clazz));
            }
            message.setPayload(objectList);
        }

        return (Message<T>) message;
    }

    private void validRequest(GenericMessageHeader messageHeader) {
        this.doValidRequest(messageHeader);
    }

    private void doValidRequest(GenericMessageHeader header) {
        Validate.notNull(header.getActionType(), "No action type provided.", new Object[0]);
        Validate.notNull(header.getContentType(), "No content type provided.", new Object[0]);
    }

    private List<Object> doFromJson(String arg0, Class<?> arg1) throws Exception {
        if (arg0 != null && arg1 != null) {
            log.debug("Processing json : {}", arg0);
            JsonNode jsonObj = this.getJsonObject(arg0);
            JsonNode payloadNode = jsonObj.get("payload");
            List<Object> obList = new LinkedList<>();

            if (payloadNode != null) {
                if (payloadNode.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) payloadNode;

                    for (JsonNode node : arrayNode) {
                        Object obj = mapper.treeToValue(node, arg1);
                        obList.add(obj);
                    }
                } else if (payloadNode.isObject()) {
                    obList.add(mapper.treeToValue(payloadNode, arg1));
                }
            }
            return obList;

        } else {
            return null;
        }
    }

    private JsonNode getJsonObject(String arg) throws JsonProcessingException {
        return mapper.readTree(arg);
    }


}
