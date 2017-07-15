package com.feimeng.fdroid.mvp.model.api.bean;

/**
 * 响应结果
 * Created by feimeng on 2017/2/27.
 */
public interface FDResponse<T> {
    /**
     * 请求是否成功
     *
     * @return true 成功，false 失败
     */
    boolean isSuccess();

    /**
     * 返回结果码
     *
     * @return 小于10000的数字，例如：200；203；
     */
    int getCode();

    /**
     * 结果说明
     *
     * @return 描述结果码，例如：200 成功；203 用户不存在；
     */
    String getInfo();

    /**
     * 得到实体结果
     *
     * @return T
     */
    T getData();
}
