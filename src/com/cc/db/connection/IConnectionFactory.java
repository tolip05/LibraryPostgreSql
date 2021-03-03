package com.cc.db.connection;

import java.sql.Connection;

public interface IConnectionFactory {
    public Connection getConnection();

    public void closeDataSource();
}