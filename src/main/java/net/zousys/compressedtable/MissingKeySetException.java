package net.zousys.compressedtable;

public class MissingKeySetException extends Exception{
    public MissingKeySetException() {
    }

    public MissingKeySetException(String message) {
        super(message);
    }

    public MissingKeySetException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingKeySetException(Throwable cause) {
        super(cause);
    }

    public MissingKeySetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
