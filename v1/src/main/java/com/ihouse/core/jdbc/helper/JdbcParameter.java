package com.ihouse.core.jdbc.helper;

public class JdbcParameter {

    String paramName;
    String columnName;
    short columnType;
    String columnTypeStr;
    short dataType;
    String dataTypeStr;
    String typeName;
    String javaTypeName;
    boolean output;
    boolean nullable;

    public JdbcParameter() {
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public short getColumnType() {
        return this.columnType;
    }

    public void setColumnType(short columnType) {
        this.columnType = columnType;
    }

    public String getColumnTypeStr() {
        return this.columnTypeStr;
    }

    public void setColumnTypeStr(String columnTypeStr) {
        this.columnTypeStr = columnTypeStr;
    }

    public short getDataType() {
        return this.dataType;
    }

    public void setDataType(short dataType) {
        this.dataType = dataType;
    }

    public String getDataTypeStr() {
        return this.dataTypeStr;
    }

    public void setDataTypeStr(String dataTypeStr) {
        this.dataTypeStr = dataTypeStr;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getJavaTypeName() {
        return this.javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.typeName = javaTypeName;
    }

    public boolean isOutput() {
        return this.output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
