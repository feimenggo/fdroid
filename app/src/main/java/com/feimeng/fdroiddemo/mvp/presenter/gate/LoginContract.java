package com.feimeng.fdroiddemo.mvp.presenter.gate;

import com.feimeng.fdroiddemo.base.BasePresenter;
import com.feimeng.fdroiddemo.base.BaseView;

public interface LoginContract {
    interface View extends BaseView<String> {
        void login(String result, String error);
    }

    abstract class Presenter extends BasePresenter<View, String> {
        public abstract void loginBySync(String phone, String password);

        public abstract void loginByAsync(String phone, String password);
    }
}
