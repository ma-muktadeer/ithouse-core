package com.ihouse.core.message.interfaces;

import com.ihouse.core.message.AbstractMessageHeader;

public interface Message<T> {
    AbstractMessageHeader getHeader();

    T getPayload();
}
