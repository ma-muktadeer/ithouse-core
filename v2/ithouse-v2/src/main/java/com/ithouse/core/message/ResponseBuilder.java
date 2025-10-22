package com.ithouse.core.message;

import com.ithouse.core.message.interfaces.Message;

import java.util.HashMap;
import java.util.Map;

public final class ResponseBuilder<T> {

    public static <T> Message<T> buildResponse(AbstractMessageHeader header, T payload) {
        return doBuildResponse(header, payload);
    }

    private static <T> Message<T> doBuildResponse(AbstractMessageHeader header, T payload) {
        GenericMessage<T> message = new GenericMessage<>();
        header.setStatus("OK");
        message.setHeader(header);
        message.setPayload(payload);
        return message;
    }

    public static <T> Message<T> buildErrorResponse(AbstractMessageHeader header, Exception ex) {
        return doBuildResponse(header, ex);
    }

    private static <T> Message<T> doBuildResponse(AbstractMessageHeader header, Exception ex) {
        GenericMessage<T> message = new GenericMessage<>();
        String error = ex.getMessage();
        if(error != null && error.contains("#")) {
            header.setErrorMsg(error.substring(0, error.indexOf("#")+1));
        }else {
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
}
