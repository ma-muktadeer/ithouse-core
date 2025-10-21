package com.ihouse.core.message;

import com.ihouse.core.message.interfaces.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ResponseBuilder<T> {
    public ResponseBuilder() {}

    public static <T> Message<T> buildResponse(AbstractMessageHeader header, T payload) throws Exception {
        return doBuildResponse(header, payload);
    }

    private static <T> Message<T> doBuildResponse(AbstractMessageHeader header, T payload) throws Exception {
        GenericMessage<T> message = new GenericMessage<>();
        message.setHeader(header);
        header.setStatus("OK");
        message.setPayload(payload);

        return message;
    }

    public static <T> Message<T> buildErrorResponse(AbstractMessageHeader header, Exception ex) throws Exception {
        return doBuildErrorResponse(header, ex);
    }

    private static <T> Message<T> doBuildErrorResponse(AbstractMessageHeader header, Exception ex) throws Exception {
        GenericMessage<T> message = new GenericMessage<>();
        String error = ex.getMessage();
        if (error == null) {
            error = "";
        }

        if (error.contains("#")) {
            header.setErrorMsg(error.substring(0, error.indexOf("#") + 1));
        }else{
            header.setErrorMsg(error);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("stackTrace", ex.getStackTrace());
        header.setExtraInfoMap(map);
        header.setStatus("ERROR");
        message.setHeader(header);
        message.setPayload(null);
        return message;

    }

    private static <T> ResponseBuilder<T> buildHeader(AbstractMessageHeader header, T payload) throws Exception {
        Objects.requireNonNull(header);
        return new ResponseBuilder<T>();
    }
}
