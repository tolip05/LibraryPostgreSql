package com.cc.logging;
import com.cc.db.DBConfig;

public class LOG {
    public static ILog getLog(Class<?> loggedClass) {
        return DBConfig.logFactory != null ? DBConfig.logFactory.getLog(loggedClass) : null;
    }
}
