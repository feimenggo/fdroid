package com.feimeng.fdroid.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * RxJava辅助工具库
 * Created by feimeng on 2017/8/5.
 */
public class RxJavas {
    /**
     * 根据条件选择是否执行Observable
     *
     * @param defaultValue 默认数据
     * @param observable   待执行的源事件
     */
    public static <T> Observable<T> choose(final T defaultValue, Observable<T> observable) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) {
                if (defaultValue != null) {
                    e.onNext(defaultValue);
                }
                e.onComplete();
            }
        }).switchIfEmpty(observable);
    }
}
