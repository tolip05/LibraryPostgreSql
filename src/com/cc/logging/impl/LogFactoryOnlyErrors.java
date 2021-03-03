package com.cc.logging.impl;

import com.cc.logging.ILog;
import com.cc.logging.ILogFactory;

public class LogFactoryOnlyErrors implements ILogFactory {
    public static LogFactoryOnlyErrors getInstance() {
        return new LogFactoryOnlyErrors();
    }

    public ILog getLog(Class<?> c) {
        return new OnlyErrors();
    }
}