package com.cc.logging.impl;

import com.cc.logging.ILog;

public class LogSystemOut implements ILog {
    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void debug(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }

    @Override
    public void error(Exception e) {
        e.printStackTrace();
    }
}
