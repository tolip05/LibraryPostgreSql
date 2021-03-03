package com.cc.db.statement;

import com.cc.logging.LOG;
import com.cc.pojo.PojoSelect;

import java.sql.*;
import java.util.Date;
import java.util.Map;

public abstract class UpdateSqlStatement extends SqlStatement {
    private long generatedId = -1;
    private Map<String, Object> result = null;
    private final static String RETURNING_KEYWORD = "RETURNING";

    public void execute(Connection connection) throws SQLException {
        PreparedStatement prStmt = null;
        String sql = getSqlString();
        try {
            Date startTime = new Date();
            boolean returning = sql.toUpperCase()
                    .contains(RETURNING_KEYWORD);
            if (!returning)
                prStmt = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
            else
                prStmt = connection.prepareStatement(sql);
            setParameters(prStmt);
            if (!returning) {
                int affectedRows = prStmt.executeUpdate();
                if (affectedRows > 0)
                    try (ResultSet generatedKeys = prStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            try {
                                generatedId = generatedKeys.getLong(1);
                            } catch (Exception e) {
                                // Nothing
                            }
                        } else {
                            generatedId = -1;
                        }
                    }
            } else {
                ResultSet rs = prStmt.executeQuery();
                if (rs.next())
                    result = PojoSelect.retrieveRowFromResultSet(rs);
            }
            Date endTime = new Date();
            Long duration = endTime.getTime()
                    - startTime.getTime();
            LOG.getLog(getClass())
                    .info(String.format("Executing query from %s.class with parameters %s",
                            getClassName(),
                            getParametersAsString() != null ? getParametersAsString() : "NO PARAMS"));
            // printSqlString(sql);
            LOG.getLog(getClass())
                    .info("Start Time: "
                            + formatter.format(startTime));
            LOG.getLog(getClass())
                    .info("End Time:   "
                            + formatter.format(endTime));
            LOG.getLog(getClass())
                    .info("Duration:   "
                            + duration
                            + " ms");

        } catch (SQLException e) {
            LOG.getLog(getClass()).error("Failed query is: \n");
            printSqlString(sql);
            throw e;
        } finally {
            if (prStmt != null) {
                prStmt.close();
            }
        }
    }

    public long getGeneratedId() {
        return generatedId;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public String getClassName() {
        return this.getClass()
                .getName();
    }
}