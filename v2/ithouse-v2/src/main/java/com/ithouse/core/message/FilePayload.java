package com.ithouse.core.message;

import com.ithouse.core.message.interfaces.EnableFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FilePayload<T> implements EnableFile<T> {

    private List<T> items;
    private List<MultipartFile> files;

    @Override
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }
}

