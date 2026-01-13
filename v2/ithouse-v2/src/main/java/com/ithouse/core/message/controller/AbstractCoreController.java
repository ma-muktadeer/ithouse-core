package com.ithouse.core.message.controller;

import com.ithouse.core.message.AbstractMessageHeader;
import com.ithouse.core.message.interfaces.EnableFile;
import com.ithouse.core.message.interfaces.Message;
import com.ithouse.core.message.processor.services.ProcessorService;
import com.ithouse.core.message.services.ServiceCoordinator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public abstract class AbstractCoreController {
    @Autowired
    protected ServiceCoordinator serviceCoordinator;

    @Autowired
    protected ProcessorService processorService;

    @GetMapping("/getRequest")
    public Message<?> handleGetRequest(AbstractMessageHeader messageHeader, HttpServletRequest request)
            throws Exception {
        return serviceCoordinator.service(messageHeader, processorService.findPayloadClass(messageHeader.getContentType()), false);
    }

    @PostMapping("/jsonRequest")
    public Message<?> handlePostRequest(AbstractMessageHeader messageHeader, EnableFile<?> enableFile, HttpServletRequest request) throws Exception {
        Message<?> message = processorService.buildMessage(enableFile, messageHeader);
        return serviceCoordinator.service(message, false);
    }

    @PostMapping(value = "/submit-form", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Message<?> handleRequisition(AbstractMessageHeader messageHeader, EnableFile<?> enableFile, HttpServletRequest request) throws Exception {

        Message<?> message = processorService.buildMessage(enableFile, messageHeader);
        return serviceCoordinator.service(message, false);
    }
//
//    private void enrichHeaderWithIp(AbstractMessageHeader header, HttpServletRequest request) {
//        if (header != null) {
//            header.setSenderSourceIPAddress(request.getRemoteAddr());
//            header.setSenderGatewayIPAddress(request.getHeader(VIA));
//        }
//    }
}
