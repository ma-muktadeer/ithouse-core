package com.ithouse.core.message;

import com.ithouse.core.message.interfaces.Coordinator;
import com.ithouse.core.message.interfaces.Message;
import com.ithouse.core.message.interfaces.Service;
import com.ithouse.core.message.services.PublicMapService;
import com.ithouse.core.message.services.ServiceMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AbstractCoordinator implements Coordinator {
    private static final Logger logger = LogManager.getLogger(AbstractCoordinator.class);
    private final static String SERVICE = "Service";
    private ServiceMap serviceMap;
    private PublicMapService publicMapService;

    public void setServiceMap(ServiceMap serviceMap) {
        this.serviceMap = serviceMap;
    }

    public void setPublicMapService(PublicMapService publicMapService) {
        this.publicMapService = publicMapService;
    }

    @Override
    public Service<?> getServiceByName(String name) throws Exception {
        Objects.requireNonNull(name, "Service name is null");

        if (serviceMap.getServiceMap().containsKey(name + SERVICE)) {
            return serviceMap.getServiceMap().get(name + SERVICE);
        }
        logger.error("Service not found in serviceMap=>{}", name);
        return null;
    }

    @Override
    public Message<?> service(Message<?> message, Boolean isPublicController) throws Exception {
//        if (isPublic && (publicMapService.getMap().isEmpty() || !publicMapService.getMap().containsKey(message.getHeader().getActionType()))) {
//            logger.error("Service not map in the public action=>{}", message.getHeader().getActionType());
//            throw new Exception("Service not map in the public action=>" + message.getHeader().getActionType());
//        }
        Service<?> service = null;
        if (isPublicController) {
            service = publicMapService.getService(message.getHeader());
        } else {
            service = getServiceByName(message.getHeader().getContentType());
        }

        Objects.requireNonNull(service, "Service name is null");

//publicMapService.getMap()

        return service.itHouseService(message);
    }
}
