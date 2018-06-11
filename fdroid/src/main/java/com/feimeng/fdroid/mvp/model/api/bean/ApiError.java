package com.feimeng.fdroid.mvp.model.api.bean;

/**
 * Api错误类型
 * Created by feimeng on 2017/1/20.
 */
public enum ApiError {
    SERVER,// 服务器错误 错误起源于服务器
    CLIENT,// 客户端错误 错误起源于客户端
    STOP,// 停止服务
    FORBID,// 禁止访问
    ACTION,// 正常业务
    UNKNOWN// 未知错误
}
