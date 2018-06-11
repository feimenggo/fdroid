package com.feimeng.fdroiddemo.login;

public class TestPresenter extends TestContract.Presenter {
    @Override
    public void getUserName() {
        // 这里进行具体的业务
        // ...
        // 简单模拟获取到的用户名是“小飞”
        String username = "小飞";
        // 回调结果给View层
        mView.getUserName(username);
    }
}
