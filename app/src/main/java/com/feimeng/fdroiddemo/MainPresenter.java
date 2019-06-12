package com.feimeng.fdroiddemo;

import android.support.annotation.Nullable;

public class MainPresenter extends MainContract.Presenter {
    @Nullable
    @Override
    protected Object onInit(boolean initAsync) throws Exception {
        Thread.sleep(5000);
        return super.onInit(initAsync);
    }

    public void init() {
//        new FastTask<Ignore>() {
//            @Override
//            public Ignore task() throws Exception {
//                Thread.sleep(2000);
//                return Ignore.instance;
//            }
//        }.runIO(new FastTask.Result<Ignore>() {
//            @Override
//            public void success(Ignore ignore) {
//                L.d("nodawang", "hello");
//            }
//        }, mView);
    }
}
