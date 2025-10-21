package com.ithouse.core.message.interfaces;

import com.ithouse.core.message.AbstractMessageHeader;

public interface Message<T> {
    AbstractMessageHeader getHeader();
    T getPayload();
}
