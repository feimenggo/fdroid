package com.feimeng.fdroiddemo;

import com.feimeng.fdroid.mvp.base.FDPresenter;
import com.feimeng.fdroid.mvp.base.FDView;

public interface MainContract {
    interface View extends FDView {
    }

    abstract class Presenter extends FDPresenter<View> {
    }
}
