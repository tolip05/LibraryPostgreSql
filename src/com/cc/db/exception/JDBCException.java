package com.cc.db.exception;

public class JDBCException extends RuntimeException {
    private static final long serialVersionUID = -6347859037514925869L;
    private Throwable cause;

    public JDBCException() {
        super();
        cause = null;
    }

    public JDBCException(String message) {
        super(message);
        cause = null;
    }

    public JDBCException(Throwable cause) {
        super(cause != null ? cause.toString() : null);
        this.cause = null;
        this.cause = cause;
    }

    public JDBCException(String message, Throwable cause) {
        super(message);
        this.cause = null;
        this.cause = cause;
    }


    @Override
    public Throwable getCause(){
        return cause;
    }

}
