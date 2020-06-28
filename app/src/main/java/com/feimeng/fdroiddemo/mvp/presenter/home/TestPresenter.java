package com.feimeng.fdroiddemo.mvp.presenter.home;

import com.feimeng.fdroid.utils.FastTask;

public class TestPresenter extends TestContract.Presenter {
    @Override
    public void getUserName() {
        new FastTask<String>() {
            @Override
            public String task() throws Exception {
                // 这里进行具体的业务
                // ...
                Thread.sleep(2000);
                // 简单模拟获取到的用户名是“小飞”
                String username = "小飞";

                return username;
            }
        }.runCalc(new FastTask.Result<String>() {
            @Override
            public void start() {
                showDialog();
            }

            @Override
            public void success(String username) {
                // 回调结果给View层
                if (isActive()) mView.getUserName(username, null);
            }

            @Override
            public void fail(Throwable error, String info) {
                if (isActive()) mView.getUserName(null, info);
            }

            @Override
            public void stop() {
                hideDialog();
            }
        });
    }
}
