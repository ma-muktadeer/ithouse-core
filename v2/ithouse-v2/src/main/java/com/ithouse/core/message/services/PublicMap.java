package com.ithouse.core.message.services;

import com.ithouse.core.message.interfaces.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PublicMap {

   private PublicMapKey key;

   private Service<?> service;

   private final Map<PublicMapKey, Service<?>> map = new LinkedHashMap<>();

    public PublicMap() {
    }

    public PublicMap(String actionType, String contentType, Service<?> service) {
        this.key = new PublicMapKey(actionType, contentType);
        this.service = service;
        map.put(key, service);
    }

    public Service<?> getService() {
        return service;
    }

    public void setService(Service<?> service) {
        this.service = service;
    }

    protected Map<PublicMapKey, Service<?>> getMap(){
        return Collections.unmodifiableMap(map);
    }
}
