package com.ihouse.core.jdbc.abstracts;

import com.ihouse.core.jdbc.interfaces.IJdbcStatement;
import com.ihouse.core.jdbc.service.JdbcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Map;

public abstract class AbstractJdbcStatement implements IJdbcStatement {
    private static final Logger log = LogManager.getLogger(AbstractJdbcStatement.class);

    private JdbcService jdbcService;
    private Connection conn;
    private boolean initialised;
    private String schema;
    private String sql;
    private Map<String, String> sql2BeanMap;
    private Map<String, Object> sqlArgsMap;
    private String preparedSql;

    public AbstractJdbcStatement() {
    }

    public AbstractJdbcStatement(JdbcService jdbcService, Connection conn, String schema, String sql) {
        this.jdbcService = jdbcService;
        this.conn = conn;
        this.schema = schema;
        this.sql = sql;
    }

    public JdbcService getJdbcService() {
        return this.jdbcService;
    }

    public void setJdbcService(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }

    protected Connection getConn() {
        return this.conn;
    }

    protected void setConn(Connection conn) {
        this.conn = conn;
    }

    protected boolean isInitialised() {
        return this.initialised;
    }

    protected void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected String getSchema() {
        return this.schema;
    }

    protected void setSchema(String schema) {
        this.schema = schema;
    }

    protected String getSql() {
        return this.sql;
    }

    protected void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, String> getSql2BeanMap() {
        return this.sql2BeanMap;
    }

    public void setSql2BeanMap(Map<String, String> sql2BeanMap) {
        this.sql2BeanMap = sql2BeanMap;
    }

    public Map<String, Object> getSqlArgsMap() {
        return this.sqlArgsMap;
    }

    public void setSqlArgsMap(Map<String, Object> sqlArgsMap) {
        this.sqlArgsMap = sqlArgsMap;
    }

    public String getPreparedSql() {
        return this.preparedSql;
    }

    protected void setPreparedSql(String preparedSql) {
        this.preparedSql = preparedSql;
    }
}
