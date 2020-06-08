package com.feimeng.fdroid.config;

/**
 * FDDroid配置文件
 * Created by feimeng on 2017/2/27.
 */
public class FDConfig {
    public static boolean SHOW_HTTP_LOG = false;

    public static short CONNECT_TIMEOUT = 15; // 连接超时时间 单位：秒
    public static short WRITE_TIMEOUT = 30; // 写入超时时间 单位：秒
    public static short READ_TIMEOUT = 30; // 读取超时时间 单位：秒

    public static boolean PRINT_HTTP_EXCEPTION = false; // 是否打印HTTP异常堆栈信息
    public static String INFO_UNKNOWN_HOST_EXCEPTION = "网络异常"; // 提示信息 无法解析主机
    public static String INFO_TIMEOUT_EXCEPTION = "连接超时"; // 提示信息 连接超时
    public static String INFO_CONNECT_EXCEPTION = "连接错误"; // 提示信息 连接错误
    public static String INFO_HTTP_EXCEPTION = "服务器无响应"; // 提示信息 响应错误
    public static String INFO_JSON_SYNTAX_EXCEPTION = "Json语法错误"; // 提示信息 Json语法错误
    public static String INFO_MALFORMED_JSON_EXCEPTION = "Json结构错误"; // 提示信息 Json结构错误
    public static String INFO_EOF_EXCEPTION = "Json结束错误"; // 提示信息 Json非法结束
}
