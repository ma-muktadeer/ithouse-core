package com.ihouse.core.message.interfaces;

public interface Coordinator {
    String STR_SERVICE = "Service";

    Service<?> getServiceByName(String name) throws Exception;

    Message<?> service(Message<?> message) throws Exception;
}
