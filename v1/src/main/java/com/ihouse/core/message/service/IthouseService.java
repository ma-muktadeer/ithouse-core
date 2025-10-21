package com.ihouse.core.message.service;

import com.ihouse.core.jdbc.service.JdbcService;
import com.ihouse.core.message.interfaces.Service;

public abstract class IthouseService<T> implements Service<T> {
    private JdbcService jdbcService;

    public IthouseService() {
    }

    public JdbcService getJdbcService() {
        return this.jdbcService;
    }

    public void setJdbcService(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }
}
