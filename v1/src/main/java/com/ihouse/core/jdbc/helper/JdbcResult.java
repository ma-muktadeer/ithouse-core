package com.ihouse.core.jdbc.helper;

import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JdbcResult {

    private List<String> filteredRsTypeList = new ArrayList(0);
    private boolean processWarnings;
    private boolean processUnknownRsType;
    private boolean processUpdateCount;
    private boolean processOutputParameters;
    private boolean clearBufferOnCommitOrRollback = true;
    private StringBuilder jsonMsgBuffer = new StringBuilder();
    private Map<String, JdbcParameter> filteredOutputParamMap = new LinkedHashMap(0);
    private Map<String, ResultSet> rsTypeMap = new LinkedHashMap(0);
    private List<ResultSet> unknownRsTypeList = new ArrayList(0);
    private List<SQLWarning> sqlWarningList = new ArrayList(0);
    private List<Integer> updateCountList = new ArrayList(0);
    private Map<String, Object> outputParamValueMap = new LinkedHashMap(0);

    public JdbcResult() {
    }

    public Map<String, ResultSet> getRsMap() {
        return this.rsTypeMap;
    }

    public void setRsTypeMap(Map<String, ResultSet> rsTypeMap) {
        this.rsTypeMap = rsTypeMap;
    }

    public Map<String, ResultSet> getRsTypeMap() {
        return this.rsTypeMap;
    }

    public ResultSet getRsTypeMap(String rsType) {
        return (ResultSet)this.rsTypeMap.get(rsType);
    }

    public void addRsType(String rsType, ResultSet rs) {
        this.rsTypeMap.put(rsType, rs);
    }

    public List<ResultSet> getUnknownRsTypeList() {
        return this.unknownRsTypeList;
    }

    public void setUnknownRsTypeList(List<ResultSet> unknownRsTypeList) {
        this.unknownRsTypeList = unknownRsTypeList;
    }

    public void addUnknownRsType(ResultSet unknownRsType) {
        this.unknownRsTypeList.add(unknownRsType);
    }

    public List<SQLWarning> getSqlWarningList() {
        return this.sqlWarningList;
    }

    public void setSqlWarningList(List<SQLWarning> sqlWarningList) {
        this.sqlWarningList = sqlWarningList;
    }

    public void addSqlWarning(SQLWarning sqlWarning) {
        this.sqlWarningList.add(sqlWarning);
    }

    public List<Integer> getUpdateCountList() {
        return this.updateCountList;
    }

    public void setUpdateCountList(List<Integer> updateCountList) {
        this.updateCountList = updateCountList;
    }

    public void addUpdateCount(Integer updateCount) {
        this.updateCountList.add(updateCount);
    }

    public Map<String, Object> getOutputParamValueMap() {
        return this.outputParamValueMap;
    }

    public void setOutputParamValueMap(Map<String, Object> outputParamValueMap) {
        this.outputParamValueMap = outputParamValueMap;
    }

    public void addOutputParamMap(String paramName, Object paramValue) {
        this.outputParamValueMap.put(paramName, paramValue);
    }

    public List<String> getFilteredRsTypeList() {
        return this.filteredRsTypeList;
    }

    public void setFilteredRsTypeList(List<String> filteredRsTypeList) {
        this.filteredRsTypeList = filteredRsTypeList;
    }

    public void addFilteredRsType(String rsType) {
        this.filteredRsTypeList.add(rsType);
    }

    public boolean isProcessWarnings() {
        return this.processWarnings;
    }

    public void setProcessWarnings(boolean processWarnings) {
        this.processWarnings = processWarnings;
    }

    public boolean isProcessUnknownRsType() {
        return this.processUnknownRsType;
    }

    public void setProcessUnknownRsType(boolean processUnknownRsType) {
        this.processUnknownRsType = processUnknownRsType;
    }

    public boolean isProcessUpdateCount() {
        return this.processUpdateCount;
    }

    public void setProcessUpdateCount(boolean processUpdateCount) {
        this.processUpdateCount = processUpdateCount;
    }

    public boolean isProcessOutputParameters() {
        return this.processOutputParameters;
    }

    public void setProcessOutputParameters(boolean processOutputParameters) {
        this.processOutputParameters = processOutputParameters;
    }

    public StringBuilder getJsonMsgBuffer() {
        return this.jsonMsgBuffer;
    }

    public void appendJsonMsgBuffer(String msg) {
        this.jsonMsgBuffer.append(msg);
    }

    public boolean isFilterRsType() {
        boolean status = false;
        if (this.filteredRsTypeList != null && this.filteredRsTypeList.size() > 0) {
            status = true;
            if (((String)this.filteredRsTypeList.get(0)).equals("RS_TYPE_ALL")) {
                status = false;
            }
        }

        return status;
    }

    public boolean isFilterOutputParam() {
        return this.filteredOutputParamMap != null && this.filteredOutputParamMap.size() > 0;
    }

    public Map<String, JdbcParameter> getFilteredOutputParamMap() {
        return this.filteredOutputParamMap;
    }

    public void setFilteredOutputParamMap(Map<String, JdbcParameter> filteredOutputParamMap) {
        this.filteredOutputParamMap = filteredOutputParamMap;
        this.processOutputParameters = true;
    }

    public void addFilteredOutputParam(JdbcParameter jdbcOutputParam) {
        this.processOutputParameters = true;
        this.filteredOutputParamMap.put(jdbcOutputParam.getParamName(), jdbcOutputParam);
    }

    public boolean isClearBufferOnCommitOrRollback() {
        return this.clearBufferOnCommitOrRollback;
    }

    public void setClearBufferOnCommitOrRollback(boolean clearBufferOnCommitOrRollback) {
        this.clearBufferOnCommitOrRollback = clearBufferOnCommitOrRollback;
    }

    public String getResultSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("----- ResultSummary -----").append("\n");
        sb.append("Count [rsTypeMap]           : ").append(this.rsTypeMap.size()).append("\n");
        sb.append("Count [unknownRsTypeList]   : ").append(this.unknownRsTypeList.size()).append("\n");
        sb.append("Count [outputParamValueMap] : ").append(this.outputParamValueMap.size()).append("\n");
        sb.append("Count [sqlWarningList]      : ").append(this.sqlWarningList.size()).append("\n");
        sb.append("----- END ResultSummary -----");
        return sb.toString();
    }
}
