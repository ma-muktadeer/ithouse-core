package com.ithouse.core.message.interfaces;

public interface Coordinator {

    Service<?> getServiceByName(String name) throws Exception;

    Message<?> service(Message<?> message, Boolean isPublic) throws Exception;

}
