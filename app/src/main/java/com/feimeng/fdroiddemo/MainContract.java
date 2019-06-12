package com.feimeng.fdroiddemo;

import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroid.mvp.FDView;

public interface MainContract {
    interface View extends FDView {
    }

    abstract class Presenter extends FDPresenter<View> {
    }
}
