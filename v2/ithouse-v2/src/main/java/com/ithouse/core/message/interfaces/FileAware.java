package com.ithouse.core.message.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileAware {
    void setFiles(List<MultipartFile> files);
}

