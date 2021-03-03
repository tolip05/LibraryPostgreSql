package com.cc.db.util;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionCallBack {
    Long execute(Connection connection) throws SQLException;
}