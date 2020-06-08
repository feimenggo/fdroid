package com.feimeng.fdroid.exception;

import com.feimeng.fdroid.mvp.model.api.ResponseCodeInterceptorListener;
import com.feimeng.fdroid.mvp.model.api.bean.FDResponse;

/**
 * Author: Feimeng
 * Time:   2018/11/9 10:52
 * Description: 自定义异常，当接口返回的{@link FDResponse#isSuccess()}为false时，需要抛出此异常 eg：请求参数不全、用户令牌错误等
 */
public class ApiException extends Exception {
    public static final int CODE_RESPONSE_NULL = 0;
    public static final int CODE_REQUEST_UNSUCCESSFUL = -1;
    /**
     * 请求被{@link ResponseCodeInterceptorListener#onResponse(FDResponse)} 拦截
     */
    public static final int CODE_RESPONSE_INTERCEPTOR = -2;
    private int code;
    private String message;

    public ApiException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
