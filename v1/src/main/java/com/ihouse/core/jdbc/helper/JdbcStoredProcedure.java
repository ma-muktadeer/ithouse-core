package com.ihouse.core.jdbc.helper;

import java.util.Map;

public class JdbcStoredProcedure {
    String catalogueName;
    String SchemaName;
    String procName;
    Map<String, JdbcParameter> spParamMap;
    Map<String, JdbcParameter> spOutputParamMap;

    public JdbcStoredProcedure() {
    }

    public String getCatalogueName() {
        return this.catalogueName;
    }

    public void setCatalogueName(String catalogueName) {
        this.catalogueName = catalogueName;
    }

    public String getSchemaName() {
        return this.SchemaName;
    }

    public void setSchemaName(String schemaName) {
        this.SchemaName = schemaName;
    }

    public String getProcName() {
        return this.procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public Map<String, JdbcParameter> getSpParamMap() {
        return this.spParamMap;
    }

    public void setSpParamMap(Map<String, JdbcParameter> spParamMap) {
        this.spParamMap = spParamMap;
    }

    public Map<String, JdbcParameter> getSpOutputParamMap() {
        return this.spOutputParamMap;
    }

    public void setSpOutputParamMap(Map<String, JdbcParameter> spOutputParamMap) {
        this.spOutputParamMap = spOutputParamMap;
    }
}
