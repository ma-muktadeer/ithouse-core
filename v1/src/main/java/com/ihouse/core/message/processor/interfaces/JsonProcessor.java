package com.ihouse.core.message.processor.interfaces;

import com.ihouse.core.message.interfaces.Message;

public interface JsonProcessor extends GsonProcessor{

    String toJson(Message<?> message) throws Exception;

    String toJson(Object obj) throws Exception;
}
