package com.feimeng.fdroiddemo.login;

import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroid.mvp.FDView;

public interface TestContract {
    interface View extends FDView {
        void getUserName(String username);
    }

    abstract class Presenter extends FDPresenter<View> {
        public abstract void getUserName();
    }
}
