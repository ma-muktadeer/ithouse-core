package com.ihouse.core.jdbc.utils;


import com.ihouse.core.jdbc.constants.JdbcConstants;
import com.ihouse.core.jdbc.helper.JdbcParameter;
import com.ihouse.core.jdbc.helper.JdbcResult;
import com.ihouse.core.jdbc.helper.JdbcStoredProcedure;
import com.ihouse.core.jdbc.model.BaseModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.serial.SerialClob;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class JdbcUtils extends org.springframework.jdbc.support.JdbcUtils implements JdbcConstants {
    private static final Logger log = LogManager.getLogger(JdbcUtils.class);
    static Map<Integer, String> sqlTypeMap = new HashMap<>();

    static {
        initSqlTypeMap();
//        DateConverter dateConverter = new DateConverter();
//        dateConverter.setPattern("yyyy-MM-dd");
//        ConvertUtils.register(dateConverter, Date.class);
    }
//static {
//    initSqlTypeMapn();
//}
//
//    private static void initSqlTypeMapn() {
//        // Initialize SQL_TYPE_MAP with SQL types and their corresponding names
//        sqlTypeMap.put(java.sql.Types.INTEGER, "INTEGER");
//        sqlTypeMap.put(java.sql.Types.VARCHAR, "VARCHAR");
//        sqlTypeMap.put(java.sql.Types.DATE, "DATE");
//        // Add other SQL types as needed
//    }

//    public static String formatDate(java.util.Date date) {
//        if (date == null) {
//            return null;
//        }
//        return DATE_FORMATTER.format(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
//    }

    public JdbcUtils() {
    }

    public static JdbcStoredProcedure getStoredProcMetadata(Connection conn, String schema, String spName) throws SQLException {
        // Prepare the statement to retrieve procedure columns
        DatabaseMetaData dmd = conn.getMetaData();
        try (ResultSet rs = dmd.getProcedureColumns(conn.getCatalog(), schema, spName, null)) {
            String dbType = dmd.getDatabaseProductName();
            JdbcStoredProcedure jdbcStoredProc = null;

            // Use Generics for type safety
            Map<String, JdbcParameter> spParamMap = new LinkedHashMap<>();
            Map<String, JdbcParameter> spOutputParamMap = new LinkedHashMap<>();

            // Iterate over the result set
            while (rs.next()) {
                if (jdbcStoredProc == null) {
                    jdbcStoredProc = new JdbcStoredProcedure();
                    jdbcStoredProc.setSpParamMap(spParamMap);
                    jdbcStoredProc.setSpOutputParamMap(spOutputParamMap);
                    jdbcStoredProc.setCatalogueName(rs.getString("PROCEDURE_CAT"));
                    jdbcStoredProc.setSchemaName(rs.getString("PROCEDURE_SCHEM"));
                    jdbcStoredProc.setProcName(rs.getString("PROCEDURE_NAME"));
                }

                // Create a new JdbcParameter for each column
                JdbcParameter jdbcParam = new JdbcParameter();
                jdbcParam.setParamName(rs.getString("COLUMN_NAME"));

                // Set the column name, removing specific prefixes depending on the database type
                if (dbType.equals(DbType.MySQL.toString())) {
                    jdbcParam.setColumnName(rs.getString("COLUMN_NAME").replace("p_", ""));
                } else {
                    jdbcParam.setColumnName(rs.getString("COLUMN_NAME").replace("@", ""));
                }

                // Set the column type (IN, OUT, INOUT)
                short colType = rs.getShort("COLUMN_TYPE");
                jdbcParam.setColumnType(colType);

                switch (colType) {
                    case 1:
                        jdbcParam.setColumnTypeStr("IN");
                        break;
                    case 2:
                        jdbcParam.setColumnTypeStr("INOUT");
                        jdbcParam.setOutput(true);
                        spOutputParamMap.put(jdbcParam.getParamName(), jdbcParam);
                        break;
                    case 4:
                        jdbcParam.setColumnTypeStr("OUT");
                        jdbcParam.setOutput(true);
                        spOutputParamMap.put(jdbcParam.getParamName(), jdbcParam);
                        break;
                    default:
                        // Default case for other types (e.g., stored procedure return values)
                        break;
                }

                // Set the data type and SQL/Java type name
                short dataType = rs.getShort("DATA_TYPE");
                jdbcParam.setDataType(dataType);
                jdbcParam.setDataTypeStr(getSqlTypeName(dataType));
                jdbcParam.setJavaTypeName(getSqlTypeName(dataType));
                jdbcParam.setTypeName(rs.getString("TYPE_NAME"));

                // Check for nullability
                boolean isNullable = rs.getShort("NULLABLE") == 1;
                jdbcParam.setNullable(isNullable);

                // Add the parameter to the spParamMap
                spParamMap.put(jdbcParam.getParamName(), jdbcParam);
            }

            return jdbcStoredProc; // Return the constructed JdbcStoredProcedure object
        }
    }

    public static String generateSqlTemplate(String sql, int paramCount) throws SQLException {
        log.debug("generating SQL Template");
        String preparedSql = sql;
        if (paramCount > 0) {
            preparedSql = sql + generatePlaceHolders(paramCount);
        }

        return preparedSql;
    }

    private static String generatePlaceHolders(int argCount) {
        StringBuilder sqlString = new StringBuilder(" (");

        sqlString.append("?,".repeat(Math.max(0, argCount)));

        sqlString.deleteCharAt(sqlString.length() - 1);
        sqlString.append(")");
        return sqlString.toString();
    }

    public static String escapeSql(String sql) {
        if (sql.contains("'")) {
            sql = sql.replace("'", "''");
        }

        return sql;
    }

    public static JdbcResult processResults(CallableStatement cstmt, JdbcResult jdbcResult) throws Exception {
        jdbcResult = processResults(cstmt, jdbcResult, 0);
        if (jdbcResult.isProcessOutputParameters()) {
            log.debug("Processing outParams");
            Map<String, JdbcParameter> outputParamMap = jdbcResult.getFilteredOutputParamMap();
            Map<String, Object> outParamValueMap = new LinkedHashMap<>(outputParamMap.size());
            String outputParamName = null;

            try {
                outputParamMap.keySet().parallelStream().forEach((key) -> {
                    try {
                        outParamValueMap.put(key, cstmt.getObject(key));
                    } catch (Exception var4) {
                        log.error("Exception mapping outParam : [{}]", key);
                    }

                });
                jdbcResult.setOutputParamValueMap(outParamValueMap);
            } catch (Exception var6) {
                log.error((String) outputParamName, var6);
                throw var6;
            }
        }

        cstmt.close();
        return jdbcResult;
    }

    public static JdbcResult processResults(Statement stmt, JdbcResult jdbcResult, int x) throws SQLException {
        if (jdbcResult == null) {
            jdbcResult = new JdbcResult();
            jdbcResult.setProcessUnknownRsType(true);
            jdbcResult.setProcessOutputParameters(true);
        }

        boolean filterRsType = jdbcResult.isFilterRsType();
        Map<String, String> filteredRsTypeMap = null;
        if (filterRsType) {
            List<String> filteredRsTypeList = jdbcResult.getFilteredRsTypeList();
            filteredRsTypeMap = filteredRsTypeList.parallelStream()
                    .collect(Collectors.toMap(Function.identity(), Function.identity()));
        }

        int i = 0;
        log.debug("Processing ResultSet");

        try {
            while (true) {
                // Get the current result set from the statement
                ResultSet rs = stmt.getResultSet();
                int rowsAffected = stmt.getUpdateCount();

                if (rs == null) {
                    // If there is no result set, handle the update count
                    if (rowsAffected != -1 && jdbcResult.isProcessUpdateCount()) {
                        log.info("Storing updateCount");
                        jdbcResult.addUpdateCount(rowsAffected);
                    }
                } else {
                    // Process the ResultSet
                    ResultSetMetaData rsmd = rs.getMetaData();
                    if (rsmd.getColumnLabel(1).equals("tx_rs_type")) {
                        String rsTypeName = null;
                        if (rs.next()) {
                            rsTypeName = rs.getString(1);
                            rs.beforeFirst();
                        }

                        log.debug("rsTypeName : [{}] filterRsType: [{}]", rsTypeName, filterRsType);
                        if ((!filterRsType || !filteredRsTypeMap.containsKey(rsTypeName)) && filterRsType) {
                            log.debug("Skipping Named rsType {}", rsTypeName);
                        } else {
                            log.debug("Storing NamedResultSet {}", rsTypeName);
                            jdbcResult.addRsType(rsTypeName, rs);
                        }
                    } else if (jdbcResult.isProcessUnknownRsType()) {
                        log.info("Storing ResultSet");
                        jdbcResult.addUnknownRsType(rs);
                    }
                }

                // Check if there are more results
                if (!stmt.getMoreResults() && stmt.getUpdateCount() == -1) {
                    log.debug("Processing SQLWarnings");
                    if (jdbcResult.isProcessWarnings()) {
                        for (SQLWarning sqlWarning = stmt.getWarnings(); sqlWarning != null; sqlWarning = sqlWarning.getNextWarning()) {
                            jdbcResult.addSqlWarning(sqlWarning);
                        }
                    }
                    return jdbcResult;
                }

                ++i;
            }
        } catch (SQLException var11) {
            log.error("Error whilst processing {} result set {}", i, var11);
            throw var11;
        }
    }

    public static String printOutputParamValueMap(Map<String, Object> outputValueMap) {
        if (outputValueMap == null || outputValueMap.isEmpty()) {
            return "";
        }

        // Using Map's entrySet() and StringBuilder for efficient string concatenation
        return outputValueMap.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .reduce((s1, s2) -> s1 + ", " + s2)
                .map(result -> {
                    log.info("Output Param : {}", result);
                    return result;
                })
                .orElse("");
    }

    public static void printSqlWarnings(Statement stmt) {
        try {
            printSqlWarnings(stmt.getWarnings());
        } catch (SQLException var2) {
            log.error("Error {}, {}", var2, var2);
        }

    }

    public static String printSqlWarnings(SQLWarning warn) {
        StringBuilder sb = new StringBuilder();
        if (warn != null) {
            warn = warn.getNextWarning();
            sb.append("\n---SQL Warning(s)---\n");

            while (warn != null) {
                sb.append("SQLState : [").append(warn.getSQLState()).append("] Vendor : [").append(warn.getErrorCode()).append("] Message : [").append(warn.getMessage()).append("]");
                sb.append(JDBC_NEW_LINE);
                warn = warn.getNextWarning();
            }

            sb.append("---END SQL Warning(s)---\n");
            log.warn("Warn {}", sb);
        }

        return sb.toString();
    }

    public static void printSqlExceptions(SQLException sqlex) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n---SQL Exception(s)---\n");

        while (sqlex != null) {
            sb.append("SQLState : [").append(sqlex.getSQLState()).append("] Message : [").append(sqlex.getMessage()).append("] Vendor : [").append(sqlex.getErrorCode()).append("]");
            sb.append(JDBC_NEW_LINE);
            sqlex = sqlex.getNextException();
        }

        sb.append("---END SQL Exception(s)---\n");
        log.error("sqlExceptions {}", sb);
    }

    public static String printUpdateCount(List<Integer> updateCountList) {
        StringBuilder sb = new StringBuilder();
        if (updateCountList != null) {

            for (int i : updateCountList) {
                sb.append(i).append("; ");
            }

            log.debug("UpdateCount : {}", sb);
        }

        return sb.toString();
    }

    public static void printResults(Statement stmt) throws SQLException {
        if (log.isInfoEnabled()) {
            int i = 0;

            while (true) {
                ResultSet rs = stmt.getResultSet();
                int rowsAffected = stmt.getUpdateCount();
                if (rs != null) {
                    printResultSet(rs);
                } else {
                    log.debug("[{}] rows affected.", rowsAffected);
                }

                if (!stmt.getMoreResults() && stmt.getUpdateCount() == -1) {
                    break;
                }

                ++i;
            }
        }

        printSqlWarnings(stmt);
    }

    public static void printResultSet(Map<String, ResultSet> rsMap) {

        if (rsMap == null || rsMap.isEmpty()) {
            log.debug("The map is empty or null.");
            return;
        }

        for (Map.Entry<String, ResultSet> entry : rsMap.entrySet()) {
            log.debug("***** {} *****", entry.getKey());
            try {
                printResultSet(entry.getValue());
            } catch (SQLException e) {
                log.error("Error printing ResultSet for key {}: {}", entry.getKey(), e.getMessage(), e);
            }
        }
    }

    public static void printResultSet(List<ResultSet> rsList) {

        for (ResultSet resultSet : rsList) {
            try {
                printResultSet(resultSet);
            } catch (SQLException e) {
                log.error("Error printing ResultSet for {}", e.getMessage(), e);
            }
        }
    }

    public static String printResultSet(ResultSet rs) throws SQLException {
        if (rs == null) {
            return "ResultSet is null";
        }

        StringBuilder sb = new StringBuilder();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCols = rsmd.getColumnCount();

        sb.append("\n----RESULTS-----\n");

        // Print column headers
        for (int i = 1; i <= numCols; i++) {
            if (i > 1) {
                sb.append("\t\t");
            }
            sb.append(rsmd.getColumnLabel(i))
                    .append("[")
                    .append(rsmd.getColumnTypeName(i))
                    .append("]");
        }

        sb.append(JDBC_NEW_LINE);

        // Print row data
        while (rs.next()) {
            for (int i = 1; i <= numCols; i++) {
                if (i > 1) {
                    sb.append("\t\t");
                }
                String value = rs.getString(i);
                sb.append(rs.wasNull() ? "NULL" : value);
            }
            sb.append(JDBC_NEW_LINE);
        }

        sb.append("---- END RESULTS-----\n");

        log.debug("ResultSet {}", sb);

        return sb.toString();
    }

    public static void XgetjdbcStoredProc(Connection conn, String spName) {
        String columnName = null;

        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet rs = dbMetaData.getProcedureColumns(conn.getCatalog(), (String) null, spName, (String) null);

            for (int i = 1; rs.next(); ++i) {
                String procedureCatalog = rs.getString(1);
                String procedureSchema = rs.getString(2);
                String procedureName = rs.getString(3);
                columnName = rs.getString(4);
                short columnReturn = rs.getShort(5);
                int columnDataType = rs.getInt(6);
                String columnReturnTypeName = rs.getString(7);
                int columnPrecision = rs.getInt(8);
                int columnByteLength = rs.getInt(9);
                short columnScale = rs.getShort(10);
                short columnRadix = rs.getShort(11);
                short columnNullable = rs.getShort(12);
                String columnRemarks = rs.getString(13);
                boolean isOutput = false;
                if (rs.getInt(5) == 2 || rs.getInt(5) == 4) {
                    isOutput = true;
                }

                String colDataTypeName = null;
                System.out.println("cdt = " + columnDataType);
                switch (columnDataType) {
                    case -7:
                        colDataTypeName = "Bit";
                        break;
                    case 93:
                        colDataTypeName = "Datetime";
                }

                System.out.println("procedureName=" + procedureName);
                System.out.println("columnName=" + columnName);
                System.out.println("columnReturn=" + columnReturn);
                System.out.println("columnDataType=" + columnDataType);
                System.out.println("columnReturnTypeName=" + columnReturnTypeName);
                System.out.println("columnByteLength=" + columnByteLength);
                System.out.println("columnRadix=" + columnRadix);
                System.out.println("columnNullable=" + columnNullable);
                System.out.println("isOutput =" + Boolean.toString(isOutput));
            }
        } catch (Exception var20) {
            log.error("error {}, {}", var20, columnName);
        }

    }

    public static void initSqlTypeMap() {
        Field[] fields = Types.class.getFields();

        for (Field field : fields) {
            try {
                String name = field.getName();
                Integer value = (Integer) field.get((Object) null);
                sqlTypeMap.put(value, name);
            } catch (IllegalAccessException var4) {
                log.error("Error {}", var4);
            }
        }

    }

    public static String getSqlTypeName(int i) {
        return (String) sqlTypeMap.get(i);
    }

    public static String getJavaTypeFromSqlType(int type) {

        return switch (type) {
            case -7 -> "java.lang.Boolean";
            case -6 -> "java.lang.Byte";
            case -5 -> "java.lang.Long";
            case -4, -3, -2 -> "java.lang.Byte[]";
            case -1, 1, 12 -> "java.lang.String";
            case 2, 3 -> "java.math.BigDecimal";
            case 4 -> "java.lang.Boolean";
            case 5 -> "java.lang.Short";
            case 6, 8 -> "java.lang.Double";
            case 7 -> "java.lang.Real";
            case 91 -> "java.sql.Date";
            case 92 -> "java.sql.Time";
            case 93 -> "java.sql.Timestamp";
            default -> "java.lang.Object";
        };
    }

    public static Map<String, Object> createSqlMap(Object bean, Map<String, String> sqlInputMap) throws Exception {
        return createSqlMap(bean, sqlInputMap, (Map) null, true);
    }

    public static Map<String, Object> createSqlMap(Object bean, Map<String, String> sqlInputMap, Map<String, Object> sqlInputArgs, boolean overwrite) throws Exception {
        // Initialize sqlInputArgs if it's null
        if (sqlInputArgs == null) {
            sqlInputArgs = new LinkedHashMap<>();
        }

        // Validate inputs
        Objects.requireNonNull(bean, "Bean cannot be null");
        Objects.requireNonNull(sqlInputMap, "sqlInputMap cannot be null");

        // Iterate over the sqlInputMap entries
        for (Map.Entry<String, String> entry : sqlInputMap.entrySet()) {
            String sqlParam = entry.getKey();
            String beanProperty = entry.getValue();
            log.fatal(() -> String.format("Processing sqlParam [%s] / beanProperty [%s]", sqlParam, beanProperty));

            // Get the property value using reflection
            Object beanValue = getProperty(bean, beanProperty);
            if (beanValue != null) {
                Object origSqlParamValue = sqlInputArgs.get(sqlParam);

                // Check if we need to overwrite the existing value
                if (origSqlParamValue != null && !overwrite) {
                    log.debug(() -> String.format("Skipping [%s] as it seems to be already mapped to [%s]", sqlParam, origSqlParamValue));
                } else {
                    sqlInputArgs.put(sqlParam, beanValue);
                    log.debug(() -> String.format("Mapped [%s] -> [%s] [%s]", sqlParam, beanProperty, beanValue));
                }
            } else {
                log.debug(() -> String.format("Skipping [%s] cannot find beanProperty [%s]", sqlParam, beanProperty));
            }
        }

        return sqlInputArgs;
    }

    private static Object getProperty(Object bean, String propertyName) {
        try {
            Field field = bean.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(bean);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public static Map<String, Object> validateSqlArgs(Map<String, JdbcParameter> sqlParamMap, Map<String, Object> sqlArgs) {
        log.debug("Validating Arguments");

        if (sqlArgs == null || sqlArgs.isEmpty()) {
            return new LinkedHashMap<>(); // Return an empty map if sqlArgs is null or empty
        }

        Map<String, Object> validatedMap = new LinkedHashMap<>(sqlArgs.size());

        for (Map.Entry<String, Object> entry : sqlArgs.entrySet()) {
            String sqlParam = entry.getKey();
            if (sqlParamMap.containsKey(sqlParam)) {
                validatedMap.put(sqlParam, entry.getValue());
            } else {
                log.warn("{} is not defined as an input Param, skipping....", sqlParam);
            }
        }

        return validatedMap;
    }

    public static void populateBean(Object bean, Map<String, String> sql2BeanMap, Map<String, Object> sqlValues) throws Exception {
        // Validate inputs
        Objects.requireNonNull(bean, "Bean cannot be null");
        Objects.requireNonNull(sql2BeanMap, "sql2BeanMap cannot be null");
        Objects.requireNonNull(sqlValues, "sqlValues cannot be null");

        // Iterate over the sql2BeanMap entries
        for (Map.Entry<String, String> entry : sql2BeanMap.entrySet()) {
            String sqlParam = entry.getKey();
            String beanPropertyName = entry.getValue();
            Object value = sqlValues.get(sqlParam);

            log.debug(() -> String.format("Attempting to map %s -> %s", sqlParam, beanPropertyName));
            log.fatal(() -> String.format("Data %s %s", sqlParam, value));

            if (value != null) {
                setProperty(bean, beanPropertyName, value);
                log.debug(() -> String.format("Mapped %s -> [%s]", beanPropertyName, value));
            }
        }
    }

    private static void setProperty(Object bean, String propertyName, Object value) throws Exception {
        try {
            Field field = bean.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn(() -> String.format("Failed to set property %s on bean: %s", propertyName, e.getMessage()));
            throw new Exception("Failed to set property", e);
        }
    }

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    /**
     * @deprecated
     */
    @Deprecated
    public static <T> List<T> mapRows(Class<T> clazz, Map<String, String> rs2BeanMap, ResultSet rs) throws Exception {
        List<T> list = new ArrayList<>();
        if (rs2BeanMap != null && rs != null) {
            ResultSetMetaData rmd = rs.getMetaData();
            Map<String, String> rsColumnMap = new LinkedHashMap<>();
            int columnCount = rmd.getColumnCount();

            // Build column map
            for (int i = 1; i <= columnCount; ++i) {
                String colName = rmd.getColumnLabel(i);
                rsColumnMap.put(colName, colName);
            }

            while (rs.next()) {
                T newInstance = clazz.getDeclaredConstructor().newInstance();
                Map<String, Object> beanMap = new HashMap<>();

                for (Map.Entry<String, String> entry : rs2BeanMap.entrySet()) {
                    String rsColumn = entry.getKey();
                    String beanPropertyName = entry.getValue();

                    log.fatal(() -> String.format("Attempting to map %s -> %s", rsColumn, beanPropertyName));

                    if (rsColumnMap.containsKey(rsColumn)) {
                        Object value = rs.getObject(rsColumn);
                        if (value != null) {
                            if (value instanceof SerialClob) {
                                String conversion = convertCLOBToString((SerialClob) value);
                                beanMap.put(beanPropertyName, conversion);
                            } else if (rsColumn.startsWith("dtt_") || rsColumn.startsWith("dt_")) {
                                beanMap.put(beanPropertyName, getDateString(value));
                            } else {
                                beanMap.put(beanPropertyName, value.toString());
                            }
                            log.fatal(() -> String.format("Mapped %s -> [%s]", beanPropertyName, value));
                        }
                    } else {
                        log.fatal(() -> String.format("Skipped %s as it could not be found in the ResultSet", rsColumn));
                    }
                }

                populateBean(newInstance, beanMap);
                log.fatal(() -> String.format("Adding %s to list", clazz.getName()));
                list.add(newInstance);
            }
        } else {
            log.warn("Null parameters sent! or empty result set");
        }

        return list;
    }

    private static String convertCLOBToString(SerialClob clob) throws SQLException, IOException {
        StringBuilder sb = new StringBuilder();
        try (var reader = clob.getCharacterStream()) {
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
        }
        return sb.toString();
    }

    private static String getDateString(Object value) {
        try {
            Date date = dateFormat.parse(value.toString());
            return dateFormat.format(date);
        } catch (ParseException e) {
            log.warn(() -> String.format("Failed to parse date: %s", e.getMessage()));
            return value.toString();
        }
    }

    private static <T> void populateBean(T bean, Map<String, Object> beanMap) throws Exception {
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String propertyName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = bean.getClass().getDeclaredField(propertyName);
                field.setAccessible(true);
                field.set(bean, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn(() -> String.format("Failed to set property %s on bean: %s", propertyName, e.getMessage()));
                throw new Exception("Failed to set property", e);
            }
        }
    }

    public static Map<String, Object> createSqlMap(Object bean, Map<String, JdbcParameter> spParamMap, Map<String, Object> retMap) throws Exception {
        if (retMap == null) {
            retMap = new LinkedHashMap<>();
        }

        // Convert bean class name to DB table name
        String className = bean.getClass().getSimpleName();
        String dbTableName = convertFieldNameToDbName(className);

        // Use reflection to get bean properties and values
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Allows access to private fields
            String fieldName = field.getName();
            Object value = field.get(bean);

            if (value == null) {
                continue;
            }

            String typeName = value.getClass().getSimpleName();
            String tableFieldName = createTableFieldName(dbTableName, fieldName, typeName);

            if (tableFieldName != null && spParamMap.containsKey(tableFieldName)) {
                retMap.put(tableFieldName, value);
                log.fatal(() -> String.format("Mapped %s - %s", tableFieldName, value));
            } else if (value instanceof BaseModel) {
                BaseModel baseBean = (BaseModel) value;
                handleBaseModel(baseBean, retMap);
            }
        }

        return retMap;
    }


    private static void handleBaseModel(BaseModel baseBean, Map<String, Object> retMap) {
        if (baseBean.getEnvId() != null) {
            retMap.put("@id_ds_env_key", baseBean.getEnvId());
        }

        if (baseBean.getEventId() != null) {
            retMap.put("@id_evt_key", baseBean.getEventId());
        }

        if (baseBean.getStoreId() != null) {
            retMap.put("@id_store_key", baseBean.getStoreId());
        }

        if (baseBean.getLanguageId() != null) {
            retMap.put("@id_lang_key", baseBean.getLanguageId());
        }

        if (baseBean.getDateModified() != null) {
            retMap.put("@dtt_mod", baseBean.getDateModified());
        }

        retMap.put("@is_active", baseBean.getActive());
        retMap.put("@id_user_mod", baseBean.getUserIdModified());
    }


    public static Map<String, String> createSqlMapForResultSet(Object emptyBean, Map<String, JdbcParameter> spParamMap, Map<String, String> retMap, String parentFieldName) throws Exception {
        if (retMap == null) {
            retMap = new HashMap<>();
        }

        String className = emptyBean.getClass().getSimpleName();
        String dbTableName = convertFieldNameToDbName(className);

        // Use reflection to get properties of the bean
        Field[] fields = emptyBean.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Allows access to private fields
            String fieldName = field.getName();
            String mapValue = (parentFieldName != null && !parentFieldName.isEmpty()) ? parentFieldName + "." + fieldName : fieldName;
            Class<?> type = field.getType();
            String typeName = type.getSimpleName();
            String tableFieldName = createTableFieldName(dbTableName, fieldName, typeName);

            if (tableFieldName != null && spParamMap.containsKey("@" + tableFieldName)) {
                retMap.put(tableFieldName, mapValue);
                log.fatal(() -> String.format("Mapped %s - %s", tableFieldName, mapValue));
            } else if (typeName.startsWith("com.delfian")) {
                Object nestedBean = type.getDeclaredConstructor().newInstance();
                createSqlMapForResultSet(nestedBean, spParamMap, retMap, mapValue);
            }
        }

        addDefaultMappings(retMap);
        return retMap;
    }

    private static String createTableFieldName(String dbTableName, String fieldName, String typeName) {
        switch (typeName) {
            case "String":
                return "tx_" + dbTableName + "_" + convertFieldNameToDbName(fieldName);
            case "Integer":
            case "Long":
                return null;
            case "Float":
                return "flt_" + dbTableName + "_" + convertFieldNameToDbName(fieldName);
            case "Boolean":
                return "is_" + dbTableName + "_" + convertFieldNameToDbName(fieldName);
            case "Date":
                return "dt_" + dbTableName + "_" + convertFieldNameToDbName(fieldName);
            default:
                if (fieldName.equals("id")) {
                    return "id_" + dbTableName + "_key";
                } else if (fieldName.equals("version")) {
                    return "id_" + dbTableName + "_ver";
                } else if (fieldName.endsWith("Id")) {
                    return "id_" + dbTableName + "_" + convertFieldNameToDbName(fieldName).replace("_id", "_key");
                } else {
                    return "ct_" + dbTableName + "_" + convertFieldNameToDbName(fieldName);
                }
        }
    }

    private static void addDefaultMappings(Map<String, String> retMap) {
        retMap.put("id_store_key", "storeId");
        retMap.put("id_lang_key", "languageId");
        retMap.put("@dtt_mod", "dateModified");
        retMap.put("@is_active", "active");
        retMap.put("@id_user_mod", "userIdModified");
    }

    private static String convertFieldNameToDbName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        StringBuilder sb = new StringBuilder(name.length());
        boolean upperCaseLetterFound = false;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (i == 0) {
                sb.append(Character.toLowerCase(c));
            } else if (Character.isUpperCase(c)) {
                if (upperCaseLetterFound) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
                upperCaseLetterFound = true;
            } else {
                sb.append(c);
                upperCaseLetterFound = false;
            }
        }

        return sb.toString();
    }

