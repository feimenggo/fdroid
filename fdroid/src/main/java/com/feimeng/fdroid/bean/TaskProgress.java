package com.feimeng.fdroid.bean;

/**
 * Author: Feimeng
 * Time:   2020/6/28
 * Description: 任务进度
 */
public interface TaskProgress<T> {
    /**
     * 任务开始
     */
    void start();

    /**
     * 任务执行时被取消
     *
     * @param message 消息
     */
    void info(String message);

    /**
     * 任务执行成功
     *
     * @param data 结果数据
     */
    void success(T data);

    /**
     * 任务执行失败
     *
     * @param error 错误
     * @param info  描述
     */
    void fail(Throwable error, String info);

    /**
     * 任务结束
     */
    void stop();
}
