package com.cc.db.statement;

public class FalseStatement extends SelectSqlStatement {
    @Override
    protected String getSqlString() {
        return "select falseStatement";
    }
}
