package com.cc.db.statement;

import com.cc.logging.LOG;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SqlStatement implements DBStatement {
    public static final DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    public static DateFormat ddMMyyyyFormat = new SimpleDateFormat("dd.MM.yyyy");
    private List<String> parametersAsString;

    public SqlStatement() {
        super();
    }

    protected abstract String getSqlString();

    protected void setParameters(PreparedStatement prStmt) throws SQLException {
        // no implementation
    }

    protected void printSqlString(String sql) {
        try {
            if (parametersAsString != null) {
                if (sql.contains("?")) {
                    sql = sql.replace("?",
                            "%s");
                    sql = String.format(sql,
                            getParametersAsString());
                }
            }
            LOG.getLog(getClass())
                    .info("SQL:        "
                            + sql);
        } catch (Exception e) {
            // Cannot print sql
        }
    }

    @Override
    public String sqlForLog() {
        return "";
    }

    private String removeNewLinesAndTrimIfNeccessary(String param) {
        if (param.length() > 200)
            param = String.format("Trimmed!!! -> %s...",
                    param.substring(0,
                            200));
        return param.replaceAll("[\\t\\n\\r]+",
                " ");
    }

    public List<String> getParametersAsString() {
        if (parametersAsString != null)
            return parametersAsString.stream()
                    .map(this::removeNewLinesAndTrimIfNeccessary)
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }

    public void setParametersAsString(List<String> parametersAsString) {
        this.parametersAsString = parametersAsString;
    }
}
