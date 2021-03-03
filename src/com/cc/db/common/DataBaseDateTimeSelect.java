package com.cc.db.common;

import com.cc.db.statement.SelectSqlStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DataBaseDateTimeSelect extends SelectSqlStatement {
    private static final String SELECT_SQL = "select now()";
    private Date sysDateTime;

    @Override
    protected String getSqlString() {
        return SELECT_SQL;
    }

    @Override
    protected void retrieveResult(ResultSet rs) throws SQLException {
        while (rs.next())
            sysDateTime = rs.getTimestamp(1);
    }

    public Date getSysDateTime() {
        return sysDateTime;
    }
}
