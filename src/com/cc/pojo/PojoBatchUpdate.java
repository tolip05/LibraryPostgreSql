package com.cc.pojo;

import com.cc.db.statement.BatchUpdateSqlStatement;
import com.cc.db.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PojoBatchUpdate extends BatchUpdateSqlStatement {
    private final String sqlString;
    private List<List<Param>> allParams;

    public PojoBatchUpdate(String className,
                           String sqlString,
                           List<List<Param>> allParams) {
        this.sqlString = sqlString;
        this.allParams = allParams;
    }

    public PojoBatchUpdate(String className,
                           String sqlString) {
        this(className, sqlString, null);
    }

    @Override
    public String getSqlString() {
        return sqlString;
    }

    @Override
    public List<List<Param>> getParameters() {
        return allParams;
    }

    /**
     * Method that executes PojoUpdate and returns newly generated id if
     * statement is insert
     *
     * @param updae
     * @return
     * @throws SQLException
     *             Exception if any sql exception is raised during insert/update
     *             execution
     */
    public void execute() throws SQLException {
        DBUtil.execute(this);
    }

    public void executeWithConnection(Connection connection) throws SQLException {
        execute(connection);
    }
}