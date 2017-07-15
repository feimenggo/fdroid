package com.feimeng.fdroid.mvp.model.api.bean;

/**
 * 请求结果
 * Created by feimeng on 2017/1/20.
 */
public class Response<T> implements FDResponse<T> {
    private int code;// 结果码
    private String info;// 描述信息
    private T data;// 结果数据

    /**
     * 请求是否成功
     *
     * @return true 成功，false 失败
     */
    @Override
    public boolean isSuccess() {
        return code == 200;
    }

    /**
     * 返回状态码
     *
     * @return 100 成功
     */
    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "状态码:" + code + " 描述:" + info;
    }

    @Override
    public T getData() {
        return data;
    }
}
