package com.feimeng.fdroiddemo.login;

import rx.Observable;
import rx.Subscriber;

public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void login() {
        lifecycle(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

            }
        })).subscribe();
    }
}
