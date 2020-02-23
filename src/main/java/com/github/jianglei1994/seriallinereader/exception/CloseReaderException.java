package com.github.jianglei1994.seriallinereader.exception;

/**
 * Reader关闭时抛出的异常，或者用已关闭的reader读取文件时抛出的异常
 *
 * @author jianglei43
 * @date 2019/10/17
 */
public class CloseReaderException extends RuntimeException {

    public CloseReaderException() {
        super();
    }

    public CloseReaderException(String message) {
        super(message);
    }

    public CloseReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloseReaderException(Throwable cause) {
        super(cause);
    }
}
