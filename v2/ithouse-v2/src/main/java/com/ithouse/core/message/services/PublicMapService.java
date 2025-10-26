package com.ithouse.core.message.services;

import com.ithouse.core.message.AbstractMessageHeader;
import com.ithouse.core.message.interfaces.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PublicMapService {

    Map<PublicMapKey, Service<?>> map = new LinkedHashMap<>();

    public void setMap(String actionType, String contentType, Service<?> service) {
        PublicMapKey key = new PublicMapKey(actionType, contentType);
        this.map.put(key, service);
    }

    private Map<PublicMapKey, Service<?>> getMap() {
        return Collections.unmodifiableMap(map);
    }

    //
//    public void setMap(Map<PublicMapKey, Service<?>> map) {
//        this.map = map;
//    }

    public Service<?> getService(PublicMapKey key) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(getMap(), "map is null");

        return getMap().get(key);
    }

    public Service<?> getService(AbstractMessageHeader header) {
        return getService(new PublicMapKey(header.getActionType(), header.getContentType()));
    }
}
