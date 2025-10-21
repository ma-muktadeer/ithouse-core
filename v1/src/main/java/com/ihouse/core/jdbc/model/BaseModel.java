package com.ihouse.core.jdbc.model;

import java.util.Date;

public class BaseModel extends BaseSimpleModel{
    private Integer active = 1;
    private Integer envId;
    private Date dateModified;
    private Integer userIdModified;
    private String userNameModified;
    private Integer eventId;
    private Integer storeId;
    private Integer languageId;

    public BaseModel() {
    }

    public Integer getActive() {
        return this.active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getEnvId() {
        return this.envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getUserIdModified() {
        return this.userIdModified;
    }

    public void setUserIdModified(Integer userIdModified) {
        this.userIdModified = userIdModified;
    }

    public String getUserNameModified() {
        return this.userNameModified;
    }

    public void setUserNameModified(String userNameModified) {
        this.userNameModified = userNameModified;
    }

    public Integer getStoreId() {
        return this.storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }
}
