package com.feimeng.fdroid.config;

/**
 * Author: Feimeng
 * Time:   2020/6/28
 * Description: RxJava配置
 */
public class RxJavaConfig {
    public static ErrorInterceptor interceptor; // RxJava错误拦截器

    public interface ErrorInterceptor {
        /**
         * 拦截RxJava错误
         *
         * @param throwable 异常
         * @return 是否继续调用FastTask/FDApi的fail方法
         */
        boolean onError(Throwable throwable);
    }
}
