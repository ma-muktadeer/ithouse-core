package com.ithouse.core.message;

import com.ithouse.core.message.interfaces.Coordinator;
import com.ithouse.core.message.interfaces.EnablePagination;
import com.ithouse.core.message.interfaces.Message;
import com.ithouse.core.message.interfaces.Service;
import com.ithouse.core.message.services.PublicMapService;
import com.ithouse.core.message.services.ServiceMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
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
        Service<?> service = null;
        if (isPublicController) {
            Objects.requireNonNull(publicMapService, "PublicMapService is null");
            service = publicMapService.getService(message.getHeader());
        } else {
            service = getServiceByName(message.getHeader().getContentType());
        }

        Objects.requireNonNull(service, "Service name is null");

        return service.itHouseService(message);
    }


    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Message<?> service(AbstractMessageHeader header, Class<?> payloadClass, Boolean isPublicController) throws Exception {
        GenericMessage message = new GenericMessage();
        message.setHeader(header);
        message.setPayload(setDefaultPayload(payloadClass, header.getPageNumber(), header.getPageSize()));
        return service(message, isPublicController);
    }

    private <T> List<T> setDefaultPayload(Class<T> payloadClass, Integer pageNumber, Integer pageSize) {
        try {
            T payload = payloadClass.getDeclaredConstructor().newInstance();
            if (payload instanceof EnablePagination) {
                ((EnablePagination) payload).setPageNumber(pageNumber);
                ((EnablePagination) payload).setPageSize(pageSize);
            }
            return List.of(payload);
        } catch (Exception e) {
            throw new RuntimeException("Could not create default", e);
        }
    }

}
