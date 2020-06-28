package com.feimeng.fdroid.mvp.model.api.bean;

import com.feimeng.fdroid.bean.TaskProgress;
import com.feimeng.fdroid.exception.ApiException;

/**
 * API结束回调
 * Created by feimeng on 2017/1/20.
 */
public interface FDApiFinish<T> extends TaskProgress<T> {
    /**
     * 接口返回异常
     *
     * @param exception 异常对象
     * @return 是否继续调用fail(Throwable error, String info)
     */
    boolean apiFail(ApiException exception);
}
