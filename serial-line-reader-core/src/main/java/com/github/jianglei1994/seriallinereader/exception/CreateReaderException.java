package com.github.jianglei1994.seriallinereader.exception;

/**
 * Reader创建时抛出的异常
 *
 * @author jianglei43
 * @date 2019/10/17
 */
public class CreateReaderException extends RuntimeException {

    public CreateReaderException() {
        super();
    }

    public CreateReaderException(String message) {
        super(message);
    }

    public CreateReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateReaderException(Throwable cause) {
        super(cause);
    }
}
