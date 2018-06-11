package com.feimeng.fdroid.mvp.model.api;

/**
 * 请求头
 * Created by feimeng on 2017/3/9.
 */
public class HeaderParam {
    private String key;
    private String value;
    private boolean replace;

    /**
     * 请求头
     *
     * @param key   键
     * @param value 值
     */
    public HeaderParam(String key, String value) {
        this(key, value, true);
    }

    /**
     * 请求头
     *
     * @param key     键
     * @param value   值
     * @param replace 同名请求，是否覆盖
     */
    public HeaderParam(String key, String value, boolean replace) {
        this.key = key;
        this.value = value;
        this.replace = replace;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isReplace() {
        return replace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeaderParam that = (HeaderParam) o;

        return key != null ? key.equals(that.key) : that.key == null;

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
