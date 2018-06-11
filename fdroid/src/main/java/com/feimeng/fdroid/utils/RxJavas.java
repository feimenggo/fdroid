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
     * @param condition  true 执行,false 不执行
     * @param observable 待执行的源事件
     */
    public static <T> Observable<T> choose(final boolean condition, Observable<T> observable) {
        return Observable.concat(Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                if (!condition) emitter.onNext(null);
                emitter.onComplete();
            }
        }), observable).firstElement().toObservable();
    }

    public static class NULL {
    }
}
