package com.feimeng.fdroid.mvp.model.api.bean;

import java.util.List;

/**
 * 结果列表 适用于分页数据
 * Created by feimeng on 2017/1/20.
 */
public class ResultList<T> {
    private int total;// 总数
    private int pageNum;// 当前页码，例：1，2，3
    private int pageSize;// 每页条数，例：10
    private List<T> list;// 列表数据

    public int getTotal() {
        return total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getList() {
        return list;
    }
}
