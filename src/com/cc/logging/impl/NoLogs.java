package com.cc.logging.impl;

import com.cc.logging.ILog;

public class NoLogs implements ILog {

    @Override
    public void info(String message) {
    }

    @Override
    public void debug(String message) {
    }

    @Override
    public void error(String message) {
    }

    @Override
    public void error(Exception e) {
    }

}