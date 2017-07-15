package com.feimeng.fdroiddemo.login;

import com.trello.rxlifecycle.android.ActivityEvent;

import rx.Observable;
import rx.Subscriber;

public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void login() {
        untilEvent(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

            }
        }), ActivityEvent.STOP).subscribe();
    }
}
