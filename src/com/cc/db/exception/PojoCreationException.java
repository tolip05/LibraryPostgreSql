package com.cc.db.exception;

public class PojoCreationException extends Exception {
    private static final long serialVersionUID = 9056128291289049737L;

    public PojoCreationException() {
        super();
    }

    public PojoCreationException(String message) {
        super(message);
    }

    public PojoCreationException(String message,
                                 Throwable cause) {
        super(message, cause);
    }

    public PojoCreationException(Throwable cause) {
        super(cause);
    }

}
