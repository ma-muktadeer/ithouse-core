package com.ihouse.core.jdbc.utils;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class NamedParamStatement {

    private static final String COLON = ":";
    private static final String Q = "?";
    private PreparedStatement prepStmt;
    private List<String> fields;

    public NamedParamStatement(Connection conn, String sql) throws SQLException {
        int pos;
        int end;
        for(this.fields = new LinkedList(); (pos = sql.indexOf(":")) != -1; sql = sql.substring(0, pos) + "?" + sql.substring(end)) {
            end = sql.substring(pos).indexOf(" ");
            if (end == -1) {
                end = sql.length();
            } else {
                end += pos;
            }

            this.fields.add(sql.substring(pos + 1, end));
        }

        this.prepStmt = conn.prepareStatement(sql);
    }

    public PreparedStatement getPreparedStatement() {
        return this.prepStmt;
    }

    public Statement execute() throws SQLException {
        this.prepStmt.execute();
        return this.prepStmt;
    }

    public void close() throws SQLException {
        this.prepStmt.close();
    }

    public void setInt(String name, int value) throws SQLException {
        this.prepStmt.setInt(this.getIndex(name), value);
    }

    public void setDouble(String name, double value) throws SQLException {
        this.prepStmt.setDouble(this.getIndex(name), value);
    }

    public void setLong(String name, long value) throws SQLException {
        this.prepStmt.setLong(this.getIndex(name), value);
    }

    public void setString(String name, String value) throws SQLException {
        this.prepStmt.setString(this.getIndex(name), value);
    }

    public void setDate(String name, Date value) throws SQLException {
        this.prepStmt.setDate(this.getIndex(name), value);
    }

    public void setDate(String name, java.util.Date value) throws SQLException {
        if (value != null) {
            this.setDate(name, new Date(value.getTime()));
        }
    }

    private int getIndex(String name) {
        return this.fields.indexOf(name) + 1;
    }
}

