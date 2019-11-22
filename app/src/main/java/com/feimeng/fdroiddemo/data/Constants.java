package com.feimeng.fdroiddemo.data;

/**
 * 常量
 * Created by feimeng on 2016/3/20.
 */
public class Constants {
    public static final String NAME = "信达商品"; // 应用名
    public static final int VERSION = 1; // 应用版本号

    /**
     * 消息头
     */
    public static final String HEADER_CLIENT_TYPE_NAME = "Client-Type";// 客户端类型
    public static final String HEADER_CLIENT_TYPE_VALUE = "android-170";
    public static final String HEADER_SIGN_NAME = "Sign";// 身份令牌
    public static final String HEADER_TIMESTAMP_NAME = "Timestamp";// 身份令牌

    /**
     * 配置
     */
    public static final String SP_NAME = "fdroid";
    public static final String SP_VERSION = "version_number"; // 应用版本号
    public static final String SP_MASTER_UID = "master_uid"; // 当前登录账号的用户ID
    public static final String SP_START_WELCOME = "welcome"; // 是否进入欢迎界面	Boolean
}