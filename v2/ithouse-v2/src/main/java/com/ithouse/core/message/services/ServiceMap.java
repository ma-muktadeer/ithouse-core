package com.ithouse.core.message.services;

import com.ithouse.core.message.interfaces.Service;

import java.util.Map;

public class ServiceMap {

    private Map<String, Service<?>> serviceMap;

    public Map<String, Service<?>> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, Service<?>> serviceMap) {
        this.serviceMap = serviceMap;
    }
}
