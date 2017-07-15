package com.feimeng.fdroid.mvp.base;

/**
 * 空
 * Created by feimeng on 2017/1/4.
 */
public interface FDEmptyContract {
    interface View extends FDView {
    }

    abstract class Presenter extends FDPresenter<View> {
    }
}
