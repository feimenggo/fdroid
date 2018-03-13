package com.feimeng.fdroid.config;

/**
 * FDDroid配置文件
 * Created by feimeng on 2017/2/27.
 */
public class FDConfig {
    public static boolean DEBUG = false;// 激活debug模式(异常处理器)

    public static String SP_NAME = "fdroid";// SharedPreferences文件名

    public static boolean SHOW_HTTP_LOG = false;
    public static boolean SHOW_TOAST = true;
    public static boolean SHOW_LOG = true;

    public static short CONNECT_TIMEOUT = 15;// 连接超时时间 单位：秒
    public static short WRITE_TIMEOUT = 20;// 写入超时时间 单位：秒
    public static short READ_TIMEOUT = 20;// 读取超时时间 单位：秒

    public static boolean SHOW_HTTP_EXCEPTION_INFO = false;// 是否显示HTTP异常信息
    public static String INFO_TIMEOUT_EXCEPTION = "连接超时";// 提示信息 连接超时
    public static String INFO_CONNECT_EXCEPTION = "连接错误";// 提示信息 连接错误
    public static String INFO_HTTP_EXCEPTION = "无响应";// 提示信息 响应错误
    public static String INFO_JSON_SYNTAX_EXCEPTION = "Json语法错误";// 提示信息 Json语法错误
    public static String INFO_MALFORMED_JSON_EMPTY = "Json内容为空";// 提示信息 Json为空
    public static String INFO_MALFORMED_JSON_EXCEPTION = "Json结构错误";// 提示信息 Json结构错误
    public static String INFO_EOF_EXCEPTION = "Json非法结束";// 提示信息 Json非法结束
    public static String INFO_UNKNOWN_EXCEPTION = "未知网络错误";// 提示信息 未知错误
}
