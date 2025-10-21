package com.ihouse.core.jdbc.model;


public class BaseSimpleModel {
    private Integer id;
    private Integer version;

    public BaseSimpleModel() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
