package com.cc.db.dbf.reader.processor;

public interface DbfRowMapper<T> {
    T mapRow(Object[] row);
}