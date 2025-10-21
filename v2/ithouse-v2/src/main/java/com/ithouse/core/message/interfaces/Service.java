package com.ithouse.core.message.interfaces;

public interface Service<T> {
    Message<T> itHouseService(Message<?> message) throws Exception;
}
