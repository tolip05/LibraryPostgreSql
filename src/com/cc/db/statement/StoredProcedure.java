package com.cc.db.statement;

import com.cc.db.DBConfig;
import com.cc.logging.ILog;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class StoredProcedure extends SqlStatement {
    private static final ILog LOG = DBConfig.logFactory.getLog(StoredProcedure.class);
    protected static final String SUCCESS = "OK";

    public void execute(Connection connection) throws SQLException {
        CallableStatement callableStatement = null;
        callableStatement = connection.prepareCall(getSqlString());
        setParameters(callableStatement);
        callableStatement.executeUpdate();
        retrieveResult(callableStatement);
        try {
            if (callableStatement != null)
                callableStatement.close();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    protected abstract void setParameters(CallableStatement callableStatement) throws SQLException;

    protected void retrieveResult(CallableStatement callableStatement) throws SQLException {
        // no implementation
    }

}
