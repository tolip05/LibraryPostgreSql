package com.cc.db.dbf.reader.exception;

public class DbfException extends RuntimeException {
    private static final long serialVersionUID = 5304399011333062664L;

    public DbfException(String message,
                        Throwable cause) {
        super(message,
                cause);
    }

    public DbfException(String message) {
        super(message);
    }
}
