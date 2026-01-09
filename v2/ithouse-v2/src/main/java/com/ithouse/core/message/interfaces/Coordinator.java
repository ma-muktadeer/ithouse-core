package com.ithouse.core.message.interfaces;

import com.ithouse.core.message.AbstractMessageHeader;

public interface Coordinator {

    Service<?> getServiceByName(String name) throws Exception;

    Message<?> service(Message<?> message, Boolean isPublic) throws Exception;

    Message<?> service(AbstractMessageHeader header, Class<?> payloadClass, Boolean isPublic) throws Exception;

}
