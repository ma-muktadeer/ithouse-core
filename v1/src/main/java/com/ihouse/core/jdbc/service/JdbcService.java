package com.ihouse.core.jdbc.service;

import com.ihouse.core.jdbc.enums.JdbcStatementType;
import com.ihouse.core.jdbc.factory.JdbcStatementFactory;
import com.ihouse.core.jdbc.helper.JdbcResult;
import com.ihouse.core.jdbc.helper.JdbcStoredProcedure;
import com.ihouse.core.jdbc.interfaces.IJdbcStatement;
import com.ihouse.core.jdbc.utils.JdbcUtils;
import com.ihouse.core.jdbc.utils.NamedParamStatement;
import com.ihouse.core.security.Security;
import com.ihouse.core.security.SecurityProvider;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcService extends JdbcAccessor {
    private static final Logger log = LogManager.getLogger(JdbcService.class);

    private NamedParameterJdbcTemplate jdbcTempplace;
    private AtomicInteger connCount = new AtomicInteger();
    private PlatformTransactionManager transactionManager;
    private JdbcStatementFactory jdbcStatementFactory;
    private boolean cacheStoredProcedures = true;
    private Map<String, JdbcStoredProcedure> jdbcStoredProcedureCache;
    private final Security security = new SecurityProvider();

    public JdbcService() {
    }


    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public JdbcStatementFactory getJdbcStatementFactory() {
        return this.jdbcStatementFactory;
    }

    public void setJdbcStatementFactory(JdbcStatementFactory jdbcSF) {
        this.jdbcStatementFactory = jdbcSF;
    }

    public Connection getConnection() throws Exception {
        if (this.getDataSource() instanceof BasicDataSource) {
            BasicDataSource dataSource = (BasicDataSource)this.getDataSource();
            if (dataSource.getUsername().startsWith("ENC[")) {
                dataSource.setUsername(this.security.decrypt(this.security.get(dataSource.getUsername())));
            }

            if (dataSource.getPassword().startsWith("ENC[")) {
                dataSource.setPassword(this.security.decrypt(this.security.get(dataSource.getPassword())));
            }

            this.jdbcTempplace = new NamedParameterJdbcTemplate(dataSource);
            this.setDataSource(dataSource);
        }

        return DataSourceUtils.getConnection(this.getDataSource());
    }

    public void closeConnection(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }

    }

    public TransactionStatus beginTran() {
        if (this.transactionManager == null) {
            log.warn("null txn manager");
            return null;
        } else {
            TransactionDefinition txn = new DefaultTransactionDefinition();
            return this.transactionManager.getTransaction(txn);
        }
    }

    public void commitTran(TransactionStatus status) {
        this.commitTran(status, (JdbcResult)null);
    }

    public boolean commitTran(TransactionStatus txnStatus, JdbcResult jdbcResult) {
        boolean committed;
        if (this.transactionManager != null && txnStatus != null) {
            this.transactionManager.commit(txnStatus);
            committed = true;
        } else {
            log.warn("No Txn to commit!");
            committed = false;
        }

        if (jdbcResult != null && jdbcResult.isClearBufferOnCommitOrRollback()) {
            jdbcResult.getJsonMsgBuffer().setLength(0);
        }

        return committed;
    }

    public boolean rollbackTran(TransactionStatus txnStatus) {
        log.error("Rolling back Txn");
        boolean rolledBack;
        if (this.transactionManager != null && txnStatus != null) {
            this.transactionManager.rollback(txnStatus);
            rolledBack = true;
        } else {
            log.warn("No Txn to Rollback");
            rolledBack = false;
        }

        return rolledBack;
    }

    public boolean rollbackTran(TransactionStatus txnStatus, SQLException sqlEx, JdbcResult jdbcResult) {
        boolean rolledBack = false;
        JdbcUtils.printSqlExceptions(sqlEx);
        if (jdbcResult != null && jdbcResult.isClearBufferOnCommitOrRollback()) {
            jdbcResult.getJsonMsgBuffer().setLength(0);
        }

        return rolledBack;
    }

    public JdbcStoredProcedure getJdbcStoredProcedure(String spName) throws Exception {
        return this.getJdbcStoredProcedure((Connection)null, (String)null, spName);
    }

    public JdbcStoredProcedure getJdbcStoredProcedure(String schema, String spName) throws Exception {
        return this.getJdbcStoredProcedure((Connection)null, "dbo", spName);
    }

    public JdbcStoredProcedure getJdbcStoredProcedure(Connection conn, String schema, String spName) throws Exception {
        boolean createConn = false;
        JdbcStoredProcedure jdbcStoredProcedure = null;
        if (schema == null) {
            schema = "dbo";
        }

        String fullSpName = schema + "." + spName;
        log.info("Getting SP Definition -> " + fullSpName);
        if (this.jdbcStoredProcedureCache != null) {
            jdbcStoredProcedure = (JdbcStoredProcedure)this.jdbcStoredProcedureCache.get(fullSpName);
        }

        try {
            if (conn == null || conn.isClosed()) {
                createConn = true;
                conn = this.getConnection();
            }

            if (jdbcStoredProcedure == null) {
                jdbcStoredProcedure = JdbcUtils.getStoredProcMetadata(conn, schema, spName);
            }

            if (jdbcStoredProcedure != null && this.cacheStoredProcedures) {
                if (this.jdbcStoredProcedureCache == null) {
                    this.jdbcStoredProcedureCache = new LinkedHashMap();
                }

                this.jdbcStoredProcedureCache.put(fullSpName, jdbcStoredProcedure);
            }

            if (createConn) {
                this.closeConnection(conn);
                conn = null;
            }

            return jdbcStoredProcedure;
        } catch (SQLException var8) {
            SQLException sqlEx = var8;
            throw this.translateSqlException(sqlEx);
        }
    }

    private DataAccessException translateSqlException(SQLException sqlException) {
        return this.getExceptionTranslator().translate((String)null, (String)null, sqlException);
    }

    private DataAccessException translateSqlException(SQLException sqlException, String context, String sql) {
        return this.getExceptionTranslator().translate(context, sql, sqlException);
    }

    public Statement executeSP(String action, String schema, String spName, Map<String, Object> spArgsMap) throws Exception {
        return this.execute((Connection)null, action, schema, spName, JdbcStatementType.JDBC_CALLABLE_STATEMENT, spArgsMap);
    }

    public JdbcResult executeSP(String action, String schema, String spName, Map<String, Object> spArgsMap, JdbcResult jdbcResult) throws Exception {
        return this.executeSP((Connection)null, action, schema, spName, JdbcStatementType.JDBC_CALLABLE_STATEMENT, spArgsMap, jdbcResult);
    }

    public Statement execute(Connection conn, String action, String schema, String sql, JdbcStatementType jdbcStmtType, Map<String, Object> sqlArgsMap) throws Exception {
        Statement stmt = null;
        boolean connCreated = false;

        try {
            if (conn != null && (conn == null || !conn.isClosed())) {
                log.debug("***** Seem to have an active connection ***** ");
            } else {
                log.debug("***** Null connection getting connection ***** ");
                conn = this.getConnection();
                connCreated = true;
            }

            IJdbcStatement jdbcStatement = this.jdbcStatementFactory.createJDBCStatement(this, conn, jdbcStmtType, schema, sql);
            if (action != null && sqlArgsMap != null) {
                log.debug("Adding {} to sqlArgsMap [{}]", "@tx_action_name", action);
                sqlArgsMap.put("@tx_action_name", action);
            }

            stmt = jdbcStatement.execute(sqlArgsMap);
        } finally {
            if (connCreated) {
                try {
                    this.closeConnection(conn);
                } catch (SQLException var15) {
                    SQLException ex = var15;
                    log.warn("Error Closing Connection {}", ex);
                }
            }

        }

        return stmt;
    }

    public JdbcResult executeSP(String action, String schema, String sql, JdbcStatementType jdbcStmtType, Map<String, Object> sqlArgsMap, JdbcResult jdbcResult) throws Exception {
        return this.executeSP((Connection)null, action, schema, sql, jdbcStmtType, sqlArgsMap, jdbcResult);
    }

    public JdbcResult executeQuery(String sql, LinkedHashMap<String, Object> sqlArgsMap, JdbcResult jdbcResult) throws Exception {
        return JdbcUtils.processResults(this.doExecuteQuery(sql, sqlArgsMap, jdbcResult), jdbcResult, 0);
    }

    private Statement doExecuteQuery(String sql, LinkedHashMap<String, Object> sqlArgsMap, JdbcResult jdbcResult) throws Exception {
        if (sql != null && sql.length() != 0) {
            long paramCount = sql.codePoints().filter((ch) -> {
                return ch == 58;
            }).count();
            if (sqlArgsMap != null && sqlArgsMap.size() > 0 && paramCount != (long)sqlArgsMap.size()) {
                throw new Exception("number of param and number supplied param not same");
            } else {
                NamedParamStatement ps = new NamedParamStatement(this.getConnection(), sql);
                Iterator var8 = sqlArgsMap.entrySet().iterator();

                while(var8.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)var8.next();
                    if (entry.getValue() instanceof Integer) {
                        ps.setInt((String)entry.getKey(), (Integer)entry.getValue());
                    } else if (entry.getValue() instanceof String) {
                        ps.setString((String)entry.getKey(), (String)entry.getValue());
                    } else if (entry.getValue() instanceof Date) {
                        ps.setDate((String)entry.getKey(), (Date)entry.getValue());
                    } else if (entry.getValue() instanceof Double) {
                        ps.setDouble((String)entry.getKey(), (Double)entry.getValue());
                    } else if (entry.getValue() instanceof Long) {
                        ps.setLong((String)entry.getKey(), (Long)entry.getValue());
                    }
                }

                return ps.execute();
            }
        } else {
            throw new Exception("Sql must not empty.");
        }
    }

    private JdbcResult executeSP(Connection conn, String action, String schema, String sql, JdbcStatementType jdbcStmtType, Map<String, Object> sqlArgsMap, JdbcResult jdbcResult) throws Exception {
        CallableStatement cstmt = null;
        boolean createConn = false;

        try {
            if (conn != null && (conn == null || !conn.isClosed())) {
                log.debug("***** Seem to have an active connection ***** ");
            } else {
                log.debug("***** Null connection getting connection ***** ");
                conn = this.getConnection();
                createConn = true;
            }

            log.debug("Making call to SP {}", sql);
            cstmt = (CallableStatement)this.execute(conn, action, schema, sql, jdbcStmtType, sqlArgsMap);
            log.debug("Processing Result");
            jdbcResult = JdbcUtils.processResults(cstmt, jdbcResult);
        } finally {
            JdbcUtils.closeStatement(cstmt);
            cstmt = null;
            if (createConn) {
                try {
                    this.closeConnection(conn);
                } catch (SQLException var15) {
                    SQLException e = var15;
                    log.warn("Error closing connection", e);
                }
            }

        }

        return jdbcResult;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.transactionManager == null) {
            this.transactionManager = new DataSourceTransactionManager();
        }

        if (this.jdbcStatementFactory == null) {
            this.jdbcStatementFactory = new JdbcStatementFactory();
        }

        if (this.cacheStoredProcedures && this.jdbcStoredProcedureCache == null) {
            this.jdbcStoredProcedureCache = new LinkedHashMap();
        }

    }
}