//    private static String convertFieldNameToDbName(String name) {
//        List charList = new ArrayList();
//        boolean upperCaseLetterFound = false;
//        int i = 0;
//        char[] var7;
//        int var6 = (var7 = name.toCharArray()).length;
//
//        for(int var5 = 0; var5 < var6; ++var5) {
//            char c = var7[var5];
//            if (i == 0) {
//                charList.add(Character.toLowerCase(c));
//            } else if (Character.isUpperCase(c)) {
//                charList.add('_');
//                charList.add(Character.toLowerCase(c));
//                upperCaseLetterFound = true;
//            } else {
//                charList.add(c);
//            }
//
//            ++i;
//        }
//
//        if (!upperCaseLetterFound) {
//            return name.toLowerCase();
//        } else {
//            Character[] charArray = (Character[])charList.toArray(new Character[0]);
//            char[] cArray = new char[charArray.length];
//            i = 0;
//            Character[] var9 = charArray;
//            int var8 = charArray.length;
//
//            for(int var13 = 0; var13 < var8; ++var13) {
//                Character character = var9[var13];
//                cArray[i] = character;
//                ++i;
//            }
//
//            return String.valueOf(cArray);
//        }
//    }

//    static String getDateString(Object obj) throws Exception {
//        if (obj != null) {
//            if (obj.toString().indexOf(":") == -1) {
//                return obj.toString() + " 00:00:00";
//            }
//
//            obj.toString();
//        }
//
//        return null;
//    }
}
