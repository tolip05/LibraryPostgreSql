package com.cc.pojo;

import com.cc.db.statement.UpdateSqlStatement;
import com.cc.db.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PojoUpdate extends UpdateSqlStatement {
    private final String className;
    private final String sqlString;
    private List<Param> queryParams;

    public PojoUpdate(String className,
                      String sqlString,
                      List<Param> params) {
        this.className = className;
        this.sqlString = sqlString;
        queryParams = params;
    }

    public PojoUpdate(String className,
                      String sqlString) {
        this(className, sqlString, null);
    }

    @Override
    protected String getSqlString() {
        return sqlString;
    }

    @Override
    protected void setParameters(PreparedStatement prStmt) throws SQLException {
        List<String> parametersAsString = Param.setParameters(prStmt,
                queryParams);
        setParametersAsString(parametersAsString);
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
    public long execute() throws SQLException {
        DBUtil.execute(this);
        return getGeneratedId();
    }

    public long executeWithConnection(Connection connection) throws SQLException {
        execute(connection);
        return getGeneratedId();
    }

    public Map<String, Object> executeReturning() throws SQLException {
        DBUtil.execute(this);
        return getResult();
    }

    public Map<String, Object> executeReturningWithConnection(Connection connection) throws SQLException {
        execute(connection);
        return getResult();
    }

    public String getClassName() {
        return className;
    }

}