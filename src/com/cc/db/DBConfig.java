package com.cc.db;

import com.cc.db.connection.IConnectionFactory;
import com.cc.logging.ILogFactory;

public class DBConfig {
    public static ILogFactory logFactory;
    public static IConnectionFactory connectionFactory;

    public static void initDBConfig(ILogFactory logFactory, IConnectionFactory connectionFactory) {
        DBConfig.logFactory = logFactory;
        DBConfig.connectionFactory = connectionFactory;
    }
}