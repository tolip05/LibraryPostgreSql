package com.cc.db.connection;

import com.cc.db.exception.JDBCException;

import javax.naming.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConnectionFactory implements IConnectionFactory {
    // public static final String DATASOURCE_NAME = "jdbc/proxy_ws_1";
    public static final String DB_CONN_RESOURCE = "jdbc";
    private static DataSource dataSource;

    private DataSource getDataSource() {
        if (dataSource != null)
            return dataSource;
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            NamingEnumeration<NameClassPair> connList = envContext.list(DB_CONN_RESOURCE);
            while (connList.hasMoreElements()) {
                NameClassPair pair = connList.nextElement();
                String tmpStr = DB_CONN_RESOURCE
                        + "/"
                        + pair.getName();
                dataSource = (DataSource) envContext.lookup(tmpStr);
            }

            return dataSource;
        } catch (NamingException e) {
            throw new JDBCException(e);
        }
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
        // Nothing to do
    }
}
