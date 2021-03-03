package com.cc.logging;

public interface ILog {
    void info(String message);

    void debug(String message);

    void error(String message);

    void error(Exception e);
}