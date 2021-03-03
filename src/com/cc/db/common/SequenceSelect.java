package com.cc.db.common;

import com.cc.db.statement.SelectSqlStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SequenceSelect extends SelectSqlStatement {
    private long nextVal;
    private String tableName;
    private String pkg;

    public SequenceSelect(String tableName) {
        this.tableName = tableName;
    }

    public SequenceSelect(String pkg, String tableName) {
        this.tableName = tableName;
        this.pkg = pkg;
    }

    @Override
    protected String getSqlString() {
        if (pkg == null)
            return "select nextval('s_" + tableName + "') nextVal";
        else
            return "select nextval('" + pkg + ".s_" + tableName
                    + "') nextVal ";
    }

    @Override
    protected void retrieveResult(ResultSet rs) throws SQLException {
        while (rs.next())
            nextVal = rs.getLong("nextVal");
    }

    public long getNextVal() {
        return this.nextVal;
    }
}