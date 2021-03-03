package com.cc.db.common;

import com.cc.db.statement.SelectSqlStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseVersionSelect extends SelectSqlStatement {
    private static final String SELECT_SQL = "select version()";
    private String dbVersion;

    @Override
    protected String getSqlString() {
        return SELECT_SQL;
    }

    @Override
    protected void retrieveResult(ResultSet rs) throws SQLException {
        while (rs.next())
            dbVersion = rs.getString(1);
    }

    public String getDbVersion() {
        return dbVersion;
    }
}
