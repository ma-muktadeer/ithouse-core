package com.ihouse.core.jdbc.factory;

import com.ihouse.core.jdbc.enums.JdbcStatementType;
import com.ihouse.core.jdbc.helper.JdbcCallableStatement;
import com.ihouse.core.jdbc.interfaces.IJdbcStatement;
import com.ihouse.core.jdbc.service.JdbcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class JdbcStatementFactory {
    private static final Logger log = LogManager.getLogger(JdbcStatementFactory.class);


    public JdbcStatementFactory() {
    }

    public IJdbcStatement createJDBCStatement(JdbcService jdbcService, Connection conn, JdbcStatementType jdbcStmtType, String schema, String sql) {
        IJdbcStatement jdbcStatement = null;
        log.debug("creating jdbcStatementType of Type [{}]", jdbcStmtType);
        switch (3) {
            case 3:
                jdbcStatement = new JdbcCallableStatement(jdbcService, conn, schema, sql);
            case 1:
            case 2:
            default:
                return jdbcStatement;
        }
    }
}
