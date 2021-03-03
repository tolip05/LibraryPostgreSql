package com.cc.db.statement;

import com.cc.logging.LOG;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public abstract class SelectSqlStatement extends SqlStatement {

    public void execute(Connection connection) throws SQLException {
        PreparedStatement prStmt = null;
        String sql = getSqlString();
        try {
            long startTime = System.currentTimeMillis();
            prStmt = connection.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            setParameters(prStmt);
            LOG.getLog(getClass())
                    .info(String.format("Executing query from %s.class with parameters %s",
                            getClassName(),
                            getParametersAsString() != null ? getParametersAsString() : "NO PARAMS"));
            // printSqlString(sql);
            LOG.getLog(getClass())
                    .info("Start Time: "
                            + formatter.format(startTime));
            retrieveResult(prStmt.executeQuery());
            Date endTime = new Date();

            LOG.getLog(getClass())
                    .info("End Time:   "
                            + formatter.format(endTime));
            LOG.getLog(getClass())
                    .info("Duration:   "
                            + (System.currentTimeMillis()
                            - startTime)

                            + " ms");

        } catch (SQLException e) {
            LOG.getLog(getClass())
                    .error("Failed query is: \n");
            printSqlString(sql);
            throw e;
        } finally {
            if (prStmt != null) {
                prStmt.close();
            }
        }
    }

    public String getClassName() {
        return this.getClass()
                .getName();
    }

    protected void retrieveResult(ResultSet rs) throws SQLException {
        // no implementation
    }

}
