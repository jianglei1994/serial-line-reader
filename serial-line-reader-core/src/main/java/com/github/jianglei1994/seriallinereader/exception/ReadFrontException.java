package com.github.jianglei1994.seriallinereader.exception;

/**
 * Reader进行前向读取时抛出的异常
 *
 * @author jianglei43
 * @date 2019/10/17
 */
public class ReadFrontException extends RuntimeException {

    public ReadFrontException() {
        super();
    }

    public ReadFrontException(String message) {
        super(message);
    }

    public ReadFrontException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadFrontException(Throwable cause) {
        super(cause);
    }
}
