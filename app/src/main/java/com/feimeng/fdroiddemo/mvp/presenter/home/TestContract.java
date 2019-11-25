package com.feimeng.fdroiddemo.mvp.presenter.home;

import com.feimeng.fdroiddemo.base.BasePresenter;
import com.feimeng.fdroiddemo.base.BaseView;

public interface TestContract {
    interface View extends BaseView {
        void getUserName(String username);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void getUserName();
    }
}
