package com.ihouse.core.message;

import com.ihouse.core.message.interfaces.Message;

public abstract class AbstractMessage<T> implements Message<T> {
    private T payload;
    private AbstractMessageHeader header;

    public AbstractMessage() {}

    public AbstractMessageHeader getHeader() {
        return this.header;
    }

    public void setHeader(AbstractMessageHeader header) {
        this.header = header;
    }

    public T getPayload() { return this.payload; }

    public void setPayload(T payload) { this.payload = payload; }

}
