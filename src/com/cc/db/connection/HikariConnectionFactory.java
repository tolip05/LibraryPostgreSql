package com.cc.db.connection;

import com.cc.db.exception.JDBCException;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HikariConnectionFactory implements IConnectionFactory {
    private final HikariCustomConfig config;
    private HikariDataSource dataSource;

    public HikariConnectionFactory(HikariCustomConfig config) {
        super();
        this.config = config;
    }

    public HikariDataSource getDataSource() {
        if (dataSource != null)
            return dataSource;
        return dataSource = new HikariDataSource(config.getHikariConfig());
    }

    @Override
    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            while (DriverManager.getDrivers()
                    .hasMoreElements())
                try {
                    DriverManager.deregisterDriver(DriverManager.getDrivers()
                            .nextElement());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }
}