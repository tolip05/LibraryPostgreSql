package com.cc.logging.impl;

import com.cc.logging.ILog;
import com.cc.logging.ILogFactory;

public class LogFactorySystemOut implements ILogFactory {
    public static LogFactorySystemOut getInstance() {
        return new LogFactorySystemOut();
    }

    public ILog getLog(Class<?> c) {
        return new LogSystemOut();
    }
}