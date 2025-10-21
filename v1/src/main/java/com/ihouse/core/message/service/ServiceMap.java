package com.ihouse.core.message.service;

import com.ihouse.core.message.interfaces.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServiceMap {
    private static Logger log = Logger.getLogger(ServiceMap.class.getName());
    private Map<String, Service<?>> serviceMap = new LinkedHashMap<String, Service<?>>();

    public ServiceMap() {}

    public Map<String, Service<?>> getServiceMap() { return this.serviceMap; }

    public void setServiceMap(Map<String, Service<?>> serviceMap) { this.serviceMap = serviceMap; }
}
