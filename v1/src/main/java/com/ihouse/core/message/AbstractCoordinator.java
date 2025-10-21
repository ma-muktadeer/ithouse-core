package com.ihouse.core.message;

import com.ihouse.core.message.interfaces.Coordinator;
import com.ihouse.core.message.interfaces.Message;
import com.ihouse.core.message.interfaces.Service;
import com.ihouse.core.message.service.ServiceMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AbstractCoordinator implements Coordinator {
    private static final Logger log = LogManager.getLogger(AbstractCoordinator.class);

    private ServiceMap serviceMap;

    public AbstractCoordinator() {}

    public ServiceMap getServiceMap() { return this.serviceMap; }
    public void setServiceMap(ServiceMap serviceMap) { this.serviceMap = serviceMap; }

    @Override
    public Service<?> getServiceByName(String name) throws Exception {
        Objects.requireNonNull(name, "Service name is null");

        if(this.serviceMap.getServiceMap().containsKey(name+"Service")){
            return this.serviceMap.getServiceMap().get(name + "Service");
        }else {
            log.info("Service not map in ServiceMap class. [{}]", name + "Service");
            return null;
        }
    }

    @Override
    public Message<?> service(Message<?> message) throws Exception {
        Validate.notNull(message, "Message can not be null");
        return this.getServiceByName(message.getHeader().getContentType()).ithouseService(message);
    }
}
