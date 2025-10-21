package com.ithouse.core.message;

import com.ithouse.core.message.interfaces.Coordinator;
import com.ithouse.core.message.interfaces.Message;
import com.ithouse.core.message.interfaces.Service;
import com.ithouse.core.message.services.ServiceMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AbstractCoordinator implements Coordinator {
    private static final Logger  logger = LogManager.getLogger(AbstractCoordinator.class);

    private final static String SERVICE = "Service";
    private ServiceMap serviceMap;

    public ServiceMap getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(ServiceMap serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public Service<?> getServiceByName(String name) throws Exception {
        Objects.requireNonNull(name, "Service name is null");

        if(serviceMap.getServiceMap().containsKey(name + SERVICE)) {
            return serviceMap.getServiceMap().get(name + SERVICE);
        }
        logger.error("Service not found in serviceMap=>{}",name);
        return null;
    }

    @Override
    public Message<?> service(Message<?> message) throws Exception {
        return getServiceByName(message.getHeader().getContentType()).itHouseService(message);
    }
}
