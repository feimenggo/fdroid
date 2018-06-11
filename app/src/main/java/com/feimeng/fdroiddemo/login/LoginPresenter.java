package com.feimeng.fdroiddemo.login;

import com.feimeng.fdroid.utils.FastTask;

public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void login() {
        lifecycle(new FastTask<Void>() {
            @Override
            public Void task() {
                return null;
            }
        }.fast());
    }
}
