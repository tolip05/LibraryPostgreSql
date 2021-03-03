package com.cc.pojo;

import com.cc.db.exception.EmptyResultSetException;
import com.cc.db.statement.SelectSqlStatement;
import com.cc.db.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PojoSelect extends SelectSqlStatement {
    private final String className;
    private final String sqlString;
    private List<Param> queryParams;
    private List<Map<String, Object>> result;

    public PojoSelect(String className,
                      String sqlString,
                      List<Param> params) {
        this.className = className;
        this.sqlString = sqlString;
        queryParams = params;
    }

    public PojoSelect(String className,
                      String sqlString) {
        this(className,
                sqlString,
                null);
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

    @Override
    protected void retrieveResult(ResultSet rs) throws SQLException {
        result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = retrieveRowFromResultSet(rs);
            result.add(row);
        }
    }

    public static Map<String, Object> retrieveRowFromResultSet(ResultSet rs) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();
        for (int i = 1; i < numberOfColumns
                + 1; i++) {
            String fieldName = rsMetaData.getColumnLabel(i);
            if (rsMetaData.getColumnType(i) == Types.DATE)
                row.put(fieldName,
                        rs.getDate(fieldName));
            else if (rsMetaData.getColumnType(i) == Types.TIME)
                row.put(fieldName,
                        rs.getTime(fieldName));
            else if (rsMetaData.getColumnType(i) == Types.TIMESTAMP)
                row.put(fieldName,
                        rs.getTimestamp(fieldName));
            else {
                /*
                 * if (rs.getString(fieldName) != null &&
                 * rs.getString(fieldName) .startsWith("{")) row.put(fieldName,
                 * convertJsonStringToMap(rs.getString(fieldName))); else
                 */
                row.put(fieldName,
                        rs.getObject(fieldName));
            }
        }
        return row;
    }

    private PojoSelect execute() throws SQLException {
        DBUtil.execute(this);
        return this;
    }

    /**
     * Method that executes PojoSelect and returns first selected row
     *
     * @return
     * @throws EmptyResultSetException
     *             Exception if empty result set is found after select execution
     * @throws SQLException
     *             Exception if any sql exception is raised during select
     *             execution
     */
    public Map<String, Object> selectOneObject() throws EmptyResultSetException, SQLException {
        execute();
        if (result == null
                || result.isEmpty())
            throw new EmptyResultSetException();
        return result.get(0);
    }

    /**
     * Method that executes PojoSelect and returns first selected row
     *
     * @param connection
     *            Previously openned connection to db
     * @return
     * @throws EmptyResultSetException
     *             Exception if empty result set is found after select execution
     * @throws SQLException
     *             Exception if any sql exception is raised during select
     *             execution
     */
    public Map<String, Object> selectOneObject(Connection connection) throws EmptyResultSetException, SQLException {
        execute(connection);
        if (result == null
                || result.isEmpty())
            throw new EmptyResultSetException();
        return result.get(0);
    }

    /**
     * Method that executes PojoSelect and returns list with all selected rows
     *
     * @param select
     * @return
     * @throws EmptyResultSetException
     *             Exception if empty result set is found after select execution
     * @throws SQLException
     *             Exception if any sql exception is raised during select
     *             execution
     */
    public List<Map<String, Object>> selectListObjects() throws EmptyResultSetException, SQLException {
        execute();
        if (result == null
                || result.isEmpty())
            throw new EmptyResultSetException();
        return result;
    }

    @Override
    public String getClassName() {
        return className;
    }
}