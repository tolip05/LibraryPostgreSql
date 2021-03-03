package com.cc.db.statement;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBStatement {
    public void execute(Connection connection) throws SQLException;
    public String sqlForLog();

}
