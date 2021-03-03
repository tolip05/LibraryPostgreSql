package com.cc.db.statement;

import com.cc.db.DBConfig;
import com.cc.logging.ILog;
import com.cc.pojo.Param;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BatchUpdateSqlStatement implements DBStatement {
    private static ILog LOG;
    public static final DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    public static DateFormat ddMMyyyyFormat = new SimpleDateFormat("dd.MM.yyyy");
    private List<List<String>> parametersAsString;
    private int DEFAULT_SUB_BATCH_SIZE = 1000;

    public BatchUpdateSqlStatement() {
        super();
        LOG = DBConfig.logFactory.getLog(getClass());
    }

    public abstract String getSqlString();

    public List<List<Param>> getParameters() {
        return null;
    }

    /**
     * Override this method if you want to increase or decrease sub batch size
     *
     * @return
     */
    public int getSubBatchSize() {
        return DEFAULT_SUB_BATCH_SIZE;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        parametersAsString = new ArrayList<List<String>>();
        PreparedStatement prStmt = null;
        try {
            long startTime = System.currentTimeMillis();
            long subStartTime = System.currentTimeMillis();
            String sql = getSqlString();
            prStmt = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            int count = 0;
            int batchNo = 1;
            for (List<Param> queryParams : getParameters()) {
                parametersAsString.add(Param.setParameters(prStmt,
                        queryParams));
                prStmt.addBatch();
                prStmt.clearParameters();
                if (++count
                        % getSubBatchSize() == 0) {
                    prStmt.executeBatch();
                    prStmt.clearBatch();
                    LOG.info(String.format("Executing sub batch #%d took: %s ms",
                            batchNo++,
                            System.currentTimeMillis()
                                    - subStartTime));
                    subStartTime = System.currentTimeMillis();
                }
            }
            prStmt.executeBatch();

            Date endTime = new Date();

            // printSqlString(sql);
            LOG.info("Start Time: "
                    + BatchUpdateSqlStatement.formatter.format(startTime));
            LOG.info("End Time:   "
                    + BatchUpdateSqlStatement.formatter.format(endTime));
            LOG.info("Total Duration:   "
                    + (System.currentTimeMillis()
                    - startTime)
                    + " ms");

        } catch (SQLException e) {
            throw e;
        } finally {
            if (prStmt != null) {
                prStmt.close();
            }
        }
    }

    public void printSqlString(String sql) {
        try {
            if (parametersAsString != null) {
                if (sql.contains("?")) {
                    sql = sql.replace("?",
                            "%s");
                    for (List<String> parameters : parametersAsString)
                        LOG.info("SQL:        "
                                + String.format(sql,
                                parameters.toArray()));
                }
            } else
                LOG.info("SQL:        "
                        + sql);

        } catch (

                Exception e) {
            // Cannot print sql
        }
    }

    @Override
    public String sqlForLog() {
        return "";
    }

}