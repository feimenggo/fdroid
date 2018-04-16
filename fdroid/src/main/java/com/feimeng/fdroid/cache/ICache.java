package com.feimeng.fdroid.cache;

import rx.Observable;

/**
 * Created by feimeng on 2018/3/30.
 */
public interface ICache {
    <T> Observable<T> get(String key, Class<T> cls);

    <T> void put(String key, T t);
}