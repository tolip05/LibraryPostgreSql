package com.cc.db.connection;

import com.zaxxer.hikari.HikariConfig;

public class HikariCustomConfig {
    private final HikariConfig hikariConfig;

    public HikariCustomConfig(DBResource resource) {
        this(resource.getDriverClassName(), resource.getJdbcUrl(), resource.getUsername(), resource.getPassword(), resource.getMaximumPoolSize(), resource.getValidationQuery());
    }

    public HikariCustomConfig(String driverClassName,
                              String jdbcUrl,
                              String username,
                              String password,
                              int maximumPoolSize,
                              String validationQuery) {
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.addDataSourceProperty("cachePrepStmts",
                "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize",
                "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit",
                "2048");
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(0);
        if (validationQuery != null
                && validationQuery.isEmpty())
            hikariConfig.setConnectionTestQuery(validationQuery);
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }
}