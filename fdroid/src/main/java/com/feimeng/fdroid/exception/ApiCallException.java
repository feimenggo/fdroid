package com.feimeng.fdroid.exception;

/**
 * Author: Feimeng
 * Time:   2020/6/8
 * Description: FDAPi.call()方法异常
 */
public class ApiCallException extends Exception {
    public ApiCallException() {
    }

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
