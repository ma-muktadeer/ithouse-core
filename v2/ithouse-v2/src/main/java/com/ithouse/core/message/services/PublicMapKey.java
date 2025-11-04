package com.ithouse.core.message.services;

import java.util.Objects;

public class PublicMapKey {
    private final String actionType;
    private final String contentType;

    public PublicMapKey(String actionType, String contentType) {
        this.actionType = actionType;
        this.contentType = contentType;
    }

    public String getActionType() {
        return actionType;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicMapKey that)) return false;
        return Objects.equals(actionType, that.actionType) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, contentType);
    }

}
