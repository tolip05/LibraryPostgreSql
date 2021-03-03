package com.cc.logging.impl;

import com.cc.logging.ILog;
import com.cc.logging.ILogFactory;

public class LogFactoryNoLogs implements ILogFactory {
    public static LogFactoryNoLogs getInstance() {
        return new LogFactoryNoLogs();
    }
    public ILog getLog(Class<?> c) {
        return new NoLogs();
    }
}