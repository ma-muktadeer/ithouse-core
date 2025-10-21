package com.ithouse.core.message;

import com.ithouse.core.message.interfaces.Message;

public abstract class AbstractMessage<T> implements Message<T> {
    private T payload;
    private AbstractMessageHeader header;

    public void setHeader(AbstractMessageHeader header) {
        this.header = header;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public AbstractMessageHeader getHeader() {
        return header;
    }

    @Override
    public T getPayload() {
        return payload;
    }
}
