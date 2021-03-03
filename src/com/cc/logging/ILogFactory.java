package com.cc.logging;

public interface ILogFactory {
    ILog getLog(Class<?> loggedClass);
}