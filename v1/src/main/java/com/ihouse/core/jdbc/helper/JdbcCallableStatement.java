package com.ihouse.core.jdbc.helper;

import com.ihouse.core.jdbc.abstracts.AbstractJdbcStatement;
import com.ihouse.core.jdbc.constants.JdbcConstants;
import com.ihouse.core.jdbc.service.JdbcService;
import com.ihouse.core.jdbc.utils.DateUtils;
import com.ihouse.core.jdbc.utils.DbType;
import com.ihouse.core.jdbc.utils.JdbcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class JdbcCallableStatement extends AbstractJdbcStatement {

    private static final Logger log = LogManager.getLogger(JdbcCallableStatement.class);

    private JdbcStoredProcedure jdbcStoredProcedure;
    private CallableStatement cstmt;

    public JdbcCallableStatement() {
    }

    public JdbcCallableStatement(JdbcService jdbcService, Connection conn, String schema, String sql) {
        super(jdbcService, conn, schema, sql);
    }

    public void setJdbcStoredProcedure(JdbcStoredProcedure jdbcStoredProcedure) {
        this.jdbcStoredProcedure = jdbcStoredProcedure;
    }

    public JdbcStoredProcedure getJdbcStoredProcedure() {
        return this.jdbcStoredProcedure;
    }

    public boolean initialise(Map<String, Object> sqlArgsMap) throws Exception {
        log.debug("Initilising...");
        if (!this.isInitialised()) {
            if (this.jdbcStoredProcedure == null) {
                log.info("Getting Stored Proc definition - > " + this.getSql());
                this.jdbcStoredProcedure = this.getJdbcService().getJdbcStoredProcedure(super.getConn(), this.getSchema(), this.getSql());
            }

            Map<String, JdbcParameter> spOutParams = this.jdbcStoredProcedure.getSpOutputParamMap();
            String preparedSql;
            if (spOutParams != null) {

                for (String string : spOutParams.keySet()) {
                    preparedSql = string;
                    if (!sqlArgsMap.containsKey(preparedSql)) {
                        log.debug("Auto Adding output Param {}", preparedSql);
                        sqlArgsMap.put(preparedSql, (Object) null);
                    }
                }
            }

            sqlArgsMap = JdbcUtils.validateSqlArgs(this.jdbcStoredProcedure.getSpParamMap(), sqlArgsMap);
            preparedSql = null;
            String schema = this.getSchema();
            if (schema != null) {
                preparedSql = this.getSchema() + "." + this.getSql();
            } else {
                preparedSql = this.getSql();
            }

            String dbType = this.getConn().getMetaData().getDatabaseProductName();
            if (sqlArgsMap != null) {
                this.setSqlArgsMap(sqlArgsMap);
                if (dbType.equals(DbType.MySQL.toString())) {
                    preparedSql = JdbcUtils.generateSqlTemplate(preparedSql, this.jdbcStoredProcedure.getSpParamMap().size());
                } else {
                    preparedSql = JdbcUtils.generateSqlTemplate(preparedSql, sqlArgsMap.size());
                }
            }

            this.setPreparedSql(preparedSql);
            if (dbType.equals(DbType.MySQL.toString())) {
                if (!preparedSql.toUpperCase().startsWith("CALL")) {
                    preparedSql = "CALL " + preparedSql;
                }

                this.cstmt = this.getConn().prepareCall(preparedSql);
            } else {
                this.cstmt = this.getConn().prepareCall(preparedSql);
            }

            log.debug("Preparing Call");
            this.setInitialised(true);
            log.debug("Initilised");
        }

        return this.isInitialised();
    }

    public Statement execute(Map<String, Object> sqlArgsMap) throws Exception {
        this.initialise(sqlArgsMap);
        if (sqlArgsMap != null && sqlArgsMap.size() > 0) {
            StringBuilder sb = new StringBuilder();
            if (log.isInfoEnabled()) {
                sb.append(this.getPreparedSql()).append(JdbcConstants.JDBC_NEW_LINE);
                sb.append(this.getSql()).append(" ");
            }

            this.cstmt.clearParameters();
            Map<String, JdbcParameter> spParamMap = this.jdbcStoredProcedure.getSpParamMap();
            JdbcParameter spParam = null;
            String key = null;
            int paramCounter = 1;
            Iterator var8 = spParamMap.entrySet().iterator();

            while(true) {
                do {
                    if (!var8.hasNext()) {
                        log.info("Executing {}", sb);
                        this.cstmt.execute();
                        return this.cstmt;
                    }

                    Map.Entry entry = (Map.Entry)var8.next();
                    spParam = (JdbcParameter)entry.getValue();
                    key = (String)entry.getKey();
                } while(!sqlArgsMap.containsKey(key));

                Object value = sqlArgsMap.get(key);
                log.debug("Mapping {}", key);
                if (spParam.getTypeName().equals("date") && value != null && spParam.getDataType() == 12) {
                    value = JdbcUtils.escapeSql(DateUtils.DF_yyyy_MM_dd_HH_mm_ss.format((Date)value));
                } else if (spParam.getDataType() == 91 && value != null) {
                    value = new java.sql.Date(((Date)value).getTime());
                } else if (spParam.getDataType() == 93 && value != null) {
                    value = new Timestamp(((Date)value).getTime());
                }

                this.cstmt.setObject(key, value, spParam.getDataType());
                if (log.isInfoEnabled()) {
                    this.buildLog(sb, spParam, value, paramCounter);
                }

                if (spParam.isOutput()) {
                    log.debug("registering {} as output param", key);
                    if (log.isInfoEnabled()) {
                        sb.append("--OUTPUT");
                    }

                    this.cstmt.registerOutParameter(key, spParam.getDataType());
                }

                ++paramCounter;
            }
        } else {
            return this.cstmt;
        }
    }

    private void buildLog(StringBuilder sb, JdbcParameter spParam, Object value, int paramCounter) {
        try {
            if (paramCounter == 1) {
                if (spParam.getDataType() != 4) {
                    sb.append("\n").append(spParam.getParamName()).append("=").append(value == null ? "" : "'").append(value).append(value == null ? "" : "'");
                } else {
                    sb.append("\n").append(spParam.getParamName()).append("=").append(value);
                }
            } else if (spParam.getDataType() != 4) {
                sb.append("\n").append(",").append(spParam.getParamName()).append("=").append(value == null ? "" : "'").append(value).append(value == null ? "" : "'");
            } else {
                sb.append("\n").append(",").append(spParam.getParamName()).append("=").append(value);
            }
        } catch (Exception var6) {
            Exception e = var6;
            log.error("Exception building log ", e);
        }

    }
}
