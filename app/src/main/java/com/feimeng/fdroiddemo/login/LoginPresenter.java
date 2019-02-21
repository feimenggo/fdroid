package com.feimeng.fdroiddemo.login;

import com.feimeng.fdroid.mvp.model.api.bean.ApiFinish2;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.api.ApiWrapper;

public class LoginPresenter extends LoginContract.Presenter {
    @Override
    public void login() {
        lifecycle(withNet(ApiWrapper.getInstance().login("10086", "123456"), new OnWithoutNetwork() {
            @Override
            public void withoutNetwork(Object data) {
                T.showS(getContext(), "无网络连接");
            }
        })).subscribe(ApiWrapper.subscriber("login", new ApiFinish2<Integer>() {
            @Override
            public void start() {
                showDialog("登录中...", true, "login");
            }

            @Override
            public void success(Integer data) {
                T.showS(getContext(), "登录成功");
            }

            @Override
            public void fail(Throwable error, String info) {
                T.showS(getContext(), "登录出错" + info);
            }

            @Override
            public void stop() {
                hideDialog();
            }
        }));
    }
}
