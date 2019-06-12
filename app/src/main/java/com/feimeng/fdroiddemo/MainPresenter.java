package com.feimeng.fdroiddemo;

import androidx.annotation.Nullable;

public class MainPresenter extends MainContract.Presenter {
    @Nullable
    @Override
    protected Object onInit(boolean initAsync) throws Exception {
        Thread.sleep(5000);
        return super.onInit(initAsync);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }
}
