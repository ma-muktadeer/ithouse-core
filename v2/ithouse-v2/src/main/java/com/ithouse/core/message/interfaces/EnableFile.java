package com.ithouse.core.message.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//public interface EnableFile {
//    String getEntityType();
//    void setFiles(List<MultipartFile> files);
//    List<MultipartFile> getFiles();
//
//}

public interface EnableFile<T> {
    List<T> getItems();
    void setFiles(List<MultipartFile> files);
}
