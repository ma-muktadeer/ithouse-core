package com.ihouse.core.jdbc.interfaces;

import java.sql.Statement;
import java.util.Map;

public interface IJdbcStatement {
    boolean initialise(Map<String, Object> var1) throws Exception;

    Statement execute(Map<String, Object> var1) throws Exception;
}
