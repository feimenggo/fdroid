package com.feimeng.fdroiddemo.mvp.presenter.home;

import com.feimeng.fdroiddemo.base.BasePresenter;
import com.feimeng.fdroiddemo.base.BaseView;

public interface TestContract {
    interface View extends BaseView<Object> {
        void getUserName(String username, String error);
    }

    abstract class Presenter extends BasePresenter<View, Object> {
        public abstract void getUserName();
    }
}
